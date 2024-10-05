package com.grtship.account.feignclient;

import java.util.List;
import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.grtship.common.interceptor.FeignInterceptorConfig;
import com.grtship.core.dto.AddressDTO;
import com.grtship.core.dto.CurrencyDTO;

@FeignClient(value = "gateway-service", configuration = { FeignInterceptorConfig.class })
public interface MasterModule {

	@PostMapping(value = "/mdm/address")
	AddressDTO saveAddress(AddressDTO addressDto);

	@PutMapping(value = "/mdm/address")
	AddressDTO updateAddress(AddressDTO addressDto);

	@PostMapping("/mdm/address/getAllAddressesByIdList")
	Map<Long, AddressDTO> getAllAddresses(List<Long> addressIdList);

	@DeleteMapping("/mdm/address/{id}")
	public void deleteAddress(@PathVariable(name = "id") Long id);

	@PostMapping("/mdm/api/currencies/getAllCurrenciesByIdList")
	Map<Long, CurrencyDTO> getAllCurrenciesByIdList(List<Long> currencyIdList);

	@GetMapping("/mdm/api/object-codes/generateCode")
	public String generateCode(@RequestParam(name = "objectName") String objectName,
			@RequestParam(name = "parentCode", required = false) String parentCode);

}
