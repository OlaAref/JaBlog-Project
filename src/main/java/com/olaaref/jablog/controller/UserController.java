package com.olaaref.jablog.controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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

import com.olaaref.jablog.entity.Role;
import com.olaaref.jablog.entity.User;
import com.olaaref.jablog.exception.UserNotFoundException;
import com.olaaref.jablog.gcp.GoogleCloudUtility;
import com.olaaref.jablog.helper.FileUploadUtil;
import com.olaaref.jablog.service.UserService;

@Controller
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	private UserService userService;
	
	@GetMapping("/list")
	public String listAll(Model model) {
		return listByPage(1, "id", "asc", null, model);
	}
	
	@GetMapping("/page/{pageNum}")
	public String listByPage(@PathVariable("pageNum") int pageNum,
							 @RequestParam("sortField") String sortField,
							 @RequestParam("sortDir") String sortDir,
							 @RequestParam("keyword") String keyword,
							 Model model) {
		
		Page<User> page = userService.listByPage(pageNum, sortField, sortDir, keyword);
		model.addAttribute("users", page.getContent());
		
		updateModelAttributes(pageNum, page, model, sortField, sortDir, keyword);
		
		return "user/all-users";
	}
	
	private void updateModelAttributes(int pageNum, Page<User> pages, Model model, String sortField, String sortDir, String keyword) {
		int pageSize = UserService.USER_PER_PAGE;
		
		long startCount = (pageNum - 1) * pageSize + 1;
		long endCount = startCount + pageSize - 1;
		if(endCount > pages.getTotalElements()) endCount = pages.getTotalElements();
		
		String revSortDir = sortDir.equals("asc") ? "desc" : "asc";
		
		model.addAttribute("moduleLink", "/user");
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("pageNum", pageNum);
		model.addAttribute("startCount", startCount);
		model.addAttribute("endCount", endCount);
		model.addAttribute("totalElements", pages.getTotalElements());
		model.addAttribute("totalPages", pages.getTotalPages());
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("revSortDir", revSortDir);
		model.addAttribute("keyword", keyword);
		
	}

	@GetMapping("/new")
	public String addUser(Model model) {
		model.addAttribute("countries", userService.listAllCountries());
		model.addAttribute("user", new User());
		model.addAttribute("roles", userService.listAllRoles());
		model.addAttribute("pageTitle", "Add");
		
		return "user/user-form";
	}
	
	@PostMapping("/save")
	public String saveUser(@ModelAttribute("user") User user,
						   @RequestParam("photo") MultipartFile multipartFile,
						   RedirectAttributes redirectAttributes) throws IOException {
		
		
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
			if(user.getImage().isEmpty()) {
				user.setImage(null);
			}
			userService.save(user);
		}
		
		redirectAttributes.addFlashAttribute("message", "The user has been saved successfully.");
		
		String firstPartOfEmail = user.getEmail().split("@")[0];
		return "redirect:/user/page/1?sortField=id&sortDir=asc&keyword="+firstPartOfEmail;
	}
	
	@GetMapping("/edit/{id}")
	public String editUser(@PathVariable("id") Integer id,
							RedirectAttributes redirectAttributes,
							Model model) {
		try {
			List<Role> roles = userService.listAllRoles();
			User user = userService.getUserById(id);
			
			model.addAttribute("countries", userService.listAllCountries());
			model.addAttribute("user", user);
			model.addAttribute("roles", roles);
			model.addAttribute("pageTitle", "Edit");
			
			return "user/user-form";
			
		} 
		catch (UserNotFoundException e) {
			redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
			return "redirect:/user/list";
		}
		
	}
	
	@GetMapping("/delete/{id}")
	public String deleteUser(@PathVariable("id") Integer id,
							  RedirectAttributes redirectAttributes) throws FileNotFoundException, IOException {
		
		try {
			userService.deleteUser(id);
			GoogleCloudUtility utility = new GoogleCloudUtility();
			utility.deleteFolder("user-photos/"+id+"/");
			redirectAttributes.addFlashAttribute("message", "The user has been deleted successfully.");
			
		} catch (UserNotFoundException e) {
			redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
		}
		
		return "redirect:/user/list";
	}
	
	@GetMapping("/{id}/enable/{status}")
	public String updateUserEnabledStatus(@PathVariable("id") Integer id,
										  @PathVariable("status") boolean status,
										  RedirectAttributes redirectAttributes){
		
		try {
			userService.updateEnabledStatus(id, status);
			
			String enabled = (status) ? "enabled" : "disabled";
			String message = "User " + userService.getUserById(id).getFullName() + " has been " + enabled + " successfully.";
			
			redirectAttributes.addFlashAttribute("message", message);
			
		} 
		catch (UserNotFoundException e) {
			redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
		}
		
		return "redirect:/user/list";
	}

	
	
	
	
	
	
	
	
	
}











