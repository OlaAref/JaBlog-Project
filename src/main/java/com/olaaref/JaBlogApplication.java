package com.olaaref;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan({"com.olaaref"})
public class JaBlogApplication {

	public static void main(String[] args) {
		SpringApplication.run(JaBlogApplication.class, args);
	}

}
