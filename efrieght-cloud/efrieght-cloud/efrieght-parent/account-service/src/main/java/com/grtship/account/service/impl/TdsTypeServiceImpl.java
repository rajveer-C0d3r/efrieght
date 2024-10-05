package com.grtship.account.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.grtship.account.domain.Tds;
import com.grtship.account.mapper.TdsTypeMapper;
import com.grtship.account.repository.TdsTypeRepository;
import com.grtship.account.service.TdsTypeService;
import com.grtship.core.dto.TdsTypeDTO;

/**
 * Service Implementation for managing {@link Tds Type}.
 */

@Service
@Transactional
public class TdsTypeServiceImpl implements TdsTypeService {

	private final Logger log = LoggerFactory.getLogger(TdsTypeServiceImpl.class);

	@Autowired
	private TdsTypeRepository tdsTypeRepository;

	@Autowired
	private TdsTypeMapper tdsTypeMapper;

	@Override
	@Transactional(readOnly = true)
	public List<TdsTypeDTO> findAll() {
		log.debug("Request to get all tds types");
		return tdsTypeRepository.findAll().stream().filter(tdsType -> tdsType.getId() != null).map(tdsTypeMapper::toDto)
				.collect(Collectors.toList());
	}

}
