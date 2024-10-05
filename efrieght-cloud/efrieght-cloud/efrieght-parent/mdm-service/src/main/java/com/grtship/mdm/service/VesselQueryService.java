package com.grtship.mdm.service;

import java.util.List;

import javax.persistence.criteria.JoinType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.grtship.core.annotation.AccessFilter;
import com.grtship.core.dto.VesselDTO;
import com.grtship.mdm.criteria.VesselCriteria;
import com.grtship.mdm.domain.Vessel;
import com.grtship.mdm.domain.ExternalEntity_;
import com.grtship.mdm.domain.Vessel_;
import com.grtship.mdm.generic.QueryService;
import com.grtship.mdm.mapper.VesselMapper;
import com.grtship.mdm.repository.VesselRepository;

/**
 * Service for executing complex queries for {@link Vessel} entities in the
 * database. The main input is a {@link VesselCriteria} which gets converted to
 * {@link Specification}, in a way that all the filters must apply. It returns a
 * {@link List} of {@link VesselDTO} or a {@link Page} of {@link VesselDTO}
 * which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class VesselQueryService extends QueryService<Vessel> {

	private final Logger log = LoggerFactory.getLogger(VesselQueryService.class);

	private final VesselRepository vesselRepository;

	private final VesselMapper vesselMapper;

	public VesselQueryService(VesselRepository vesselRepository, VesselMapper vesselMapper) {
		this.vesselRepository = vesselRepository;
		this.vesselMapper = vesselMapper;
	}

	/**
	 * Return a {@link List} of {@link VesselDTO} which matches the criteria from
	 * the database.
	 * 
	 * @param criteria The object which holds all the filters, which the entities
	 *                 should match.
	 * @return the matching entities.
	 */
	@Transactional(readOnly = true)
	public List<VesselDTO> findByCriteria(VesselCriteria criteria) {
		log.debug("find by criteria : {}", criteria);
		final Specification<Vessel> specification = createSpecification(criteria);
		return vesselMapper.toDto(vesselRepository.findAll(specification));
	}

	/**
	 * Return a {@link Page} of {@link VesselDTO} which matches the criteria from
	 * the database.
	 * 
	 * @param criteria The object which holds all the filters, which the entities
	 *                 should match.
	 * @param page     The page, which should be returned.
	 * @return the matching entities.
	 */
	@AccessFilter(clientAccessFlag = true, companyAccessFlag = true, branchAccessFlag = true)
	@Transactional(readOnly = true)
	public Page<VesselDTO> findByCriteria(VesselCriteria criteria, Pageable page) {
		log.debug("find by criteria : {}, page: {}", criteria, page);
		final Specification<Vessel> specification = createSpecification(criteria);
		return vesselRepository.findAll(specification, page).map(vesselMapper::toDto);
	}

	/**
	 * Return the number of matching entities in the database.
	 * 
	 * @param criteria The object which holds all the filters, which the entities
	 *                 should match.
	 * @return the number of matching entities.
	 */
	@Transactional(readOnly = true)
	public long countByCriteria(VesselCriteria criteria) {
		log.debug("count by criteria : {}", criteria);
		final Specification<Vessel> specification = createSpecification(criteria);
		return vesselRepository.count(specification);
	}

	/**
	 * Function to convert {@link VesselCriteria} to a {@link Specification}
	 * 
	 * @param criteria The object which holds all the filters, which the entities
	 *                 should match.
	 * @return the matching {@link Specification} of the entity.
	 */
	protected Specification<Vessel> createSpecification(VesselCriteria criteria) {
		Specification<Vessel> specification = Specification.where(null);
		if (criteria != null) {
			specification = getIdNotEqualToSpecification(criteria, specification);
			specification = getIdListSpecification(criteria, specification);
			specification = getNameEqualToNameSpecification(criteria, specification);
			specification = getIdSpecification(criteria, specification);
			specification = getNameSpecification(criteria, specification);
			specification = getDeactivatedSpecification(criteria, specification);
			specification = getOperatorIdSpecification(criteria, specification);
			specification = getOperatorNameSpecification(criteria, specification);
		}
		return specification;
	}

	private Specification<Vessel> getIdNotEqualToSpecification(VesselCriteria criteria,
			Specification<Vessel> specification) {
		if (criteria.getNotEqualToId() != null) {
			specification = specification.and((root, query, criteriaBuilder) -> criteriaBuilder
					.notEqual(root.get(Vessel_.id), criteria.getNotEqualToId()));
		}
		return specification;
	}

	private Specification<Vessel> getIdListSpecification(VesselCriteria criteria, Specification<Vessel> specification) {
		if (!CollectionUtils.isEmpty(criteria.getNotInIdList())) {
			specification = specification.and((root, query, criteriaBuilder) -> criteriaBuilder
					.not(root.get(Vessel_.id).in(criteria.getNotInIdList())));
		}
		return specification;
	}

	private Specification<Vessel> getNameEqualToNameSpecification(VesselCriteria criteria,
			Specification<Vessel> specification) {
		if (criteria.getEqualsToName() != null) {
			specification = specification.and((root, query, criteriaBuilder) -> criteriaBuilder
					.equal(root.get(Vessel_.name), criteria.getEqualsToName()));
		}
		return specification;
	}

	private Specification<Vessel> getIdSpecification(VesselCriteria criteria, Specification<Vessel> specification) {
		if (criteria.getId() != null) {
			log.debug("Id to mach {}", criteria.getId());
			specification = specification.and(
					(root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(Vessel_.id), criteria.getId()));
		}
		return specification;
	}

	private Specification<Vessel> getNameSpecification(VesselCriteria criteria, Specification<Vessel> specification) {
		if (criteria.getName() != null) {
			specification = specification.and(buildStringSpecification(criteria.getName(), Vessel_.name));
		}
		return specification;
	}

	private Specification<Vessel> getDeactivatedSpecification(VesselCriteria criteria,
			Specification<Vessel> specification) {
		if (criteria.getDeactivate() != null) {
			specification = specification.and((root, query, criteriaBuilder) -> criteriaBuilder
					.equal(root.get(Vessel_.active), Boolean.valueOf(!criteria.getDeactivate())));
		}
		return specification;
	}

	private Specification<Vessel> getOperatorIdSpecification(VesselCriteria criteria,
			Specification<Vessel> specification) {
		if (criteria.getOperatorId() != null) {
			specification = specification.and(buildSpecification(criteria.getOperatorId(),
					root -> root.join(Vessel_.operator, JoinType.LEFT).get(ExternalEntity_.id)));
		}
		return specification;
	}

	private Specification<Vessel> getOperatorNameSpecification(VesselCriteria criteria,
			Specification<Vessel> specification) {
		if (criteria.getContainsOperatorName() != null) {
			specification = specification.and((root, query, criteriaBuilder) -> criteriaBuilder.like(
					root.join(Vessel_.operator).get(ExternalEntity_.name), "%" + criteria.getNotEqualToId() + "%"));
		}
		return specification;
	}

}
