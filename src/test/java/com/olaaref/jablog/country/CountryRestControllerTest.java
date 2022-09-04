package com.olaaref.jablog.country;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.olaaref.jablog.entity.Country;
import com.olaaref.jablog.repository.CountryRepository;

@SpringBootTest
@AutoConfigureMockMvc
public class CountryRestControllerTest {

	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private CountryRepository countryRepository;
	
	@Test
	@WithMockUser(username = "fake@mail.com", password = "fakePassword", roles = "admin")
	public void testListCountries() throws Exception {
		String url = "/countries/list";
		mockMvc
			//apply GET mapping request
			.perform(get(url))
			//expect '200' OK status as response
			.andExpect(status().isOk())
			//print the response
			.andDo(print());	
	}
	
	@Test
	@WithMockUser(username = "fake@mail.com", password = "fakePassword", roles = "admin")
	public void testSaveCountry() throws Exception {
		
		String url = "/countries/save";
		Country country = new Country("Test Country", "TC");
		
		//convert the Country POJO file to JSON
		String jsonRequest = objectMapper.writeValueAsString(country);
		
		mockMvc
			.perform(
					//apply POST mapping request
					post(url)
					//set the content type of request to json
					.contentType("application/json")
					//attach the json file to the request
					.content(jsonRequest)
					//send CSRF cause it is POST
					.with(csrf())
					)
			//expect '200' OK status as response
			.andExpect(status().isOk())
			//print the response
			.andDo(print());	
	}
	
	@Test
	@WithMockUser(username = "fake@mail.com", password = "fakePassword", roles = "admin")
	public void testUpdateCountry() throws Exception {
		
		String url = "/countries/save";
		Country country = countryRepository.findById(251).get();
		country.setIso3("TCO");
		
		//convert the Country POJO file to JSON
		String jsonRequest = objectMapper.writeValueAsString(country);
		
		mockMvc
			.perform(
					//apply POST mapping request
					post(url)
					//set the content type of request to json
					.contentType("application/json")
					//attach the json file to the request
					.content(jsonRequest)
					//send CSRF cause it is POST
					.with(csrf())
					)
			//expect '200' OK status as response
			.andExpect(status().isOk())
			//print the response
			.andDo(print());	
	}
	
	@Test
	@WithMockUser(username = "fake@mail.com", password = "fakePassword", roles = "admin")
	public void testDeleteCountry() throws Exception {
		
		String url = "/countries/delete/251";
		
		mockMvc
			.perform(
					//apply DELETE mapping request
					delete(url)
					//send CSRF cause it is DELETE
					.with(csrf())
					)
			//expect '200' OK status as response
			.andExpect(status().isOk());	
	}
}
















