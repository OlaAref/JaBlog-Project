package com.olaaref.jablog.category;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.olaaref.jablog.entity.Category;
import com.olaaref.jablog.entity.Tag;
import com.olaaref.jablog.repository.CategoryRepository;
import com.olaaref.jablog.repository.TagRepository;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class TagRepositoryTest {
	
	@Autowired
	private TagRepository tagRepository;
	
	@Autowired
	private CategoryRepository categoryRepository;
	
	@Test
	public void createTagTest() {
		Category category = categoryRepository.findById(3).get();
		
		Tag tag = new Tag();
		tag.setTitle("Spring S");
		tag.setDescription("");
		tag.setSlug("spring");
		tag.setCategory(category);
		
		tagRepository.save(tag);
	}

}
