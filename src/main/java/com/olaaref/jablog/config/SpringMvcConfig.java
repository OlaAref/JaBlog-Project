package com.olaaref.jablog.config;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SpringMvcConfig implements WebMvcConfigurer{

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		exposeDirectory(registry, "user-photos");
		exposeDirectory(registry, "site-logo");
		exposeDirectory(registry, "category-photos");
		exposeDirectory(registry, "post-photos");
		
	}

	private void exposeDirectory(ResourceHandlerRegistry registry, String pathPattern) {
		
		Path userPhotoPath = Paths.get(pathPattern);
		
		String absolutePath = userPhotoPath.toFile().getAbsolutePath();
		String logicalPath = "/" + pathPattern + "/**";
		
		registry
			.addResourceHandler(logicalPath)
			.addResourceLocations("file:/" + absolutePath + "/");
	}

}
