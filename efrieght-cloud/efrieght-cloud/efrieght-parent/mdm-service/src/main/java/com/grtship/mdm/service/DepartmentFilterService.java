package com.grtship.mdm.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.grtship.core.dto.DepartmentDTO;
import com.grtship.mdm.criteria.DepartmentCriteria;
import com.grtship.mdm.domain.Department;

public interface DepartmentFilterService {

	public List<Department> findByCriteria(DepartmentCriteria criteria);

	public Page<DepartmentDTO> findByCriteria(DepartmentCriteria criteria, Pageable page);

	public long countByCriteria(DepartmentCriteria criteria);

}
