package com.olaaref.jablog.service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.olaaref.jablog.entity.Category;
import com.olaaref.jablog.exception.CategoryNotFoundException;
import com.olaaref.jablog.repository.CategoryRepository;

@Service
public class CategoryService {
	public static final int CATEGORIES_PER_PAGE = 3;
	
	@Autowired
	private CategoryRepository categoryRepository;
	
	public List<Category> listAllCategories(){
		return categoryRepository.findAll();
	}
	
	public List<Category> getRootCategories(){
		return categoryRepository.findRootCategories();
	}
	
	public Page<Category> listByPage(int pageNum, String sortField, String sortDir, String keyword){
		Sort sort = Sort.by(sortField);
		sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
		
		Pageable pageable = PageRequest.of(pageNum - 1, CATEGORIES_PER_PAGE, sort);
		
		if(keyword == null || keyword.isEmpty()) {
			return categoryRepository.findAll(pageable);
		}
		else {
			return categoryRepository.findAll(keyword, pageable);
		}
		
	}
	
	public Category findCategoryById(Integer id) throws CategoryNotFoundException {
		try {
			return categoryRepository.findById(id).get();
			
		} catch (NoSuchElementException | EntityNotFoundException e) {
			throw new CategoryNotFoundException("There is no Category found with ID : " + id);
		}
	}
	
	public Category save(Category category) {
		if(category.getSlug().isEmpty()) {
			String title = category.getTitle().toLowerCase().trim();
			String slug = title.replaceAll(" ", "_");
			category.setSlug(slug);
		}
		return categoryRepository.save(category);
	}
	
	public List<Category> getFormCategories(){
		List<Category> formCategories = new ArrayList<>();
		List<Category> dbCategories = categoryRepository.findAll();
		
		for (Category category : dbCategories) {
			if(category.getParent() == null) {
				formCategories.add(Category.copyIdAndTitle(category));
				
				Set<Category> children = category.getChildren();
				for (Category child : children) {
					String title = "--" + child.getTitle();
					formCategories.add(Category.copyIdAndTitle(child.getId(), title));
					
					listChildren(child, formCategories, 1);
				}
				
			}
		}
		return formCategories;
	}

	private void listChildren(Category parent, List<Category> formCategories, int subLevel) {
		
		int nextSubLevel = subLevel + 1;
		
		Set<Category> children = parent.getChildren();
		
		for (Category child : children) {
			String title = "";
			for (int i = 0; i < nextSubLevel; i++) {
				title += "--";
			}
			title += child.getTitle();
			formCategories.add(Category.copyIdAndTitle(child.getId(), title));
			
			listChildren(child, formCategories, nextSubLevel);
		}
		
	}
	
	public List<Category> getHierarchicalCategories(){
		List<Category> roots = categoryRepository.findRootCategories();
		return listHierarchicalCategories(roots);
	}

	private List<Category> listHierarchicalCategories(List<Category> roots) {
		List<Category> hierarchicalCategories = new ArrayList<>();
		
		for (Category root : roots) {
			hierarchicalCategories.add(Category.copyFull(root));
			Set<Category> children = root.getChildren();
			for(Category child : children) {
				String title = "--" + child.getTitle();
				hierarchicalCategories.add(Category.copyFull(child, title));
				listChildrenHierarchicalCategories(child, hierarchicalCategories, 1);
			}
		}
		
		return hierarchicalCategories;
	}

	private void listChildrenHierarchicalCategories(Category parent, List<Category> hierarchicalCategories, int subLevel) {
		int nextSubLevel = subLevel + 1;
		Set<Category> children = parent.getChildren();
		
		for(Category child : children) {
			String title = "";
			for(int i = 0; i < nextSubLevel; i++) {
				title += "--";
			}
			title += child.getTitle();
			hierarchicalCategories.add(Category.copyFull(child, title));
			
			listChildrenHierarchicalCategories(child, hierarchicalCategories, nextSubLevel);
		}
		
	}
	
	public String checkUnique(Integer id, String title, String slug) {
		
		boolean isNew = (id == null || id == 0);
		
		Category categoryByTitle = categoryRepository.findByTitle(title);
		Category categoryBySlug = categoryRepository.findBySlug(slug);
		
		if(categoryBySlug == null && categoryByTitle == null) return "OK";
		
		if(isNew) {
			if(categoryByTitle != null) return "DuplicateTitle";
			else if(categoryBySlug != null) return "DuplicateSlug";
		}
		else {
			if(categoryByTitle != null && categoryByTitle.getId() != id) return "DuplicateTitle";
			else if(categoryBySlug != null && categoryBySlug.getId() != id) return "DuplicateSlug";
		}
		
		return "OK";
	}
	
	public void deleteCategory(Integer id) throws CategoryNotFoundException {
		Long categoryCount = categoryRepository.countById(id);
		if(categoryCount == 0 || categoryCount == null) {
			throw new CategoryNotFoundException("There is no category found with id :" + id);
		}
		categoryRepository.deleteById(id);
	}

	public Category findBySlug(String categorySlug) throws CategoryNotFoundException {
		
		try {
			return categoryRepository.findBySlug(categorySlug);
		} catch (NoSuchElementException e) {
			throw new CategoryNotFoundException("There is no category with slug : " + categorySlug);
		}
	}
}
























