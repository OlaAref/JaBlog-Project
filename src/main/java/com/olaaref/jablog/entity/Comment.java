package com.olaaref.jablog.entity;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "comments")
public class Comment {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;
	
	@ManyToOne
	@JoinColumn(name = "author_id", nullable = true)
	private User author;
	
	@Column(name = "content", nullable = false, length = 65535)
	private String content;
	
	@ManyToOne()
	@JoinColumn(name = "post_id", nullable = false)
	private Post post;
	
	@OneToOne
	@JoinColumn(name = "parent_id")
	private Comment parent;
	
	@OneToMany(mappedBy = "parent")
	private Set<Comment> children = new HashSet<Comment>();
	
	@CreationTimestamp
	@Column(name = "created_time")
	private LocalDateTime createdTime;
	
	@Transient
	private String duration;

	public Comment() {
	}

	public Comment(Integer id) {
		this.id = id;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public User getAuthor() {
		return author;
	}

	public void setAuthor(User author) {
		this.author = author;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Post getPost() {
		return post;
	}

	public void setPost(Post post) {
		this.post = post;
	}

	public Comment getParent() {
		return parent;
	}

	public void setParent(Comment parent) {
		this.parent = parent;
	}

	public boolean hasChildren() {
		return !this.children.isEmpty();
	}
	
	public Set<Comment> getChildren() {
		return children;
	}

	public void setChildren(Set<Comment> children) {
		this.children = children;
	}

	public LocalDateTime getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(LocalDateTime createdTime) {
		this.createdTime = createdTime;
	}
	
	public String getDuration() {
		LocalDateTime createdTime = this.createdTime;
		LocalDateTime now = LocalDateTime.now();
		
		long between = ChronoUnit.SECONDS.between(createdTime, now);
		
		return getDurationString(between);
	}

	private String getDurationString(long duration) {
		
		Map<String, Long> durationMap = new HashMap<>();
		long years = duration/(12*30*24*3600);
		durationMap.put("years", years);
		duration = duration%(12*30*24*3600); 
		long months = duration/(30*24*3600); 
		durationMap.put("months", months);
		duration = duration%(30*24*3600); 
		long days = duration/(24*3600); 
		durationMap.put("days", days);
        duration = duration%(24*3600); 
        long hours = duration/3600; 
        durationMap.put("hours", hours);
        duration %= 3600; 
        long minutes = duration/60; 
        durationMap.put("minutes", minutes);
        duration %= 60; 
        long seconds = duration;
        durationMap.put("seconds", seconds);
		
		return getTheViewDuration(durationMap);
	}
	
	private String getTheViewDuration(Map<String, Long> map) {
		String view = null;
		if(map.get("years") != 0) {
			view = map.get("years") + " years ago";
		}
		else if(map.get("months") != 0) {
			view = map.get("months") + " months ago";
		}
		else if(map.get("days") != 0) {
			view = map.get("days") + " days ago";
		}
		else if(map.get("hours") != 0) {
			view = map.get("hours") + " hours ago";
		}
		else if(map.get("minutes") != 0) {
			view = map.get("minutes") + " minutes ago";
		}
		else if(map.get("seconds") != 0) {
			view = map.get("seconds") + " seconds ago";
		}
		else {
			view = "just now";
		}
		
		return view;
	}

	@Override
	public String toString() {
		return "Comment [id=" + id + ", author=" + (author != null ? author.getFullName() : "Anonymous") + ", content=" + content + ", post=" + post.getTitle() + "]";
	}
	
	
}
