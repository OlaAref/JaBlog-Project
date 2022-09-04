package com.olaaref.jablog.entity.setting;

import java.util.List;

import com.olaaref.jablog.entity.Setting;

public class GeneralSettingBag extends SettingBag {

	public GeneralSettingBag(List<Setting> settingList) {
		super(settingList);
	}
	
	public void updateSiteLogo(String value) {
		super.setValue("SITE_LOGO", value);
	}

}
