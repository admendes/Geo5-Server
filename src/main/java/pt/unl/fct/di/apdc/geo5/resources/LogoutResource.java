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

import pt.unl.fct.di.apdc.geo5.util.AuthToken;


@Path("/logout")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class LogoutResource {
	
	/**
	 * Logger Object
	 */
	private static final Logger LOG = Logger.getLogger(LoginResource.class.getName());
	private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
	
	private final KeyFactory tokenKeyFactory = datastore.newKeyFactory().setKind("Token");

	public LogoutResource() { }

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response doLogout(AuthToken data) {
		LOG.fine("Attempt to logout user: " + data.username);
		Key tokenKey = tokenKeyFactory.newKey(data.tokenID);
		Transaction txn = datastore.newTransaction();
		try {
			Entity e = txn.get(tokenKey);
			if(e == null) {
				LOG.warning("Failed logout attempt for username: " + data.username);
				return Response.status(Status.FORBIDDEN).build();
			}
            AuthToken token = new AuthToken(
            		e.getString("user_name"),
                    e.getString("token_ID"),
                    e.getLong("creation_data"),
                    e.getLong("expiration_data"),
                    e.getString("user_role"));
            if(data.equals(token) && data.validToken() && e.getBoolean("validity")) {
            	data.makeInvalid();
            	e = Entity.newBuilder(tokenKey)
						.set("user_name", data.username)
						.set("token_ID", data.tokenID)
						.set("user_role", data.role)
						.set("creation_data", data.creationData)
						.set("expiration_data", data.expirationData)
            			.set("validity", false).build();
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
