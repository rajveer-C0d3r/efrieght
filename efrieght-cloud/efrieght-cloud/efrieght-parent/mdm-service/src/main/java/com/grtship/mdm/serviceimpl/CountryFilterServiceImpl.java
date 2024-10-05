package com.grtship.mdm.serviceimpl;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.grtship.core.dto.CountryDTO;
import com.grtship.core.dto.DocumentDTO;
import com.grtship.core.dto.ObjectAliasDTO;
import com.grtship.core.enumeration.DomainStatus;
import com.grtship.core.filter.LongFilter;
import com.grtship.core.filter.StringFilter;
import com.grtship.mdm.criteria.CountryCriteria;
import com.grtship.mdm.domain.Country;
import com.grtship.mdm.domain.Country_;
import com.grtship.mdm.domain.ObjectAlias;
import com.grtship.mdm.domain.ObjectAlias_;
import com.grtship.mdm.domain.Sector_;
import com.grtship.mdm.generic.QueryService;
import com.grtship.mdm.mapper.CountryMapper;
import com.grtship.mdm.repository.CountryRepository;
import com.grtship.mdm.service.DocumentService;
import com.grtship.mdm.service.ObjectAliasQueryService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(readOnly = true)
public class CountryFilterServiceImpl extends QueryService<Country> {

	private static final String COUNTRY = "Country";
	private final CountryRepository countryRepository;
	private final CountryMapper countryMapper;

	@Autowired
	private ObjectAliasQueryService aliasQueryService;

	@Autowired
	private DocumentService documentService;

	public CountryFilterServiceImpl(CountryRepository countryRepository, CountryMapper countryMapper) {
		this.countryRepository = countryRepository;
		this.countryMapper = countryMapper;
	}

	@Transactional(readOnly = true)
	public List<CountryDTO> findByCriteria(CountryCriteria criteria) {
		log.debug("find by criteria : {}", criteria);
		final Specification<Country> specification = createSpecification(criteria);
		List<CountryDTO> countryDtos = countryMapper.toDto(countryRepository.findAll(specification));
		prepareAliasAndDocList(countryDtos);

		return countryDtos;
	}

	@Transactional(readOnly = true)
	public Page<CountryDTO> findByCriteria(CountryCriteria criteria, Pageable page) {
		final Specification<Country> specification = createSpecification(criteria);
		Page<Country> countries = countryRepository.findAll(specification, page);
		List<CountryDTO> countryDtos = countryMapper.toDto(countries.getContent());
		prepareAliasAndDocList(countryDtos);
		return new PageImpl<>(countryDtos, page, countries.getTotalElements());
	}

	private void prepareAliasAndDocList(List<CountryDTO> countryDtos) {
		List<Long> countryIds = countryDtos.stream().filter(obj -> obj.getId() != null).map(CountryDTO::getId)
				.collect(Collectors.toList());
		if (!CollectionUtils.isEmpty(countryIds)) {
			List<ObjectAliasDTO> aliasList = aliasQueryService
					.getListOfAliasByReferanceIdListAndReferenceName(countryIds, COUNTRY);
			Map<Long, Set<DocumentDTO>> documentDtoMap = documentService
					.getMapOfDocumentsByReferenceIdListAndReferenceName(countryIds, COUNTRY);
			countryDtos.forEach(countryDto -> {
				countryDto.setAliases(aliasList.stream().filter(obj -> obj.getReferenceId().equals(countryDto.getId()))
						.collect(Collectors.toSet()));
				countryDto.setDocuments(documentDtoMap.get(countryDto.getId()));
			});
		}
	}

	@Transactional(readOnly = true)
	public long countByCriteria(CountryCriteria criteria) {
		log.debug("count by criteria : {}", criteria);
		final Specification<Country> specification = createSpecification(criteria);
		return countryRepository.count(specification);
	}

	private Specification<Country> createSpecification(CountryCriteria criteria) {
		Specification<Country> specification = Specification.where(null);
		if (criteria != null) {
			specification = specification.and(getCountryByIdSpec(criteria.getId()))
					.and(getCountryByNameSpec(criteria.getName())).and(getCountryByCodeSpec(criteria.getCode()))
					.and(getCountryBySectorName(criteria.getSectorName())).and(getCountryByAlias(criteria.getAlias()))
					.and(getCountryByStatus(criteria.getStatus()))
					.and(getCountryByActiveFlag(criteria.getActiveFlag()));
		}
		return specification;
	}

	private Specification<Country> getCountryByIdSpec(LongFilter id) {
		if (id != null)
			return buildRangeSpecification(id, Country_.id);
		return Specification.where(null);
	}

	private Specification<Country> getCountryByNameSpec(StringFilter name) {
		if (name != null)
			return buildStringSpecification(name, Country_.name);
		return Specification.where(null);
	}

	private Specification<Country> getCountryByCodeSpec(StringFilter code) {
		if (code != null)
			return buildStringSpecification(code, Country_.code);
		return Specification.where(null);
	}

	private Specification<Country> getCountryBySectorName(StringFilter sectorName) {
		if (sectorName != null)
			return buildSpecification(sectorName, root -> root.join(Country_.sector, JoinType.LEFT).get(Sector_.NAME));
		return Specification.where(null);
	}

	private Specification<Country> getCountryByAlias(String alias) {
		if (alias != null)
			return (root, query, criteriaBuilder) -> {
				Root<ObjectAlias> aliasroot = query.from(ObjectAlias.class);
				return criteriaBuilder.and(criteriaBuilder.like(aliasroot.get(ObjectAlias_.name), "%" + alias + "%"),
						criteriaBuilder.equal(aliasroot.get(ObjectAlias_.referenceName), COUNTRY),
						criteriaBuilder.equal(root.get(Country_.id), aliasroot.get(ObjectAlias_.referenceId)));
			};
		return Specification.where(null);
	}

	private Specification<Country> getCountryByStatus(String status) {
		if (status != null)
			return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(Country_.status),
					DomainStatus.valueOf(status));
		return Specification.where(null);
	}

	private Specification<Country> getCountryByActiveFlag(Boolean activeFlag) {
		if (activeFlag != null)
			return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(Country_.activeFlag), activeFlag);
		return Specification.where(null);
	}

}
