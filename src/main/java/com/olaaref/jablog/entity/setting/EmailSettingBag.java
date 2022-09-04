package com.olaaref.jablog.entity.setting;

import java.util.List;

import com.olaaref.jablog.entity.Setting;

public class EmailSettingBag extends SettingBag {

	public EmailSettingBag(List<Setting> settingList) {
		super(settingList);
	}
	
	public String getHost() {
		return super.getValue("MAIL_HOST");
	}
	
	public int getPort() {
		return Integer.parseInt(super.getValue("MAIL_PORT"));
	}
	
	public String getUsername() {
		return super.getValue("MAIL_USERNAME");
	}

	public String getPassword() {
		return super.getValue("MAIL_PASSWORD");
	}
	
	public String getSenderName() {
		return super.getValue("MAIL_SENDER_NAME");
	}
	
	public String getMailFrom() {
		return super.getValue("MAIL_FROM");
	}
	
	public String getSmtpAuth() {
		return super.getValue("SMTP_AUTH");
	}
	
	public String getSmtpSecured() {
		return super.getValue("SMTP_SECURED");
	}
	
	public String getMailVerifySubject() {
		return super.getValue("USER_VERIFY_SUBJECT");
	}
	
	public String getMailVerifyContent() {
		return super.getValue("USER_VERIFY_CONTENT");
	}
}
















