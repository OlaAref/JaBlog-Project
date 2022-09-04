package com.olaaref.jablog.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.olaaref.jablog.entity.Setting;
import com.olaaref.jablog.entity.setting.GeneralSettingBag;
import com.olaaref.jablog.gcp.GoogleCloudUtility;
import com.olaaref.jablog.service.SettingService;

@Controller
@RequestMapping("/setting")
public class SettingController {

	@Autowired
	private SettingService settingService;
	
	@GetMapping("/list")
	public String listAllSettings(Model model) {
		
		List<Setting> settings = settingService.listAllSettings();
		for (Setting setting : settings) {
			model.addAttribute(setting.getKey(), setting.getValue());
		}
		
		return "settings/list-settings";
	}
	
	@PostMapping("/saveGeneral")
	public String saveGeneralSetting(@RequestParam("photo") MultipartFile multipartFile,
									 HttpServletRequest request,
									 RedirectAttributes redirectAttributes) throws IOException {
		
		GeneralSettingBag generalBag = settingService.getGeneralSetting(); 
		saveSiteLogo(multipartFile, generalBag);
		updateSettings(request, generalBag.getAllSetting());
		
		redirectAttributes.addFlashAttribute("message", "General settings have been saved.");
		return "redirect:/setting/list";
		
	}

	private void saveSiteLogo(MultipartFile multipartFile, GeneralSettingBag generalBag) throws IOException {
		if(!multipartFile.isEmpty()) {
			String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			String UploadDir = "site-logo";
			
			String value = "/site-logo/" + fileName;
			generalBag.updateSiteLogo(value);
			
			GoogleCloudUtility utility = new GoogleCloudUtility();
			String prfix = UploadDir + "/";
			utility.deleteFolder(prfix);
			utility.upload(multipartFile, prfix);
		}
		
	}

	private void updateSettings(HttpServletRequest request, List<Setting> allSetting) {
		
		//update all settings
		for (Setting setting : allSetting) {
			//get the value from the form
			String value = request.getParameter(setting.getKey());
			if(value != null) {
				setting.setValue(value);
			}
		}
		
		//save settings after update
		settingService.saveAll(allSetting);
	}

	@PostMapping("/saveMailServer")
	public String saveMailServerSetting(HttpServletRequest request,
									 RedirectAttributes redirectAttributes) throws IOException {
		
		List<Setting> mailServerSettings = settingService.getMailServerSettings();
		updateSettings(request, mailServerSettings);
		
		redirectAttributes.addFlashAttribute("message", "Mail Server settings have been saved.");
		return "redirect:/setting/list";
		
	}
	
	@PostMapping("/saveMailTemplates")
	public String saveMailTemplatesSetting(HttpServletRequest request,
									 RedirectAttributes redirectAttributes) throws IOException {
		
		List<Setting> mailServerSettings = settingService.getMailTemplatesSetting();
		updateSettings(request, mailServerSettings);
		
		redirectAttributes.addFlashAttribute("message", "Mail Templates settings have been saved.");
		return "redirect:/setting/list";
		
	}
	
	
	
	
	
	
	
	
	
}



