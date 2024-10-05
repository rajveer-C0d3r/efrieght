package com.grtship.mdm.serviceimpl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.grtship.core.dto.ObjectCodeDTO;
import com.grtship.mdm.criteria.ObjectCodeCriteria;
import com.grtship.mdm.domain.ObjectCode;
import com.grtship.mdm.domain.ObjectCode_;
import com.grtship.mdm.generic.QueryService;
import com.grtship.mdm.mapper.ObjectCodeMapper;
import com.grtship.mdm.repository.ObjectCodeRepository;

/**
 * Service for executing complex queries for {@link ObjectCode} entities in the
 * database. The main input is a {@link ObjectCodeCriteria} which gets converted
 * to {@link Specification}, in a way that all the filters must apply. It
 * returns a {@link List} of {@link ObjectCodeDTO} or a {@link Page} of
 * {@link ObjectCodeDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ObjectCodeFilterServiceImpl extends QueryService<ObjectCode> {

	private final Logger log = LoggerFactory.getLogger(ObjectCodeFilterServiceImpl.class);

	private final ObjectCodeRepository objectCodeRepository;

	private final ObjectCodeMapper objectCodeMapper;

	public ObjectCodeFilterServiceImpl(ObjectCodeRepository objectCodeRepository, ObjectCodeMapper objectCodeMapper) {
		this.objectCodeRepository = objectCodeRepository;
		this.objectCodeMapper = objectCodeMapper;
	}

	/**
	 * Return a {@link List} of {@link ObjectCodeDTO} which matches the criteria
	 * from the database.
	 * 
	 * @param criteria The object which holds all the filters, which the entities
	 *                 should match.
	 * @return the matching entities.
	 */
	@Transactional(readOnly = true)
	public List<ObjectCodeDTO> findByCriteria(ObjectCodeCriteria criteria) {
		log.debug("find by criteria : {}", criteria);
		final Specification<ObjectCode> specification = createSpecification(criteria);
		return objectCodeMapper.toDto(objectCodeRepository.findAll(specification));
	}

	/**
	 * Return a {@link Page} of {@link ObjectCodeDTO} which matches the criteria
	 * from the database.
	 * 
	 * @param criteria The object which holds all the filters, which the entities
	 *                 should match.
	 * @param page     The page, which should be returned.
	 * @return the matching entities.
	 */
	@Transactional(readOnly = true)
	public Page<ObjectCodeDTO> findByCriteria(ObjectCodeCriteria criteria, Pageable page) {
		log.debug("find by criteria : {}, page: {}", criteria, page);
		final Specification<ObjectCode> specification = createSpecification(criteria);
		return objectCodeRepository.findAll(specification, page).map(objectCodeMapper::toDto);
	}

	/**
	 * Return the number of matching entities in the database.
	 * 
	 * @param criteria The object which holds all the filters, which the entities
	 *                 should match.
	 * @return the number of matching entities.
	 */
	@Transactional(readOnly = true)
	public long countByCriteria(ObjectCodeCriteria criteria) {
		log.debug("count by criteria : {}", criteria);
		final Specification<ObjectCode> specification = createSpecification(criteria);
		return objectCodeRepository.count(specification);
	}

	/**
	 * Function to convert {@link ObjectCodeCriteria} to a {@link Specification}
	 * 
	 * @param criteria The object which holds all the filters, which the entities
	 *                 should match.
	 * @return the matching {@link Specification} of the entity.
	 */
	protected Specification<ObjectCode> createSpecification(ObjectCodeCriteria criteria) {
		Specification<ObjectCode> specification = Specification.where(null);
		if (criteria != null) {
			if (criteria.getId() != null) {
				specification = specification.and(buildRangeSpecification(criteria.getId(), ObjectCode_.id));
			}
			if (criteria.getObjectName() != null) {
				specification = specification
						.and(buildStringSpecification(criteria.getObjectName(), ObjectCode_.objectName));
			}
			if (criteria.getPrefix() != null) {
				specification = specification.and(buildStringSpecification(criteria.getPrefix(), ObjectCode_.prefix));
			}
			if (criteria.getPadding() != null) {
				specification = specification.and(buildRangeSpecification(criteria.getPadding(), ObjectCode_.padding));
			}
			if (criteria.getCounter() != null) {
				specification = specification.and(buildRangeSpecification(criteria.getCounter(), ObjectCode_.counter));
			}
		}
		return specification;
	}
}
