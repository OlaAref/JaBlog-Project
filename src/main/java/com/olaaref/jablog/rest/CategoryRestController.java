package com.olaaref.jablog.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.olaaref.jablog.service.CategoryService;

@RestController
@RequestMapping("/categories")
public class CategoryRestController {
	
	@Autowired 
	private CategoryService categoryService;
	
	@PostMapping("/check_unique")
	public String checkCategoryIsUnique(@RequestParam(name = "id", required = false) Integer id,
										@RequestParam("title") String title,
										@RequestParam("slug") String slug) {
		return categoryService.checkUnique(id, title, slug);
	}
	
}
