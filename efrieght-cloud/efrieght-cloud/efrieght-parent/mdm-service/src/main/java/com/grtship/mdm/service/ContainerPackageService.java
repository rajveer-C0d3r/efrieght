package com.grtship.mdm.service;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.grtship.core.annotation.Auditable;
import com.grtship.core.annotation.Auditable.ActionType;
import com.grtship.core.dto.ContainerPackageDTO;
import com.grtship.mdm.domain.ContainerPackage;
import com.grtship.mdm.mapper.ContainerPackageMapper;
import com.grtship.mdm.repository.ContainerPackageRepository;

/**
 * Service Implementation for managing {@link ContainerPackage}.
 */
@Service
@Transactional
public class ContainerPackageService {

	private final Logger log = LoggerFactory.getLogger(ContainerPackageService.class);

	private final ContainerPackageRepository containerPackageRepository;

	private final ContainerPackageMapper containerPackageMapper;

	public ContainerPackageService(ContainerPackageRepository containerPackageRepository,
			ContainerPackageMapper containerPackageMapper) {
		this.containerPackageRepository = containerPackageRepository;
		this.containerPackageMapper = containerPackageMapper;
	}

	/**
	 * Save a containerPackage.
	 *
	 * @param containerPackageDTO the entity to save.
	 * @return the persisted entity.
	 */

	@Auditable(action = ActionType.SAVE,module = com.grtship.core.annotation.Auditable.Module.CONTAINER_PACKAGE)
	public ContainerPackageDTO save(ContainerPackageDTO containerPackageDTO) {
		log.debug("Request to save ContainerPackage : {}", containerPackageDTO);
		ContainerPackage containerPackage = containerPackageMapper.toEntity(containerPackageDTO);
		containerPackage = containerPackageRepository.save(containerPackage);
		return containerPackageMapper.toDto(containerPackage);
	}

	/**
	 * Get all the containerPackages.
	 *
	 * @return the list of entities.
	 */
	@Transactional(readOnly = true)
	public List<ContainerPackageDTO> findAll() {
		log.debug("Request to get all ContainerPackages");
		return containerPackageRepository.findAll().stream().map(containerPackageMapper::toDto)
				.collect(Collectors.toCollection(LinkedList::new));
	}

	/**
	 * Get one containerPackage by id.
	 *
	 * @param id the id of the entity.
	 * @return the entity.
	 */
	@Transactional(readOnly = true)
	public Optional<ContainerPackageDTO> findOne(Long id) {
		log.debug("Request to get ContainerPackage : {}", id);
		return containerPackageRepository.findById(id).map(containerPackageMapper::toDto);
	}

	/**
	 * Delete the containerPackage by id.
	 *
	 * @param id the id of the entity.
	 */
	@Auditable(action = ActionType.DELETE,module = com.grtship.core.annotation.Auditable.Module.CONTAINER_PACKAGE)
	public void delete(Long id) {
		log.debug("Request to delete ContainerPackage : {}", id);
		containerPackageRepository.deleteById(id);
	}
}
