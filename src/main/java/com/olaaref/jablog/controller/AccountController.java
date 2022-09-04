package com.olaaref.jablog.controller;

import java.io.IOException;
import java.security.Principal;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.olaaref.jablog.entity.User;
import com.olaaref.jablog.exception.UserNotFoundException;
import com.olaaref.jablog.gcp.GoogleCloudUtility;
import com.olaaref.jablog.helper.Utility;
import com.olaaref.jablog.oauth.JaBlogOAuth2User;
import com.olaaref.jablog.security.JablogUserDetails;
import com.olaaref.jablog.service.UserService;

@Controller
@RequestMapping("/account")
public class AccountController {
	
	@Autowired
	private UserService userService;
	
	@GetMapping("/details")
	public String viewDetails(HttpServletRequest request,
								Model model) {
		String email = Utility.getEmailForAuthenticatedUser(request);
		User user = userService.getUserByEmail(email);
		model.addAttribute("user", user);
		model.addAttribute("countries", userService.listAllCountries());
		return "user/user-profile";
	}
	
	@PostMapping("/save")
	public String saveAccount(@ModelAttribute("user") User user,
							  @RequestParam("photo") MultipartFile multipartFile,
							  HttpServletRequest request,
							  RedirectAttributes redirectAttributes) throws IOException, UserNotFoundException, ServletException {
		
		String loggedEmail = Utility.getEmailForAuthenticatedUser(request);
		User existUser = userService.getUserByEmail(loggedEmail);
		
		if(existUser != null) {
			user.setEnabled(existUser.isEnabled());
			user.setRole(existUser.getRole());
		}
		String formEmail = user.getEmail();
		
		if(!multipartFile.isEmpty()) {
			String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			user.setImage(fileName);
			User savedUser = userService.save(user);
			String uploadDir = "user-photos/" + savedUser.getId();
			
			GoogleCloudUtility utility = new GoogleCloudUtility();
			String prefix = uploadDir + "/";
			utility.deleteFolder(prefix);
			utility.upload(multipartFile, prefix);
		}
		else {
			if(user.getImage() == null || user.getImage().isEmpty()) {
				user.setImage(null);
			}
			userService.save(user);
		}
		
		//set full name
		setFullnameForAuthenticatedCustomer(request, user);
		
		//if user change the email, redirect to login page
		if(!formEmail.equals(loggedEmail)) {
			request.logout();
			redirectAttributes.addFlashAttribute("emailChanged", "Email changed successfully, please log in again.");
			return "redirect:/login";
		}

		redirectAttributes.addFlashAttribute("message", "The account has been saved successfully.");
		return "redirect:/account/details";
	}
	
	private void setFullnameForAuthenticatedCustomer(HttpServletRequest request, User user) {
		Principal principal = request.getUserPrincipal();
		
		//if user logged in with an email
		if(principal instanceof UsernamePasswordAuthenticationToken || principal instanceof RememberMeAuthenticationToken) {
			JablogUserDetails jablogUserDetails = getJablogUserDetailsObject(principal);
			User dbAuthUser = jablogUserDetails.getUser();
			dbAuthUser.setFirstName(user.getFirstName());
			dbAuthUser.setLastName(user.getLastName());
		}
		//if user logged in with google/facebook account
		else if(principal instanceof OAuth2AuthenticationToken) {
			OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) principal;
			JaBlogOAuth2User oauthUser = (JaBlogOAuth2User) oauthToken.getPrincipal();
			oauthUser.setFullName(user.getFullName());
		}
	}

	private JablogUserDetails getJablogUserDetailsObject(Principal principal) {
		
		JablogUserDetails jablogUserDetails = null;
		
		if(principal instanceof UsernamePasswordAuthenticationToken) {
			UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) principal;
			jablogUserDetails = (JablogUserDetails) token.getPrincipal();
		}
		else if(principal instanceof RememberMeAuthenticationToken) {
			RememberMeAuthenticationToken token = (RememberMeAuthenticationToken) principal;
			jablogUserDetails = (JablogUserDetails) token.getPrincipal();
		}
		
		return jablogUserDetails;
	}

	@GetMapping("/changePasswordRequest/{id}")
	public String changePasswordRequest(@PathVariable("id") int id,
										Model model) throws UserNotFoundException {
		User user = userService.getUserById(id);
		model.addAttribute("user", user);
		return "user/change-password";
	}
	
	@PostMapping("/changePassword")
	public String changePassword(@RequestParam("id") int id,
								 @RequestParam("currentPassword") String currentPassword,
								 @RequestParam("newPassword") String newPassword,
								 @RequestParam("confirmNewPassword") String confirmNewPassword,
								 Model model,
								 RedirectAttributes redirectAttributes) {
		
		 
		try {
			User user = userService.getUserById(id);
			
			BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
			boolean isCurrentPassword = passwordEncoder.matches(currentPassword, user.getPassword());
			
			if(!isCurrentPassword) {
				String errorMessage = "Current Password is incorrect.";
				model.addAttribute("errorMessage", errorMessage);
				model.addAttribute("user", user);
				return "user/change-password";
			}
			
			if(!newPassword.equals(confirmNewPassword)) {
				String errorMessage = "Confirm Password and New Password are not identical.";
				model.addAttribute("errorMessage", errorMessage);
				model.addAttribute("user", user);
				return "user/change-password";
			}
			
			user.setPassword(passwordEncoder.encode(newPassword));
			userService.save(user);
			redirectAttributes.addFlashAttribute("passwordChanged", "Password Changed Successfully.");
			
			return "redirect:/account/details";
			
		} catch (UserNotFoundException e) {
			model.addAttribute("errorMessage", e.getMessage());
			return "user/change-password";
		}
		
	}

}









