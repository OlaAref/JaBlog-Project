package com.olaaref.jablog.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.olaaref.jablog.entity.User;
import com.olaaref.jablog.repository.UserRepository;

@Service
@Transactional
public class JablogUserDetailsService implements UserDetailsService {

	@Autowired
	private UserRepository userRepository;
	
	@Override
	public UserDetails loadUserByUsername(String userEmail) throws UsernameNotFoundException {
		User user = userRepository.findByEmail(userEmail);
		
		if(user == null) {
			throw new UsernameNotFoundException("Invalid username or password");
		}
		
		return new JablogUserDetails(user);
	}

}
