package com.olaaref.jablog.repository;

import java.util.Date;
import java.util.logging.Logger;

import javax.persistence.EntityManager;

import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.olaaref.jablog.entity.LoginsPersistent;

@Repository("persistentTokenRepository")
@Transactional
public class PersistentTokenDaoImpl implements PersistentTokenRepository {
	private static final Logger logger = Logger.getLogger(PersistentTokenDaoImpl.class.getName());
	
	@Autowired
	private EntityManager entityManager;

	@Override
	public void createNewToken(PersistentRememberMeToken token) {
		//create new loginsPersistent using the token
		LoginsPersistent login = new LoginsPersistent();
		login.setSeries(token.getSeries());
		login.setUsername(token.getUsername());
		login.setToken(token.getTokenValue());
		login.setLastUsed(token.getDate());
		
		//open new session to save the login 
		Session session = entityManager.unwrap(Session.class);
		session.save(login);
		session.close();
		
		logger.info("PersistentTokenRepository ==> Save User : " + login.getUsername());
	}

	@Override
	public void updateToken(String series, String tokenValue, Date lastUsed) {
		//open new session to get the login
		Session session = entityManager.unwrap(Session.class);
		
		//get the login from the database
		LoginsPersistent login = session.get(LoginsPersistent.class, series);
		
		//update the login
		login.setToken(tokenValue);
		login.setLastUsed(lastUsed);
		
		//save the updated login to database
		session.update(login);
		session.close();
		
		
		logger.info("PersistentTokenRepository ==> Update User : " + login.getUsername());
	}

	@Override
	public PersistentRememberMeToken getTokenForSeries(String seriesId) {
		//open new session
		Session session = entityManager.unwrap(Session.class);
		
		//get the login from the database
		LoginsPersistent login = session.get(LoginsPersistent.class, seriesId);
		
		if(login != null) {
			logger.info("PersistentTokenRepository ==> Get User : " + login.getUsername());
			return new PersistentRememberMeToken(login.getUsername(), login.getSeries(), login.getToken(), login.getLastUsed());
		}
		else {
			logger.info("PersistentTokenRepository ==> No User Exist.");
			return null;
		}
		
	}

	@Override
	public void removeUserTokens(String username) {
		//Open session
		Session session = entityManager.unwrap(Session.class);
		
		logger.info("PersistentTokenRepository ==> Remove User : " + username);
		
		session
			.createQuery("delete from LoginsPersistent where username=:name")
			.setParameter("name", username)
			.executeUpdate();

	}

}
