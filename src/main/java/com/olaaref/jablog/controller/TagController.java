package com.olaaref.jablog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.olaaref.jablog.entity.Tag;
import com.olaaref.jablog.exception.TagNotFoundException;
import com.olaaref.jablog.service.CategoryService;
import com.olaaref.jablog.service.TagService;

@Controller
@RequestMapping("/tag")
public class TagController {

	@Autowired
	private TagService tagService;
	
	@Autowired
	private CategoryService categoryService; 
	
	@GetMapping("/list")
	public String listAllTags(Model model) {
		return listByPage(1, "id", "asc", null, model);
	}
	
	@GetMapping("/page/{pageNum}")
	public String listByPage(@PathVariable("pageNum") int pageNum,
							 @RequestParam("sortField") String sortField,
							 @RequestParam("sortDir") String sortDir,
							 @RequestParam("keyword") String keyword,
							 Model model) {
		
		Page<Tag> page = tagService.listByPage(pageNum, sortField, sortDir, keyword);
		updateModelAttributes(page, pageNum, sortField, sortDir, keyword, model);
		
		return "tags/all-tags";
	}
	
	private void updateModelAttributes(Page<Tag> page, int pageNum, String sortField, String sortDir, String keyword, Model model) {
		int pageSize = TagService.TAGS_PER_PAGE;
		
		long startCount = (pageNum - 1) * pageSize + 1;
		long endCount = startCount + pageSize - 1;
		if(endCount > page.getTotalElements()) endCount = page.getTotalElements();
		
		String revSortDir = (sortDir.equals("asc")) ? "desc" : "asc";
		
		model.addAttribute("moduleLink", "/tag");
		model.addAttribute("tags", page.getContent());
		model.addAttribute("pageNum", pageNum);
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("startCount", startCount);
		model.addAttribute("endCount", endCount);
		model.addAttribute("totalElements", page.getTotalElements());
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("revSortDir", revSortDir);
		model.addAttribute("keyword", keyword);
		
		
	}

	@GetMapping("/new")
	public String tagForm(Model model) {
		model.addAttribute("tag", new Tag());
		model.addAttribute("categories", categoryService.getFormCategories());
		model.addAttribute("pageTitle", "Add");
		return "tags/tag-form";
	}
	
	@PostMapping("/save")
	public String saveTag(@ModelAttribute("tag") Tag tag,
						  RedirectAttributes redirectAttributes) {
		tagService.save(tag);
		redirectAttributes.addFlashAttribute("message", "The tag has been saved successfully.");
		return "redirect:/tag/list";
	}
	
	@GetMapping("/edit/{id}")
	public String editTagForm(@PathVariable("id") Integer id,
							  Model model,
							  RedirectAttributes redirectAttributes) {
		try {
			Tag tag = tagService.findById(id);
			model.addAttribute("tag", tag);
			model.addAttribute("categories", categoryService.getFormCategories());
			model.addAttribute("pageTitle", "Edit");
			return "tags/tag-form";
			
		} catch (TagNotFoundException e) {
			redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
			return "redirect:/tag/list";
		}
		
	}
	
	@GetMapping("/delete/{id}")
	public String deleteTag(@PathVariable("id") Integer id,
							  RedirectAttributes redirectAttributes) {
		try {
			tagService.deleteById(id);
			redirectAttributes.addFlashAttribute("message", "The tag has been deleted successfully.");
			return "redirect:/tag/list";
			
		} catch (TagNotFoundException e) {
			redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
			return "redirect:/tag/list";
		}
		
	}
}












