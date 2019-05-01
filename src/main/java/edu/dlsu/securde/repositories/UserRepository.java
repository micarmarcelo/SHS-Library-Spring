package edu.dlsu.securde.repositories;

import java.util.ArrayList;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import edu.dlsu.securde.model.User;

public interface UserRepository extends CrudRepository<User, Integer>{

	@Query ("SELECT u " +
			"FROM User u " +
			"WHERE u.id = :id")
	ArrayList<User> findUserById(@Param("id") Integer id);
	
	@Query ("SELECT u " +
			"FROM User u " +
			"WHERE u.userType = :userType")
	ArrayList<User> findUserByUserType(@Param("userType") Integer userType);
	
	@Query ("SELECT u " +
			"FROM User u " +
			"WHERE u.username = :username "
			+ "AND u.password = :password")
	ArrayList<User> findUserByUsernameAndPassword(@Param("username") String username, @Param("password") String password);
	
	@Query ("SELECT u " +
			"FROM User u " +
			"WHERE u.username = :username")
	ArrayList<User> findExistingUser(@Param("username") String username);
	
}
