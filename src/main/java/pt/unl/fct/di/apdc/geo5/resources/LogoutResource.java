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
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.Transaction;

import com.google.cloud.datastore.KeyFactory;
import com.google.cloud.datastore.PathElement;

import pt.unl.fct.di.apdc.geo5.util.AuthToken;
import pt.unl.fct.di.apdc.geo5.util.Jwt;
import pt.unl.fct.di.apdc.geo5.util.JwtData;


@Path("/logout")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class LogoutResource {
	
	/**
	 * Logger Object
	 */
	private static final Logger LOG = Logger.getLogger(LoginResource.class.getName());
	private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
	
	private final KeyFactory tokenKeyFactory = datastore.newKeyFactory().setKind("UserStats");

	public LogoutResource() { }

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response doLogout(JwtData jData) {
		Jwt j = new Jwt();
		AuthToken data = j.getAuthToken(jData);
		LOG.fine("Attempt to logout user: " + data.username);
		Key userStatsKey = tokenKeyFactory.addAncestors(PathElement.of("User", data.username)).newKey("counters");
		Transaction txn = datastore.newTransaction();
		try {
			Entity e = txn.get(userStatsKey);
			if(e == null) {
				LOG.warning("Failed logout attempt for username: " + data.username);
				return Response.status(Status.FORBIDDEN).build();
			}
            String jwt = e.getString("user_token");
            if(jData.id.equals(jwt) && data.validToken()) {
            	e = Entity.newBuilder(userStatsKey)
						.set("user_stats_logins", e.getLong("user_stats_logins"))
						.set("user_stats_failed", e.getLong("user_stats_failed"))
						.set("user_first_login", e.getTimestamp("user_first_login"))
						.set("user_last_login", e.getTimestamp("user_last_login"))
						.set("user_last_attempt", e.getTimestamp("user_last_attempt"))
						.set("user_token", "null")
            			.build();
            	txn.update(e);
				txn.commit();
				LOG.info("User '" + data.username + "' logged out successfully.");
				return Response.ok("{}").build();
			}
			else {
				LOG.warning("Invalid token for username: " + data.username);
				return Response.status(Status.FORBIDDEN).build();
			}
		} catch(Exception e) {
			txn.rollback();
			LOG.severe(e.getMessage());
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();	
		} finally {
			if (txn.isActive()) {
				txn.rollback();
				return Response.status(Status.INTERNAL_SERVER_ERROR).build();
			}
		}
	}
}
