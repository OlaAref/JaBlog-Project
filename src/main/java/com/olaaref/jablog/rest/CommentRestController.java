package com.olaaref.jablog.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.olaaref.jablog.exception.PostNotFoundException;
import com.olaaref.jablog.service.CommentService;

@RestController
@RequestMapping("/comments")
public class CommentRestController {

	@Autowired
	private CommentService commentService;
	
	@PostMapping("/save")
	public String saveComment(@RequestParam("email") String email,
							  @RequestParam("content") String content,
							  @RequestParam("post") Integer post) throws PostNotFoundException {
		
		if(email.equals("anonymousUser")) {
			commentService.save(null, content, post);
		}
		else {
			commentService.save(email, content, post);
		}
		
		return "OK";
	}
	
	@PostMapping("/saveReply")
	public String saveReply(@RequestParam("email") String email,
							@RequestParam("content") String content,
							@RequestParam("parent") Integer parentId,
							@RequestParam("post") Integer post) throws PostNotFoundException {

		if(email.equals("anonymousUser")) {
			commentService.saveReply(null, content, post, parentId);
		}
		else {
			commentService.saveReply(email, content, post, parentId);
		}
		
		return "OK";
	}
	
	@DeleteMapping("/delete/{commentId}")
	public void deleteComment(@PathVariable("commentId") Integer commentId) {
		commentService.deleteCommentWithChildren(commentId);
	}
}









