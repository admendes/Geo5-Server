package pt.unl.fct.di.apdc.geo5.util;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreFailureException;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;

import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.ServingUrlOptions;

import pt.unl.fct.di.apdc.geo5.resources.LoginResource;

public class Upload extends HttpServlet {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
	private static final Logger LOG = Logger.getLogger(LoginResource.class.getName());
	private ImagesService imagesService = ImagesServiceFactory.getImagesService();

    @Override
    public void doPost2(HttpServletRequest req, HttpServletResponse res)
        throws ServletException, IOException {

        Map<String, List<BlobKey>> blobs = blobstoreService.getUploads(req);
        List<BlobKey> blobKeys = blobs.get("myFile");

        if (blobKeys == null || blobKeys.isEmpty()) {
            res.sendRedirect("/");
        } else {
            res.sendRedirect("/serve?blob-key=" + blobKeys.get(0).getKeyString());
        }
    }
    
    
    
	public void doPost2(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		//Upload file to specified bucket. The request must have the form /gcs/<bucket>/<object>
		java.nio.file.Path objectPath = Paths.get(req.getPathInfo());
		if (objectPath.getNameCount() != 2) {
			throw new IllegalArgumentException("The URL is not formed as expected. " + "Expecting /gcs/<bucket>/<object>");
		}
		//Get the bucket and object from the URL
		String bucketName = objectPath.getName(0).toString();
		String srcFilename = objectPath.getName(1).toString();		
		//Upload to  Google Cloud Storage (see Google's documentation)
		Storage storage = StorageOptions.getDefaultInstance().getService();
		BlobId blobId = BlobId.of(bucketName, srcFilename);
		BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType(req.getContentType()).build();
		//The Following is deprecated since it is better to upload directly to GCS from the client
		Blob blob = storage.create(blobInfo, req.getInputStream());
	}
	
	public void doGet2(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		//Download file from a specified bucket. The request must have the form /gcs/<bucket>/<object>
		Storage storage = StorageOptions.getDefaultInstance().getService();
		//Parse the request URL
		java.nio.file.Path objectPath = Paths.get(req.getPathInfo());
		if (objectPath.getNameCount() != 2) {
			throw new IllegalArgumentException("The URL is not formed as expected. " + "Expecting /gcs/<bucket>/<object>");
		}
		//Get the bucket and object names
		String bucketName = objectPath.getName(0).toString();
		String srcFilename = objectPath.getName(1).toString();		
		Blob blob = storage.get(BlobID.of(bucketName, srcFilename));
		//Download object to the output stream. See Google's documentation.
		blob.download(resp.getOutputStream());
	}
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		LOG.info("inside the doGet method");
		resp.setContentType("application/json");
		try { 
			resp.getWriter().println("{ \"url\":\"" + blobstoreService.createUploadUrl("/upload")+"\"}");
		}catch (Exception e) {
			e.printStackTrace();
			LOG.warning("Exception occurred while optaining url from blob store" + e.getMessage());
		}
	}
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		LOG.info("inside the doPost method");
		
		Map<String, List<BlobKey>> blobs = blobstoreService.getUploads(req);
		List<BlobKey> blobKeys = blobs.get("myFile");
		try {
			if (blobKeys == null || blobKeys.isEmpty()) {
				LOG.info("recieved null");
				resp.sendRedirect("/");
			}
			else {
				LOG.info("inside the doPost method-processing blobs");
				
				ServingUrlOptions servingOptions = ServingUrlOptions.Builder.withBlobKey(blobKeys.get(0));
				String servingUrl = imagesService.getServingUrl(servingOptions);
				
				String outputJson = "{ \"key\":\""+blobKeys.get(0).getKeyString()+"\" ,\"url\":\""+servingUrl+"\"}";
				
				LOG.info("inside the doPost method-done-outputjson   ::  "+ outputJson);
				resp.setContentType("application/json");
				resp.getWriter().println(outputJson);
			}
		} catch(Exception e){
			LOG.info("inside doPost occurred exception: " +e.getMessage());
			e.printStackTrace();
		}
	}
	
	public void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String key = req.getParameter("key");
		LOG.info("inside the doDelete " +key);
		try {
			if (key!=null) blobstoreService.delete(new BlobKey(key));
		} catch (BlobstoreFailureException e) {
			LOG.info("inside doDelete occurred blobstorefailureexception: " +e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			LOG.info("inside doDelete occurred exception: " +e.getMessage());
			e.printStackTrace();
		}
		
	}
}