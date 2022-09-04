package com.olaaref.jablog.security;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.olaaref.jablog.entity.Role;
import com.olaaref.jablog.entity.User;

public class JablogUserDetails implements UserDetails {
	
	private User user;
	
	public JablogUserDetails(User user) {
		super();
		this.user = user;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		Collection<Role> roles = Arrays.asList(user.getRole());
		return roles
					.stream()
					.map(role -> new SimpleGrantedAuthority(role.getName()))
					.collect(Collectors.toList());
	}

	@Override
	public String getPassword() {
		return user.getPassword();
	}

	@Override
	public String getUsername() {
		return user.getEmail();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return this.user.isEnabled();
	}

	public String getFullName() {
		return user.getFullName();
	}
	
	public User getUser() {
		return this.user;
	}
	
	public boolean hasRole(String roleName) {
		return this.user.hasRole(roleName);
	}
}
