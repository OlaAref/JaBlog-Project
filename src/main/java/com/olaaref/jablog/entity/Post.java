package com.olaaref.jablog.entity;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.olaaref.jablog.gcp.Constants;

@Entity
@Table(name = "posts")
public class Post {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;
	
	@ManyToOne()
	@JoinColumn(name = "user_id", nullable = false)
	private User author;
	
	@Column(name = "title", nullable = false, length = 100)
	private String title;
	
	@Column(name = "content", nullable = false, length = 16777215)
	private String content;
	
	@Column(name = "summary", length = 255)
	private String summary;
	
	@Column(name = "slug", nullable = false, length = 125, unique = true)
	private String slug;
	
	@OneToOne
	@JoinColumn(name = "parent_id")
	private Post parent;
	
	@ManyToOne
	@JoinColumn(name = "category_id")
	private Category category;
	
	@ManyToMany()
	@JoinTable(
			name = "posts_tags",
			joinColumns = @JoinColumn(name = "post_id"),
			inverseJoinColumns = @JoinColumn(name = "tag_id")
			)
	private Set<Tag> tags = new HashSet<>();
	
	@Column(name = "main_picture")
	private String mainPic;
	
	@Transient
	private String mainPicPath;
	
	@Column(name = "likes")
	private int likes;
	
	@Column(name = "publish")
	private boolean publish;
	
	@CreationTimestamp
	@Column(name = "created_time")
	private LocalDateTime createdTime;
	
	@UpdateTimestamp
	@Column(name = "updated_time")
	private LocalDateTime updatedTime;
	
	@Column(name = "published_time")
	private LocalDateTime publishedTime;

	public Post() {
		
	}

	public Post(Integer id) {
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

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getSlug() {
		return slug;
	}

	public void setSlug(String slug) {
		this.slug = slug;
	}

	public Post getParent() {
		return parent;
	}

	public void setParent(Post parent) {
		this.parent = parent;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public Set<Tag> getTags() {
		return tags;
	}

	public void setTags(Set<Tag> tags) {
		this.tags = tags;
	}

	public String getMainPic() {
		return mainPic;
	}

	public void setMainPic(String mainPic) {
		this.mainPic = mainPic;
	}

	public String getMainPicPath() {
		if(id == null || mainPic == null) return "/image/main-pic-default.jpg";
		return Constants.GCP_Base_URI + "/post-photos/" + this.id + "/" + this.mainPic;
	}


	public int getLikes() {
		return likes;
	}

	public void setLikes(int likes) {
		this.likes = likes;
	}

	public boolean isPublish() {
		return publish;
	}

	public void setPublish(boolean publish) {
		this.publish = publish;
	}

	public LocalDateTime getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(LocalDateTime createdTime) {
		this.createdTime = createdTime;
	}

	public LocalDateTime getUpdatedTime() {
		return updatedTime;
	}

	public void setUpdatedTime(LocalDateTime updatedTime) {
		this.updatedTime = updatedTime;
	}

	public LocalDateTime getPublishedTime() {
		return publishedTime;
	}

	public void setPublishedTime(LocalDateTime publishedTime) {
		this.publishedTime = publishedTime;
	}
	
	public boolean hasParent() {
		return this.parent != null;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Post other = (Post) obj;
		return Objects.equals(id, other.id);
	}

	@Override
	public String toString() {
		return "Post [id=" + id + ", author=" + author.getFullName() + ", title=" + title + ", category=" + category.getTitle() + ", likes="
				+ likes + ", publish=" + publish + ", createdTime=" + createdTime + "]";
	}
	
	
	
}
