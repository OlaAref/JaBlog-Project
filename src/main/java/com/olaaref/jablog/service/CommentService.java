package com.olaaref.jablog.service;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.olaaref.jablog.entity.Comment;
import com.olaaref.jablog.entity.Post;
import com.olaaref.jablog.entity.User;
import com.olaaref.jablog.exception.PostNotFoundException;
import com.olaaref.jablog.repository.CommentRepository;

@Service
@Transactional
public class CommentService {

	@Autowired
	private CommentRepository commentRepository;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private PostService postService;
	
	public List<Comment> listAllComments(){
		return commentRepository.findAll();
	}
	
	public List<Comment> listByPost(Post post){
		return commentRepository.findByPost(post);
	}
	
	public List<Comment> listByAuthor(User author){
		return commentRepository.findByAuthor(author);
	}
	
	public List<Comment> listByParent(Comment parent){
		return commentRepository.findByParent(parent);
	}
	
	public List<Comment> listCommentWithChildren(Comment comment){
		List<Comment> commentWithChildren = new ArrayList<>();
		return listChildren(comment, commentWithChildren);
	}
	
	private List<Comment> listChildren(Comment comment, List<Comment> commentWithChildren) {
		
		commentWithChildren.add(comment);
		
		if(comment.hasChildren()) {
			for (Comment c : comment.getChildren()) {
				comment = c;
				listChildren(comment, commentWithChildren);
			}
		}
		
		return commentWithChildren;
	}

	public Comment save(Comment comment) {
		return commentRepository.save(comment);
	}
	
	public Comment save(String email, String content, Integer postId) throws PostNotFoundException {
		Comment comment = new Comment();
		
		if(email == null) {
			comment.setAuthor(null);
		}
		else {
			User author = userService.getUserByEmail(email);
			comment.setAuthor(author);
		}
		
		Post post = postService.getById(postId);
		comment.setPost(post);
		comment.setContent(content);
		comment.setParent(null);
		
		return commentRepository.save(comment);
	}
	
	public Comment saveReply(String email, String content, Integer postId, Integer parentId) throws PostNotFoundException {
		Comment comment = new Comment();
		
		if(email == null) {
			comment.setAuthor(null);
		}
		else {
			User author = userService.getUserByEmail(email);
			comment.setAuthor(author);
		}
		
		Post post = postService.getById(postId);
		comment.setPost(post);
		comment.setContent(content);
		
		Comment parent = commentRepository.getById(parentId);
		comment.setParent(parent);
		
		return commentRepository.save(comment);
	}
	
	
	public void deleteCommentWithChildren(Integer commentId) {
		deleteCommentChildren(commentRepository.getById(commentId));
	}
	
	private void deleteCommentChildren(Comment comment) {
		commentRepository.deleteById(comment.getId());
		if(comment.hasChildren()) {
			for(Comment child : comment.getChildren()) {
				deleteCommentChildren(child);
			}
		}
	}
}
