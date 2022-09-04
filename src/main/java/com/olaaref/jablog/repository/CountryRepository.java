package com.olaaref.jablog.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.olaaref.jablog.entity.Country;

@Repository
public interface CountryRepository extends JpaRepository<Country, Integer> {

	public List<Country> findAllByOrderByNameAsc();
	public Country findByIso3(String iso3);
	public Country findByCode(String code);
}
