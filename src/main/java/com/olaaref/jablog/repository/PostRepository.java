package com.olaaref.jablog.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.olaaref.jablog.entity.Post;
import com.olaaref.jablog.entity.User;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {

	public long count();
	public Long countById(Integer id);
	
	@Query(value = "select p from Post p where p.publish = true")
	public List<Post> findAllPublished();
	@Query(value = "select p from Post p where p.publish = true")
	public Page<Post> findAllPublished(Pageable pageable);
	@Query(value = "SELECT * FROM posts p WHERE MATCH(title, content, summary) AGAINST(?1) AND p.publish = true", nativeQuery = true)
	public Page<Post> findAllPublished(String keyword, Pageable pageable);
	
	@Query(value = "SELECT * FROM posts p WHERE MATCH(title, content, summary) AGAINST(?1)", nativeQuery = true)
	public Page<Post> findAll(String keyword, Pageable pageable);
	
	public Page<Post> findByAuthor(User author, Pageable pageable);
	@Query(value = "SELECT * FROM posts p WHERE MATCH(title, content, summary) AGAINST(?1) AND p.user_id = ?2", nativeQuery = true)
	public Page<Post> findByAuthor(String keyword, int authorId, Pageable pageable);
	
	@Query(value = "select p from Post p where p.author.id = ?1 AND p.publish = true")
	public List<Post> findByAuthorPublished(int authorId);
	@Query(value = "select p from Post p where p.author.id = ?1 AND p.publish = true")
	public Page<Post> findByAuthorPublished(int authorId, Pageable pageable);
	@Query(value = "SELECT * FROM posts p WHERE MATCH(title, content, summary) AGAINST(?1) AND p.user_id = ?2 AND p.publish = true", nativeQuery = true)
	public Page<Post> findByAuthorPublished(String keyword, int authorId, Pageable pageable);
	
	@Query(value = "select p from Post p join p.tags t where t.slug = ?1 AND p.publish = true")
	public Page<Post> findByTag(String tagSlug, Pageable pageable);
	
	@Query(value = "select p from Post p where p.category.slug = ?1 AND p.publish = true")
	public Page<Post> findByCategory(String categorySlug, Pageable pageable);
	
	
	public Post findByTitle(String title);
	public Post findByContent(String content);
	public Post findBySlug(String slug);
	public Post findByParent(Post parent);

	@Modifying
	@Query("UPDATE Post p SET p.publish = ?2 WHERE p.id = ?1")
	public void updatePublishStatus(Integer postId, boolean status);

	@Modifying
	@Query("UPDATE Post p SET p.publish = ?2, p.publishedTime = ?3 WHERE p.id = ?1")
	public void updatePublishStatus(Integer postId, boolean status, LocalDateTime now);
	
	
	
}
