package pt.unl.fct.di.apdc.geo5.resources;

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
import com.google.cloud.datastore.Transaction;

import pt.unl.fct.di.apdc.geo5.util.GeoSpotData;

@Path("/geoSpot")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class GeoSpotResource {
	
	private static final Logger LOG = Logger.getLogger(LoginResource.class.getName());
	private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
	
	public GeoSpotResource() { }

	@POST
	@Path("/submit")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response submitRoute(GeoSpotData data) {
		LOG.fine("Attempt to submit geoSpot: " + data.name + " from user: " + data.username);
		
		if (!data.validRegistration()) {
			return Response.status(Status.BAD_REQUEST).entity("Missing or wrong parameter.").build();
		}
		
		Transaction txn = datastore.newTransaction();
		try {
			Key geoSpotKey = datastore.newKeyFactory().setKind("GeoSpot").newKey(data.name);
			Entity geoSpot = datastore.get(geoSpotKey);
			if (geoSpot != null) {
				txn.rollback();
				return Response.status(Status.BAD_REQUEST).entity("GeoSpot already exists.").build();
			} else {
				geoSpot = Entity.newBuilder(geoSpotKey)
						.set("geoSpot_name", data.name)
						.set("geoSpot_owner", data.username)
						.set("geoSpot_description", data.description)
						.set("geoSpot_creation_time", Timestamp.now())
						.set("geoSpot_lat", data.location.lat)
						.set("geoSpot_lon", data.location.lon)
						.set("active_geoSpot", true)
						.build();
				txn.add(geoSpot);
				LOG.info("GeoSpot registered " + data.name + "from user: " + data.username);
				txn.commit();
				return Response.ok("{}").build();
			}
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
