package com.olaaref.jablog.entity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "post_likes")
public class PostLike {
	
	@EmbeddedId
	private PostLikeId id;

	public PostLike(PostLikeId id) {
		this.id = id;
	}

	public PostLike() {
		
	}

	public PostLikeId getId() {
		return id;
	}

	public void setId(PostLikeId id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "PostLike [user=" + id.getUser() + ", post=" + id.getPost() + "]";
	}
	
	
}
