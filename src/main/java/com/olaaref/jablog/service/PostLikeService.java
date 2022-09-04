package com.olaaref.jablog.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.olaaref.jablog.entity.Post;
import com.olaaref.jablog.entity.PostLike;
import com.olaaref.jablog.entity.User;
import com.olaaref.jablog.exception.PostLikeAlreadyExistException;
import com.olaaref.jablog.exception.PostLikeNotFoundException;
import com.olaaref.jablog.repository.PostLikeRepository;

@Service
public class PostLikeService {

	@Autowired
	private PostLikeRepository likeRepository;
	
	
	public List<PostLike> findByPost(Post post) {
		return likeRepository.findByIdPost(post.getId()); 
	}
	
	public List<PostLike> findByUser(User user) {
		return likeRepository.findByIdUser(user.getId());
	}
	
	public PostLike findByPostAndUser(Post post, User user) {
		return likeRepository.findByIdPostAndIdUser(post.getId(), user.getId());
	}
	
	public void deleteLike(Integer postId, Integer userId) throws PostLikeNotFoundException {
		Long count = likeRepository.countByIdPostAndIdUser(postId, userId);
		if(count == null || count == 0) {
			throw new PostLikeNotFoundException("This like not found");
		}
		likeRepository.deleteByIdPostAndIdUser(postId, userId);
	}
	
	public PostLike save(PostLike like) throws PostLikeAlreadyExistException {
		PostLike dbLike = likeRepository.findByIdPostAndIdUser(like.getId().getPost(), like.getId().getUser());
		
		if(dbLike == null) {
			return likeRepository.save(like);
		}
		else {
			throw new PostLikeAlreadyExistException("This Like is already exist");
		}
	}
}
