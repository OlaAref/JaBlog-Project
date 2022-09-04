package com.olaaref.jablog.controller;

import java.io.FileNotFoundException;
import java.io.IOException;

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

import com.olaaref.jablog.entity.Category;
import com.olaaref.jablog.exception.CategoryNotFoundException;
import com.olaaref.jablog.gcp.GoogleCloudUtility;
import com.olaaref.jablog.service.CategoryService;

@Controller
@RequestMapping("/category")
public class CategoryController {

	@Autowired
	private CategoryService categoryService;
	
	@GetMapping("/list")
	public String listCategories(Model model) {
		return listCategoriesByPage(1, "id", "asc", null, model);
	}
	
	@GetMapping("/page/{pageNum}")
	public String listCategoriesByPage(@PathVariable("pageNum") int pageNum,
									   @RequestParam("sortField") String sortField,
									   @RequestParam("sortDir") String sortDir,
									   @RequestParam("keyword") String keyword,
									   Model model) {
		
		Page<Category> page = categoryService.listByPage(pageNum, sortField, sortDir, keyword);
		updateModelAttributes(page, pageNum, sortField, sortDir, keyword, model);
		
		return "categories/all-categories";
	}
	
	private void updateModelAttributes(Page<Category> page, int pageNum, String sortField, String sortDir, String keyword, Model model) {
		int pageSize = CategoryService.CATEGORIES_PER_PAGE;
		
		long startCount = (pageNum - 1) * pageSize + 1;
		long endCount = startCount + pageSize - 1;
		if(endCount > page.getTotalElements()) endCount = page.getTotalElements();
		
		String revSortDir = (sortDir.equals("asc")) ? "desc" : "asc";
		
		model.addAttribute("moduleLink", "/category");
		model.addAttribute("categories", page.getContent());
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("pageNum", pageNum);
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
	public String newCategoryForm(Model model) {
		model.addAttribute("pageTitle", "Add");
		model.addAttribute("categories", categoryService.getFormCategories());
		model.addAttribute("category", new Category());
		return "categories/category-form";
	}
	
	@PostMapping("/save")
	public String saveCategory(@ModelAttribute("category") Category category,
							   @RequestParam("photo") MultipartFile multipartFile,
							   RedirectAttributes redirectAttributes) throws IOException{
		
		if(!multipartFile.isEmpty()) {
			String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			category.setImage(fileName);
			Category savedCategory = categoryService.save(category);
			String uploadDir = "category-photos/" + savedCategory.getId();
			
			GoogleCloudUtility utility = new GoogleCloudUtility();
			String prefix = uploadDir + "/";
			utility.deleteFolder(prefix);
			utility.upload(multipartFile, prefix);
		}
		else {
			if(category.getImage().isEmpty()) {
				category.setImage(null);
			}
			categoryService.save(category);
			
		}
		
		redirectAttributes.addFlashAttribute("message", "The user has been saved successfully.");
			
		return "redirect:/category/list";
	}
	
	@GetMapping("/edit/{id}")
	public String editCategoryForm(@PathVariable("id") Integer id, 
								   Model model,
								   RedirectAttributes redirectAttributes) {
		try {
			
			Category category = categoryService.findCategoryById(id);
			model.addAttribute("pageTitle", "Edit");
			model.addAttribute("categories", categoryService.getFormCategories());
			model.addAttribute("category", category);
			return "categories/category-form";
			
		} catch (CategoryNotFoundException e) {		
			redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
			return "redirect:/category/list";
		}
		
	}
	
	@GetMapping("/delete/{id}")
	public String deleteCategory(@PathVariable("id") Integer id, 
								   Model model,
								   RedirectAttributes redirectAttributes) throws FileNotFoundException, IOException {
		try {
			categoryService.deleteCategory(id);
			GoogleCloudUtility utility = new GoogleCloudUtility();
			utility.deleteFolder("category-photos/"+id+"/");
			redirectAttributes.addFlashAttribute("message", "The user has been deleted successfully.");
			
		} catch (CategoryNotFoundException e) {		
			redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
		}
		
		return "redirect:/category/list";
		
	}
}

























