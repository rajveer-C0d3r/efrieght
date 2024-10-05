package com.grtship.authorisation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableEurekaClient
@ComponentScan({"com.**"})
@EnableFeignClients
@EnableJpaAuditing(auditorAwareRef = "auditAware")
@EnableAutoConfiguration(exclude = {SecurityAutoConfiguration.class})
public class AuthorisationServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuthorisationServiceApplication.class, args);
	}

}
