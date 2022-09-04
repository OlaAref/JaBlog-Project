package com.olaaref.jablog.setting;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.olaaref.jablog.entity.Setting;
import com.olaaref.jablog.entity.setting.SettingCategory;
import com.olaaref.jablog.gcp.GoogleCloudUtility;
import com.olaaref.jablog.repository.SettingRepository;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class SettingRepositoryTest {
	
	@Autowired
	private SettingRepository settingRepository;
	
	@Test
	public void createGeneralSettings() {
		Setting setting1 = new Setting("SITE_NAME", "JaBlog", SettingCategory.GENERAL);
		Setting setting2 = new Setting("SITE_LOGO", "/site-logo/JaBlog.png", SettingCategory.GENERAL);
		Setting setting3 = new Setting("COPYRIGHT", "Copyright &copy; <b>Ola Aref</b> 2022", SettingCategory.GENERAL);
		
		settingRepository.saveAll(List.of(setting1, setting2, setting3));
	}
	
	@Test
	public void createMailServerSettings() {
		Setting setting1 = new Setting("MAIL_FROM", "jablog.team", SettingCategory.MAIL_SERVER);
		Setting setting2 = new Setting("MAIL_HOST", "smtp.gmail.com", SettingCategory.MAIL_SERVER);
		Setting setting3 = new Setting("MAIL_PASSWORD", "zfckfzlivfpviwrr", SettingCategory.MAIL_SERVER);
		Setting setting4 = new Setting("MAIL_PORT", "587", SettingCategory.MAIL_SERVER);
		Setting setting5 = new Setting("MAIL_SENDER_NAME", "JaBlog Site", SettingCategory.MAIL_SERVER);
		Setting setting6 = new Setting("MAIL_USERNAME", "mintshop.team@gmail.com", SettingCategory.MAIL_SERVER);
		Setting setting7 = new Setting("SMTP_AUTH", "true", SettingCategory.MAIL_SERVER);
		Setting setting8 = new Setting("SMTP_SECURED", "true", SettingCategory.MAIL_SERVER);
		
		settingRepository.saveAll(List.of(setting1, setting2, setting3, setting4, setting5, setting6, setting7, setting8));
	}
	
	@Test
	public void createMailTemplatesSettings() {
		Setting setting1 = new Setting("USER_VERIFY_CONTENT", "<div style=\"text-align: center;\"><span style=\"font-size: 24px; font-weight: var(--bs-body-font-weight); text-align: var(--bs-body-text-align);\">&nbsp;We're thrilled to have you here! Get ready to dive into your new account.&nbsp;</span></div><h1 style=\"text-align: center;\"><b><br></b></h1><h1 style=\"text-align: center;\"><b>\r\n"
				+ " Welcome [[name]]!</b></h1><div style=\"text-align: center;\"><span style=\"font-size: 18px;\"><br></span></div>\r\n"
				+ " <span style=\"font-size: 18px;\"><div style=\"text-align: center;\"><span style=\"font-weight: var(--bs-body-font-weight); text-align: var(--bs-body-text-align);\">We're excited to have you get started. First, you need to confirm your account. Just press the button below.</span></div></span><div><div style=\"text-align: center;\"><span style=\"font-size: 18px;\"><br></span></div><h2 style=\"text-align: center;\">\r\n"
				+ " \r\n"
				+ " <a href=\"[[URL]]\" target=\"_self\">Confirm Account</a></h2><div style=\"text-align: center;\"><br></div><div style=\"text-align: center;\">If that doesn't work, copy and paste the following link in your browser:</div><div style=\"text-align: center;\"><br></div><div style=\"text-align: center;\"><a href=\"[[URL]]\" target=\"_self\" style=\"font-size: var(--bs-body-font-size); font-weight: var(--bs-body-font-weight); text-align: var(--bs-body-text-align);\">[[URL]]</a><a href=\"[[URL]]\" target=\"_self\"><br></a></div><div><a href=\"[[URL]]\" target=\"_self\"><font color=\"#0000ff\"></font></a></div><div style=\"text-align: center;\"><br></div><div style=\"text-align: center;\">If you have any questions, just reply to this email—we're always happy to help out.</div><div style=\"text-align: center;\">Cheers,</div><div style=\"text-align: center;\">JaBlog Site</div></div>", SettingCategory.MAIL_TEMPLATES);
		Setting setting2 = new Setting("USER_VERIFY_SUBJECT", "Please Verify your registration", SettingCategory.MAIL_TEMPLATES);
		
		settingRepository.saveAll(List.of(setting1, setting2));
	}
	
	@Test
	public void deleteLogoTest() throws FileNotFoundException, IOException {
		GoogleCloudUtility utility = new GoogleCloudUtility();
		boolean deleted = utility.deleteObject("site-logo/");
		System.out.println(deleted);
	}
	
	@Test
	public void listFilesTest() throws FileNotFoundException, IOException {
		GoogleCloudUtility utility = new GoogleCloudUtility();
		List<String> files = utility.listFolder("site-logo/");
		for (String name : files) {
			System.out.println(name);
		}
	}
	
	@Test
	public void deleteFolderTest() throws FileNotFoundException, IOException {
		GoogleCloudUtility utility = new GoogleCloudUtility();
		utility.deleteFolder("site-logo/");
		
	}

}
