package com.olaaref.jablog.helper;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.logging.Logger;

import org.springframework.web.multipart.MultipartFile;

public class FileUploadUtil {
	private static final Logger logger = Logger.getLogger(FileUploadUtil.class.getName());  

	public static void saveFile(String uploadDirectory, String fileName, MultipartFile multipartFile) throws IOException {
		//create a path from the chosen directory 
		Path uploadPath = Paths.get(uploadDirectory);
		
		//if the path not exist, create it
		if(Files.notExists(uploadPath)) Files.createDirectories(uploadPath);
		
		try (InputStream inputStream = multipartFile.getInputStream()) {
			
			//covert fileName to a path
			//and then add it to the uploadPath
			Path filePath = uploadPath.resolve(fileName);
			
			//copy the uploaded photo to the filePath
			//and if there is another photo in the directory, replace it 
			Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
		}
		catch(IOException e) {
			throw new IOException("Could not save file: " + fileName, e);
		}
	}
	
	public static void cleanDirectory(String directory) {
		//covert directory name to a path
		Path dirPath = Paths.get(directory);
		
		//list all files in the directory
		try {
			//loop over files in the directory
			Files.list(dirPath).forEach(file -> {
				//check if the element is a file not a directory
				if(!Files.isDirectory(file)) {
					try {
						//delete the file
						Files.delete(file);
					} catch (IOException e) {
						logger.warning("Could not delete file: " + file);
					}
				}
			});
		} catch (IOException e) {
			logger.warning("Could not list directory: " + directory);
		}
	}
	
	
	
	
	
}
