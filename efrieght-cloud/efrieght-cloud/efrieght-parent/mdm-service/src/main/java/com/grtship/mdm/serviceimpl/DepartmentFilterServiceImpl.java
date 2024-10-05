package com.grtship.mdm.serviceimpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.grtship.core.annotation.AccessFilter;
import com.grtship.core.dto.DepartmentDTO;
import com.grtship.core.enumeration.DepartmentType;
import com.grtship.mdm.criteria.DepartmentCriteria;
import com.grtship.mdm.domain.Department;
import com.grtship.mdm.domain.Department_;
import com.grtship.mdm.generic.QueryService;
import com.grtship.mdm.mapper.DepartmentMapper;
import com.grtship.mdm.repository.DepartmentRepository;
import com.grtship.mdm.service.DepartmentFilterService;

@Service
@Transactional(readOnly = true)
public class DepartmentFilterServiceImpl extends QueryService<Department> implements DepartmentFilterService {

	@Autowired
	private DepartmentRepository departmentRepository;
	@Autowired
	private DepartmentMapper departmentMapper;

	@Transactional(readOnly = true)
	@Override
	@AccessFilter(clientAccessFlag = true, companyAccessFlag = true, branchAccessFlag = false)
	public List<Department> findByCriteria(DepartmentCriteria criteria) {
		final Specification<Department> specification = createSpecification(criteria);
		return departmentRepository.findAll(specification);
	}

	@Transactional(readOnly = true)
	@Override
	@AccessFilter(clientAccessFlag = true, companyAccessFlag = true, branchAccessFlag = false)
	public Page<DepartmentDTO> findByCriteria(DepartmentCriteria criteria, Pageable page) {
		final Specification<Department> specification = createSpecification(criteria);
		Page<Department> countries = departmentRepository.findAll(specification, page);
		List<DepartmentDTO> countryDTOs = departmentMapper.toDto(countries.getContent());
		return new PageImpl<>(countryDTOs, page, countries.getTotalElements());
	}

	@Transactional(readOnly = true)
	@Override
	@AccessFilter(clientAccessFlag = true, companyAccessFlag = true, branchAccessFlag = false)
	public long countByCriteria(DepartmentCriteria criteria) {
		final Specification<Department> specification = createSpecification(criteria);
		return departmentRepository.count(specification);
	}

	private Specification<Department> createSpecification(DepartmentCriteria criteria) {
		Specification<Department> specification = Specification.where(null);
		if (criteria != null) {
			if (criteria.getName() != null) {
				specification = specification.and(buildStringSpecification(criteria.getName(), Department_.name));
			}
			if (criteria.getCode() != null) {
				specification = specification.and(buildStringSpecification(criteria.getCode(), Department_.code));
			}
			if (!CollectionUtils.isEmpty(criteria.getIds())) {
				specification = getDepartmentsByIdsSpecs(criteria.getIds(), specification);
			}
			specification = departmentTypeSpec(criteria, specification);
		}
		return specification;
	}

	private Specification<Department> departmentTypeSpec(DepartmentCriteria criteria,
			Specification<Department> specification) {
		if (criteria.getType() != null) {
			for (DepartmentType departmentType : DepartmentType.values()) {
				if (departmentType.name().equals(criteria.getType())) {
					return specification.and((root, query, criteriaBuilder) -> criteriaBuilder
							.equal(root.get(Department_.type), DepartmentType.valueOf(criteria.getType())));
				}
			}
		}
		return specification.and(null);
	}

	public Specification<Department> getDepartmentsByIdsSpecs(List<Long> ids, Specification<Department> specification) {
		return specification.and((root, query, criteriaBuilder) -> root.get("id").in(ids));
	}

}
