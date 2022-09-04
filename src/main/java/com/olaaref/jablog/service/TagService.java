package com.olaaref.jablog.service;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.olaaref.jablog.entity.Tag;
import com.olaaref.jablog.exception.TagNotFoundException;
import com.olaaref.jablog.repository.TagRepository;

@Service
public class TagService {
	
	public static final int TAGS_PER_PAGE = 2;

	@Autowired
	private TagRepository tagRepository;
	
	public List<Tag> listAllTags(){
		return tagRepository.findAll();
	}
	
	public Tag findById(Integer id) throws TagNotFoundException {
		try {
			return tagRepository.findById(id).get();
		} catch (NoSuchElementException e) {
			throw new TagNotFoundException("There is no tag found with ID : " + id);
		}
	}
	
	public Page<Tag> listByPage(int pageNum, String sortField, String sortDir, String keyword){
		
		Sort sort = Sort.by(sortField);
		sort = (sortDir.equals("asc")) ? sort.ascending() : sort.descending();
		
		Pageable pageable = PageRequest.of(pageNum - 1, TAGS_PER_PAGE, sort);
		
		if(keyword == null || keyword.isEmpty()) {
			return tagRepository.findAll(pageable);
		}
		else {
			return tagRepository.findAll(keyword, pageable);
		}
	}
	
	public Tag save(Tag tag) {
		if(tag.getSlug().isEmpty()) {
			String title = tag.getTitle().toLowerCase().trim();
			String slug = title.replace(" ", "_");
			tag.setSlug(slug);
		}
		return tagRepository.save(tag);
	}
	
	public String checkUnique(Integer id, String title, String slug) {
		boolean isNew = (id == null || id == 0);
		
		Tag tagByTitle = tagRepository.findByTitle(title);
		Tag tagBySlug = tagRepository.findBySlug(slug);
		
		if(tagByTitle == null && tagBySlug == null) return "OK";
		
		if(isNew) {
			if(tagByTitle != null) return "DuplicateTitle";
			else if(tagBySlug != null) return "DuplicateSlug";
		}
		else {
			if(tagByTitle != null && tagByTitle.getId() != id) return "DuplicateTitle";
			else if(tagBySlug != null && tagBySlug.getId() != id) return "DuplicateSlug";
		}
		
		return "OK";
	}

	public void deleteById(Integer id) throws TagNotFoundException {
		
		Long tagCounts = tagRepository.countById(id);
		if(tagCounts == 0 || tagCounts == null) {
			throw new TagNotFoundException("There is no tag found with ID : " + id);
		}
		tagRepository.deleteById(id);
		
	}

	public Tag findBySlug(String tagSlug) throws TagNotFoundException {
		try {
			return tagRepository.findBySlug(tagSlug);
		}
		catch (NoSuchElementException e) {
			throw new TagNotFoundException("There is no tag found with slug : " + tagSlug);
		}
	}
}















