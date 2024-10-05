package com.grt.elogfrieght.services.user.feignclient;

import java.util.List;
import java.util.Set;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.grtship.common.interceptor.FeignInterceptorConfig;
import com.grtship.core.dto.ClientDTO;
import com.grtship.core.dto.CompanyBranchDTO;
import com.grtship.core.dto.UserAccessCompanyBranchDTO;
import com.grtship.core.dto.UserCompanyDTO;
import com.grtship.core.dto.UserSpecificBranchDTO;
import com.grtship.core.dto.UserSpecificCBRequest;
import com.grtship.core.dto.UserSpecificCBResponse;
import com.grtship.core.dto.UserSpecificCompanyDTO;

@FeignClient(value = "gateway-service"  ,configuration = {FeignInterceptorConfig.class} )
public interface ClientModule {

	@PostMapping("client/api/clients/getByIds")
	public ResponseEntity<List<ClientDTO>> getClientsByIds(@RequestBody List<Long> clientIds);

	@PostMapping("client/api/company-branch/getByClientIds")
	public ResponseEntity<List<CompanyBranchDTO>> getBranchesByClientIds(@RequestBody List<Long> clientIds);

	@PostMapping("client/api/company-branch/getByCompanyIds")
	public ResponseEntity<List<CompanyBranchDTO>> getBranchesByCompanyIds(List<Long> companyIds);

	@PostMapping("client/api/company-branch/getByIds")
	public ResponseEntity<List<CompanyBranchDTO>> getBranchByids(@RequestBody List<Long> companyBranchIds);

	@PostMapping("client/api/company/getByUserAccess")
	public ResponseEntity<Set<UserCompanyDTO>> getByUserAccess(@RequestBody List<UserCompanyDTO> userCompany);
	
	@PostMapping(value = "client/api/company/userSpecificCBDetails")
	public UserSpecificCBResponse userSpecificCBDetails(@RequestBody UserSpecificCBRequest cbRequest);
	
	@PostMapping(value = "client/api/company/userSpecificCompanyDetails")
	public List<UserSpecificCompanyDTO> userSpecificCompanyDetails(@RequestBody UserAccessCompanyBranchDTO companyBranchDTO);
	
	@PostMapping(value = "client/api/company-branch/userSpecificBranchDetails")
	public List<UserSpecificBranchDTO> userSpecificBranchDetails(@RequestBody UserAccessCompanyBranchDTO companyBranchDTO);
}
