package com.moniepoint.analytic_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class AnalyticApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(AnalyticApiApplication.class, args);
	}

}
