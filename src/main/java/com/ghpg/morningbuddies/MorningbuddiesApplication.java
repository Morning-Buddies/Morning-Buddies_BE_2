package com.ghpg.morningbuddies;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class MorningbuddiesApplication {

	public static void main(String[] args) {
		SpringApplication.run(MorningbuddiesApplication.class, args);
	}

}
