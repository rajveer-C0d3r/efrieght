package com.grtship.client.adaptor;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.grtship.client.feignclient.MasterModule;
import com.grtship.core.dto.AddressDTO;
import com.grtship.core.dto.BaseDTO;
import com.grtship.core.dto.CountryDTO;
import com.grtship.core.dto.DestinationDTO;

@Component
public class MasterModuleAdapter {

	@Autowired
	private MasterModule masterModule;

	public List<CountryDTO> getCountriesByIds(Set<Long> countryIdList) {
		return masterModule.getCountriesByIds(countryIdList);
	}

	public Boolean isStateMandatoryForGivenCountry(Long id) {
		return masterModule.isStateMandatoryForGivenCountry(id);
	}

	public List<DestinationDTO> getDestinationsByIds(List<Long> destinationIdList) {
		return masterModule.getDestinationsByIds(destinationIdList);
	}

	public List<BaseDTO> getStatesByIds(List<Long> stateIdList) {
		return masterModule.getStatesByIds(stateIdList);
	}

	public AddressDTO saveAddress(AddressDTO addressDto) {
		return masterModule.saveAddress(addressDto);
	}

	public AddressDTO updateAddress(AddressDTO addressDto) {
		return masterModule.updateAddress(addressDto);
	}

	public Map<Long, AddressDTO> getAllAddresses(List<Long> addressIdList) {
		return masterModule.getAllAddresses(addressIdList);
	}

	public void deleteAddress(Long id) {
		masterModule.deleteAddress(id);
	}
	
	public String generateCode(String objectName, String parentCode) {
		return masterModule.generateCode(objectName, parentCode);
	}

}
