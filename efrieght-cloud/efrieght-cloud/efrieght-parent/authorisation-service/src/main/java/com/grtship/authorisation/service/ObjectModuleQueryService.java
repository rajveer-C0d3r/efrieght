package com.grtship.authorisation.service;

import java.util.List;
import java.util.Optional;

import javax.persistence.criteria.JoinType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.grtship.authorisation.criteria.ObjectModuleCriteria;
import com.grtship.authorisation.domain.ObjectApprovalSequence_;
import com.grtship.authorisation.domain.ObjectModule;
import com.grtship.authorisation.domain.ObjectModule_;
import com.grtship.authorisation.generic.QueryService;
import com.grtship.authorisation.mapper.ObjectModuleMapper;
import com.grtship.authorisation.repository.ObjectModuleRepository;
import com.grtship.core.annotation.AccessFilter;
import com.grtship.core.dto.ObjectModuleDTO;
import com.grtship.core.filter.LongFilter;

/**
 * Service for executing complex queries for {@link ObjectModule} entities in
 * the database. The main input is a {@link ObjectModuleCriteria} which gets
 * converted to {@link Specification}, in a way that all the filters must apply.
 * It returns a {@link List} of {@link ObjectModuleDTO} or a {@link Page} of
 * {@link ObjectModuleDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ObjectModuleQueryService extends QueryService<ObjectModule> {

	private final Logger log = LoggerFactory.getLogger(ObjectModuleQueryService.class);

	private final ObjectModuleRepository objectModuleRepository;

	private final ObjectModuleMapper objectModuleMapper;

	public ObjectModuleQueryService(ObjectModuleRepository objectModuleRepository,
			ObjectModuleMapper objectModuleMapper) {
		this.objectModuleRepository = objectModuleRepository;
		this.objectModuleMapper = objectModuleMapper;
	}

	/**
	 * Return a {@link List} of {@link ObjectModuleDTO} which matches the criteria
	 * from the database.
	 * 
	 * @param criteria The object which holds all the filters, which the entities
	 *                 should match.
	 * @return the matching entities.
	 */
	@AccessFilter(companyAccessFlag = true, clientAccessFlag = true)
	@Transactional(readOnly = true)
	public List<ObjectModuleDTO> findByCriteria(ObjectModuleCriteria criteria) {
		log.debug("find by criteria : {}", criteria);
		final Specification<ObjectModule> specification = createSpecification(criteria);
		return objectModuleMapper.toDto(objectModuleRepository.findAll(specification));
	}

	/**
	 * Return a {@link Page} of {@link ObjectModuleDTO} which matches the criteria
	 * from the database.
	 * 
	 * @param criteria The object which holds all the filters, which the entities
	 *                 should match.
	 * @param page     The page, which should be returned.
	 * @return the matching entities.
	 */
	@AccessFilter(companyAccessFlag = true, clientAccessFlag = true)
	@Transactional(readOnly = true)
	public Page<ObjectModuleDTO> findByCriteria(ObjectModuleCriteria criteria, Pageable page) {
		log.debug("find by criteria : {}, page: {}", criteria, page);
		final Specification<ObjectModule> specification = createSpecification(criteria);
		return objectModuleRepository.findAll(specification, page).map(objectModuleMapper::toDto);
	}

	/**
	 * Return the number of matching entities in the database.
	 * 
	 * @param criteria The object which holds all the filters, which the entities
	 *                 should match.
	 * @return the number of matching entities.
	 */
	@AccessFilter(companyAccessFlag = true, clientAccessFlag = true)
	@Transactional(readOnly = true)
	public long countByCriteria(ObjectModuleCriteria criteria) {
		log.debug("count by criteria : {}", criteria);
		final Specification<ObjectModule> specification = createSpecification(criteria);
		return objectModuleRepository.count(specification);
	}

	/**
	 * Function to convert {@link ObjectModuleCriteria} to a {@link Specification}
	 * 
	 * @param criteria The object which holds all the filters, which the entities
	 *                 should match.
	 * @return the matching {@link Specification} of the entity.
	 */
	protected Specification<ObjectModule> createSpecification(ObjectModuleCriteria criteria) {
		Specification<ObjectModule> specification = Specification.where(null);
		if (criteria != null) {
			specification = getIdSpecification(criteria, specification);
			specification = getModuleNameSpecification(criteria, specification);
			specification = getApprovalSequenceIdSpecification(criteria, specification);
			specification = getActionSpecification(criteria, specification);
			specification = getApprovalRequiredSpecification(criteria, specification);
			specification = getDuplicateApproverSpecification(criteria, specification);
			specification = getParallelApproverSpecification(criteria, specification);
			specification = getMakerAsApproverSpecification(criteria, specification);
			specification = getApprovalLevelSpecification(criteria, specification);
		}
		return specification;
	}

	private Specification<ObjectModule> getApprovalSequenceIdSpecification(ObjectModuleCriteria criteria,
			Specification<ObjectModule> specification) {
		if (criteria.getObjectApprovalSequenceId() != null) {
			specification = specification.and(buildSpecification(criteria.getObjectApprovalSequenceId(), root -> root
					.join(ObjectModule_.objectApprovalSequences, JoinType.LEFT).get(ObjectApprovalSequence_.id)));
		}
		return specification;
	}

	private Specification<ObjectModule> getModuleNameSpecification(ObjectModuleCriteria criteria,
			Specification<ObjectModule> specification) {
		if (criteria.getModuleName() != null) {
			specification = specification
					.and(buildStringSpecification(criteria.getModuleName(), ObjectModule_.moduleName));
		}
		return specification;
	}

	private Specification<ObjectModule> getIdSpecification(ObjectModuleCriteria criteria,
			Specification<ObjectModule> specification) {
		if (criteria.getId() != null) {
			specification = specification.and(buildRangeSpecification(criteria.getId(), ObjectModule_.id));
		}
		return specification;
	}
	
	private Specification<ObjectModule> getActionSpecification(ObjectModuleCriteria criteria,
			Specification<ObjectModule> specification) {
		if (criteria.getAction() != null) {
			specification = specification.and((root, query, builder) -> builder
					.equal(root.get(ObjectModule_.action).as(String.class), criteria.getAction()));
		}
		return specification;
	}
	
	private Specification<ObjectModule> getApprovalRequiredSpecification(ObjectModuleCriteria criteria,
			Specification<ObjectModule> specification) {
		if (criteria.getApprovalRequired() != null) {
			specification = specification.and(buildSpecification(criteria.getApprovalRequired(), ObjectModule_.approvalRequired));
		}
		return specification;
	}
	
	private Specification<ObjectModule> getDuplicateApproverSpecification(ObjectModuleCriteria criteria,
			Specification<ObjectModule> specification) {
		if (criteria.getDuplicateApprover() != null) {
			specification = specification
					.and(buildSpecification(criteria.getDuplicateApprover(), ObjectModule_.duplicateApprover));
		}
		return specification;
	}
	
	private Specification<ObjectModule> getMakerAsApproverSpecification(ObjectModuleCriteria criteria,
			Specification<ObjectModule> specification) {
		if (criteria.getMakerAsApprover() != null) {
			specification = specification
					.and(buildSpecification(criteria.getMakerAsApprover(), ObjectModule_.makerAsApprover));
		}
		return specification;
	}
	
	private Specification<ObjectModule> getParallelApproverSpecification(ObjectModuleCriteria criteria,
			Specification<ObjectModule> specification) {
		if (criteria.getParallelApprover() != null) {
			specification = specification
					.and(buildSpecification(criteria.getParallelApprover(), ObjectModule_.duplicateApprover));
		}
		return specification;
	}
	
	private Specification<ObjectModule> getApprovalLevelSpecification(ObjectModuleCriteria criteria,
			Specification<ObjectModule> specification) {
		if (criteria.getApprovalLevel() != null) {
			specification = specification
					.and(buildRangeSpecification(criteria.getApprovalLevel(), ObjectModule_.approvalLevel));
		}
		return specification;
	}

	@Transactional(readOnly = true)
	public Optional<ObjectModuleDTO> findOne(Long id) {
		log.debug("Request to get ObjectModule : {}", id);
		LongFilter longFilter = new LongFilter();
		longFilter.setEquals(id);
		ObjectModuleCriteria criteria = new ObjectModuleCriteria();
		criteria.setId(longFilter);
		return Optional.ofNullable(findByCriteria(criteria).get(0));
	}
}
