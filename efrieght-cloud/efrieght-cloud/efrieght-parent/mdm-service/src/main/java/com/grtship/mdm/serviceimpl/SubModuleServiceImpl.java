package com.grtship.mdm.serviceimpl;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.grtship.core.dto.SubModuleDTO;
import com.grtship.mdm.domain.SubModule;
import com.grtship.mdm.mapper.SubModuleMapper;
import com.grtship.mdm.repository.SubModuleRepository;
import com.grtship.mdm.service.SubModuleService;

/**
 * Service Implementation for managing {@link SubModule}.
 */
@Service
@Transactional
public class SubModuleServiceImpl implements SubModuleService {

	private final Logger log = LoggerFactory.getLogger(SubModuleServiceImpl.class);

	@Autowired
	private SubModuleRepository subModuleRepository;

	@Autowired
	private SubModuleMapper subModuleMapper;

	@Override
	@Transactional(readOnly = true)
	public List<SubModuleDTO> findAll() {
		log.debug("Request to get all SubModules");
		return subModuleRepository.findAll().stream().filter(subModule -> subModule.getId() != null)
				.map(subModuleMapper::toDto).collect(Collectors.toList());
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<SubModuleDTO> findOne(Long id) {
		log.debug("Request to get SubModule : {}", id);
		Optional<SubModule> subModule = subModuleRepository.findById(id);
		return (subModule.isPresent()) ? Optional.of(subModuleMapper.toDto(subModule.get())) : Optional.empty();
	}

	@Override
	@Transactional(readOnly = true)
	public List<SubModuleDTO> findByMainModuleId(Long mainModuleId) {
		log.debug("Request to get all SubModules of MainModule : {}", mainModuleId);
		List<SubModule> subModuleList = subModuleRepository.findByMainModuleId(mainModuleId);
		return (!CollectionUtils.isEmpty(subModuleList)) ? subModuleMapper.toDto(subModuleList)
				: Collections.emptyList();
	}

}
