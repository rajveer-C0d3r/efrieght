package com.grtship.mdm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan({ "com.**" })
@EnableFeignClients
@EnableJpaAuditing(auditorAwareRef = "auditAware")
@EnableSwagger2
//@EnableAutoConfiguration(exclude = SecurityAutoConfiguration.class)
@EnableAutoConfiguration
public class MdmServiceApplication {
	public static void main(String[] args) {
		SpringApplication.run(MdmServiceApplication.class, args);
	}

}
