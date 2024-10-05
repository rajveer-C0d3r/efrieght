package com.grtship.account.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.grtship.account.criteria.TdsCriteria;
import com.grtship.account.domain.Tds;
import com.grtship.account.domain.Tds_;
import com.grtship.account.generic.QueryService;
import com.grtship.account.mapper.TdsMapper;
import com.grtship.account.repository.TdsRepository;
import com.grtship.account.service.TdsFilterService;
import com.grtship.core.dto.TdsDTO;

/**
 * Service for executing complex queries for {@link Tds} entities in the
 * database. The main input is a {@link TdsCriteria} which gets converted to
 * {@link Specification}, in a way that all the filters must apply. It returns a
 * {@link List} of {@link TdsDTO} or a {@link Page} of {@link TdsDTO} which
 * fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class TdsFilterServiceImpl extends QueryService<Tds> implements TdsFilterService {

	private final Logger log = LoggerFactory.getLogger(TdsFilterServiceImpl.class);

	private final TdsRepository tdsRepository;

	private final TdsMapper tdsMapper;

	public TdsFilterServiceImpl(TdsRepository tdsRepository, TdsMapper tdsMapper) {
		this.tdsRepository = tdsRepository;
		this.tdsMapper = tdsMapper;
	}

	/**
	 * Return a {@link List} of {@link TdsDTO} which matches the criteria from the
	 * database.
	 * 
	 * @param criteria The object which holds all the filters, which the entities
	 *                 should match.
	 * @return the matching entities.
	 */
	@Transactional(readOnly = true)
	public List<TdsDTO> findByCriteria(TdsCriteria criteria) {
		log.debug("find by criteria : {}", criteria);
		final Specification<Tds> specification = createSpecification(criteria);
		return tdsMapper.toDto(tdsRepository.findAll(specification));
	}

	/**
	 * Return a {@link Page} of {@link TdsDTO} which matches the criteria from the
	 * database.
	 * 
	 * @param criteria The object which holds all the filters, which the entities
	 *                 should match.
	 * @param page     The page, which should be returned.
	 * @return the matching entities.
	 */
	@Transactional(readOnly = true)
	public Page<TdsDTO> findByCriteria(TdsCriteria criteria, Pageable page) {
		log.debug("find by criteria : {}, page: {}", criteria, page);
		final Specification<Tds> specification = createSpecification(criteria);
		return tdsRepository.findAll(specification, page).map(tdsMapper::toDto);
	}

	/**
	 * Return the number of matching entities in the database.
	 * 
	 * @param criteria The object which holds all the filters, which the entities
	 *                 should match.
	 * @return the number of matching entities.
	 */
	@Transactional(readOnly = true)
	public long countByCriteria(TdsCriteria criteria) {
		log.debug("count by criteria : {}", criteria);
		final Specification<Tds> specification = createSpecification(criteria);
		return tdsRepository.count(specification);
	}

	/**
	 * Function to convert {@link TdsCriteria} to a {@link Specification}
	 * 
	 * @param criteria The object which holds all the filters, which the entities
	 *                 should match.
	 * @return the matching {@link Specification} of the entity.
	 */
	protected Specification<Tds> createSpecification(TdsCriteria criteria) {
		Specification<Tds> specification = Specification.where(null);
		if (criteria != null) {
			if (criteria.getId() != null) {
				specification = specification.and(buildRangeSpecification(criteria.getId(), Tds_.id));
			}
			if (criteria.getCode() != null) {
				specification = specification.and(buildStringSpecification(criteria.getCode(), Tds_.code));
			}
			if (criteria.getDescription() != null) {
				specification = specification
						.and(buildStringSpecification(criteria.getDescription(), Tds_.description));
			}
			if (criteria.getActiveFlag() != null) {
				specification = specification.and(buildSpecification(criteria.getActiveFlag(), Tds_.activeFlag));
			}
		}
		return specification;
	}
}
