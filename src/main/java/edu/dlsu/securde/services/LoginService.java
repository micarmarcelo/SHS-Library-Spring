package edu.dlsu.securde.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.dlsu.securde.model.User;
import edu.dlsu.securde.repositories.UserRepository;

@Service
public class LoginService {
	
	@Autowired
	private UserRepository userRepository;
	
	public List<User> getAllUsers(){
		List<User> users = new ArrayList<User>();
		userRepository.findAll().forEach(users::add);
		return users;
	}
	
	public List<User> getAllStudentsEmployees(){
		List<User> users = userRepository.findUserByUserType(0);
		return users;
	}
	
	public List<User> checkUsernameAndPassword(String username, String password){
		List<User> users = userRepository.findUserByUsernameAndPassword(username, password);
		return users;
	}
	
}
