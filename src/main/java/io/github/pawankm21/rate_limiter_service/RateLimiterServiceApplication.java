package io.github.pawankm21.rate_limiter_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;


/**
 * @author Spencer Gibb
 */
@SpringBootConfiguration
@EnableAutoConfiguration
public class RateLimiterServiceApplication {


	public static void main(String[] args) {
		SpringApplication.run(RateLimiterServiceApplication.class, args);
	}




}