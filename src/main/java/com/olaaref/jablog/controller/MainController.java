package com.olaaref.jablog.controller;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.olaaref.jablog.gcp.GoogleCloudUtility;

@Controller
public class MainController {

	@GetMapping("/")
	public String mainPage() {
		return "redirect:/view/posts/page/1?sortField=publishedTime&sortDir=desc&keyword=";
	}
	
	@GetMapping("/about")
	public String aboutPage() {
		return "about";
	}
	
	@GetMapping("/contact")
	public String contactPage() {
		return "contact";
	}
	
	@GetMapping("/login")
	public String loginPage() {
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if(authentication == null || authentication instanceof AnonymousAuthenticationToken) {
			return "login";
		}
		return "redirect:/";
	}
	
	
	@GetMapping("/post")
	public String postPage() {
		
		return "post";
	}
	
	@GetMapping("/post-form")
	public String postFormPage() {
		return "post-form";
	}
	
	@PostMapping("/testGcp")
	public String testGcpForm(@RequestParam("file") MultipartFile file,
							RedirectAttributes ra) throws FileNotFoundException, IOException {
		 GoogleCloudUtility utility = new GoogleCloudUtility();
		 String message = utility.upload(file, "user-photos/2/");
		 ra.addFlashAttribute("message", message);
		return "redirect:/post";
	}
	
	@GetMapping("/testDelete")
	public String testDeleteGcpForm(RedirectAttributes ra) throws FileNotFoundException, IOException {
		 GoogleCloudUtility utility = new GoogleCloudUtility();
		 boolean deleted = utility.deleteObject("user-photos/2/2.jpg");
		 ra.addFlashAttribute("message", "image deleted : "+deleted);
		return "redirect:/post";
	}

	
	
}
