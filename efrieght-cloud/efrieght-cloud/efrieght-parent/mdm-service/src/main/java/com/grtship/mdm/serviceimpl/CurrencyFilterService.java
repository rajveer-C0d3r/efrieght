package com.grtship.mdm.serviceimpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.grtship.core.dto.CurrencyDTO;
import com.grtship.mdm.criteria.CurrencyCriteria;
import com.grtship.mdm.domain.Currency;
import com.grtship.mdm.domain.Currency_;
import com.grtship.mdm.generic.QueryService;
import com.grtship.mdm.mapper.CurrencyMapper;
import com.grtship.mdm.repository.CurrencyRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(readOnly = true)
public class CurrencyFilterService extends QueryService<Currency> {

	@Autowired
	private CurrencyRepository currencyRepository;

	@Autowired
	private CurrencyMapper currencyMapper;

	@Transactional(readOnly = true)
	public List<CurrencyDTO> findByCriteria(CurrencyCriteria criteria) {
		log.debug("find by criteria : {}", criteria);
		final Specification<Currency> specification = createSpecification(criteria);
		List<Currency> currencies = currencyRepository.findAll(specification);
		List<CurrencyDTO> currencyDtos = currencyMapper.toDto(currencies);
		return currencyDtos;
	}

	@Transactional(readOnly = true)
	public Page<CurrencyDTO> findByCriteria(CurrencyCriteria criteria, Pageable page) {
		log.debug("find by criteria : {}, page: {}", criteria, page);
		final Specification<Currency> specification = createSpecification(criteria);
		Page<Currency> currencies = currencyRepository.findAll(specification, page);
		List<CurrencyDTO> currencyDtos = currencyMapper.toDto(currencies.getContent());
		return new PageImpl<>(currencyDtos, page, currencies.getTotalElements());
	}

	@Transactional(readOnly = true)
	public long countByCriteria(CurrencyCriteria criteria) {
		log.debug("count by criteria : {}", criteria);
		final Specification<Currency> specification = createSpecification(criteria);
		return currencyRepository.count(specification);
	}

	protected Specification<Currency> createSpecification(CurrencyCriteria criteria) {
		Specification<Currency> specification = Specification.where(null);
		if (criteria != null) {
			if (criteria.getId() != null) {
				specification = specification.and(buildRangeSpecification(criteria.getId(), Currency_.id));
			}
			if (criteria.getName() != null) {
				specification = specification.and(buildStringSpecification(criteria.getName(), Currency_.name));
			}
			if (criteria.getCode() != null) {
				specification = specification.and(buildStringSpecification(criteria.getCode(), Currency_.code));
			}
		}
		return specification;
	}
}
