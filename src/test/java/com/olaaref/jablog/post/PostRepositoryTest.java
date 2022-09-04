package com.olaaref.jablog.post;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;

import com.olaaref.jablog.entity.Category;
import com.olaaref.jablog.entity.Post;
import com.olaaref.jablog.entity.Tag;
import com.olaaref.jablog.entity.User;
import com.olaaref.jablog.repository.CategoryRepository;
import com.olaaref.jablog.repository.PostRepository;
import com.olaaref.jablog.repository.TagRepository;
import com.olaaref.jablog.repository.UserRepository;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class PostRepositoryTest {
	
	@Autowired
	private PostRepository postRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private CategoryRepository categoryRepository;
	
	@Autowired
	private TagRepository tagRepository;
	
	
	@Test
	public void createPostTest() {
		Post post = new Post();
		User user = userRepository.findById(1).get();
		Category category = categoryRepository.findById(1).get();
		Set<Tag> tags = new HashSet<Tag>();
		tags.add(tagRepository.findById(7).get());
		tags.add(tagRepository.findById(8).get());
		tags.add(tagRepository.findById(9).get());
		
		post.setContent("<p>The Java String <code>compareTo()</code> method compares two strings <a href=\"https://en.wikipedia.org/wiki/Lexicographic_order\">lexicographically</a> (alphabetical order). It returns a positive number, negative number, or zero.</p><pre><code class=\"language-java\">  s1.compareTo(s2)</code></pre><ul><li>if <code>s1 &lt; s2</code>, it returns negative number.</li><li>if <code>s1 &gt; s2</code>, it returns positive number.</li><li>if <code>s1 == s2</code>, it returns 0.</li></ul><p>Table of contents</p><ul class=\"list-toc\"><li><a href=\"#acomparetoc-negative-integer\">1. \"a\".compareTo(\"c\"), negative integer</a></li><li><a href=\"#ccomparetoa-positive-integer\">2. \"c\".compareTo(\"a\"), positive integer</a></li><li><a href=\"#acomparetoa-zero\">3. \"a\".compareTo(\"a\"), zero</a></li><li><a href=\"#acomparetoab-different-lengths\">4. \"a\".compareTo(\"ab\"), different lengths</a></li><li><a href=\"#comparetoignorecase\">5. compareToIgnoreCase()</a></li><li><a href=\"#java-string-compareto-examples\">6. Java String compareTo() examples</a></li><li><a href=\"#download-source-code\">7. Download Source Code</a></li><li><a href=\"#references\">8. References</a></li></ul><h2 id=\"acomparetoc-negative-integer\">1. &ldquo;a&rdquo;.compareTo(&ldquo;c&rdquo;), negative integer</h2><p>The \"a\" is lexicographically precedes the argument string \"c\"; it returns a negative integer.</p><p>In simple words, for lexicographical or alphabetical order, \"a\" comes before the \"c\" and returns a negative integer.</p><pre><code class=\"language-java\">  System.out.println(\"a\".compareTo(\"b\")); // -1  System.out.println(\"a\".compareTo(\"c\")); // -2  System.out.println(\"a\".compareTo(\"d\")); // -3  System.out.println(\"a\".compareTo(\"e\")); // -4  System.out.println(\"1\".compareTo(\"2\")); // -1  System.out.println(\"1\".compareTo(\"3\")); // -2  System.out.println(\"1\".compareTo(\"4\")); // -3  System.out.println(\"1\".compareTo(\"5\")); // -4</code></pre><div class=\"ads-container ads-container-video\"><div id=\"mkyongFreeStarVideoAdContainer\"><div id=\"freestar-video-parent\"><div id=\"freestar-video-child\">&nbsp;</div></div></div></div><h2 id=\"ccomparetoa-positive-integer\">2. &ldquo;c&rdquo;.compareTo(&ldquo;a&rdquo;), positive integer</h2><p>The \"c\" lexicographically follows the argument string \"a\"; it returns positive integer 2.</p><p>In simple words, for lexicographical or alphabetical order, \"c\" comes after the \"a\" and returns a positive integer.</p><pre><code class=\"language-java\">  System.out.println(\"b\".compareTo(\"a\")); // 1  System.out.println(\"c\".compareTo(\"a\")); // 2  System.out.println(\"d\".compareTo(\"a\")); // 3  System.out.println(\"e\".compareTo(\"a\")); // 4  System.out.println(\"2\".compareTo(\"1\")); // 1  System.out.println(\"3\".compareTo(\"1\")); // 2  System.out.println(\"4\".compareTo(\"1\")); // 3  System.out.println(\"5\".compareTo(\"1\")); // 4</code></pre><h2 id=\"acomparetoa-zero\">3. &ldquo;a&rdquo;.compareTo(&ldquo;a&rdquo;), zero</h2><p>If the strings are equal, it returns zero or 0.</p><pre><code class=\"language-java\">  System.out.println(\"a\".compareTo(\"a\")); // 0  System.out.println(\"1\".compareTo(\"1\")); // 0</code></pre><div class=\"ads-container\"><div id=\"mkyong_incontent_leaderboard_atf\" align=\"center\" data-freestar-ad=\"__320x100 __728x90\">&nbsp;</div><div><h2 id=\"acomparetoab-different-lengths\">4. &ldquo;a&rdquo;.compareTo(&ldquo;ab&rdquo;), different lengths</h2><p>For different lengths comparison strings, it works the same.</p><pre><code class=\"language-java\">  System.out.println(\"a\".compareTo(\"ab\"));    // -1  System.out.println(\"a\".compareTo(\"abc\"));   // -2  System.out.println(\"a\".compareTo(\"abcd\"));  // -3  System.out.println(\"11\".compareTo(\"112\"));    // -1  System.out.println(\"11\".compareTo(\"1123\"));   // -2  System.out.println(\"11\".compareTo(\"11234\"));  // -3</code></pre><h2 id=\"comparetoignorecase\">5. compareToIgnoreCase()</h2><p>The Java String <code>compareToIgnoreCase()</code> method compares two strings lexicographically, ignoring cases.</p><pre><code class=\"language-java\">  System.out.println(\"a\".compareTo(\"A\"));           // 32  System.out.println(\"a\".compareToIgnoreCase(\"A\")); // 0</code></pre><h2 id=\"java-string-compareto-examples\">6. Java String compareTo() examples</h2><p>Below is a complete Java String <code>compareTo()</code> examples.</p><div class=\"filename\">StringCompareTo.java</div><pre><code class=\"language-java\">package com.mkyong.string.compare;public class StringCompareTo {    public static void main(String[] args) {        System.out.println(\"-Negative Number-\");        System.out.println(\"a\".compareTo(\"b\")); // -1        System.out.println(\"a\".compareTo(\"c\")); // -2        System.out.println(\"a\".compareTo(\"d\")); // -3        System.out.println(\"a\".compareTo(\"e\")); // -4        System.out.println(\"1\".compareTo(\"2\")); // -1        System.out.println(\"1\".compareTo(\"3\")); // -2        System.out.println(\"1\".compareTo(\"4\")); // -3        System.out.println(\"1\".compareTo(\"5\")); // -4        System.out.println(\"-Positive Number-\");        System.out.println(\"b\".compareTo(\"a\")); // 1        System.out.println(\"c\".compareTo(\"a\")); // 2        System.out.println(\"d\".compareTo(\"a\")); // 3        System.out.println(\"e\".compareTo(\"a\")); // 4        System.out.println(\"2\".compareTo(\"1\")); // 1        System.out.println(\"3\".compareTo(\"1\")); // 2        System.out.println(\"4\".compareTo(\"1\")); // 3        System.out.println(\"5\".compareTo(\"1\")); // 4        System.out.println(\"-Zero-\");        System.out.println(\"a\".compareTo(\"a\")); // 0        System.out.println(\"1\".compareTo(\"1\")); // 0        System.out.println(\"-Vary Length-\");        System.out.println(\"a\".compareTo(\"ab\"));    // -1        System.out.println(\"a\".compareTo(\"abc\"));   // -2        System.out.println(\"a\".compareTo(\"abcd\"));  // -3        System.out.println(\"11\".compareTo(\"112\"));    // -1        System.out.println(\"11\".compareTo(\"1123\"));   // -2        System.out.println(\"11\".compareTo(\"11234\"));  // -3        System.out.println(\"-compareToIgnoreCase-\");        System.out.println(\"a\".compareTo(\"A\")); // 32        System.out.println(\"a\".compareToIgnoreCase(\"A\")); // 0    }}</code></pre><p>Output</p><div class=\"filename\">Terminal</div><pre><code class=\"language-bash\">-Negative Number--1-2-3-4-1-2-3-4-Positive Number-12341234-Zero-00-Vary Length--1-2-3-1-2-3-compareToIgnoreCase-320</code></pre><h2 id=\"download-source-code\">7. Download Source Code</h2><div class=\"github\"><p>$ git clone <a href=\"https://github.com/mkyong/core-java\">https://github.com/mkyong/core-java</a></p><p>$ cd java-string</p></div><h2 id=\"references\">8. References</h2><ul><li><a href=\"https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/lang/String.html#compareTo(java.lang.String)\">String#compareTo JavaDoc</a></li><li><a href=\"https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/lang/CharSequence.html\">CharSequence JavaDoc</a></li><li><a href=\"https://en.wikipedia.org/wiki/Lexicographic_order\">Lexicographic order</a></li><li><a href=\"/java/java-how-to-compare-string/\">How to compare Strings in Java</a></li></ul><div class=\"pre-next-container d-flex justify-content-between\">&nbsp;</div></div></div>");
		post.setAuthor(user);
		post.setCategory(category);
		post.setPublish(true);
		post.setPublishedTime(LocalDateTime.now());
		post.setTitle("Java String compareTo() examples");
		post.setSlug("java_string_compareTo");
		post.setTags(tags);
		System.out.println(post);
		postRepository.save(post);
		
	}
	
	@Test
	public void searchPostTest() {
		
		String keyword = "admin";
		Pageable pageabele = PageRequest.of(0, 10, Sort.by("id").ascending());
		
		Page<Post> posts = postRepository.findAll(keyword, pageabele);
		posts.forEach(System.out::println);

	}
	
	@Test
	public void getPostByUserTest() {
		
		User user = userRepository.findById(3).get();
		Pageable pageabele = PageRequest.of(0, 10, Sort.by("id").ascending());
		
		List<Post> posts = postRepository.findByAuthorPublished(user.getId());
		posts.forEach(System.out::println);

	}
	
	@Test
	public void getPostByParentTest() {
		
		Post post = postRepository.findById(13).get();
		Post child = postRepository.findByParent(post);
		System.out.println(post);
		System.out.println(child);

	}
	
	@Test
	public void countAllPostsTest() {
		System.out.println(postRepository.count());
	}
	
	@Test
	public void getNextPostsTest() {
		Post post = postRepository.findById(14).get();
		long count = postRepository.count();
		
		Post next = postRepository.findByParent(post);
		List<Post> nextPosts = new ArrayList<Post>();
		nextPosts.add(next);
		Post temp = next;
		
		for(int i=0; i < count; i++) {
			Post p = postRepository.findByParent(temp);
			if(p != null) {
				nextPosts.add(p);
				temp = p;
			}
			else {
				break;
			}
		}
		
		nextPosts.forEach(System.out::println);
	}
	
	@Test
	public void getPrevPostsTest() {
		Post post = postRepository.findById(14).get();
		long count = postRepository.count();
		
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
		
		prevPosts.forEach(System.out::println);
	}
	
	@Test
	public void getAllPostPublishedTest() {
		List<Post> posts = postRepository.findAllPublished();
		posts.forEach(System.out::println);

	}
	
	@Test
	public void getPostByUserPublishedTest() {
		
		User user = userRepository.findById(17).get();
		Pageable pageabele = PageRequest.of(0, 10, Sort.by("id").ascending());
		
		Page<Post> posts = postRepository.findByAuthorPublished(user.getId(), pageabele);
		posts.forEach(System.out::println);

	}
	
	@Test
	public void getPostByUserAndKeywordTest() {
		
		Pageable pageabele = PageRequest.of(0, 10, Sort.by("id").ascending());
		Page<Post> posts = postRepository.findByAuthor("compareTo()", 1, pageabele);
		System.out.println("Result :");
		posts.forEach(System.out::println);

	}
	
	@Test
	public void getPostByTagTest() {
		Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
		Page<Post> posts = postRepository.findByTag("java", pageable);
		posts.forEach(System.out::println);

	}
	
	@Test
	public void getPostByCategoryTest() {
		Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
		Page<Post> posts = postRepository.findByCategory("", pageable);
		posts.forEach(System.out::println);

	}
	


}











