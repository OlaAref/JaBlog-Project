package com.olaaref.jablog.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.olaaref.jablog.oauth.DatabaseLoginSuccessHandler;
import com.olaaref.jablog.oauth.JaBlogOAuth2UserService;
import com.olaaref.jablog.oauth.OAuth2LoginSuccessHandler;
import com.olaaref.jablog.repository.PersistentTokenDaoImpl;
import com.olaaref.jablog.security.JablogUserDetailsService;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter{

	@Autowired
	private JablogUserDetailsService userDetailsService;
	
	@Autowired
	private JaBlogOAuth2UserService oauth2UserService;
	
	@Autowired
	private OAuth2LoginSuccessHandler oauth2LoginSuccessHandler;
	
	@Autowired 
	private DatabaseLoginSuccessHandler databaseLoginSuccessHandler;
	
	@Autowired
	private PersistentTokenDaoImpl persistentTokenRepository;
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
				.antMatchers("/css/**", "/js/**", "/image/**", "/fonts/**", "/webfonts/**", "/webjars/**", "/richText/**", "/countries/list").permitAll()
				.antMatchers("/user-photos/**", "/category-photos/**", "/post-photos/**", "/site-logo/**").permitAll()
				.antMatchers("/", "/signup", "/about", "/contact", "/blogger/**", "/users/check_email", "/password/**").permitAll()
				.antMatchers("/view/**", "/views/anonymous/**", "/comments/save", "/comments/saveReply").permitAll()
				.antMatchers("/user/**", "/setting/**", "/category/**").hasAuthority("admin")
				.antMatchers("/account/**", "/post/**", "/views/authenticated/**").hasAnyAuthority("admin", "blogger")
				
				.anyRequest().authenticated()
			.and()
			.formLogin()
				.loginPage("/login")
				.usernameParameter("email")
				.successHandler(databaseLoginSuccessHandler)
				.permitAll()
			.and()
			.oauth2Login()
				.loginPage("/login")
				.userInfoEndpoint()
				.userService(oauth2UserService)
				.and()
				.successHandler(oauth2LoginSuccessHandler)
			.and()
			.logout()
				.permitAll()
			.and()
			.rememberMe()
				.key("rem-me-key")//to make remember-me cookies survive after restarting server
				.tokenRepository(persistentTokenRepository)
				.tokenValiditySeconds(1*24*60*60)
				.userDetailsService(userDetailsService)
			.and()
			.sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.ALWAYS);
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(authenticationProvider());
	}

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder(); 
	}
	
	
    @Bean 
	public DaoAuthenticationProvider authenticationProvider() {
	  
	DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
	authenticationProvider.setPasswordEncoder(passwordEncoder());
	authenticationProvider.setUserDetailsService(userDetailsService);
	  
	return authenticationProvider; 
   }
	 
}
