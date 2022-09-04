package com.olaaref.jablog.postLike;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.olaaref.jablog.entity.PostLike;
import com.olaaref.jablog.entity.PostLikeId;
import com.olaaref.jablog.repository.PostLikeRepository;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class PostLikeRepositoryTest {
	
	@Autowired
	private PostLikeRepository likeRepository;
	
	
	@Test
	public void createPostLikeTest() {
		//PostLikeId id = new PostLikeId(15, 2);
		PostLike saved = likeRepository.save(new PostLike(new PostLikeId(15, 2)));
		System.out.println(saved);
	}
	
	@Test
	public void findPostLikeTest() {
		PostLike saved = likeRepository.findByIdPostAndIdUser(2, 3);
		System.out.println(saved);
	}
	
	@Test
	public void findPostLikeByUserTest() {
		List<PostLike> saved = likeRepository.findByIdUser(3);
		System.out.println(saved);
	}
	
	@Test
	public void findPostLikeByPostTest() {
		List<PostLike> saved = likeRepository.findByIdPost(2);
		System.out.println(saved);
	}
	
	@Test
	public void deleteByPostAndUserTest() {
		likeRepository.deleteByIdPostAndIdUser(2, 15);
	}

	@Test
	public void countByPostAndUserTest() {
		Long count = likeRepository.countByIdPostAndIdUser(3, 15);
		System.out.println(count);
	}

}











