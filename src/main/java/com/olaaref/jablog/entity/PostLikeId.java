package com.olaaref.jablog.entity;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class PostLikeId implements Serializable {
	
	@Column(name = "user_id")
	private Integer user;
	@Column(name = "post_id")
	private Integer post;
	
	public PostLikeId() {
		
	}
	
	public PostLikeId(Integer user, Integer post) {
		super();
		this.user = user;
		this.post = post;
	}

	public Integer getUser() {
		return user;
	}

	public void setUser(Integer user) {
		this.user = user;
	}

	public Integer getPost() {
		return post;
	}

	public void setPost(Integer post) {
		this.post = post;
	}

	@Override
	public int hashCode() {
		return Objects.hash(post, user);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PostLikeId other = (PostLikeId) obj;
		return Objects.equals(post, other.post) && Objects.equals(user, other.user);
	}
	
	
	
}
