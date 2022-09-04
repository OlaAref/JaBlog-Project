package com.olaaref.jablog.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.olaaref.jablog.entity.Setting;
import com.olaaref.jablog.entity.setting.SettingCategory;

public interface SettingRepository extends JpaRepository<Setting, String>{
	
	public List<Setting> findByCategory(SettingCategory category);
	
}
