package com.olaaref.jablog.category;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.olaaref.jablog.entity.Category;
import com.olaaref.jablog.repository.CategoryRepository;
import com.olaaref.jablog.service.CategoryService;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
public class CategoryServiceTest {

	@MockBean
	private CategoryRepository categoryRepository;
	
	@InjectMocks
	private CategoryService categoryService;
	
	@Test
	public void checkUniqueTest() {
		Integer id = 1;
		String title = "Java";
		String slug = "java";
		
		Category cat = new Category(id, title, slug);
		
		Mockito.when(categoryRepository.findByTitle(title)).thenReturn(cat);
		Mockito.when(categoryRepository.findBySlug(slug)).thenReturn(null);
		
		String result = categoryService.checkUnique(id, title, slug);
		System.out.println(result);
	}
}
