/**
 * 
 */
package com.grtship.notification.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import lombok.Data;

/**
 * @author Ajay
 *
 */
@Configuration("mail-configuration")
@Component
@Data
public class MailConfig {
	@Value("${spring.mail.base-url-client}")
	private String baseUrlClient;
	
	@Value("${spring.mail.base-url-admin}")
	private String baseUrlAdmin;
	
	@Value("${spring.mail.from}")
	private String from;
}
