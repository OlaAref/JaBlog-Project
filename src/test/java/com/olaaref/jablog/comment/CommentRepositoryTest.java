package com.olaaref.jablog.comment;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.olaaref.jablog.entity.Comment;
import com.olaaref.jablog.entity.Post;
import com.olaaref.jablog.entity.User;
import com.olaaref.jablog.repository.CommentRepository;
import com.olaaref.jablog.repository.PostRepository;
import com.olaaref.jablog.repository.UserRepository;

@DataJpaTest(showSql = true)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class CommentRepositoryTest {

	@Autowired
	private CommentRepository commentRepository;
	
	@Autowired
	private PostRepository postRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Test
	public void createTableTest() {
		
	}
	
	@Test
	public void createCommentTest() {
		Comment comment = new Comment();
		comment.setAuthor(null);
		comment.setContent("Loads of thanks for such a wonderful article");
		comment.setPost(postRepository.getById(14));
		
		Comment saved = commentRepository.save(comment);
		System.out.println(saved);
	}
	
	@Test
	public void createCommentWithUserTest() {
		Comment comment = new Comment();
		comment.setAuthor(null);
		comment.setContent("the best collection of info in one place. Thank you for explanation");
		comment.setPost(postRepository.getById(14));
		comment.setParent(commentRepository.getById(11));
		
		Comment saved = commentRepository.save(comment);
		System.out.println(saved);
	}
	
	@Test
	public void findByPostTest() {
		Post post = postRepository.getById(14);
		List<Comment> posts = commentRepository.findByPost(post);
		posts.forEach(System.out::println);
	}
	
	@Test
	public void findByAuthorTest() {
		User user = userRepository.findById(1).get();
		List<Comment> posts = commentRepository.findByAuthor(user);
		posts.forEach(System.out::println);
	}
	
	@Test
	public void getDurationTest() {
		//User user = userRepository.findById(1).get();
		//List<Comment> posts = commentRepository.findByAuthor(user);
		//LocalDateTime createdTime = posts.get(0).getCreatedTime();
		//LocalDateTime now = LocalDateTime.now();
		//long between = ChronoUnit.SECONDS.between(createdTime, now);
		Map<String, Long> between = getDurationMap(20);
		String view = getTheViewDuration(between);
		System.out.println(view);
	}

	private String getTheViewDuration(Map<String, Long> map) {
		String view = null;
		if(map.get("years") != 0) {
			view = map.get("years") + " years";
		}
		else if(map.get("months") != 0) {
			view = map.get("months") + " months";
		}
		else if(map.get("days") != 0) {
			view = map.get("days") + " days";
		}
		else if(map.get("hours") != 0) {
			view = map.get("hours") + " hours";
		}
		else if(map.get("minutes") != 0) {
			view = map.get("minutes") + " minutes";
		}
		else if(map.get("seconds") != 0) {
			view = map.get("seconds") + " seconds";
		}
		
		return view;
	}

	private Map<String, Long> getDurationMap(long s) {
		
		Map<String, Long> durationMap = new HashMap<>();
		long years = s/(12*30*24*3600);
		durationMap.put("years", years);
		s = s%(12*30*24*3600); 
		long months = s/(30*24*3600); 
		durationMap.put("months", months);
		s = s%(30*24*3600); 
		long days = s/(24*3600); 
		durationMap.put("days", days);
        s = s%(24*3600); 
        long hours = s/3600; 
        durationMap.put("hours", hours);
        s %= 3600; 
        long minutes = s/60; 
        durationMap.put("minutes", minutes);
        s %= 60; 
        long seconds = s;
        durationMap.put("seconds", seconds);
		
		return durationMap;
	}
	
	@Test
	public void findByPostChildrenTest() {
		Comment comment = commentRepository.findById(5).get();
		printChildren(comment);
	}
	
	private void printChildren(Comment comment) {
		
		System.out.println(comment);
		if(comment.hasChildren()) {
			
			for (Comment c : comment.getChildren()) {
				printChildren(c);
			}
			
		}
		
	}
	
	@Test
	public void deleteCommentWithChildrenTest() {
		Comment comment = commentRepository.findById(16).get();
		deleteWithChildren(comment);
	}

	private void deleteWithChildren(Comment comment) {
		//delete the comment
		commentRepository.deleteById(comment.getId());
		if(comment.hasChildren()) {
			for(Comment child : comment.getChildren()) {
				deleteWithChildren(child);
			}
		}
		
	}

}











