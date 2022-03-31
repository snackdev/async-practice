package com.example.asyncpractice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class AsyncPracticeApplication {

	public static void main(String[] args) {
		SpringApplication.run(AsyncPracticeApplication.class, args);
	}

}
