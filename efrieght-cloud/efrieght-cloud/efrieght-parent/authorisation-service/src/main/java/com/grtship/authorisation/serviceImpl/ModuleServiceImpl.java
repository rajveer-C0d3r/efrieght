package com.grtship.authorisation.serviceImpl;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.grtship.authorisation.domain.Module;
import com.grtship.authorisation.mapper.ModuleMapper;
import com.grtship.authorisation.repository.ModuleRepository;
import com.grtship.authorisation.service.ModuleService;
import com.grtship.core.annotation.Auditable;
import com.grtship.core.annotation.Auditable.ActionType;
import com.grtship.core.dto.ModuleDTO;

/**
 * Service Implementation for managing {@link Module}.
 */
@Service
@Transactional
public class ModuleServiceImpl implements ModuleService {

	private final Logger log = LoggerFactory.getLogger(ModuleServiceImpl.class);

	private final ModuleRepository moduleRepository;

	private final ModuleMapper moduleMapper;

	public ModuleServiceImpl(ModuleRepository moduleRepository, ModuleMapper moduleMapper) {
		this.moduleRepository = moduleRepository;
		this.moduleMapper = moduleMapper;
	}

	@Override
	@Auditable(action = ActionType.SAVE, module = com.grtship.core.annotation.Auditable.Module.MODULE)
	public ModuleDTO save(ModuleDTO moduleDTO) {
		log.debug("Request to save Module : {}", moduleDTO);
		Module module = moduleMapper.toEntity(moduleDTO);
		module = moduleRepository.save(module);
		return moduleMapper.toDto(module);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<ModuleDTO> findAll(Pageable pageable) {
		log.debug("Request to get all Modules");
		return moduleRepository.findAll(pageable).map(moduleMapper::toDto);
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<ModuleDTO> findOne(String moduleName) {
		log.debug("Request to get Module : {}", moduleName);
		return moduleRepository.findById(moduleName).map(moduleMapper::toDto);
	}

	@Override
	@Auditable(action = ActionType.DELETE, module = com.grtship.core.annotation.Auditable.Module.MODULE)
	public void delete(String moduleName) {
		log.debug("Request to delete Module : {}", moduleName);
		moduleRepository.deleteById(moduleName);
	}
}
