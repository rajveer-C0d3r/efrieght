package com.grtship.mdm.service;

import java.util.List;
import java.util.Optional;

import com.grtship.core.dto.EntityGroupCriteriaDTO;
import com.grtship.core.dto.EntityGroupDTO;

/**
 * Service Interface for managing {@link com.grt.efreight.domain.EntityGroup}.
 */
public interface EntityGroupService {

	public EntityGroupDTO save(EntityGroupDTO entityGroupsDto);

	public List<EntityGroupDTO> findAll(EntityGroupCriteriaDTO entityGroupsCriteria);

	public Optional<EntityGroupDTO> findOne(Long id);

	public void delete(Long id);

}
