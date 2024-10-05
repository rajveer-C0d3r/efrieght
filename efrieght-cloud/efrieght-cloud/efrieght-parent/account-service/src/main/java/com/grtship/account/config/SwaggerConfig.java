/**
 * 
 */
package com.grtship.account.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;

/**
 * @author Ajay
 *
 */
@Configuration
public class SwaggerConfig {

	@Value("${swagger.title}")
	private String title;
	@Value("${swagger.description}")
	private String description;
	@Value("${swagger.version}")
	private String version;
	@Value("${swagger.termsOfServiceUrl}")
	private String termsOfServiceUrl;
	@Value("${swagger.contactName}")
	private String contactName;
	@Value("${swagger.license}")
	private String license;
	@Value("${swagger.licenseUrl}")
	private String licenseUrl;

	@Value("${swagger.api.name}")
	private String name;
	@Value("${swagger.api.keyname}")
	private String keyname;
	@Value("${swagger.api.passAs}")
	private String passAs;

	@Value("${swagger.authorizationScope.scope}")
	private String scope;
	@Value("${swagger.authorizationScope.description}")
	private String scopeDescription;

	private ApiKey apiKey() {
		return new ApiKey(name, keyname, passAs);
	}

	private SecurityContext secuirtyContext() {
		return SecurityContext.builder().securityReferences(defaultAuth()).build();
	}

	private List<SecurityReference> defaultAuth() {
		AuthorizationScope authorizationScope = new AuthorizationScope(scope, scopeDescription);
		AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
		authorizationScopes[0] = authorizationScope;
		return Arrays.asList(new SecurityReference(name, authorizationScopes));
	}

	@SuppressWarnings("deprecation")
	private ApiInfo apiInfo() {
		return new ApiInfo(title, description, version, termsOfServiceUrl, contactName, license, licenseUrl);
	}

	@Bean
	public Docket api() {
		return new Docket(DocumentationType.SWAGGER_2)
				.apiInfo(apiInfo())
				.securityContexts(Arrays.asList(secuirtyContext()))
				.securitySchemes(Arrays.asList(apiKey())).select()
				.apis(RequestHandlerSelectors.any())
				.paths(PathSelectors.any())
				.build();
	}
}
