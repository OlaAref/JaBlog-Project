package com.olaaref.jablog.entity.setting;

import java.util.List;

import com.olaaref.jablog.entity.Setting;

public class SettingBag {

	private List<Setting> settingsList;
	
	public SettingBag(List<Setting> settingList) {
		this.settingsList = settingList;
	}
	
	//utils :
	//get the setting
	//get the value of the setting
	//set the value of the setting
	//get all setting
	
	public Setting getSetting(String key) {
		int index = settingsList.indexOf(new Setting(key));
		if(index >= 0) {
			return settingsList.get(index);
		}
		
		return null;
	}
	
	public String getValue(String key) {
		Setting setting = getSetting(key);
		if(setting != null) {
			return setting.getValue();
		}
		
		return null;
	}
	
	public void setValue(String key, String value) {
		Setting setting = getSetting(key);
		if(setting != null && value != null) {
			setting.setValue(value);
		}
	}
	
	public List<Setting> getAllSetting(){
		return this.settingsList;
	}
}
