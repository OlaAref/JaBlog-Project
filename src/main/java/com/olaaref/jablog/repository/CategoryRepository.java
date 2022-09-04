package com.olaaref.jablog.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.olaaref.jablog.entity.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {

	@Query("SELECT c FROM Category c WHERE c.parent.id = null")
	public List<Category> findRootCategories();
	
	@Query("SELECT c FROM Category c WHERE c.parent.id = null")
	public Page<Category> findRootCategories(Pageable pageable);
	
	public Category findByTitle(String title);
	public Category findBySlug(String slug);
	public Long countById(Integer id);
	
	@Query("SELECT c FROM Category c WHERE CONCAT(c.title, ' ', c.description, ' ', c.slug) LIKE %?1%")
	public Page<Category> findAll(String keyword, Pageable pageable);
}
