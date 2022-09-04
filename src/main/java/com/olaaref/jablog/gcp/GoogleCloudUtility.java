package com.olaaref.jablog.gcp;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.google.api.gax.paging.Page;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

@Component
public class GoogleCloudUtility {

	private Storage storage = StorageOptions.getDefaultInstance().getService();
	
	public GoogleCloudUtility() throws FileNotFoundException, IOException {
	}
	
	public String upload(MultipartFile file, String prefix) {
		try {
			String mimeType = URLConnection.guessContentTypeFromName(file.getOriginalFilename());
			BlobId blobId = BlobId.of("jablog-files", prefix + file.getOriginalFilename());
			BlobInfo info = BlobInfo.newBuilder(blobId).setContentType(mimeType).build();

			Blob created = storage.create(info, file.getBytes());
			
			return created.getMediaLink();
		} 
		catch(IllegalStateException | IOException e){
			throw new RuntimeException(e);
		}
	}
	
	public boolean deleteObject(String fileNameWithPrefix) {
		try {
			BlobId blobId = BlobId.of("jablog-files", fileNameWithPrefix);
			boolean deleted = storage.delete(blobId);
			
			return deleted;
		} 
		catch(IllegalStateException e){
			throw new RuntimeException(e);
		}
	}
	
	public void deleteFolder(String folderName) {
		try {
			Page<Blob> blobs =
	        storage.list(
	            "jablog-files",
	            Storage.BlobListOption.prefix(folderName),
	            Storage.BlobListOption.currentDirectory());

		    for (Blob blob : blobs.iterateAll()) {
		    	BlobId blobId = BlobId.of("jablog-files", blob.getName());
		    	storage.delete(blobId);
		    }
			
		} 
		catch(IllegalStateException e){
			throw new RuntimeException(e);
		}
	}
	
	public List<String> listFolder(String folderName) {
		List<String> files = new ArrayList<String>();
		
		Page<Blob> blobs =
        storage.list(
            "jablog-files",
            Storage.BlobListOption.prefix(folderName),
            Storage.BlobListOption.currentDirectory());

	    for (Blob blob : blobs.iterateAll()) {
	      files.add(blob.getName());
	    }
	    
	    
		return files;
		
	}
	
}
