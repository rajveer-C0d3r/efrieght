package com.grtship.client.feignclient;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.grtship.common.interceptor.FeignInterceptorConfig;
//import com.grtship.client.interceptor.FeignInterceptorConfig;
import com.grtship.core.dto.AddressDTO;
import com.grtship.core.dto.BaseDTO;
import com.grtship.core.dto.CountryDTO;
import com.grtship.core.dto.DestinationDTO;


@FeignClient(value = "gateway-service"  ,configuration = {FeignInterceptorConfig.class} )
@Component
public interface MasterModule {

	@PostMapping(value = "mdm/api/countries/getCountriesByIdList")
	List<CountryDTO> getCountriesByIds(Set<Long> countryIdList);

	@GetMapping("mdm/api/countries/isStateMandatory/{id}")
	Boolean isStateMandatoryForGivenCountry(@PathVariable(name = "id") Long id);

	@PostMapping(value = "mdm/api/destination/getDestinationByIdList")
	List<DestinationDTO> getDestinationsByIds(List<Long> destinationIdList);

	@PostMapping(value = "mdm/api/states/getStatesByIds")
	List<BaseDTO> getStatesByIds(List<Long> stateIdList);

	@PostMapping(value = "mdm/addresses")
	AddressDTO saveAddress(AddressDTO addressDto);

	@PutMapping(value = "mdm/addresses")
	AddressDTO updateAddress(AddressDTO addressDto);

	@PostMapping("mdm/addresses/getAllAddressesByIdList")
	Map<Long, AddressDTO> getAllAddresses(List<Long> addressIdList);

	@DeleteMapping("mdm/addresses/{id}")
	public void deleteAddress(@PathVariable(name = "id") Long id);

	@GetMapping("mdm/api/object-codes/generateCode")
	public String generateCode(@RequestParam(name = "objectName") String objectName,
			@RequestParam(name = "parentCode", required = false) String parentCode);
}
