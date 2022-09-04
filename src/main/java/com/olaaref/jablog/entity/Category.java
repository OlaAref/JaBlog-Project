package com.olaaref.jablog.entity;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.olaaref.jablog.gcp.Constants;

@Entity
@Table(name = "categories")
public class Category {
	
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
	
	@OneToOne
	@JoinColumn(name = "parent_id")
	private Category parent;
	
	@OneToMany(mappedBy = "parent")
	private Set<Category> children = new HashSet<>();
	
	@Column(name = "image")
	private String image;
	
	@Transient
	private String imagePath;

	public Category() {
		
	}

	public Category(Integer id) {
		super();
		this.id = id;
	}
	
	public Category(Integer id, String title, String slug) {
		super();
		this.id = id;
		this.title = title;
		this.slug = slug;
	}

	public static Category copyIdAndTitle(Category category) {
		Category copy = new Category();
		copy.setId(category.getId());
		copy.setTitle(category.getTitle());
		return copy;
	}
	
	public static Category copyIdAndTitle(int id, String title) {
		Category copy = new Category();
		copy.setId(id);
		copy.setTitle(title);
		return copy;
	}
	
	public static Category copyFull(Category category) {
		Category copy = new Category();
		copy.setId(category.getId());
		copy.setTitle(category.getTitle());
		copy.setImage(category.getImage());
		copy.setDescription(category.getDescription());
		copy.setSlug(category.getSlug());
		copy.setParent(category.getParent());
		
		return copy;
	}
	
	public static Category copyFull(Category category, String title) {
		Category copy = Category.copyFull(category);
		copy.setTitle(title);
		
		return copy;
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

	public Category getParent() {
		return parent;
	}

	public void setParent(Category parent) {
		this.parent = parent;
	}

	public Set<Category> getChildren() {
		return children;
	}

	public void setChildren(Set<Category> children) {
		this.children = children;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getImagePath() {
		if(id == null || image == null) return "/image/default-image.png";
		return Constants.GCP_Base_URI + "/category-photos/" + this.id + "/" + this.image;
	}
	
	public boolean hasChildren() {
		if(!this.children.isEmpty())return true;
		return false;
	}

	@Override
	public String toString() {
		return "Category [id=" + id + ", title=" + title + ", slug=" + slug + "]";
	}
	
	
	
	
	
	
}
