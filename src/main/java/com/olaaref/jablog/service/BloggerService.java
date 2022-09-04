package com.olaaref.jablog.service;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.olaaref.jablog.entity.AuthenticationType;
import com.olaaref.jablog.entity.Role;
import com.olaaref.jablog.entity.User;
import com.olaaref.jablog.repository.CountryRepository;
import com.olaaref.jablog.repository.UserRepository;

@Service
@Transactional
public class BloggerService {
	
	public static final int USER_PER_PAGE = 3;

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private CountryRepository countryRepository;

	
	public User getUserByEmail(String email) {
		return userRepository.findByEmail(email);
	}

	public void updateAuthenticationType(AuthenticationType authType, User user) {
		
		if(!user.getAuthenticationType().equals(authType)) {
			userRepository.updateAuthenticationType(authType, user.getId());
		}
	}

	public void addNewUserUponOAuth2Login(String name, String email, String countryIso3, AuthenticationType authenType) {
		
		User user = new User();
		user.setEmail(email);
		setName(name, user);
		user.setAuthenticationType(authenType);
		user.setEnabled(true);
		user.setPassword("");
		user.setRole(new Role(2));
		user.setCountry(countryRepository.findByCode(countryIso3));
		
		userRepository.save(user);
		
	}

	private void setName(String name, User user) {
		String[] nameArray = name.split(" ");
		
		if(nameArray.length < 2) {
			//the name consist of one part
			user.setFirstName(name);
			user.setLastName("");
		}
		else {
			//the name consist of more than one part
			String firstName = nameArray[0];
			String lastName = name.replace(firstName, "");//delete the first part of the name
			user.setFirstName(firstName);
			user.setLastName(lastName);
		}
	}

	












}
