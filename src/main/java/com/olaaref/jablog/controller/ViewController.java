package com.olaaref.jablog.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.olaaref.jablog.entity.Category;
import com.olaaref.jablog.entity.Comment;
import com.olaaref.jablog.entity.Post;
import com.olaaref.jablog.entity.PostLike;
import com.olaaref.jablog.entity.Tag;
import com.olaaref.jablog.entity.User;
import com.olaaref.jablog.exception.CategoryNotFoundException;
import com.olaaref.jablog.exception.PostNotFoundException;
import com.olaaref.jablog.exception.TagNotFoundException;
import com.olaaref.jablog.exception.UserNotFoundException;
import com.olaaref.jablog.helper.Utility;
import com.olaaref.jablog.service.CategoryService;
import com.olaaref.jablog.service.CommentService;
import com.olaaref.jablog.service.PostLikeService;
import com.olaaref.jablog.service.PostService;
import com.olaaref.jablog.service.TagService;
import com.olaaref.jablog.service.UserService;

@Controller
@RequestMapping("/view")
public class ViewController {
	
	@Autowired
	private CategoryService categoryService;
	
	@Autowired
	private TagService tagService;
	
	@Autowired
	private PostService postService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private PostLikeService likeService;
	
	@Autowired
	private CommentService commentService;
	
	@GetMapping("/categories/list")
	public String viewAllCategories(Model model) {
		List<Category> categories = categoryService.listAllCategories();
		model.addAttribute("categories", categories);
		return "view/categories-view";
	}
	
	
	@GetMapping("/tags/new")
	public String viewTagForm(Model model) {
		model.addAttribute("tag", new Tag());
		model.addAttribute("categories", categoryService.getFormCategories());
		return "posts/add-tag-modal";
	}
	
	@PostMapping("/tags/save")
	public String saveTag(@ModelAttribute("tag") Tag tag,
						  RedirectAttributes redirectAttributes) {
		tagService.save(tag);
		
		return "redirect:/post/new";
	}
	/*
	@GetMapping("/posts/{postId}")
	public String listAllPosts(@PathVariable("postId") int postId,
							   HttpServletRequest request,
							   Model model) {
		
		try {
			
			Post post = postService.getById(postId);
			String email = Utility.getEmailForAuthenticatedUser(request);
			
			if (post.isPublish()) {
				
				if(email != null) {
					User user = userService.getUserByEmail(email);
					PostLike like = likeService.findByPostAndUser(post, user);
					if(like != null) {
						model.addAttribute("authLikes", "yes");
					}
					else {
						model.addAttribute("authLikes", "no");
					}
				}
				
				List<Category> categories = categoryService.listAllCategories();
				String liked = isLikedCookie(request, post.getSlug());
				
				List<Post> relatedPosts = getRelatedPosts(post.getCategory(), 3);
				List<Post> seriesPosts = postService.getSeriesPosts(post);
				
				model.addAttribute("categories", getRandomCategories(categories, 6));
				model.addAttribute("postCategories", getPostCategories(post));
				model.addAttribute("seriesPosts", seriesPosts);
				model.addAttribute("relatdPosts", relatedPosts);
				model.addAttribute("liked", liked);
				model.addAttribute("post", post);
			}
			else {
				return "redirect:/";
			}
			
		} catch (PostNotFoundException e) {
			return "redirect:/";
		}
		
		return "view/post-view";
	}
	*/
	@GetMapping("/posts/contact")
	public String viewContactForm() {
		return "contact";
	}
	@GetMapping("/posts/{postSlug}")
	public String viewPost(@PathVariable("postSlug") String postSlug,
							   HttpServletRequest request,
							   Model model) {
		
		try {
			
			Post post = postService.getBySlug(postSlug);
			String email = Utility.getEmailForAuthenticatedUser(request);
			
			if (post.isPublish()) {
				
				if(email != null) {
					User user = userService.getUserByEmail(email);
					PostLike like = likeService.findByPostAndUser(post, user);
					if(like != null) {
						model.addAttribute("authLikes", "yes");
					}
					else {
						model.addAttribute("authLikes", "no");
					}
				}
				
				List<Category> categories = categoryService.listAllCategories();
				String liked = isLikedCookie(request, post.getSlug());
				
				List<Post> relatedPosts = getRelatedPosts(post, post.getCategory(), 3);
				List<Post> seriesPosts = postService.getSeriesPosts(post);
				
				List<Comment> comments = commentService.listByPost(post);
				
				model.addAttribute("categories", getRandomCategories(categories, 6));
				model.addAttribute("postCategories", getPostCategories(post));
				model.addAttribute("seriesPosts", seriesPosts);
				model.addAttribute("relatdPosts", relatedPosts);
				model.addAttribute("liked", liked);
				model.addAttribute("comments", comments);
				model.addAttribute("commentForm", new Comment());
				model.addAttribute("post", post);
			}
			else {
				return "redirect:/";
			}
			
		} catch (PostNotFoundException e) {
			return "redirect:/";
		}
		
		return "view/post-view";
	}
	
	private List<Post> getRelatedPosts(Post mainPost, Category category, int noOfElements) {
		List<Post> allRelatedPosts = new ArrayList<Post>();
		List<Post> relatedPosts = new ArrayList<Post>();
		postService.listByCategoryAndChildren(category, 1, "id", "asc", null).forEach(post -> allRelatedPosts.add(post));
		Random random = new Random();
		for (int i = 0; i < noOfElements; i++) {
			int randomIndex = random.nextInt(allRelatedPosts.size());
			if(!mainPost.getId().equals(allRelatedPosts.get(randomIndex).getId())) {
				relatedPosts.add(allRelatedPosts.get(randomIndex));
			}
			
			allRelatedPosts.remove(randomIndex);
			if(allRelatedPosts.isEmpty()) {
				break;
			}
		}
		
		return relatedPosts;
	}
	
	



	private String isLikedCookie(HttpServletRequest request, String postSlug) {
		return Arrays.stream(request.getCookies())
				.filter(cookie -> postSlug.equals(cookie.getName()))
				.map(Cookie::getValue)
				.findAny()
				.orElse("no");
	}


	private List<Category> getPostCategories(Post post) {
		List<Category> postCategories = new ArrayList<>();
		Category category = post.getCategory();
		
		while(category.getParent() != null) {
			category = category.getParent();
			postCategories.add(category);
		}
		
		Collections.reverse(postCategories);
		
		return postCategories;
	}
	
	private List<Category> getRandomCategories(List<Category> categories, int noOfElements) {
		List<Category> randomCategories = new ArrayList<>();
		Random random = new Random();
		for (int i = 0; i < noOfElements; i++) {
			int randomIndex = random.nextInt(categories.size());
			Category randomCategory = categories.get(randomIndex);
			randomCategories.add(randomCategory);
			categories.remove(randomIndex);
		}
		return randomCategories;
	}


	@GetMapping("/posts/list")
	public String listAllPosts(Model model) {
		return listPostsByPage(1, "publishedTime", "desc", null, model);
	}
	
	@GetMapping("/posts/page/{pageNum}")
	public String listPostsByPage(@PathVariable("pageNum") int pageNum,
							   @RequestParam("sortField") String sortField,
							   @RequestParam("sortDir") String sortDir,
							   @RequestParam("keyword") String keyword,
							   Model model) {
		Page<Post> page = postService.listByPagePublished(pageNum, sortField, sortDir, keyword);
		updateModelAttributes(page, pageNum, sortField, sortDir, keyword, "/view/posts", model);

		return "index";
	}
	
	private void updateModelAttributes(Page<Post> page, int pageNum, String sortField, String sortDir, String keyword, String moduleLink, Model model) {

		String revSortDir = sortDir.equals("asc") ? "desc" : "asc";
		
		model.addAttribute("moduleLink", moduleLink);
		model.addAttribute("pageNum", pageNum);
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("posts", page.getContent());
		model.addAttribute("totalElements", page.getTotalElements());
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("revSortDir", revSortDir);
		model.addAttribute("keyword", keyword);
		
		
	}
	
	@GetMapping("/posts/author/{authorId}/page/{pageNum}")
	public String listPostsByAuthor(@PathVariable("pageNum") int pageNum,
									@PathVariable("authorId") int authorId,
									@RequestParam("sortField") String sortField,
									@RequestParam("sortDir") String sortDir,
									@RequestParam("keyword") String keyword,
									Model model) {
		
		try {
			User author = userService.getUserById(authorId);
			model.addAttribute("author", author);
			Page<Post> page = postService.listByUserPublished(author, pageNum, sortField, sortDir, keyword);
			updateModelAttributes(page, pageNum, sortField, sortDir, keyword, "/view/posts/author/"+authorId, model);
			
		} 
		catch (UserNotFoundException e) {
			model.addAttribute("errorMessage", e.getMessage());
		}
		

		return "view/posts-by-author";
	}
	
	@GetMapping("/posts/tag/{tagSlug}/page/{pageNum}")
	public String listPostsByTag(@PathVariable("pageNum") int pageNum,
								@PathVariable("tagSlug") String tagSlug,
								@RequestParam("sortField") String sortField,
								@RequestParam("sortDir") String sortDir,
								@RequestParam("keyword") String keyword,
								Model model,
								RedirectAttributes redirectAttributes) {
		
		try {
			Tag tag = tagService.findBySlug(tagSlug);
			model.addAttribute("tag", tag);
			Page<Post> page = postService.listByTag(tag, pageNum, sortField, sortDir, keyword);
			updateModelAttributes(page, pageNum, sortField, sortDir, keyword, "/view/posts/tag/"+tagSlug, model);
			
		} 
		catch (TagNotFoundException e) {
			redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
			return "redirect:/";
		}

		return "view/posts-by-tag";
	}
	
	@GetMapping("/posts/category/{categorySlug}/page/{pageNum}")
	public String listPostsByCategory(@PathVariable("pageNum") int pageNum,
								@PathVariable("categorySlug") String categorySlug,
								@RequestParam("sortField") String sortField,
								@RequestParam("sortDir") String sortDir,
								@RequestParam("keyword") String keyword,
								Model model,
								RedirectAttributes redirectAttributes) {
		
		try {
			Category category = categoryService.findBySlug(categorySlug);
			model.addAttribute("category", category);
			Page<Post> page = postService.listByCategoryAndChildren(category, pageNum, sortField, sortDir, keyword);
			updateModelAttributes(page, pageNum, sortField, sortDir, keyword, "/view/posts/category/"+categorySlug, model);
			
		} 
		catch (CategoryNotFoundException e) {
			redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
			return "redirect:/";
		}

		return "view/posts-by-category";
	}

}
