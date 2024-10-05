/**
 * 
 */
package com.grtship.authorisation.feignclient;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.grtship.common.interceptor.FeignInterceptorConfig;
import com.grtship.core.dto.ApprovalRequestDTO;
import com.grtship.core.dto.LoggedInUserDto;
import com.grtship.core.dto.UserApprovalRequestDTO;
import com.grtship.core.dto.UserDetailDto;

/**
 * @author ER Ajay Sharma
 * 
 *
 */
@FeignClient(value = "gateway-service", configuration = { FeignInterceptorConfig.class })
public interface UserFeignClient {

	@GetMapping(value = "user/auth/getUserDetails")
	public UserDetailDto getUserDetails(@RequestParam String username);

	@GetMapping(value = "user/auth/getLoggedInUser")
	public LoggedInUserDto getLoggedInUser();
	
	@PostMapping(value = "user/api/user/getUsersByRoleIds")
	public void getUsersByRoleIds(@RequestBody ApprovalRequestDTO approvalRequestDTO);
	
	@PostMapping(value = "user/api/user/getUserApprovalRequestData")
	public UserApprovalRequestDTO getUserApprovalRequestData(@RequestBody UserApprovalRequestDTO approvalRequestDTO);

	@PostMapping(value = "user/api/user/saveUserApprovalRequestData")
	public void saveUserApprovalRequest(UserApprovalRequestDTO userApprovalRequestDTO);
	
}
