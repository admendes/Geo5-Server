package pt.unl.fct.di.apdc.geo5.util;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

@SuppressWarnings("serial")
public class MediaResourceServlet extends HttpServlet {
	
	/**
	 * 
	 */
	private final String BUCKET = "apdc-geoproj.appspot.com";
	
	public MediaResourceServlet() {
		
	}
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		//Upload file to specified bucket. The request must have the form /media/<object>
		Path objectPath = Paths.get(req.getPathInfo());
		if (objectPath.getNameCount() != 3) {
			throw new IllegalArgumentException("The URL is not formed as expected. " + "Expecting /media/upload/<object>");
		}
		//Get the bucket and object from the URL
		String bucketName = BUCKET;
		String srcFilename = objectPath.getName(2).toString();		
		//Upload to  Google Cloud Storage (see Google's documentation)
		Storage storage = StorageOptions.getDefaultInstance().getService();
		BlobId blobId = BlobId.of(bucketName, srcFilename);
		BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType(req.getContentType()).build();
		//The Following is deprecated since it is better to upload directly to GCS from the client
	    @SuppressWarnings({ "deprecation", "unused" })
		Blob blob = storage.create(blobInfo, req.getInputStream());
	}
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		//Download file from a specified bucket. The request must have the form /media/<object>
		Storage storage = StorageOptions.getDefaultInstance().getService();
		//Parse the request URL
		Path objectPath = Paths.get(req.getPathInfo());
		if (objectPath.getNameCount() != 3) {
			throw new IllegalArgumentException("The URL is not formed as expected. " + "Expecting /media/download/<object>");
		}
		String srcFilename = objectPath.getName(2).toString();	
		Blob blob = storage.get(BlobId.of(BUCKET, srcFilename));
		//Download object to the output stream. See Google's documentation.
		ServletOutputStream output = resp.getOutputStream();
	    blob.downloadTo(output);
	    output.close();
	}
	
	public void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		//Delete file from a specified bucket. The request must have the form /media/<object>
		Storage storage = StorageOptions.getDefaultInstance().getService();
		//Parse the request URL
		Path objectPath = Paths.get(req.getPathInfo());
		if (objectPath.getNameCount() != 3) {
			throw new IllegalArgumentException("The URL is not formed as expected. " + "Expecting /media/delete/<object>");
		}
		String srcFilename = objectPath.getName(2).toString();	
	    storage.delete(BUCKET, srcFilename);
	}
}
