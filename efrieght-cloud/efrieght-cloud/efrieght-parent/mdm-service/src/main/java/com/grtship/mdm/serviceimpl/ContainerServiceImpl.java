package com.grtship.mdm.serviceimpl;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.grtship.core.annotation.Auditable;
import com.grtship.core.annotation.Auditable.ActionType;
import com.grtship.core.dto.ContainerDTO;
import com.grtship.mdm.domain.Container;
import com.grtship.mdm.mapper.ContainerMapper;
import com.grtship.mdm.repository.ContainerRepository;
import com.grtship.mdm.service.ContainerService;

/**
 * Service Implementation for managing {@link Container}.
 */
@Service
@Transactional
public class ContainerServiceImpl implements ContainerService {

	private final Logger log = LoggerFactory.getLogger(ContainerServiceImpl.class);

	private final ContainerRepository containerRepository;

	private final ContainerMapper containerMapper;

	public ContainerServiceImpl(ContainerRepository containerRepository, ContainerMapper containerMapper) {
		this.containerRepository = containerRepository;
		this.containerMapper = containerMapper;
	}

	@Override
	@Auditable(action = ActionType.SAVE,module = com.grtship.core.annotation.Auditable.Module.CONTAINER)
	public ContainerDTO save(ContainerDTO containerDTO) {
		log.debug("Request to save Container : {}", containerDTO);
		Container container = containerMapper.toEntity(containerDTO);
		container = containerRepository.save(container);
		return containerMapper.toDto(container);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<ContainerDTO> findAll(Pageable pageable) {
		log.debug("Request to get all Containers");
		return containerRepository.findAll(pageable).map(containerMapper::toDto);
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<ContainerDTO> findOne(Long id) {
		log.debug("Request to get Container : {}", id);
		return containerRepository.findById(id).map(containerMapper::toDto);
	}

	@Override
	@Auditable(action = ActionType.DELETE,module = com.grtship.core.annotation.Auditable.Module.CONTAINER)
	public void delete(Long id) {
		log.debug("Request to delete Container : {}", id);
		containerRepository.deleteById(id);
	}
}
