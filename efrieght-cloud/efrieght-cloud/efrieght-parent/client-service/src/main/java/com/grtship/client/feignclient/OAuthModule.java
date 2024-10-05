package com.grtship.client.feignclient;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.grtship.common.interceptor.FeignInterceptorConfig;
import com.grtship.core.dto.CompanyDTO;
//import com.grtship.client.interceptor.FeignInterceptorConfig;
import com.grtship.core.dto.CsaDetailsDTO;
import com.grtship.core.dto.GsaDetailsDTO;
import com.grtship.core.dto.UserAccessDTO;
import com.grtship.core.enumeration.UserType;

@FeignClient(value = "gateway-service" ,configuration = {FeignInterceptorConfig.class} )
@Component
public interface OAuthModule {

	@PostMapping("user/api/user/createGsaUsers")
	public ResponseEntity<Void> generateGsaUsers(List<GsaDetailsDTO> gsaUserDetails);

	@GetMapping("user/api/user/getCurrentUserCompanyBranchIds/{userId}")
	public ResponseEntity<List<UserAccessDTO>> getCurrentUserCompanyBranchIds(
			@PathVariable(name = "userId") Long userId);

	@PostMapping("user/api/user/createCsaUsers")
	public ResponseEntity<Void> generateCsaUsers(List<CsaDetailsDTO> csaUserDetails);

	@PostMapping("user/api/user/getGsaUsersByClientIdList")
	public Map<Long, List<GsaDetailsDTO>> getGsaUsersByClientIdList(List<Long> idList);

	@PostMapping("user/api/user/getCsaUsersByCompanyIdList")
	public Map<Long, List<CsaDetailsDTO>> getCsaUsersByCompanyIdList(List<Long> idList);

	@GetMapping("user/api/user/getUserType/{login}")
	public Optional<UserType> getUserType(@PathVariable(name = "login") String login);
	
	@PostMapping("user/api/user/validateCsaUsers")
	public String validateCsaUsers(CompanyDTO companyDTO);
}
