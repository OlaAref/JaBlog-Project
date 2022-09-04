package com.olaaref.jablog.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.olaaref.jablog.entity.Tag;

@Repository
public interface TagRepository extends JpaRepository<Tag, Integer> {

	@Query("SELECT t FROM Tag t WHERE CONCAT(t.title, ' ', t.description, ' ', t.slug) LIKE %?1%")
	public Page<Tag> findAll(String keyword, Pageable pageable);
	
	public Tag findByTitle(String title);
	public Tag findBySlug(String slug);
	
	public Long countById(Integer id);
}
