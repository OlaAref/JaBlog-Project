package com.olaaref.jablog.user;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import com.olaaref.jablog.entity.Role;
import com.olaaref.jablog.entity.User;
import com.olaaref.jablog.repository.UserRepository;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class UserRepositoryTest {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private TestEntityManager entityManager;
	
	@Test
	public void addUsersTest() {
		Role admin = entityManager.find(Role.class, 1);
		Role bloggrt = entityManager.find(Role.class, 2);
		
		User user1 = new User("admin", "admin", "admin@mail.com", "123");
		user1.setRole(admin);
		User user2 = new User("blogger", "blogger", "blogger@mail.com", "123");
		user2.setRole(bloggrt);
		
		User saved1 = userRepository.save(user1);
		User saved2 = userRepository.save(user2);
		
		assertThat(saved1.getId()).isGreaterThan(0);
		assertThat(saved2.getId()).isGreaterThan(0);
		
	}
	
	@Test
	public void testListAllUsers() {
		List<User> users = (List<User>) userRepository.findAll();
		users.forEach(System.out::println);
	}
	
	
	@Test
	public void testGetUserById() {
		User user = userRepository.findById(1).get();
		System.out.println(user);
		
		assertThat(user).isNotNull();
	}
	
	@Test
	public void testUpdateUser() {
		User user = userRepository.findById(1).get();
		user.setEnabled(true);
		
		User saved = userRepository.save(user);
		
		assertThat(saved).isNotNull();
	}
	
	@Test
	public void testUpdateUsersRole() {
		User user = userRepository.findById(2).get();
		user.setRole(new Role(2));
		
		User saved = userRepository.save(user);
		
		assertThat(saved).isNotNull();
	}
	
}














