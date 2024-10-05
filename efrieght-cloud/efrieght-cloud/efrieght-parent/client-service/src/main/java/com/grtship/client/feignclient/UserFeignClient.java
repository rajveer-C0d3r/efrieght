/**
 * 
 */
package com.grtship.client.feignclient;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.grtship.common.interceptor.FeignInterceptorConfig;
import com.grtship.core.dto.EmailPresentDto;
import com.grtship.core.dto.UserDetailDto;

import io.swagger.v3.oas.annotations.parameters.RequestBody;

/**
 * @author ER Ajay Sharma
 * 
 *
 */
@FeignClient(value = "gateway-service", configuration = { FeignInterceptorConfig.class })
public interface UserFeignClient {

	@GetMapping(value = "user/auth/getUserDetails")
	public UserDetailDto getUserDetails(@RequestParam String username);
	
	@PostMapping("user/api/user/checkEmailPresent")
	public EmailPresentDto checkEmailPresent(@RequestBody List<String> emailIds);

	@GetMapping("authorisation/api/authorisation/isAuthorisationRequired")
	Boolean isApprovalRequired(@RequestBody Object object);
}
