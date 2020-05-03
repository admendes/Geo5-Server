package pt.unl.fct.di.apdc.geo5.resources;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;
import com.google.cloud.datastore.Transaction;
import com.google.cloud.datastore.StructuredQuery.PropertyFilter;
import com.google.gson.Gson;

import pt.unl.fct.di.apdc.geo5.data.DeleteData;

@Path("/delete")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class DeleteResource {

	/**
	 * Logger Object
	 */
	private static final Logger LOG = Logger.getLogger(LoginResource.class.getName());
	private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

	private final Gson g = new Gson();

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response doDelete(DeleteData data) {
		LOG.fine("Attempt to delete user: " + data.username);
		Transaction txn = datastore.newTransaction();
		try {
//			if (!token.role.equals("SU")) {
//				LOG.warning("Insufficient permissions for username: " + data.username);
//				return Response.status(Status.FORBIDDEN).build();
//			}
			Key userKey = datastore.newKeyFactory().setKind("User").newKey(data.username);
			if (txn.get(userKey) == null) {
				LOG.warning("Failed delete attempt for username: " + data.username);
				return Response.status(Status.FORBIDDEN).build();
			}
			txn.delete(userKey);
			LOG.info("User deleted: " + data.username);
			txn.commit();
			return Response.ok("{}").build();
		} catch (Exception e) {
			txn.rollback();
			LOG.severe(e.getMessage());
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		} finally {
			if (txn.isActive()) {
				txn.rollback();
			}
		}
	}
	
	@POST
	@Path("/inactiveUsers")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response deleteInactiveUsers(DeleteData data) {
		LOG.fine("Attempt to delete inactive users");
		Transaction txn = datastore.newTransaction();
		try {
			Query<Entity> query = Query.newEntityQueryBuilder()
					.setKind("User")
					.setFilter(PropertyFilter.eq("active_account", false))
					.build();
			QueryResults<Entity> logs = datastore.run(query);
			List<Entity> inactiveUsersList = new ArrayList<Entity>();
			logs.forEachRemaining(inactiveUsersLog -> {
				inactiveUsersList.add(inactiveUsersLog);
				txn.delete(inactiveUsersLog.getKey());
			});
			LOG.info("Inactive users deleted");
			txn.commit();
			return Response.ok(g.toJson(inactiveUsersList)).build();
		} catch (Exception e) {
			txn.rollback();
			LOG.severe(e.getMessage());
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		} finally {
			if (txn.isActive()) {
				txn.rollback();
			}
		}
	}
	
	@POST
	@Path("/invalidTokens")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response deleteInvalidTokens(DeleteData data) {
		LOG.fine("Attempt to delete invalid tokens");
		Transaction txn = datastore.newTransaction();
		try {
			Query<Entity> query = Query.newEntityQueryBuilder()
					.setKind("Token")
					.setFilter(PropertyFilter.eq("validity", false))
					.build();
			QueryResults<Entity> logs = datastore.run(query);
			List<Entity> invalidTokensList = new ArrayList<Entity>();
			logs.forEachRemaining(invalidTokensLog -> {
				invalidTokensList.add(invalidTokensLog);
				txn.delete(invalidTokensLog.getKey());
			});
			LOG.info("Invalid tokens deleted");
			txn.commit();
			return Response.ok(g.toJson(invalidTokensList)).build();
		} catch (Exception e) {
			txn.rollback();
			LOG.severe(e.getMessage());
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		} finally {
			if (txn.isActive()) {
				txn.rollback();
			}
		}
	}
}