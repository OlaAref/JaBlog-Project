package com.olaaref.jablog.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.olaaref.jablog.entity.AuthenticationType;
import com.olaaref.jablog.entity.User;

@Repository
public interface UserRepository extends PagingAndSortingRepository<User, Integer> {

	public User findByEmail(@Param("email") String email);
	public User findByVerificationCode(String code);
	public User findByResetPasswordToken(String resetPasswordToken);
	public Long countById(Integer id);
	
	@Query("select u from User u where concat(u.email, ' ', u.firstName, ' ', u.lastName, ' ', u.role.name) like %?1%")
	public Page<User> findAll(String keyword, Pageable pageable);
	
	@Query("update User u set u.enabled = ?2 where u.id = ?1")
	@Modifying//for update DML
	public void updateEnabledStatus(Integer id, boolean status);
	
	@Query("update User u set u.enabled = true, u.verificationCode = null where u.id = ?1")
	@Modifying
	public void enableAccount(Integer id);
	
	@Query("update User u set u.authenticationType = ?1 where u.id = ?2")
	@Modifying
	public void updateAuthenticationType(AuthenticationType authType, Integer id);
}
