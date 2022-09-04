package com.olaaref.jablog.entity.setting;

import java.io.IOException;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.olaaref.jablog.entity.Setting;
import com.olaaref.jablog.gcp.Constants;
import com.olaaref.jablog.service.SettingService;

@Component
public class SettingFilter implements Filter {

	@Autowired
	private SettingService settingService;
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		
		//cast request to http servlet request to get the requested URLs
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		String url = httpRequest.getRequestURL().toString();
		
		//ignore the static resources
		if(url.endsWith("css") || url.endsWith("js") || url.endsWith("map") || 
		   url.endsWith("jpg") || url.endsWith("png") || url.endsWith("svg") || url.endsWith("ico") ||
		   url.endsWith("woff") || url.endsWith("woff2") || url.endsWith("ttf")) {
			
			chain.doFilter(request, response);
			return;
		}
		
		//send the general settings to the whole project
		List<Setting> generalSetting = settingService.getGeneralSetting().getAllSetting();
		
		for (Setting setting : generalSetting) {
			request.setAttribute(setting.getKey(), setting.getValue());
		}
		
		request.setAttribute("GCP_Base_URI", Constants.GCP_Base_URI);
		
		//apply the filter
		chain.doFilter(request, response);

	}

}
