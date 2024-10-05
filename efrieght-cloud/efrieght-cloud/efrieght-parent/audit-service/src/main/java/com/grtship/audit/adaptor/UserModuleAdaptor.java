/**
 * 
 */
package com.grtship.audit.adaptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.grtship.audit.feignclient.UserFeignClient;
import com.grtship.core.dto.UserDetailDto;

/**
 * @author ER Ajay Sharma
 *
 */
@Component
public class UserModuleAdaptor {
	@Autowired
	private UserFeignClient userFeignClient;
	
	public UserDetailDto getUserDetails(String username) {
		return userFeignClient.getUserDetails(username);
	}
	
}
