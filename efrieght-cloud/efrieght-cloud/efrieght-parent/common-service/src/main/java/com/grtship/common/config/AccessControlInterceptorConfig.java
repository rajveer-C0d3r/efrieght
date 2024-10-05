package com.grtship.common.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.grtship.common.interceptor.AccessControlInterceptor;

@Configuration
public class AccessControlInterceptorConfig extends WebMvcConfigurerAdapter {
	@Autowired
	AccessControlInterceptor accessControlInterceptor;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(accessControlInterceptor).addPathPatterns("/**");
	}
}
