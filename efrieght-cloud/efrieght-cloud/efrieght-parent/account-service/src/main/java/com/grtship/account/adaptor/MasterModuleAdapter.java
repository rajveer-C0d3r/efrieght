package com.grtship.account.adaptor;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.grtship.account.feignclient.MasterModule;
import com.grtship.core.dto.AddressDTO;
import com.grtship.core.dto.CurrencyDTO;

@Component
public class MasterModuleAdapter {

	@Autowired
	private MasterModule masterModule;

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

	public Map<Long, CurrencyDTO> getAllCurrenciesByIdList(List<Long> currencyIdList) {
		return masterModule.getAllCurrenciesByIdList(currencyIdList);
	}

	public String generateCode(String objectName, String parentCode) {
		return masterModule.generateCode(objectName, parentCode);
	}

}
