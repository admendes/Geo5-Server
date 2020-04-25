package pt.unl.fct.di.apdc.geo5.resources;

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
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.Transaction;

import pt.unl.fct.di.apdc.geo5.util.RegisterData;

@Path("/delete")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class DeleteResource {

	/**
	 * Logger Object
	 */
	private static final Logger LOG = Logger.getLogger(LoginResource.class.getName());
	private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response doDelete(RegisterData data) {
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
}