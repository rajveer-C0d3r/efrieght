/**
 * 
 */
package com.grtship.client.adaptor;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.grtship.client.feignclient.UserFeignClient;
import com.grtship.core.dto.EmailPresentDto;
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
	
	public EmailPresentDto checkEmailPresent(List<String> emailIds) {
		return userFeignClient.checkEmailPresent(emailIds);
	}

	public Boolean isApporvalRequired(Object object) {
		return userFeignClient.isApprovalRequired(object);
	}
}
