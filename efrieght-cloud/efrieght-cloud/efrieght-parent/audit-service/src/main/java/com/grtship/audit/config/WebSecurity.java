/**
 * 
 */
package com.grtship.audit.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.config.http.SessionCreationPolicy;

import com.grtship.audit.filter.JWTAuthorizationFilter;
import com.grtship.audit.filter.ServiceContextFilter;
import com.grtship.audit.spring.audit.SpringSecurityAuditorAware;

/**
 * @author ER Ajay Sharma
 *
 */
@Configuration
@EnableWebSecurity(debug = true)
@EnableGlobalMethodSecurity(prePostEnabled = true, proxyTargetClass = true, securedEnabled = true)
public class WebSecurity extends WebSecurityConfigurerAdapter {

//	String[] urls = {"/actuator/**","/management/**","/v2/api-docs","/swagger-resources/**","/webjars/**","/swagger-ui/**" };
	String[] urls = {"/swagger-resources/**","/webjars/**" ,"/v2/**","/v3/**","/actuator/info","/swagger-ui/**"};
	
	@Bean
	public AuditorAware<String> auditAware() {
		return new SpringSecurityAuditorAware();
	}

	@Bean
	public AuthenticationManager getAuthenticationManager() throws Exception {
		return super.authenticationManagerBean();
	}

	@Bean
	public JWTAuthorizationFilter getJwtAuth() throws Exception {
		return new JWTAuthorizationFilter(getAuthenticationManager());
	}

	@Bean
	public GrantedAuthorityDefaults grantedAuthorityDefaults() {
		return new GrantedAuthorityDefaults("");
	}

	@Bean
	public ServiceContextFilter getServiceContextFilter() {
		return new ServiceContextFilter();
	}

	@Override
	public void configure(org.springframework.security.config.annotation.web.builders.WebSecurity web)
			throws Exception {
		web.ignoring().antMatchers(HttpMethod.OPTIONS);
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.anonymous().disable()
		.csrf().disable()
		.requestMatchers().antMatchers("/audit")
		.and()
		.authorizeRequests()
		.antMatchers(urls).permitAll()
		.antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
		.anyRequest().authenticated().and()
		.addFilter(getJwtAuth())
		.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
	}

}
