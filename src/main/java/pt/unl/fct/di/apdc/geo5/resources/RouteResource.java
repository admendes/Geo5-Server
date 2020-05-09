package pt.unl.fct.di.apdc.geo5.resources;

import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
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
import com.google.gson.JsonObject;

import pt.unl.fct.di.apdc.geo5.data.AuthToken;
import pt.unl.fct.di.apdc.geo5.data.RouteData;
import pt.unl.fct.di.apdc.geo5.data.AddRouteData;
import pt.unl.fct.di.apdc.geo5.util.Jwt;

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
	public Response submitRoute(AddRouteData routeData, @Context HttpHeaders headers) {
		Jwt j = new Jwt();
		AuthToken data = j.getAuthToken(headers.getHeaderString("token"));
		LOG.fine("Attempt to submit route: " + routeData.title + " from user: " + data.username);
		if (!j.validToken(headers.getHeaderString("token"))) {
			LOG.warning("Invalid token for username: " + data.username);
			return Response.status(Status.FORBIDDEN).build();
		}
		if (!routeData.validRegistration()) {
			return Response.status(Status.BAD_REQUEST).entity("Missing or wrong parameter.").build();
		}
		Transaction txn = datastore.newTransaction();
		try {
			Key routeKey = datastore.newKeyFactory().setKind("Route").newKey(routeData.title);
			Entity route = datastore.get(routeKey);
			if (route != null) {
				txn.rollback();
				return Response.status(Status.BAD_REQUEST).entity("Route already exists.").build();
			} else {
				route = Entity.newBuilder(routeKey)
						.set("route_name", routeData.title)
						.set("route_owner", data.username)
						.set("route_description", routeData.description)
						.set("route_start_lat", routeData.origin.lat)
						.set("route_start_lon", routeData.origin.lng)
						.set("route_end_lat", routeData.destination.lat)
						.set("route_end_lon", routeData.destination.lng)
						.set("route_creation_time", Timestamp.now())
						.set("active_route", true)
						.build();
				txn.add(route);
				LOG.info("Route registered " + routeData.title + "from user: " + data.username);
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
	public Response getRoute(RouteData routeData, @Context HttpHeaders headers) {
		Jwt j = new Jwt();
		AuthToken data = j.getAuthToken(headers.getHeaderString("token"));
		LOG.fine("Attempt to get route: " + routeData.routeName + " by user: " + data.username);
		if (!j.validToken(headers.getHeaderString("token"))) {
			LOG.warning("Invalid token for username: " + data.username);
			return Response.status(Status.FORBIDDEN).build();
		}
		if (routeData.routeName.equals("")) {
			return Response.status(Status.BAD_REQUEST).entity("Please enter a route name.").build();
		}
		try {
			Key routeKey = datastore.newKeyFactory().setKind("Route").newKey(routeData.routeName);
			Entity r = datastore.get(routeKey);
			if (r == null) {
				return Response.status(Status.BAD_REQUEST).entity("Route does not exist.").build();
			} else {
		        JsonObject result = new JsonObject();
		        result.addProperty("route_name", r.getKey().getName());
		        result.addProperty("route_owner", r.getString("route_owner"));
		        result.addProperty("route_description", r.getString("route_description"));
		        result.addProperty("route_start_lat", r.getLong("route_start_lat"));
		        result.addProperty("route_start_lon", r.getLong("route_start_lon"));
		        result.addProperty("route_end_lat", r.getLong("route_end_lat"));
		        result.addProperty("route_end_lon", r.getLong("route_end_lon"));
		        result.addProperty("route_creation_time", r.getTimestamp("route_creation_time").toString());
		        result.addProperty("active_route", r.getBoolean("active_route"));
				LOG.info("Got route: " + routeData.routeName + " for user: " + data.username);
				return Response.ok(g.toJson(result)).build();
			}
		} catch (Exception e) {
			LOG.severe(e.getMessage());
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
	}
}
