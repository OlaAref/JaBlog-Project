package com.olaaref.jablog.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "logins_persistent")
public class LoginsPersistent {

	@Id
	@Column(name = "series", length = 64)
	private String series;
	
	@Column(name = "username", length = 50, nullable = false)
	private String username;
	
	@Column(name = "token", length = 64, nullable = false)
	private String token;
	
	@Column(name = "last_used", nullable = false)
	private Date lastUsed;

	public String getSeries() {
		return series;
	}

	public void setSeries(String series) {
		this.series = series;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public Date getLastUsed() {
		return lastUsed;
	}

	public void setLastUsed(Date lastUsed) {
		this.lastUsed = lastUsed;
	}

	@Override
	public String toString() {
		return "LoginsPersistent [series=" + series + ", username=" + username + ", token=" + token + ", lastUsed="
				+ lastUsed + "]";
	}
	
	
}
