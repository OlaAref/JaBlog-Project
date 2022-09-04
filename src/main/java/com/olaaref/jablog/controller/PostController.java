package com.olaaref.jablog.controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.olaaref.jablog.entity.Post;
import com.olaaref.jablog.entity.User;
import com.olaaref.jablog.exception.PostNotFoundException;
import com.olaaref.jablog.gcp.GoogleCloudUtility;
import com.olaaref.jablog.helper.FileUploadUtil;
import com.olaaref.jablog.helper.Utility;
import com.olaaref.jablog.service.CategoryService;
import com.olaaref.jablog.service.PostService;
import com.olaaref.jablog.service.TagService;
import com.olaaref.jablog.service.UserService;

@Controller
@RequestMapping("/post")
public class PostController {
	
	@Autowired
	private PostService postService;
	
	@Autowired
	private CategoryService categoryService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private TagService tagService;
	
	@GetMapping("/list")
	public String listAllPosts(Model model, HttpServletRequest request) {
		return listAllByPage(1, "id", "asc", null, request, model);
	}
	
	@GetMapping("/page/{pageNum}")
	public String listAllByPage(@PathVariable("pageNum") int pageNum,
								@RequestParam("sortField") String sortField,
								@RequestParam("sortDir") String sortDir,
								@RequestParam("keyword") String keyword,
								HttpServletRequest request,
								Model model) {
		
		Page<Post> page = null;
		
		String email = Utility.getEmailForAuthenticatedUser(request);
		User user = userService.getUserByEmail(email);
		
		if(user.getRole().getName().equals("admin")) {
			page = postService.listByPage(pageNum, sortField, sortDir, keyword);
		}
		else {
			page = postService.listByUser(user, pageNum, sortField, sortDir, keyword);	
		}

		updateModelAttributes(page, pageNum, sortField, sortDir, keyword, model);
		return "posts/all-posts";
	}
	
	private void updateModelAttributes(Page<Post> page, int pageNum, String sortField, String sortDir, String keyword, Model model) {
		int pageSize = PostService.POSTS_PER_PAGE;
		
		long startCount = (pageNum - 1) * pageSize + 1;
		long endCount = startCount + pageSize - 1;
		if(page.getTotalElements() < endCount) endCount = page.getTotalElements();
		
		String revSortDir = sortDir.equals("asc") ? "desc" : "asc";
		
		model.addAttribute("moduleLink", "/post");
		model.addAttribute("pageNum", pageNum);
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("startCount", startCount);
		model.addAttribute("endCount", endCount);
		model.addAttribute("posts", page.getContent());
		model.addAttribute("totalElements", page.getTotalElements());
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("revSortDir", revSortDir);
		model.addAttribute("keyword", keyword);
		
		
	}


	
	@GetMapping("/new")
	public String newPostForm(Model model, HttpServletRequest request) {
		
		String email = Utility.getEmailForAuthenticatedUser(request);
		User author = userService.getUserByEmail(email);
		List<Post> posts = new ArrayList<Post>();
		if(author.getRole().getName().equals("admin")) {
			posts = postService.allPublishedPosts();
		}
		else {
			posts = postService.allUserPublishedPosts(author);
		}
		
		model.addAttribute("pageTitle", "Add");
		model.addAttribute("categories", categoryService.getFormCategories());
		model.addAttribute("tagsList", tagService.listAllTags());
		model.addAttribute("posts", posts);
		model.addAttribute("post", new Post());
		return "posts/post-form";
	}
	
	@PostMapping("/save")
	public String savePost(@ModelAttribute("post") Post post,
						   @RequestParam("photo") MultipartFile multipartFile,
						   HttpServletRequest request,
						   RedirectAttributes redirectAttributes) throws IOException {
		String email = Utility.getEmailForAuthenticatedUser(request);
		User author = userService.getUserByEmail(email);
		post.setAuthor(author);
		
		if(!multipartFile.isEmpty()) {
			String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			post.setMainPic(fileName);
			Post savedPost = postService.save(post);
			String uploadDir = "post-photos/" + savedPost.getId();
			
			GoogleCloudUtility utility = new GoogleCloudUtility();
			String prefix = uploadDir + "/";
			utility.deleteFolder(prefix);
			utility.upload(multipartFile, prefix);
		}
		else {
			if(post.getMainPic().isEmpty()) {
				post.setMainPic(null);
			}
			postService.save(post);
		}
		
		if (post.isPublish()) {
			redirectAttributes.addFlashAttribute("message", "The Post has been published successfully.");
		}
		else {
			redirectAttributes.addFlashAttribute("message", "The Post has been saved successfully.");
		}
		
		return "redirect:/post/list";
		
	}
	
	@GetMapping("/edit/{id}")
	public String editPostForm(@PathVariable("id") Integer id,
							   HttpServletRequest request,
							   Model model,
							   RedirectAttributes redirectAttributes) {
		
		

		try {
			String email = Utility.getEmailForAuthenticatedUser(request);
			User user = userService.getUserByEmail(email);
			Post post = postService.getById(id);
			
			if (user.getRole().getName().equals("admin")) {
				model.addAttribute("pageTitle", "Edit");
				model.addAttribute("categories", categoryService.listAllCategories());
				model.addAttribute("tagsList", tagService.listAllTags());
				model.addAttribute("post", post);
			}
			else {
				if(userOwnPost(user, post)) {
					model.addAttribute("pageTitle", "Edit");
					model.addAttribute("categories", categoryService.listAllCategories());
					model.addAttribute("tagsList", tagService.listAllTags());
					model.addAttribute("post", post);
				}
				else {
					redirectAttributes.addFlashAttribute("errorMessage", "You are not allowed to edit this post.");
					return "redirect:/post/list";
				}
			}
			
		} catch (PostNotFoundException e) {
			redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
			return "redirect:/post/list";
		}
		
		
		
		return "posts/post-form";
	}

	private boolean userOwnPost(User user, Post post) {
		if(post.getAuthor().getId() == user.getId()) return true;
		return false;
	}
	
	@GetMapping("/delete/{id}")
	public String deletePost(@PathVariable("id") Integer id,
							   HttpServletRequest request,
							   Model model,
							   RedirectAttributes redirectAttributes) throws FileNotFoundException, IOException {

		try {
			String email = Utility.getEmailForAuthenticatedUser(request);
			User user = userService.getUserByEmail(email);
			Post post = postService.getById(id);
			
			if (user.getRole().getName().equals("admin")) {
				postService.deletePost(id);
				GoogleCloudUtility utility = new GoogleCloudUtility();
				utility.deleteFolder("post-photos/"+id+"/");
			}
			else {
				if(userOwnPost(user, post)) {
					postService.deletePost(id);
					GoogleCloudUtility utility = new GoogleCloudUtility();
					utility.deleteFolder("post-photos/"+id+"/");
				}
				else {
					redirectAttributes.addFlashAttribute("errorMessage", "You are not allowed to delete this post.");
					return "redirect:/post/list";
				}
			}
			
		} catch (PostNotFoundException e) {
			redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
			return "redirect:/post/list";
		}
		
		
		redirectAttributes.addFlashAttribute("message", "The Post has been deleted successfully.");
		return "redirect:/post/list";
	}

	@GetMapping("{id}/publish/{status}")
	public String publishPost(@PathVariable("id") Integer id,
							  @PathVariable("status") boolean status,
							   HttpServletRequest request,
							   Model model,
							   RedirectAttributes redirectAttributes) {

		try {
			String email = Utility.getEmailForAuthenticatedUser(request);
			User user = userService.getUserByEmail(email);
			Post post = postService.getById(id);
			
			if (user.getRole().getName().equals("admin")) {
				postService.updatePublishStatus(post.getId(), status);
			}
			else {
				if(userOwnPost(user, post)) {
					postService.updatePublishStatus(post.getId(), status);
				}
				else {
					redirectAttributes.addFlashAttribute("errorMessage", "You are not allowed to publish this post.");
					return "redirect:/post/list";
				}
			}
			
		} catch (PostNotFoundException e) {
			redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
			return "redirect:/post/list";
		}
		
		String publish = (status) ? "published" : "unpublished";
		
		redirectAttributes.addFlashAttribute("message", "The Post has been "+publish+" successfully.");
		return "redirect:/post/list";
	}

}
