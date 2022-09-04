package com.olaaref.jablog.rest;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.olaaref.jablog.exception.PostLikeAlreadyExistException;
import com.olaaref.jablog.exception.PostLikeNotFoundException;
import com.olaaref.jablog.exception.PostNotFoundException;
import com.olaaref.jablog.service.PostService;

@RestController
@RequestMapping("/views")
public class ViewRestController {
	@Autowired
	private PostService postService;
	
	@GetMapping("/anonymous/like/{postSlug}")
	public String anonymousLike(@PathVariable("postSlug") String postSlug,
							    HttpServletRequest request,
							    HttpServletResponse response) throws PostNotFoundException {
		if(isLiked(request, postSlug)) {
			return "OK";
		}
		else {
			addLikedCookie(response, postSlug);
			return postService.anonymousLike(postSlug);
		}
	}
	
	private void addLikedCookie(HttpServletResponse response, String postSlug) {
		Cookie cookie = new Cookie(postSlug, "yes");
		cookie.setMaxAge(365 * 24 * 60 * 60);
		cookie.setDomain("");
		cookie.setPath("/");
		cookie.setSecure(false);
		response.addCookie(cookie);
	}

	private boolean isLiked(HttpServletRequest request, String postSlug) {
		Cookie cookies[] = request.getCookies();
		for (Cookie cookie : cookies) {
			if(cookie.getName().equals(postSlug)) {
				return true;
			}
		}
		return false;
	}

	@PostMapping("/authenticated/like/{postSlug}")
	public String authenticatedLike(@PathVariable("postSlug") String postSlug,
									@RequestParam("email") String email) throws PostNotFoundException {
		try {
			return postService.authenticatedLike(postSlug, email);
		} catch (PostNotFoundException | PostLikeNotFoundException | PostLikeAlreadyExistException e) {
			return "Duplicate";
		} 
	}
}
