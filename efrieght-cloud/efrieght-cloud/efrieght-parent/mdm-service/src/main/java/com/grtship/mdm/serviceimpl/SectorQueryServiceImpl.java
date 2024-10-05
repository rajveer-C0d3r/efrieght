/**
 * 
 */
package com.grtship.mdm.serviceimpl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.grtship.core.dto.SectorDTO;
import com.grtship.mdm.criteria.SectorCriteria;
import com.grtship.mdm.domain.Sector;
import com.grtship.mdm.domain.Sector_;
import com.grtship.mdm.mapper.SectorMapper;
import com.grtship.mdm.repository.SectorRepository;
import com.grtship.mdm.service.SectorQueryService;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Ajay
 *
 */
@Service
@Transactional
@Slf4j
public class SectorQueryServiceImpl implements SectorQueryService {

	@Autowired
	private SectorRepository sectorRepository;

	@Autowired
	private SectorMapper sectorMapper;

	private Specification<Sector> createSpecification(SectorCriteria criteria) {
		Specification<Sector> specification = Specification.where(null);
		if (criteria != null) {
			specification = specification.and(getIdSpec(criteria.getId())).and(getNameSpec(criteria.getName()))
					.and(getCodeSpec(criteria.getCode()));
		}
		return specification;
	}

	private Specification<Sector> getNameSpec(String name) {
		return (!StringUtils.isBlank(name))
				? (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get(Sector_.name), "%" + name + "%")
				: Specification.where(null);
	}

	private Specification<Sector> getIdSpec(Long id) {
		return (id != null) ? (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(Sector_.id), id)
				: Specification.where(null);
	}

	private Specification<Sector> getCodeSpec(String code) {
		return (!StringUtils.isBlank(code))
				? (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get(Sector_.code), "%" + code + "%")
				: Specification.where(null);
	}

	@Override
	public Page<SectorDTO> findByCriteria(SectorCriteria sectorCriteria, Pageable pageable) {
		log.debug("find by criteria : {}", sectorCriteria);
		final Specification<Sector> specification = createSpecification(sectorCriteria);
		Page<Sector> sector = sectorRepository.findAll(specification, pageable);
		List<SectorDTO> sectorDtos = sectorMapper.toDto(sector.getContent());
		return new PageImpl<>(sectorDtos, pageable, sector.getTotalElements());
	}

}
