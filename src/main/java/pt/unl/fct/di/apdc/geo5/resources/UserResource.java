package pt.unl.fct.di.apdc.geo5.resources;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.google.cloud.Timestamp;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;
import com.google.cloud.datastore.StructuredQuery.CompositeFilter;
import com.google.cloud.datastore.StructuredQuery.PropertyFilter;
import com.google.gson.Gson;

import pt.unl.fct.di.apdc.geo5.data.LoginData;
import pt.unl.fct.di.apdc.geo5.data.UserData;

@Path("/user")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class UserResource {
	
	private static final Logger LOG = Logger.getLogger(LoginResource.class.getName());
	private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
	
	private final Gson g = new Gson();
	
	public UserResource() {
		
	}
	
	@POST
	@Path("/get")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getUser(String username) {
		LOG.fine("Attempt to get user: " + username);
		if (username.equals("")) {
			return Response.status(Status.BAD_REQUEST).entity("Please enter a username.").build();
		}
		try {
			Key userKey = datastore.newKeyFactory().setKind("User").newKey(username);
			Entity u = datastore.get(userKey);
			if (u == null) {
				return Response.status(Status.BAD_REQUEST).entity("Username does not exist.").build();
			} else {
				UserData user = new UserData(
						u.getKey().toString(),
						u.getString("user_name"),
						u.getString("user_email"));
				LOG.info("Got user: " + username);
				return Response.ok(g.toJson(user)).build();
			}
		} catch (Exception e) {
			LOG.severe(e.getMessage());
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
	}
	
	@POST
	@Path("/listActive")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listActiveUsers(LoginData data) {
		LOG.fine("Attempt to list active users");
		Query<Entity> query = Query.newEntityQueryBuilder()
				.setKind("User")
				.setFilter(PropertyFilter.eq("active_account", true))
				.build();
		QueryResults<Entity> logs = datastore.run(query);
		List<Entity> activeUsersList = new ArrayList<Entity>();
		logs.forEachRemaining(activeUsersLog -> {
			activeUsersList.add(activeUsersLog);
		});
		LOG.info("Inactive users deleted");
		return Response.ok(g.toJson(activeUsersList)).build();
	}
	
	@POST
	@Path("/last24hlogins")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response last24hlogins(LoginData data) {
		LOG.fine("Attempt to get last 24h logins");
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -1);
		Timestamp yesterday = Timestamp.of(cal.getTime());
		Query<Entity> query = Query.newEntityQueryBuilder()
				.setKind("UserLog")
				.setFilter(
						CompositeFilter.and(
								PropertyFilter.hasAncestor(
										datastore.newKeyFactory().setKind("User").newKey(data.username)),
								PropertyFilter.ge("user_login_time", yesterday)
								)
						)
				.build();
		QueryResults<Entity> logs = datastore.run(query);
		List<Date> loginDates = new ArrayList<Date>();
		logs.forEachRemaining(userLog -> {
			loginDates.add(userLog.getTimestamp("user_login_time").toDate());
		});
		return Response.ok(g.toJson(loginDates)).build();
	}
}
