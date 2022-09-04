package com.olaaref.jablog.controller;

import java.io.UnsupportedEncodingException;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.olaaref.jablog.entity.User;
import com.olaaref.jablog.entity.setting.EmailSettingBag;
import com.olaaref.jablog.exception.UserNotFoundException;
import com.olaaref.jablog.helper.Utility;
import com.olaaref.jablog.service.SettingService;
import com.olaaref.jablog.service.UserService;

@Controller
@RequestMapping("/password")
public class ForgotPasswordController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private SettingService settingService;
	
	@GetMapping("/requestPassword")
	public String getForgotPasswordPage() {
		return "user/forgot-password-form";
	}
	
	@PostMapping("/forgotPassword")
	public String processRequestForm(HttpServletRequest request, Model model) {
		String email = request.getParameter("email");
		
		try {
			
			String resetToken = userService.setResetPasswordToken(email);
			String link = Utility.siteUrl(request) + "/password/resetPassword?token=" + resetToken;
			sendEmail(email, link);
			model.addAttribute("pageTitle", "Reset Password");
			model.addAttribute("messageTitle", "Password Reset");
			model.addAttribute("messageBody", "Reset password email have sent to your email, please check.");
			return "message";
		} 
		catch (UserNotFoundException e) {
			model.addAttribute("errorMessage", e.getMessage());
		} 
		catch (UnsupportedEncodingException | MessagingException e) {
			model.addAttribute("errorMessage", "Could not send the email");
		} 
		
		return "user/forgot-password-form";
	}

	private void sendEmail(String email, String link) throws UnsupportedEncodingException, MessagingException {
		EmailSettingBag emailBag = settingService.getEmailSettingBag();
		JavaMailSenderImpl sender = Utility.prepareMailSender(emailBag);
		
		String fromAddress = emailBag.getMailFrom();
		String senderName = emailBag.getSenderName();
		String toAddress = email;
		String subject = "Reset Password";
		String content = """
				<div style="text-align: center;"><span style="font-size: 24px; font-weight: var(--bs-body-font-weight); text-align: var(--bs-body-text-align);">&nbsp;Hello,&nbsp;</span></div>
				<div style="text-align: center;"><span style="font-size: 18px;"><br></span></div>
				<span style="font-size: 18px;">
				    <div style="text-align: center;">
						<span style="font-weight: var(--bs-body-font-weight); text-align: var(--bs-body-text-align);">You have requested to reset your password.</span>
						<span style="font-weight: var(--bs-body-font-weight); text-align: var(--bs-body-text-align);">Click the link below to change your password.</span>
					</div>
				</span>
				<div>
				   <div style="text-align: center;"><span style="font-size: 18px;"><br></span></div>
				   <h2 style="text-align: center;">
				      <a href="%s" target="_self">Change my password</a>
				   </h2>
				   <div style="text-align: center;"><br></div>
				   <div style="text-align: center;">Ignore this email if you do remember the password, or you have not made this request.</div>
				   <div style="text-align: center;"><br></div>
				   <div style="text-align: center;">Cheers,</div>
				   <div style="text-align: center;">JaBlog Team</div>
				</div>
				""".formatted(link);
		
		MimeMessage message = sender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);
		
		helper.setFrom(fromAddress, senderName);
		helper.setTo(toAddress);
		helper.setSubject(subject);
		helper.setText(content, true);
		
		sender.send(message);
		
	}
	
	@GetMapping("/resetPassword")
	public String showResetForm(@RequestParam("token") String token, Model model) {
		User user = userService.getByResetPasswordToken(token);
		
		if(user != null) {
			model.addAttribute("token", token);
		}
		else {
			model.addAttribute("pageTitle", "Invalid Token");
			model.addAttribute("messageTitle", "Failed !!");
			model.addAttribute("messageBody", "Invalid Token.");
			return "message";
		}
		return "user/reset-password-form";
	}
	
	@PostMapping("/resetPassword")
	public String processResetPassword(@RequestParam("token") String token,
									   @RequestParam("newPassword") String password,
									   @RequestParam("confirmNewPassword") String confirmPassword,
									   Model model) {
		if(!confirmPassword.equals(password)) {
			model.addAttribute("errorMessage", "Passwords not matched.");
			return "user/reset-password-form";
		}
		
		try {
			userService.updatePassword(token, password);
			model.addAttribute("pageTitle", "Password Saved");
			model.addAttribute("messageTitle", "Success");
			model.addAttribute("messageBody", "Password Successfully Changed.");
			return "message";
			
		} 
		catch (UserNotFoundException e) {
			model.addAttribute("pageTitle", "Error!");
			model.addAttribute("messageTitle", "Failed !!");
			model.addAttribute("messageBody", e.getMessage());
			return "message";
		}

	}

}
























