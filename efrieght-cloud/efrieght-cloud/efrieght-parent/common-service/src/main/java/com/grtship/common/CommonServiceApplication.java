package com.grtship.common;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan({ "com.**" })
@EnableFeignClients
@EnableAutoConfiguration
public class CommonServiceApplication {
	public static void main(String[] args) {
		SpringApplication.run(CommonServiceApplication.class, args);
	}
}
