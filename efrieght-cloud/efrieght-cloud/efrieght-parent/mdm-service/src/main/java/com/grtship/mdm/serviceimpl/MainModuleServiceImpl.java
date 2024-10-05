package com.grtship.mdm.serviceimpl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.grtship.core.dto.MainModuleDTO;
import com.grtship.mdm.domain.MainModule;
import com.grtship.mdm.mapper.MainModuleMapper;
import com.grtship.mdm.repository.MainModuleRepository;
import com.grtship.mdm.service.MainModuleService;

/**
 * Service Implementation for managing {@link MainModule}.
 */
@Service
@Transactional
public class MainModuleServiceImpl implements MainModuleService {

	private final Logger log = LoggerFactory.getLogger(MainModuleServiceImpl.class);

	@Autowired
	private MainModuleRepository mainModuleRepository;

	@Autowired
	private MainModuleMapper mainModuleMapper;

	@Override
	@Transactional(readOnly = true)
	public List<MainModuleDTO> findAll() {
		log.debug("Request to get all MainModules");
		return mainModuleRepository.findAll().stream().filter(mainModule -> mainModule.getId() != null)
				.map(mainModuleMapper::toDto).collect(Collectors.toList());
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<MainModuleDTO> findOne(Long id) {
		log.debug("Request to get MainModule : {}", id);
		Optional<MainModule> mainModule = mainModuleRepository.findById(id);
		return (mainModule.isPresent()) ? Optional.of(mainModuleMapper.toDto(mainModule.get())) : Optional.empty();
	}

}
