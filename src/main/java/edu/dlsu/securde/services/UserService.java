package edu.dlsu.securde.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.dlsu.securde.model.User;
import edu.dlsu.securde.repositories.UserRepository;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;

	public List<User> getUser(String username) {
		List<User> users = userRepository.findExistingUser(username);
		return users;
	}

	public List<User> findByUserType(Integer type){
		List<User> users = userRepository.findUserByUserType(type);
		return users;
	}

	public List<User> findUserById (Integer id){
		List<User> user = userRepository.findUserById(id);
		return user;
	}
	
	public void updateUser(User user){
		userRepository.save(user);
	}
	
}
