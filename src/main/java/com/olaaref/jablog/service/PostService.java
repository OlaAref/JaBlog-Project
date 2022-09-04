package com.olaaref.jablog.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.olaaref.jablog.entity.Category;
import com.olaaref.jablog.entity.Post;
import com.olaaref.jablog.entity.PostLike;
import com.olaaref.jablog.entity.PostLikeId;
import com.olaaref.jablog.entity.Tag;
import com.olaaref.jablog.entity.User;
import com.olaaref.jablog.exception.PostLikeAlreadyExistException;
import com.olaaref.jablog.exception.PostLikeNotFoundException;
import com.olaaref.jablog.exception.PostNotFoundException;
import com.olaaref.jablog.repository.PostRepository;

@Service
@Transactional
public class PostService {
	public static final int POSTS_PER_PAGE = 2;
	
	@Autowired
	private PostRepository postRepository;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private PostLikeService likeService;
	
	public List<Post> listAllPosts(){
		return postRepository.findAll();
	}
	
	public Page<Post> listByPage(int pageNum, String sortField, String sortDir, String keyword){
		Sort sort = Sort.by(sortField);
		sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
		
		Pageable pageable = PageRequest.of(pageNum - 1, POSTS_PER_PAGE, sort);
		
		if(keyword == null || keyword.isEmpty()) {
			return postRepository.findAll(pageable);
		}
		else {
			return postRepository.findAll(keyword, pageable);
		}
	}
	
	public Page<Post> listByPagePublished(int pageNum, String sortField, String sortDir, String keyword){
		Sort sort = Sort.by(sortField);
		sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
		
		Pageable pageable = PageRequest.of(pageNum - 1, POSTS_PER_PAGE, sort);
		
		if(keyword == null || keyword.isEmpty()) {
			return postRepository.findAllPublished(pageable);
		}
		else {
			return postRepository.findAllPublished(keyword, pageable);
		}
	}
	
	public Post getById(Integer id) throws PostNotFoundException{
		try {
			return postRepository.findById(id).get();
		} catch (NoSuchElementException e) {
			throw new PostNotFoundException("There is no post found with ID : " + id);
		}
	}
	
	public Post getBySlug(String postSlug) throws PostNotFoundException{
		try {
			return postRepository.findBySlug(postSlug);
		} catch (NoSuchElementException e) {
			throw new PostNotFoundException("There is no post found with Slug : " + postSlug);
		}
	}
	
	public Page<Post> listByUser(User author, int pageNum, String sortField, String sortDir, String keyword){
		Sort sort = Sort.by(sortField);
		sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
		
		Pageable pageable = PageRequest.of(pageNum - 1, POSTS_PER_PAGE, sort);
		
		if(keyword == null || keyword.isEmpty()) {
			return postRepository.findByAuthor(author, pageable);
		}
		else {
			return postRepository.findByAuthor(keyword, author.getId(), pageable);
		}
	}
	
	public Page<Post> listByUserPublished(User author, int pageNum, String sortField, String sortDir, String keyword){
		Sort sort = Sort.by(sortField);
		sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
		
		Pageable pageable = PageRequest.of(pageNum - 1, POSTS_PER_PAGE, sort);
		
		if(keyword == null || keyword.isEmpty()) {
			return postRepository.findByAuthorPublished(author.getId(), pageable);
			
		}
		else {
			return postRepository.findByAuthorPublished(keyword, author.getId(), pageable);
		}
	}
	
	public List<Post> allUserPublishedPosts(User author){
		return postRepository.findByAuthorPublished(author.getId());
	}
	
	public List<Post> allPublishedPosts(){
		return postRepository.findAllPublished();
	}
	
	public Page<Post> listByTag(Tag tag, int pageNum, String sortField, String sortDir, String keyword){
		Sort sort = Sort.by(sortField);
		sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
		
		Pageable pageable = PageRequest.of(pageNum - 1, POSTS_PER_PAGE, sort);
		
		return postRepository.findByTag(tag.getSlug(), pageable);
	}
	
	public Post save(Post post) {
		
		if(post.getId() == null || post.getId() == 0) {
			if(post.isPublish()) {
				post.setPublishedTime(LocalDateTime.now());
			}
		}
		
		return postRepository.save(post);
	}
	
	public Page<Post> listByCategory(Category category, int pageNum, String sortField, String sortDir, String keyword) {
		Sort sort = Sort.by(sortField);
		sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
		
		Pageable pageable = PageRequest.of(pageNum - 1, POSTS_PER_PAGE, sort);
		
		return postRepository.findByCategory(category.getSlug(), pageable);
	}

	public Page<Post> listByCategoryAndChildren(Category category, int pageNum, String sortField, String sortDir, String keyword) {
		Sort sort = Sort.by(sortField);
		sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
		
		Pageable pageable = PageRequest.of(pageNum - 1, POSTS_PER_PAGE, sort);
		
		List<Post> rootList = postRepository.findByCategory(category.getSlug(), pageable).getContent();
		
		List<Post> allPostsList = new ArrayList<Post>();
		allPostsList.addAll(rootList);
		
		listChildrent(category, pageable, allPostsList);

		Page<Post> page = new PageImpl<>(allPostsList, pageable, allPostsList.size());

		return page;
	}
	
	private void listChildrent(Category child, Pageable pageable, List<Post> page) {
		for (Category cat : child.getChildren()) {
			List<Post> childrenPage = postRepository.findByCategory(cat.getSlug(), pageable).getContent();
			page.addAll(childrenPage);
			
			if(cat.hasChildren()) {
				listChildrent(cat, pageable, page);
			}
		}
		
		
	}
	
	public List<Post> getNextPosts(Post post, long count) {
		
		Post next = postRepository.findByParent(post);
		List<Post> nextPosts = new ArrayList<Post>();
		if (next != null) {
			nextPosts.add(next);
			Post temp = next;
			for (int i = 0; i < count; i++) {
				Post p = postRepository.findByParent(temp);
				if (p != null) {
					nextPosts.add(p);
					temp = p;
				} else {
					break;
				}
			} 
		}
		return nextPosts;
	}
	
	public List<Post> getPrevPosts(Post post, long count) {
		
		List<Post> prevPosts = new ArrayList<Post>();
		if(post.hasParent()) {
			Post prev = post.getParent();
			prevPosts.add(prev);
			Post temp = prev;
			for(int i=0; i < count; i++) {
				Post p = temp.getParent();
				if(p != null) {
					prevPosts.add(p);
					temp = p;
				}
				else {
					break;
				}
			}
			
		}
		
		return prevPosts;
	}
	
	public List<Post> getSeriesPosts(Post post){
		long count = postRepository.count();
		List<Post> allPosts = new ArrayList<Post>();
		allPosts.addAll(getPrevPosts(post, count));
		allPosts.addAll(getNextPosts(post, count));
		return allPosts;
	}

	public String checkUnique(Integer id, String title, String content, String slug) {
		boolean isNew = (id == null || id == 0);
		
		Post postByTitle = postRepository.findByTitle(title);
		Post postByContent = postRepository.findByContent(content);
		Post postBySlug = postRepository.findBySlug(slug);
		
		if(postByTitle == null && postByContent == null && postBySlug == null) return "OK";
		
		if(isNew) {
			if(postByTitle != null) return "DuplicateTitle";
			else if(postByContent != null) return "DuplicateContent";
			else if(postBySlug != null) return "DuplicateSlug";
		}
		else {
			if(postByTitle != null && postByTitle.getId() != id) return "DuplicateTitle";
			else if(postByContent != null && postByContent.getId() != id) return "DuplicateContent";
			else if(postBySlug != null && postBySlug.getId() != id) return "DuplicateSlug";
		}
		
		return "OK";
	}
	
	public void deletePost(Integer id) throws PostNotFoundException {
		Long postCount = postRepository.countById(id);
		if(postCount == null || postCount == 0) {
			throw new PostNotFoundException("There is no post found with ID : " + id);
		}
		postRepository.deleteById(id);
	}

	public void updatePublishStatus(Integer postId, boolean status) {
		System.out.println("publish service");
		if(status == true) {
			System.out.println("publish is true");
			postRepository.updatePublishStatus(postId, status, LocalDateTime.now());
		}
		else {
			System.out.println("publish is false");
			postRepository.updatePublishStatus(postId, status);
		}
		
	}

	public String anonymousLike(String postSlug) throws PostNotFoundException {
		try {
			Post post = postRepository.findBySlug(postSlug);
			int likes = post.getLikes() + 1;
			post.setLikes(likes);
			return "OK";
			
		} catch (NoSuchElementException e) {
			throw new PostNotFoundException("There is no post found with slug : " + postSlug);
		}
	}

	public String authenticatedLike(String postSlug, String email) throws PostNotFoundException, PostLikeNotFoundException, PostLikeAlreadyExistException {
		try {
			Post post = postRepository.findBySlug(postSlug);
			User user = userService.getUserByEmail(email);
			
			return updateLikeInPostLikeTable(post, user);
			
		} catch (NoSuchElementException e) {
			throw new PostNotFoundException("There is no post found with slug : " + postSlug);
		}
	}

	private String updateLikeInPostLikeTable(Post post, User user) throws PostLikeNotFoundException, PostLikeAlreadyExistException {
		
		PostLike like = likeService.findByPostAndUser(post, user);
		boolean isLiked = (like != null);
		String status = null;
		
		if(isLiked) {
			//dislike
			likeService.deleteLike(post.getId(), user.getId());
			int likes = post.getLikes() - 1;
			post.setLikes(likes);
			status = "Dislike";
		}
		else {
			//like
			likeService.save(new PostLike(new PostLikeId(user.getId(), post.getId())));
			int likes = post.getLikes() + 1;
			post.setLikes(likes);
			status = "Like";
		}
		
		return status;
		
	}

	
}























