package com.olaaref.jablog.gcp;

public class Constants {
	public static final String GCP_Base_URI;
	
	static {
		String GCP = "https://storage.cloud.google.com";
		String bucketName = "jablog-files";
		GCP_Base_URI = GCP + "/" + bucketName;
	}

}
