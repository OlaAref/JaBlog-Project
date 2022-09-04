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
import com.olaaref.jablog.service.BloggerService;

@Component
public class OAuth2LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

	@Autowired
	private BloggerService bloggerService;
	
	private final Logger logger = Logger.getLogger(getClass().getName());
	private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
	
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, 
										HttpServletResponse response,
										Authentication authentication) throws ServletException, IOException {
		
		JaBlogOAuth2User oauth2User = (JaBlogOAuth2User) authentication.getPrincipal();
		
		String name = oauth2User.getName();
		String email = oauth2User.getEmail();
		String clientName = oauth2User.getClientName();
		String countryIso3 = request.getLocale().getISO3Country();
		
		User user = bloggerService.getUserByEmail(email);
		
		AuthenticationType authenType = getAuthentiationType(clientName);
		
		if(user == null) {
			//add new user
			bloggerService.addNewUserUponOAuth2Login(name, email, countryIso3, authenType);
		}
		else {
			//user is exist, update his/her authentication type
			oauth2User.setFullName(user.getFullName());
			bloggerService.updateAuthenticationType(authenType, user);
		}
		
		
		//redirect based on role
		redirectBasedOnRole(request, response, authentication);
		
		//super.onAuthenticationSuccess(request, response, authentication);
	}

	private AuthenticationType getAuthentiationType(String clientName) {
		
		if(clientName.equals("Google")) {
			return AuthenticationType.GOOGLE;
		}
		else if(clientName.equals("Facebook")) {
			return AuthenticationType.FACEBOOK;
		}
		else {
			return AuthenticationType.DATABASE;
		}
		
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
