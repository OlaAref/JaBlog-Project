package com.olaaref.jablog.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.olaaref.jablog.entity.Role;
import com.olaaref.jablog.entity.User;
import com.olaaref.jablog.entity.setting.EmailSettingBag;
import com.olaaref.jablog.exception.UserNotFoundException;
import com.olaaref.jablog.gcp.GoogleCloudUtility;
import com.olaaref.jablog.helper.FileUploadUtil;
import com.olaaref.jablog.helper.Utility;
import com.olaaref.jablog.oauth.JaBlogOAuth2User;
import com.olaaref.jablog.security.JablogUserDetails;
import com.olaaref.jablog.service.SettingService;
import com.olaaref.jablog.service.UserService;

@Controller
@RequestMapping("/blogger")
public class BloggerController {

	@Autowired
	private UserService userService;
	
	@Autowired
	private SettingService settingService;
	
	@GetMapping("/signup")
	public String signupForm(Model model) {
		
		model.addAttribute("countries", userService.listAllCountries());
		model.addAttribute("user", new User());
		
		return "blogger/sign-up";
	}
	
	@PostMapping("/createBlogger")
	public String createBlogger(@ModelAttribute("user") User user,
							    @RequestParam("photo") MultipartFile multipartFile,
							    @RequestParam("password") String password,
							    @RequestParam("confirmPassword") String confirmPassword,
							    HttpServletRequest request,
							    Model model,
							    RedirectAttributes redirectAttributes) throws IOException, ServletException, MessagingException {
		
		//set the role to blogger
		user.setRole(new Role(2));
		
		if(!password.equals(confirmPassword)) {
			model.addAttribute("errorMessage", "Passwords not matched.");
			return "blogger/sign-up";
		}
		
		if(!multipartFile.isEmpty()) {
			String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			user.setImage(fileName);
			User savedUser = userService.save(user);
			String uploadDir = "user-photos/" + savedUser.getId();
			FileUploadUtil.cleanDirectory(uploadDir);
			FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
		}
		else {
			if(user.getImage() == null || user.getImage().isEmpty()) {
				user.setImage(null);
			}
			userService.save(user);
		}
		
		//send verification email
		sendVerificationEmail(request, user);
		
		//logout if there is user logged in
		request.logout();
		model.addAttribute("successMessage", "Your account has been created successfully, please login.");
		
		return "login";
	}

	private void sendVerificationEmail(HttpServletRequest request, User user) throws UnsupportedEncodingException, MessagingException {
		
		EmailSettingBag emailBag = settingService.getEmailSettingBag();
		
		String toAddress = user.getEmail();
		String fromAddress = emailBag.getMailFrom();
		String senderName = emailBag.getSenderName();
		String subject = emailBag.getMailVerifySubject();
		String content = emailBag.getMailVerifyContent();
		
		//JavaMailSenderImpl => MimeMessage => MimeMessageHelper
		JavaMailSenderImpl mailSender = Utility.prepareMailSender(emailBag);
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);
		
		helper.setTo(toAddress);
		helper.setFrom(fromAddress, senderName);
		helper.setSubject(subject);
		
		//prepare content first
		//set the url and the user name
		String url = Utility.siteUrl(request) + "/blogger/verify?code=" + user.getVerificationCode();
		content = content.replace("[[URL]]", url);
		content = content.replace("[[name]]", user.getFullName());
		//set the content to the helper
		helper.setText(content, true);
		
		//send the mail
		mailSender.send(message);
				
	}
	
	@GetMapping("/verify")
	public String verifyAccount(@RequestParam("code") String verificationCode) {
		
		boolean isVerified = userService.verify(verificationCode);
		
		return (isVerified ? "blogger/verify-success" : "blogger/verify-fail");
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
			user.setPassword(existUser.getPassword());
		}
		String formEmail = user.getEmail();
		
		if(!multipartFile.isEmpty()) {
			String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			user.setImage(fileName);
			User savedUser = userService.update(user);
			String uploadDir = "user-photos/" + savedUser.getId();
			
			GoogleCloudUtility utility = new GoogleCloudUtility();
			String prefix = uploadDir + "/";
			utility.deleteFolder(prefix);
			utility.upload(multipartFile, prefix);
		}
		else {
			if(user.getImage() == null || user.getImage().isEmpty()) {
				user.setImage(existUser.getImage());
			}
			userService.update(user);
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
}

































