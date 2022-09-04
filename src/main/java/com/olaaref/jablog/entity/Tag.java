package com.olaaref.jablog.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "tags")
public class Tag {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;
	
	@Column(name = "title", nullable = false, length = 75, unique = true)
	private String title;
	
	@Column(name = "description", length = 2048)
	private String description;
	
	@Column(name = "slug", nullable = false, length = 100, unique = true)
	private String slug;
	
	@ManyToOne()
	@JoinColumn(name = "category_id", nullable = false)
	private Category category;

	public Tag() {
	}

	public Tag(Integer id) {
		this.id = id;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getSlug() {
		return slug;
	}

	public void setSlug(String slug) {
		this.slug = slug;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	@Override
	public String toString() {
		return "Tag [id=" + id + ", title=" + title + ", slug=" + slug + ", category=" + category.getTitle() + "]";
	}
	
	
}
