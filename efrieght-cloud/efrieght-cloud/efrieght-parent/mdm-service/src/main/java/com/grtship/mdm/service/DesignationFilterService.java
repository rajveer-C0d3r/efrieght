package com.grtship.mdm.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.grtship.core.dto.DesignationDTO;
import com.grtship.mdm.criteria.DesignationCriteria;
import com.grtship.mdm.domain.Designation;

public interface DesignationFilterService {

	public List<Designation> findByCriteria(DesignationCriteria criteria);

	public Page<DesignationDTO> findByCriteria(DesignationCriteria criteria, Pageable page);

	public long countByCriteria(DesignationCriteria criteria);
}
