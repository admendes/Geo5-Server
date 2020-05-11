package pt.unl.fct.di.apdc.geo5.resources;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import pt.unl.fct.di.apdc.geo5.util.MediaResourceServlet;

@Path("/media")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class MediaResource {

	public MediaResource() {
		
	}
	
	private static final Logger LOG = Logger.getLogger(LoginResource.class.getName());

	@POST
	@Path("/upload/{filepath}")
	public Response upload(@Context HttpServletRequest req, @Context HttpServletResponse resp, @PathParam("filepath") String filepath) {
		MediaResourceServlet m = new MediaResourceServlet();
		LOG.info("Attempting to upload file: " + filepath);
		try {
			m.doPost(req, resp);
		} catch (IOException e) {
			LOG.severe(e.getMessage());
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
		LOG.info("Uploaded successfully: " + filepath);
		return Response.ok("{}").build();
	}
	
	@POST
	@Path("/download/{filepath}")
	public Response download(@Context HttpServletRequest req, @Context HttpServletResponse resp, @PathParam("filepath") String filepath) {
		MediaResourceServlet m = new MediaResourceServlet();
		LOG.info("Attempting to download file: " + filepath);
		try {
			m.doGet(req, resp);
		} catch (IOException e) {
			LOG.severe(e.getMessage());
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
		LOG.info("Downloaded successfully: " + filepath);
		return Response.ok("{}").build();
	}
	
	@POST
	@Path("/delete/{filepath}")
	public Response delete(@Context HttpServletRequest req, @Context HttpServletResponse resp, @PathParam("filepath") String filepath) {
		MediaResourceServlet m = new MediaResourceServlet();
		LOG.info("Attempting to delete file: " + filepath);
		try {
			m.doDelete(req, resp);
		} catch (IOException e) {
			LOG.severe(e.getMessage());
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
		LOG.info("Deleted successfully: " + filepath);
		return Response.ok("{}").build();
	}
}
