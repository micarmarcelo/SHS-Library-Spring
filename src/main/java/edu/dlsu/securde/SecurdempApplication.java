package edu.dlsu.securde;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.boot.autoconfigure.domain.EntityScan;
//import org.springframework.context.annotation.ComponentScan;
//import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
//@ComponentScan({"controller","configuration"})
//@EntityScan(basePackages = {"model"} )
//@EnableJpaRepositories(basePackages = {"repositories"})
public class SecurdempApplication {

	public static void main(String[] args) {
		SpringApplication.run(SecurdempApplication.class, args);
	}
}
