package com.olaaref.jablog.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.olaaref.jablog.service.PostService;

@RestController
@RequestMapping("/posts")
public class PostRestController {

	@Autowired
	private PostService postService;
	
	@PostMapping("/check_unique")
	public String checkPostUnique(@RequestParam(value = "id", required = false) Integer id,
								  @RequestParam("title") String title,
								  @RequestParam("content") String content,
								  @RequestParam("slug") String slug) {
		return postService.checkUnique(id, title, content, slug);
	}
}
