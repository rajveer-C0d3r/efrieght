package com.grtship.mdm.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.grtship.core.dto.EntityMultiDropDownDTO;
import com.grtship.core.dto.ExternalEntityDTO;
import com.grtship.mdm.criteria.ExternalEntityCriteria;

public interface ExternalEntityQueryService {

	public Page<ExternalEntityDTO> findAll(ExternalEntityCriteria entityCriteria, Pageable pageable);

	public ExternalEntityDTO findByCriteria(ExternalEntityCriteria entityCriteria);

	public Optional<ExternalEntityDTO> findOne(Long id);

	public Page<EntityMultiDropDownDTO> getAllExternalEntitiesForMultiDD(ExternalEntityCriteria entityCriteria,
			Pageable pageable);

	public boolean existById(Long externalEntityId);
}
