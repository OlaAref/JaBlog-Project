package com.olaaref.jablog.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.olaaref.jablog.service.UserService;

@RestController
@RequestMapping("/users")
public class UserRestController {
	
	@Autowired
	private UserService userService;
	
	@PostMapping("/check_email")
	public String checkDuplicateEmail(@RequestParam("email") String email,
									  @RequestParam(name= "id", required = false) Integer id) {
		return userService.isEmailUnique(id, email) ? "OK" : "Duplicated";
	}

}
