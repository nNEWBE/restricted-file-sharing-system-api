package com.example.restricted_file_sharing_system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RestrictedFileSharingSystemApplication {

	public static void main(String[] args) {
		System.setProperty("spring.classformat.ignore", "true");
		SpringApplication.run(RestrictedFileSharingSystemApplication.class, args);
	}

}
