package com.olaaref.jablog.oauth;

import java.io.IOException;
import java.util.Collection;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.olaaref.jablog.entity.AuthenticationType;
import com.olaaref.jablog.entity.User;
import com.olaaref.jablog.security.JablogUserDetails;
import com.olaaref.jablog.service.BloggerService;

@Component
public class DatabaseLoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

	@Autowired
	private BloggerService bloggerService;
	
	private final Logger logger = Logger.getLogger(getClass().getName());
	private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws ServletException, IOException {
		
		JablogUserDetails userDetails = (JablogUserDetails) authentication.getPrincipal();
		User user = userDetails.getUser();
		
		bloggerService.updateAuthenticationType(AuthenticationType.DATABASE, user);
		
		//redirect based on role
		redirectBasedOnRole(request, response, authentication);
		
		//super.onAuthenticationSuccess(request, response, authentication);
	}

	private void redirectBasedOnRole(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException {
		
		final String targetUrl = determineTargetUrl(authentication);
		if(response.isCommitted()) {
			logger.info("Response has already been committed. Unable to redirect to "+targetUrl);
		}
		redirectStrategy.sendRedirect(request, response, targetUrl);
		cleanAuthenticationAttributes(request);
	}
	
	private String determineTargetUrl(Authentication authentication) {
		
		boolean isAdmin = false;
		boolean isBlogger = false;
		
		Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
		
		for (GrantedAuthority authority : authorities) {
			if(authority.getAuthority().equals("admin")) {
				isAdmin = true;
				break;
			}
			else if(authority.getAuthority().equals("blogger")) {
				isBlogger = true;
				break;
			}
		}
		
		if(isAdmin) {
			 return "/user/list";
		}
		else if(isBlogger) {
			return "/";
		}
		else {
			throw new IllegalStateException("AuthenticationSuccessHandler Error : No accepted role.");
		}
		
	}
	
	/**
	* Removes temporary authentication-related data which may have been stored in the session
	* during the authentication process.
	*/
	private void cleanAuthenticationAttributes(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		
		if(session == null) {
			return;
		}
		
		session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
		
	}

	public RedirectStrategy getRedirectStrategy() {
		return redirectStrategy;
	}

	public void setRedirectStrategy(RedirectStrategy redirectStrategy) {
		this.redirectStrategy = redirectStrategy;
	}

}
