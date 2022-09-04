package com.olaaref.jablog.service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.olaaref.jablog.entity.Setting;
import com.olaaref.jablog.entity.setting.EmailSettingBag;
import com.olaaref.jablog.entity.setting.GeneralSettingBag;
import com.olaaref.jablog.entity.setting.SettingCategory;
import com.olaaref.jablog.repository.SettingRepository;

@Service
public class SettingService {

	@Autowired
	private SettingRepository settingRepository;
	
	public List<Setting> listAllSettings(){
		return settingRepository.findAll();
	}
	
	public GeneralSettingBag getGeneralSetting(){
		List<Setting> settings = settingRepository.findByCategory(SettingCategory.GENERAL);
		return new GeneralSettingBag(settings);
	}
	
	public EmailSettingBag getEmailSettingBag() {
		List<Setting> mailServerSetting = settingRepository.findByCategory(SettingCategory.MAIL_SERVER);
		List<Setting> mailTemplatesSetting = settingRepository.findByCategory(SettingCategory.MAIL_TEMPLATES);
		
		//add two lists to one list
		List<Setting> allMailSettings = Stream.concat(mailServerSetting.stream(), mailTemplatesSetting.stream()).collect(Collectors.toList());
		
		return new EmailSettingBag(allMailSettings);
	}
	
	public List<Setting> getMailServerSettings(){
		return settingRepository.findByCategory(SettingCategory.MAIL_SERVER);
	}
	
	public List<Setting> getMailTemplatesSetting(){
		return settingRepository.findByCategory(SettingCategory.MAIL_TEMPLATES);
	}
	
	public void saveAll(Iterable<Setting> settings) {
		settingRepository.saveAll(settings);
	}
}
