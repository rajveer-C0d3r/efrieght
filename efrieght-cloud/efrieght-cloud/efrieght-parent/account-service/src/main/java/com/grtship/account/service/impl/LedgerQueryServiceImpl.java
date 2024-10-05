package com.grtship.account.service.impl;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.grtship.account.criteria.LedgerCriteria;
import com.grtship.account.domain.Group;
import com.grtship.account.domain.Group_;
import com.grtship.account.domain.Ledger;
import com.grtship.account.domain.Ledger_;
import com.grtship.account.domain.ObjectAlias;
import com.grtship.account.domain.ObjectAlias_;
import com.grtship.account.generic.QueryService;
import com.grtship.account.mapper.LedgerMapper;
import com.grtship.account.repository.LedgerRepository;
import com.grtship.account.service.LedgerQueryService;
import com.grtship.account.service.ObjectAliasService;
import com.grtship.core.annotation.AccessFilter;
import com.grtship.core.dto.LedgerDTO;
import com.grtship.core.dto.ObjectAliasDTO;
import com.grtship.core.enumeration.DomainStatus;
import com.grtship.core.enumeration.NatureOfGroup;
import com.grtship.core.filter.LongFilter;

@Service
@Transactional(readOnly = true)
public class LedgerQueryServiceImpl extends QueryService<Ledger> implements LedgerQueryService {

	private static final String LEDGER = "Ledger";

	@Autowired
	private LedgerRepository ledgerRepository;

	@Autowired
	private LedgerMapper ledgerMapper;

	@Autowired
	private ObjectAliasService aliasService;

	@Override
	@AccessFilter(clientAccessFlag = true, companyAccessFlag = true, branchAccessFlag = true)
	public List<LedgerDTO> findByCriteria(LedgerCriteria criteria) {
		final Specification<Ledger> specification = createSpecification(criteria);
		List<LedgerDTO> listOfLedger = ledgerMapper.toDto(ledgerRepository.findAll(specification));
		prepareAliasList(listOfLedger);
		return listOfLedger;
	}

	@Override
	@AccessFilter(clientAccessFlag = true, companyAccessFlag = true, branchAccessFlag = true)
	public Page<LedgerDTO> findByCriteria(LedgerCriteria criteria, Pageable page) {
		final Specification<Ledger> specification = createSpecification(criteria);
		Page<Ledger> ledgers = ledgerRepository.findAll(specification, page);
		List<LedgerDTO> listOfLedger = ledgerMapper.toDto(ledgers.getContent());
		prepareAliasList(listOfLedger);
		return new PageImpl<>(listOfLedger, page, ledgers.getTotalElements());
	}

	/*** service to get ledger by Id ***/
	@Override
	public Optional<LedgerDTO> findOne(Long id) {
		LedgerCriteria ledgerCriteria = new LedgerCriteria();
		LongFilter longFilter = new LongFilter();
		longFilter.setEquals(id);
		ledgerCriteria.setId(longFilter);

		List<LedgerDTO> ledgerDto = findByCriteria(ledgerCriteria);
		return (!CollectionUtils.isEmpty(ledgerDto)) ? Optional.of(ledgerDto.get(0)) : Optional.ofNullable(null);
	}

	private void prepareAliasList(List<LedgerDTO> listOfLedger) {
		if (!CollectionUtils.isEmpty(listOfLedger)) {
			List<Long> ledgerIdList = listOfLedger.stream().filter(ledger -> ledger.getId() != null)
					.map(LedgerDTO::getId).collect(Collectors.toList());

			Map<Long, Set<ObjectAliasDTO>> aliasMap = aliasService
					.getListOfAliasByReferanceIdListAndReferenceName(ledgerIdList, LEDGER);
			if (!CollectionUtils.isEmpty(aliasMap)) {
				listOfLedger.forEach(ledger -> ledger.setAlias(aliasMap.get(ledger.getId())));
			}
		}
	}

	/****
	 * this service has used for validation based on clientId and companyId....
	 ****/
	@Override
	@AccessFilter(clientAccessFlag = true, companyAccessFlag = true, branchAccessFlag = false)
	public long countByCriteria(LedgerCriteria criteria) {
		final Specification<Ledger> specification = createSpecification(criteria);
		return ledgerRepository.count(specification);
	}

	/**
	 * createSpecification is used to create criteria for filtering..
	 **/
	private Specification<Ledger> createSpecification(LedgerCriteria criteria) {
		Specification<Ledger> specification = Specification.where(null);
		if (criteria != null) {
			specification = specification.and(getIdSpec(criteria)).and(getCodeSpec(criteria)).and(getNameSpec(criteria))
					.and(getGroupNameSpec(criteria)).and(getAliasSpec(criteria))
					.and(getActiveFlagSpec(criteria.getActiveFlag())).and(getNatureOfGroupSpec(criteria))
					.and(getIdsSpec(criteria.getIds())).and(getGroupIdSpec(criteria.getGroupId()))
					.and(getLedgerByStatus(criteria.getStatus())).and(getAliasListSpec(criteria.getAliasList()));
		}
		return specification;
	}

	private Specification<Ledger> getNameSpec(LedgerCriteria criteria) {
		if (criteria.getName() != null)
			return buildStringSpecification(criteria.getName(), Ledger_.name);
		return Specification.where(null);
	}

	private Specification<Ledger> getCodeSpec(LedgerCriteria criteria) {
		if (criteria.getCode() != null)
			return buildStringSpecification(criteria.getCode(), Ledger_.code);
		return Specification.where(null);
	}

	private Specification<Ledger> getIdSpec(LedgerCriteria criteria) {
		if (criteria.getId() != null)
			return buildRangeSpecification(criteria.getId(), Ledger_.id);
		return Specification.where(null);
	}

	private Specification<Ledger> getGroupNameSpec(LedgerCriteria criteria) {
		if (criteria.getGroupName() != null) {
			return (root, query, criteriaBuilder) -> {
				Root<Group> groupRoot = query.from(Group.class);
				return criteriaBuilder.equal(groupRoot.get("name"), criteria.getGroupName());
			};
		}
		return Specification.where(null);
	}

	private Specification<Ledger> getAliasSpec(LedgerCriteria criteria) {
		if (criteria.getAlias() != null) {
			return (root, query, criteriaBuilder) -> {
				Root<ObjectAlias> aliasRoot = query.from(ObjectAlias.class);
				return criteriaBuilder.and(criteriaBuilder.equal(aliasRoot.get(ObjectAlias_.name), criteria.getAlias()),
						criteriaBuilder.equal(aliasRoot.get(ObjectAlias_.referenceName), LEDGER),
						criteriaBuilder.equal(root.get(Ledger_.id), aliasRoot.get(ObjectAlias_.referenceId)));
			};
		}
		return Specification.where(null);
	}

	private Specification<Ledger> getAliasListSpec(List<String> aliasList) {
		if (!CollectionUtils.isEmpty(aliasList)) {
			return (root, query, criteriaBuilder) -> {
				Root<ObjectAlias> aliasRoot = query.from(ObjectAlias.class);
				return criteriaBuilder.and(aliasRoot.get(ObjectAlias_.name).in(aliasList),
						criteriaBuilder.equal(aliasRoot.get(ObjectAlias_.referenceName), LEDGER),
						criteriaBuilder.equal(root.get(Ledger_.id), aliasRoot.get(ObjectAlias_.referenceId)));
			};
		}
		return Specification.where(null);

	}

	private Specification<Ledger> getNatureOfGroupSpec(LedgerCriteria criteria) {
		if (criteria.getNatureOfGroup() != null) {
			return (root, query, criteriaBuilder) -> {
				Root<Group> group = query.from(Group.class);
				return criteriaBuilder.and(criteriaBuilder.equal(root.get(Ledger_.group), group.get(Group_.id)),
						criteriaBuilder.equal(group.get(Group_.natureOfGroup),
								NatureOfGroup.valueOf(criteria.getNatureOfGroup())));
			};
		}
		return Specification.where(null);
	}

	private Specification<Ledger> getActiveFlagSpec(Boolean activeFlag) {
		if (activeFlag != null)
			return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(Ledger_.activeFlag), activeFlag);
		return Specification.where(null);
	}

	private Specification<Ledger> getGroupIdSpec(Long groupId) {
		if (groupId != null)
			return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(Ledger_.group), groupId);
		return Specification.where(null);
	}

	private Specification<Ledger> getIdsSpec(List<Long> ids) {
		if (!CollectionUtils.isEmpty(ids))
			return (root, query, criteriaBuilder) -> root.get("id").in(ids);
		return Specification.where(null);
	}

	public static Specification<Ledger> getLedgerByStatus(String status) {
		if (status != null) {
			return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(Ledger_.status),
					DomainStatus.valueOf(status));
		}
		return Specification.where(null);
	}

}
