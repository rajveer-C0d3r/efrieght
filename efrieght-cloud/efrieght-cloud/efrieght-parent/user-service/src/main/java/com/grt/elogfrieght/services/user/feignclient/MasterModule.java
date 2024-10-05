package com.grt.elogfrieght.services.user.feignclient;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.grtship.common.interceptor.FeignInterceptorConfig;
import com.grtship.core.dto.DepartmentDTO;
import com.grtship.core.dto.DesignationDTO;

@FeignClient(value = "gateway-service"  ,configuration = {FeignInterceptorConfig.class} )
public interface MasterModule {
	
	@PostMapping("mdm/api/departments/getByIds")
	public ResponseEntity<List<DepartmentDTO>> getDepartmentsByIds(@RequestBody List<Long> departmentIds);

	@PostMapping("mdm/api/designations/getByIds")
	public ResponseEntity<List<DesignationDTO>> getDesignationsByIds(@RequestBody List<Long> designationIds);
	
	@GetMapping("mdm/api/departments/{id}")
    public ResponseEntity<DepartmentDTO> getDepartment(@PathVariable("id") Long id);
	
	@GetMapping("mdm/api/designations/{id}")
    public ResponseEntity<DesignationDTO> getDesignation(@PathVariable("id") Long id);

}
