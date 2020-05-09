package pt.unl.fct.di.apdc.geo5.util;

import java.io.IOException;
import java.nio.file.Paths;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class MediaResourceServlet extends HttpServlet {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MediaResourceServlet() {
		
	}
	

	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
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
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
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
}
