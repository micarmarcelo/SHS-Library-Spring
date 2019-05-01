package edu.dlsu.securde.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class LoggingService {
	
	private static final Logger log = LoggerFactory.getLogger(LoggingService.class);
	
	public void logInfo(String message){
		log.info(message);
	}
	
	public void logError(String message){
		log.error(message);
	}
}
