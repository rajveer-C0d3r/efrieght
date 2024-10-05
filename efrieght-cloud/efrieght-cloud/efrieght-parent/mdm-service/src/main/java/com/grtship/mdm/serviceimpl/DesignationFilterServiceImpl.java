package com.grtship.mdm.serviceimpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.grtship.core.dto.DesignationDTO;
import com.grtship.mdm.criteria.DesignationCriteria;
import com.grtship.mdm.domain.Designation;
import com.grtship.mdm.domain.Designation_;
import com.grtship.mdm.generic.QueryService;
import com.grtship.mdm.mapper.DesignationMapper;
import com.grtship.mdm.repository.DesignationRepository;
import com.grtship.mdm.service.DesignationFilterService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(readOnly = true)
public class DesignationFilterServiceImpl extends QueryService<Designation> implements DesignationFilterService {

	@Autowired
	DesignationRepository designationRepository;

	@Autowired
	DesignationMapper designationMapper;

	@Transactional(readOnly = true)
	public List<Designation> findByCriteria(DesignationCriteria criteria) {
		log.debug("find by criteria : {}", criteria);
		final Specification<Designation> specification = createSpecification(criteria);
		return designationRepository.findAll(specification);
	}

	@Transactional(readOnly = true)
	public Page<DesignationDTO> findByCriteria(DesignationCriteria criteria, Pageable page) {
		log.debug("find by criteria : {}, page: {}", criteria, page);
		final Specification<Designation> specification = createSpecification(criteria);
		Page<Designation> countries = designationRepository.findAll(specification, page);
		List<DesignationDTO> countryDTOs = designationMapper.toDto(countries.getContent());
		return new PageImpl<>(countryDTOs, page, countries.getTotalElements());
	}

	@Transactional(readOnly = true)
	public long countByCriteria(DesignationCriteria criteria) {
		log.debug("count by criteria : {}", criteria);
		final Specification<Designation> specification = createSpecification(criteria);
		return designationRepository.count(specification);
	}

	private Specification<Designation> createSpecification(DesignationCriteria criteria) {
		Specification<Designation> specification = Specification.where(null);
		if (criteria != null) {
			if (criteria.getId() != null) {
				specification = specification.and(buildRangeSpecification(criteria.getId(), Designation_.id));
			}
			if (criteria.getName() != null) {
				specification = specification.and(buildStringSpecification(criteria.getName(), Designation_.name));
			}
			if (criteria.getCode() != null) {
				specification = specification.and(buildStringSpecification(criteria.getCode(), Designation_.code));
			}
		}
		return specification;
	}
}
