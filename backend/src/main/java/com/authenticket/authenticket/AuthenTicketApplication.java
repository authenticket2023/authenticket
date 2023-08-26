package com.authenticket.authenticket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(ConfigProperties.class)
public class AuthenTicketApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuthenTicketApplication.class, args);
	}

}
