package com.olaaref.jablog.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.olaaref.jablog.entity.PostLike;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Integer> {

	public Long countByIdPostAndIdUser(Integer postId, Integer userId);
	public List<PostLike> findByIdPost(Integer postId);
	public List<PostLike> findByIdUser(Integer userId);
	public PostLike findByIdPostAndIdUser(Integer postId, Integer userId);
	public void deleteByIdPostAndIdUser(Integer postId, Integer userId);
	
}
