package com.grtship.account.service;

import java.util.List;

import com.grtship.core.dto.TdsTypeDTO;

public interface TdsTypeService {

	/**
	 * Get all the Tds Types.
	 * 
	 * @return the list of entities.
	 */
	List<TdsTypeDTO> findAll();
}
