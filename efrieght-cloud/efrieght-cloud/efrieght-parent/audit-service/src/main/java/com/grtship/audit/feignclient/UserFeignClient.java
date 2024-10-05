/**
 * 
 */
package com.grtship.audit.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.grtship.common.interceptor.FeignInterceptorConfig;
//import com.grtship.client.interceptor.FeignInterceptorConfig;
import com.grtship.core.dto.UserDetailDto;

/**
 * @author ER Ajay Sharma
 * 
 *
 */
@FeignClient(value = "gateway-service" , configuration = { FeignInterceptorConfig.class } )
public interface UserFeignClient {

	@GetMapping(value = "user/auth/getUserDetails")
	public UserDetailDto getUserDetails(@RequestParam String username);
}
