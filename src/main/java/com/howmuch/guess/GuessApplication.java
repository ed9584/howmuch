package com.howmuch.guess;

import com.howmuch.guess.config.BackendProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(BackendProperties.class)
public class GuessApplication {

	public static void main(String[] args) {
		SpringApplication.run(GuessApplication.class, args);
	}

}
