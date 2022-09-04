package com.olaaref.jablog.oauth;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import com.olaaref.jablog.entity.Role;


public class JaBlogOAuth2User implements OAuth2User {
	
	private OAuth2User oauth2User;
	private String clientName;
	private String fullName;
	
	public JaBlogOAuth2User(OAuth2User oauth2User, String clientName) {
		this.oauth2User = oauth2User;
		this.clientName = clientName;
	}
	
	@Override
	public Map<String, Object> getAttributes() {
		return oauth2User.getAttributes();
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		Collection<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		SimpleGrantedAuthority grantedAuthority = new SimpleGrantedAuthority("blogger");
		authorities.add(grantedAuthority);
		
		return authorities;
	}

	@Override
	public String getName() {
		return oauth2User.getAttribute("name");
	}
	
	public String getEmail() {
		return oauth2User.getAttribute("email");
	}

	public String getFullName() {
		return fullName != null ? fullName : oauth2User.getAttribute("name");
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getClientName() {
		return clientName;
	}
}
