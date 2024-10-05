package com.grtship.common.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.grtship.common.interceptor.FeignInterceptorConfig;
import com.grtship.core.dto.AuthorizationContainerDTO;

@FeignClient(value = "gateway-service", configuration = { FeignInterceptorConfig.class })
public interface AuthorisationModule {
	@PostMapping(value = "authorisation/api/object-module/getApprovalRequirement")
	public void getApprovalRequirement(@RequestBody AuthorizationContainerDTO authorizationContainerDTO);
}
