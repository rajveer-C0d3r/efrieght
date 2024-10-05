/**
 * 
 */
package com.grt.elogfrieght.services.user.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.grt.elogfrieght.services.user.audit.SpringSecurityAuditorAware;
import com.grt.elogfrieght.services.user.filter.JWTAuthorizationFilter;
import com.grt.elogfrieght.services.user.filter.ServiceContextFilter;
import com.grt.elogfrieght.services.user.secuirty.DomainUserDetailsService;

/**
 * @author ER Ajay Sharma
 *
 */
@Configuration
@EnableWebSecurity(debug = true)
@EnableGlobalMethodSecurity(prePostEnabled = true, proxyTargetClass = true, securedEnabled = true)
public class WebSecurity extends WebSecurityConfigurerAdapter {

	String[] urls = { "/auth/login", "/auth/getUserDetails", "/actuator/**", "/management/**", "/v2/**", "/v3/**",
			"/swagger-resources/**", "/webjars/**", "/swagger-ui/**", "/api/account/reset-password/init",
			"/api/account/reset-password/finish"};

	@Autowired
	private DomainUserDetailsService userDetailsService;

	@Bean
	public AuthenticationManager getAuthenticationManager() throws Exception {
		return super.authenticationManagerBean();
	}

	@Bean
	public GrantedAuthorityDefaults grantedAuthorityDefaults() {
		return new GrantedAuthorityDefaults("");
	}

	@Bean
	public JWTAuthorizationFilter getJwtAuth() throws Exception {
		return new JWTAuthorizationFilter(getAuthenticationManager());
	}

	@Bean
	public ServiceContextFilter getServiceContextFilter() {
		return new ServiceContextFilter();
	}

	@Bean
	public static BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public AuditorAware<String> auditAware() {
		return new SpringSecurityAuditorAware();
	}

	@Override
	public void configure(org.springframework.security.config.annotation.web.builders.WebSecurity web)
			throws Exception {
		web.ignoring().antMatchers(HttpMethod.OPTIONS);
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.anonymous().disable().csrf().disable().requestMatchers().antMatchers("/user").and().authorizeRequests()
				.antMatchers(urls).permitAll().antMatchers(HttpMethod.OPTIONS, "/**").permitAll().anyRequest()
				.authenticated().and()
//		.addFilter(getJwtAuth())
				.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
	}

}
