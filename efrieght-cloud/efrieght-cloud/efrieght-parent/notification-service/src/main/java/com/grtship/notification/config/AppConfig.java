/**
 * 
 */
package com.grtship.notification.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;

import lombok.var;

/**
 * @author Ajay
 *
 */
@Configuration
public class AppConfig {
	@Bean
	public ResourceBundleMessageSource messageSource() {
		var source = new ResourceBundleMessageSource();
		source.setBasenames("i18n/messages");
		source.setUseCodeAsDefaultMessage(true);
		return source;
	}
}