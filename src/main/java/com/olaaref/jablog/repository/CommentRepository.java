package com.olaaref.jablog.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.olaaref.jablog.entity.Comment;
import com.olaaref.jablog.entity.Post;
import com.olaaref.jablog.entity.User;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {

	public List<Comment> findByPost(Post post);
	public List<Comment> findByAuthor(User author);
	public List<Comment> findByParent(Comment parent);
	public long deleteByParent(Comment parent);
}
