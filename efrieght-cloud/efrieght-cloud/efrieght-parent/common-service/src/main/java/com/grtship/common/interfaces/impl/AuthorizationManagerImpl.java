package com.grtship.common.interfaces.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.grtship.common.feignclient.AuthorisationModule;
import com.grtship.common.interfaces.AuthorizationManager;
import com.grtship.core.dto.AuthorizationContainerDTO;

@Service
public class AuthorizationManagerImpl implements AuthorizationManager {

	@Autowired
	private AuthorisationModule authorisationModule;

	@Override
	public void manage(AuthorizationContainerDTO containerDTO) {
		authorisationModule.getApprovalRequirement(containerDTO); 
	}
	
	
}
