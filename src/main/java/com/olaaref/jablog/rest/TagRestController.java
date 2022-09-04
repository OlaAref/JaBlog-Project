package com.olaaref.jablog.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.olaaref.jablog.entity.Tag;
import com.olaaref.jablog.exception.CategoryNotFoundException;
import com.olaaref.jablog.service.TagService;

@RestController
@RequestMapping("/tags")
public class TagRestController {

	@Autowired
	private TagService tagService;
	
	@PostMapping("/check_unique")
	public String checkUnique(@RequestParam(value = "id", required = false) Integer id,
							  @RequestParam("title") String title,
							  @RequestParam("slug") String slug) {
		return tagService.checkUnique(id, title, slug);
	}
	
	@PostMapping("/save")
	public String saveTag(@RequestBody Tag tag) throws CategoryNotFoundException {
		
		String unique = checkUnique(null, tag.getTitle(), tag.getSlug());
		if (unique.equals("OK")) {
			
			Tag savedTag = tagService.save(tag);
			return String.valueOf(savedTag.getId());
		}
		else {
			return "0";
		}
		
		
	}
	
	@GetMapping("/list")
	public List<Tag> listTags(){
		
		return tagService.listAllTags();
		
	}
	

}
