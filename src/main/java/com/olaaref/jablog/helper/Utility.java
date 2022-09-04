package com.olaaref.jablog.helper;

import java.security.Principal;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

import com.olaaref.jablog.entity.setting.EmailSettingBag;
import com.olaaref.jablog.oauth.JaBlogOAuth2User;

public class Utility {
	
	public static String siteUrl(HttpServletRequest request) {
		
		//get full url of the request (Ex: http://localhost:8080/JaBlog/account/details)
		String requestUrl = request.getRequestURL().toString();
		
		//delete the part after the context path (http://localhost:8080/JaBlog/)
		String siteUrl = requestUrl.replace(request.getServletPath(), "");
		
		return siteUrl;
	}

	public static String getEmailForAuthenticatedUser(HttpServletRequest request) {
		Principal userPrincipal = request.getUserPrincipal();
		String userEmail = null;
		
		if(userPrincipal == null) return null;
		
		//if the user logged in with email and password
		if(userPrincipal instanceof UsernamePasswordAuthenticationToken || userPrincipal instanceof RememberMeAuthenticationToken) {
			userEmail = userPrincipal.getName();
		}
		//if logged with google account or facebook account
		else if (userPrincipal instanceof OAuth2AuthenticationToken) {
			OAuth2AuthenticationToken oauth2Token = (OAuth2AuthenticationToken) userPrincipal;
			JaBlogOAuth2User oauth2User = (JaBlogOAuth2User) oauth2Token.getPrincipal();
			userEmail = oauth2User.getEmail();
		}
		
		return userEmail;
	}
	
	public static JavaMailSenderImpl prepareMailSender(EmailSettingBag emailBag) {
		
		Properties mailProperties = new Properties();
		mailProperties.setProperty("mail.smtp.auth", emailBag.getSmtpAuth());
		mailProperties.setProperty("mail.smtp.starttls.required", emailBag.getSmtpSecured());
		mailProperties.setProperty("mail.smtp.ssl.trust", "*");
		mailProperties.setProperty("mail.debug", "true");
		
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		mailSender.setHost(emailBag.getHost());
		mailSender.setPort(emailBag.getPort());
		mailSender.setUsername(emailBag.getUsername());
		mailSender.setPassword(emailBag.getPassword());
		mailSender.setJavaMailProperties(mailProperties);
		
		return mailSender;
		
	}


	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
