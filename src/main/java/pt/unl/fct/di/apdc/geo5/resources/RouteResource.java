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
import com.google.gson.Gson;

import pt.unl.fct.di.apdc.geo5.util.Pointer;
import pt.unl.fct.di.apdc.geo5.util.RouteData;

@Path("/route")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class RouteResource {

	private static final Logger LOG = Logger.getLogger(LoginResource.class.getName());
	private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
	
	private final Gson g = new Gson();

	public RouteResource() { }

	@POST
	@Path("/submit")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response submitRoute(RouteData data) {
		LOG.fine("Attempt to submit route: " + data.routeName + " from user: " + data.username);
		if (!data.validRegistration()) {
			return Response.status(Status.BAD_REQUEST).entity("Missing or wrong parameter.").build();
		}
		Transaction txn = datastore.newTransaction();
		try {
			Key routeKey = datastore.newKeyFactory().setKind("Route").newKey(data.routeName);
			Entity route = datastore.get(routeKey);
			if (route != null) {
				txn.rollback();
				return Response.status(Status.BAD_REQUEST).entity("Route already exists.").build();
			} else {
				route = Entity.newBuilder(routeKey)
						.set("route_name", data.routeName)
						.set("route_owner", data.username)
						.set("route_description", data.description)
						.set("route_start_lat", data.start.lat)
						.set("route_start_lon", data.start.lon)
						.set("route_end_lat", data.end.lat)
						.set("route_end_lon", data.end.lon)
						.set("route_creation_time", Timestamp.now())
						.set("active_route", true)
						.build();
				txn.add(route);
				LOG.info("Route registered " + data.routeName + "from user: " + data.username);
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
	
	@POST
	@Path("/get")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getRoute(String routeName) {
		LOG.fine("Attempt to get route: " + routeName);
		if (routeName.equals("")) {
			return Response.status(Status.BAD_REQUEST).entity("Please enter a route name.").build();
		}
		try {
			Key routeKey = datastore.newKeyFactory().setKind("Route").newKey(routeName);
			Entity r = datastore.get(routeKey);
			if (r == null) {
				return Response.status(Status.BAD_REQUEST).entity("Route does not exist.").build();
			} else {
				Pointer start = new Pointer(r.getLong("route_start_lat"), r.getLong("route_start_lon"));
				Pointer end = new Pointer(r.getLong("route_end_lat"), r.getLong("route_end_lon"));
				RouteData route = new RouteData(
						start,
						end,
						r.getString("route_name"),
						r.getString("route_owner"),
						r.getString("route_description"),
						r.getBoolean("active_route"));
				LOG.info("Got route: " + routeName);
				return Response.ok(g.toJson(route)).build();
			}
		} catch (Exception e) {
			LOG.severe(e.getMessage());
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
	}
}