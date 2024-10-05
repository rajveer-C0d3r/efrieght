/**
 * 
 */
package com.grtship.authorisation.adaptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.grtship.authorisation.feignclient.UserFeignClient;
import com.grtship.core.dto.LoggedInUserDto;
import com.grtship.core.dto.UserDetailDto;

/**
 * @author ER Ajay Sharma
 *
 */
@Component
public class UserModuleAdaptor {
	@Autowired
	private UserFeignClient userFeignClient;

	public LoggedInUserDto getCurrentUserDetails() {
		return userFeignClient.getLoggedInUser();
	}

	public UserDetailDto getUserDetails(String username) {
		return userFeignClient.getUserDetails(username);
	}

}
