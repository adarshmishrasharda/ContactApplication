package com.smart.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.smart.entities.Contact;
import com.smart.entities.User;

public interface ContactRepository extends JpaRepository<Contact, Integer> {

	//paginatation ....
	
	//this we use when we do not want to implement pagination
//	@Query("from Contact as c where c.user.id =:userid")
//	public List<Contact> findContactsByUser(@Param("userid") int userid);
//	
	
	
	@Query("from Contact as c where c.user.id =:userid")
	public Page<Contact> findContactsByUser(@Param("userid") int userid, Pageable pageable);
	//pageable object has two information -->1. current page---> mean "page" variable in our case
	//2.Contact per page ---> in our case "n" variable.

	//search functionality
	public List<Contact> findByNameContainingAndUser(String keyword, User user);



}
