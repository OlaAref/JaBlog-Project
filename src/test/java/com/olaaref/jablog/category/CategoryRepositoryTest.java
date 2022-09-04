package com.olaaref.jablog.category;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.olaaref.jablog.entity.Category;
import com.olaaref.jablog.repository.CategoryRepository;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class CategoryRepositoryTest {

	@Autowired
	private CategoryRepository categoryRepository;
	
	@Test
	public void createCategoryTest() {
		Category category = new Category();
		category.setTitle("Java");
		category.setDescription("Java is the #1 programming language and development platform. It reduces costs, shortens development timeframes, drives innovation, and improves application services. With millions of developers running more than 51 billion Java Virtual Machines worldwide, Java continues to be the development platform of choice for enterprises and developers. ");
		category.setSlug("java");
		
		categoryRepository.save(category);
		
	}
	
	@Test
	public void createSubCategoryTest() {
		Category category = new Category();
		category.setTitle("Spring Boot");
		category.setDescription("Spring Boot makes it easy to create stand-alone, production-grade Spring based Applications that you can \"just run\".");
		category.setSlug("spring_boot");
		category.setParent(new Category(3));
		
		categoryRepository.save(category);
		
	}
	
	@Test
	public void getCategoryTest() {
	
		Category category = categoryRepository.findById(1).get();
		Set<Category> children = category.getChildren();
		
		for (Category subCategory : children) {
			System.out.println(subCategory.getTitle());
		}
	
	}
	
	@Test
	public void getHierarchicalCategoryTest() {
	
		List<Category> categories = categoryRepository.findAll();
		
		for (Category category : categories) {
			//if it is root category print it first
			if(category.getParent() == null) {
				System.out.println(category.getTitle());
				
				Set<Category> children = category.getChildren();
				
				for (Category child : children) {
					System.out.println("  " + child.getTitle());
					printChildren(child, 1);
				}
			}
		}
	
	}

	private void printChildren(Category parent, int subLevel) {
		int newSubLevel = subLevel + 1;
		Set<Category> children = parent.getChildren();
		
		for (Category child : children) {
			for (int i = 0; i < newSubLevel; i++) {
				System.out.print("  ");
			}
			System.out.println(child.getTitle());
			printChildren(child, newSubLevel);
		}
		
	}
	
	@Test
	public void getRootCategoriesTest() {
	
		List<Category> roots = categoryRepository.findRootCategories();
		roots.forEach(root -> System.out.println(root.getTitle()));
	
	}
	
	@Test
	public void findByTitleTest() {
	
		Category cat = categoryRepository.findByTitle("Java");
		System.out.println(cat);
	
	}
	
	@Test
	public void findBySlugTest() {
	
		Category cat = categoryRepository.findBySlug("java");
		System.out.println(cat);
	
	}
}

















