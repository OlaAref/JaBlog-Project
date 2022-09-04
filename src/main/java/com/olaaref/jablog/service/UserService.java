package com.olaaref.jablog.service;

import java.util.List;
import java.util.NoSuchElementException;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.olaaref.jablog.entity.AuthenticationType;
import com.olaaref.jablog.entity.Country;
import com.olaaref.jablog.entity.Role;
import com.olaaref.jablog.entity.User;
import com.olaaref.jablog.exception.UserNotFoundException;
import com.olaaref.jablog.repository.CountryRepository;
import com.olaaref.jablog.repository.RoleRepository;
import com.olaaref.jablog.repository.UserRepository;

import net.bytebuddy.utility.RandomString;

@Service
@Transactional
public class UserService {
	
	public static final int USER_PER_PAGE = 3;

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private RoleRepository roleRepository;
	
	@Autowired
	private CountryRepository countryRepository;
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	public List<User> listAllUsers(){
		return (List<User>) userRepository.findAll();
	}
	
	public List<Country> listAllCountries(){
		return countryRepository.findAllByOrderByNameAsc();
	}
	
	public User getUserByEmail(String email) {
		return userRepository.findByEmail(email);
	}
	
	public Page<User> listByPage(int pageNum, String sortField, String sortDir, String keyword){
		Sort sort = Sort.by(sortField);
		sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
		
		Pageable pageable = PageRequest.of(pageNum - 1, USER_PER_PAGE, sort);
		
		if(keyword != null) {
			return userRepository.findAll(keyword, pageable);
		}
		else {
			return userRepository.findAll(pageable);
		}
		
	}
	
	public List<Role> listAllRoles(){
		return roleRepository.findAll();
	}
	
	public User getUserById(Integer id) throws UserNotFoundException {
		try {
			return userRepository.findById(id).get();
		} 
		catch (NoSuchElementException e) {
			throw new UserNotFoundException("There is no user found with id: " + id);
		}
	}

	public User save(User user) {
		boolean isUpdateCase = (user.getId() != null);
		
		if(isUpdateCase) {
			User existUser = userRepository.findById(user.getId()).get();
			user.setPassword(existUser.getPassword());
		}
		else {
			encodePassword(user);
			user.setAuthenticationType(AuthenticationType.DATABASE);
			//send verification code to email
			String verificationCode = RandomString.make(64);
			user.setVerificationCode(verificationCode);
		}
		
		
		return userRepository.save(user);
		
	}

	private void encodePassword(User user) {
		String plainPassword = user.getPassword();
		String encodedPassword = passwordEncoder.encode(plainPassword);
		user.setPassword(encodedPassword);
	}


	public boolean isEmailUnique(Integer id, String email) {
		User userByEmail = userRepository.findByEmail(email);
		boolean isNewUser = (id == null);
		
		//if there is no user found in db with this email, so the email is unique
		if(userByEmail == null) return true;
		
		if(isNewUser) {
			//if we create a new user but there is another user in db
			//with same email return false
			if(userByEmail != null) return false;
		}
		else {
			//if we update an existing user but there is another user in db
			//with same email return false
			if(userByEmail.getId() != id) return false;
		}
		
		//otherwise
		return true;
	}

	public void deleteUser(Integer id) throws UserNotFoundException {
		Long countByUser = userRepository.countById(id);
		
		if(countByUser == null || countByUser == 0) {
			throw new UserNotFoundException("There is no user with id: " + id);
		}
		
		userRepository.deleteById(id);
		
	}
	
	public void updateEnabledStatus(Integer id, boolean status) {
		userRepository.updateEnabledStatus(id, status);
	}

	public boolean verify(String verificationCode) {
		User user = userRepository.findByVerificationCode(verificationCode);
		
		//if the user is not exist or exist but already enabled
		if(user == null || user.isEnabled()) {
			return false;
		}
		else {
			//the account exist but not enabled, so enable it
			userRepository.enableAccount(user.getId());
			return true;
		}
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

	public User update(User formUser) {
		User dbUser = userRepository.findById(formUser.getId()).get();
		
		if (dbUser.getAuthenticationType().equals(AuthenticationType.DATABASE)) {
			if (!formUser.getPassword().isEmpty()) {
				String encodedPassword = passwordEncoder.encode(formUser.getPassword());
				formUser.setPassword(encodedPassword);
			} else {
				formUser.setPassword(dbUser.getPassword());
			} 
		}
		else {
			formUser.setPassword(dbUser.getPassword());
		}
		
		formUser.setEnabled(dbUser.isEnabled());
		formUser.setCreatedTime(dbUser.getCreatedTime());
		formUser.setVerificationCode(dbUser.getVerificationCode());
		formUser.setAuthenticationType(dbUser.getAuthenticationType());
		formUser.setResetPasswordToken(dbUser.getResetPasswordToken());
		
		return userRepository.save(formUser);	
		
	}

	public User getByResetPasswordToken(String resetPasswordToken) {
		return userRepository.findByResetPasswordToken(resetPasswordToken);
	}

	
	public String setResetPasswordToken(String email) throws UserNotFoundException {
		User user = userRepository.findByEmail(email);
		
		if(user != null) {
			//create random reset password
			String resetPassword = RandomString.make(30);
			user.setResetPasswordToken(resetPassword);
			userRepository.save(user);
			return resetPassword;
		}
		else {
			throw new UserNotFoundException("There is no user with email : " + email);
		}
		
	}
	
	public void updatePassword(String resetPasswordToken, String newPassword) throws UserNotFoundException {
		User user = userRepository.findByResetPasswordToken(resetPasswordToken);
		
		if(user != null) {
			String encodedPassword = passwordEncoder.encode(newPassword);
			user.setPassword(encodedPassword);
			user.setResetPasswordToken(null);
			userRepository.save(user);
		}
		else {
			throw new UserNotFoundException("No user found : Invalid Token");
		}
	}
	








}














