package com.ghpg.morningbuddies.global.config;

import java.io.IOException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

@Configuration
public class FirebaseConfig {

	@Bean
	public FirebaseApp firebaseApp() throws IOException {
		FirebaseOptions options = FirebaseOptions.builder()
			.build();
		return FirebaseApp.initializeApp(options);
	}
}