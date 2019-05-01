package edu.dlsu.securde.handler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import edu.dlsu.securde.model.User;
import edu.dlsu.securde.services.LoggingService;
import edu.dlsu.securde.services.UserService;

@Component("customAuthenticationFailureHandler")
public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

	private String DEFAULT_FAILURE_URL = "/login?error";

	@Autowired
	private UserService userService;
	
	@Autowired
	private LoggingService loggingService;

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {

		setDefaultFailureUrl(DEFAULT_FAILURE_URL);
		super.onAuthenticationFailure(request, response, exception);

		if (exception instanceof BadCredentialsException) {
			lockUser(request.getParameter("username"));
		}

	}

	private void lockUser(String username) {
		
		System.out.println(username);
		
		//Username found..
		if(userService.getUser(username).size()>0){
		
			User lockUser = userService.getUser(username).get(0);
			lockUser.setWrongAttempts(lockUser.getWrongAttempts() + 1);
	
			// checking..
			if (lockUser.getWrongAttempts() == 5 && lockUser.getUserType() == 4) {
				// User now locked..
				lockUser.setLocked(1);
				// reset..
				lockUser.setWrongAttempts(0);
				userService.updateUser(lockUser);
				System.out.println("Account Locked!");
				loggingService.logInfo(lockUser.getUsername()+" account is locked due to 5 times of incorrect password login.");
				
				setDefaultFailureUrl("/loginLocked");
			} else if (lockUser.getWrongAttempts() == 3
					&& (lockUser.getUserType() == 1 || lockUser.getUserType() == 2 || lockUser.getUserType() == 3)) {
				// User now locked..
				lockUser.setLocked(1);
				// reset..
				lockUser.setWrongAttempts(0);
				userService.updateUser(lockUser);
				System.out.println("Account Locked!");
				setDefaultFailureUrl("/loginLocked");
				loggingService.logInfo(lockUser.getUsername()+" account is locked due to 3 times of incorrect password login.");
			} else {
				System.out.println("Account Lock Plus 1!");
				loggingService.logInfo("Correct username login, "+lockUser.getUsername()+ " but with incorrect password. Plus 1 wrong login attempt.");
				userService.updateUser(lockUser);
			}
		}
		else{
			loggingService.logInfo("Incorrect Login by anonymous. Redirecting back to login page.");
		}
	}

}