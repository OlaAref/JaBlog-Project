package com.olaaref.jablog.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "countries")
public class Country {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;
	
	@Column(name = "name", nullable = false, length = 45)
	private String name;
	
	@Column(name = "code", nullable = false, length = 5)
	private String code;
	
	@Column(name = "iso3", length = 3)
	private String iso3;
	
	@Column(name = "emoji")
	private String emoji;

	public Country() {
		
	}

	public Country(Integer id) {
		this.id = id;
	}

	public Country(Integer id, String name, String code, String iso3, String emoji) {
		this.id = id;
		this.name = name;
		this.code = code;
		this.iso3 = iso3;
		this.emoji = emoji;
	}
	
	public Country(String name, String code) {
		this.name = name;
		this.code = code;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getIso3() {
		return iso3;
	}

	public void setIso3(String iso3) {
		this.iso3 = iso3;
	}

	public String getEmoji() {
		return emoji;
	}

	public void setEmoji(String emoji) {
		this.emoji = emoji;
	}

	@Override
	public String toString() {
		return "Country [id=" + id + ", name=" + name + ", code=" + code + ", iso3=" + iso3 + ", emoji=" + emoji + "]";
	}
	
	
}
