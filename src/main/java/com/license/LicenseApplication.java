package com.license;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;


@SpringBootApplication
@EnableCaching
public class LicenseApplication {

	public static void main(String[] args) {

		SpringApplication.run(LicenseApplication.class, args);
	}

}
