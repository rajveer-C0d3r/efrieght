package com.grtship.mdm.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.grtship.core.dto.EntityBranchDTO;
import com.grtship.mdm.criteria.EntityBranchCriteria;
import com.grtship.mdm.dto.EntityBranchMultiDropDownDTO;

public interface EntityBranchQueryService {

	public Page<EntityBranchDTO> findAll(EntityBranchCriteria branchCriteria, Pageable pageable);

	public List<EntityBranchDTO> findByCriteria(EntityBranchCriteria branchCriteria);

	public Optional<EntityBranchDTO> findOne(Long id);

	public Page<EntityBranchMultiDropDownDTO> getAllBranchesForMultiDD(EntityBranchCriteria entityCriteria,
			Pageable pageable);

	public List<Long> getBranchIdsByEntityIdAndActiveFlag(Long referenceId, Boolean true1);
}
