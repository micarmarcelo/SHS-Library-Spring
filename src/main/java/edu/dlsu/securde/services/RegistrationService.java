package edu.dlsu.securde.services;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import edu.dlsu.securde.model.Role;
import edu.dlsu.securde.model.User;
import edu.dlsu.securde.repositories.RoleRepository;
import edu.dlsu.securde.repositories.UserRepository;

@Service
public class RegistrationService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	public void addUser(User user) {
		
		// Role is USER
		if (user.getUserType() == 4) {
			user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
			user.setSecretAnswer(bCryptPasswordEncoder.encode(user.getSecretAnswer()));
			Role userRole = roleRepository.findByRole("USER");
			user.setRoles(new HashSet<Role>(Arrays.asList(userRole)));
		}
		// Role is Manager
		else if (user.getUserType() == 2) {
			user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
			Role userRole = roleRepository.findByRole("MANAGER");
			user.setRoles(new HashSet<Role>(Arrays.asList(userRole)));
		}
		// Role is Staff
		else if (user.getUserType() == 3) {
			user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
			Role userRole = roleRepository.findByRole("STAFF");
			user.setRoles(new HashSet<Role>(Arrays.asList(userRole)));
		} // Role is ADMIN
		else if (user.getUserType() == 1) {
			user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
			user.setSecretAnswer(bCryptPasswordEncoder.encode(user.getSecretAnswer()));
			Role userRole = roleRepository.findByRole("ADMIN");
			user.setRoles(new HashSet<Role>(Arrays.asList(userRole)));
		}
		userRepository.save(user);
	}

	public List<User> checkExistingUser(String name) {
		List<User> checkUser = userRepository.findExistingUser(name);
		return checkUser;
	}

}
