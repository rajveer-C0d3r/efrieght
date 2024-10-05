package com.grtship.account.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.grtship.account.criteria.GroupCriteria;
import com.grtship.core.dto.BaseDTO;
import com.grtship.core.dto.GroupDTO;

public interface GroupQueryService {
	public List<GroupDTO> findByCriteria(GroupCriteria criteria);

	public Page<GroupDTO> findByCriteria(GroupCriteria criteria, Pageable page);

	public long countByCriteria(GroupCriteria criteria);

	public Optional<GroupDTO> findOne(Long id);

	public List<BaseDTO> getParentGroups();

	public List<BaseDTO> getChildGroups(Long id);
}
