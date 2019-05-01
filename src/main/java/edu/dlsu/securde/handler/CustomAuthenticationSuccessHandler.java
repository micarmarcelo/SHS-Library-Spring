package edu.dlsu.securde.handler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import edu.dlsu.securde.model.User;
import edu.dlsu.securde.services.LoggingService;
import edu.dlsu.securde.services.UserService;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

	@Autowired
	UserService userService;
	
	@Autowired
	LoggingService loggingService;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		
		User user = userService.getUser(request.getParameter("username")).get(0);

		if(user.getLocked()==1){
			loggingService.logInfo(user.getUsername()+ "tried to log in but his/her account is locked.");
			response.sendRedirect("/loginLocked");
		}
		else{
			loggingService.logInfo(user.getUsername()+ "successfully logged in to homepage.");
			//Reset wrong attempts..
			user.setWrongAttempts(0);
			userService.updateUser(user);
			response.sendRedirect("/startUp");
		}
	}
}
