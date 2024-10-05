package com.grtship.account.service.impl;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.criteria.Root;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.grtship.account.criteria.GroupCriteria;
import com.grtship.account.domain.Group;
import com.grtship.account.domain.Group_;
import com.grtship.account.domain.ObjectAlias;
import com.grtship.account.domain.ObjectAlias_;
import com.grtship.account.generic.QueryService;
import com.grtship.account.mapper.GroupMapper;
import com.grtship.account.repository.GroupRepository;
import com.grtship.account.service.GroupQueryService;
import com.grtship.account.service.ObjectAliasService;
import com.grtship.core.annotation.AccessFilter;
import com.grtship.core.constant.ReferenceNameConstant;
import com.grtship.core.dto.BaseDTO;
import com.grtship.core.dto.GroupDTO;
import com.grtship.core.dto.ObjectAliasDTO;
import com.grtship.core.enumeration.DomainStatus;
import com.grtship.core.enumeration.NatureOfGroup;
import com.grtship.core.filter.LongFilter;

/**
 * Service for executing complex queries for {@link Group} entities in the
 * database. The main input is a {@link GroupCriteria} which gets converted to
 * {@link Specification}, in a way that all the filters must apply. It returns a
 * {@link List} of {@link GroupDTO} or a {@link Page} of {@link GroupDTO} which
 * fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class GroupQueryServiceImpl extends QueryService<Group> implements GroupQueryService {

	private final Logger log = LoggerFactory.getLogger(GroupQueryServiceImpl.class);

	private static final String GROUP = "Group";

	@Autowired
	private GroupRepository groupRepository;

	@Autowired
	private GroupMapper groupMapper;

	@Autowired
	private ObjectAliasService aliasService;

	/**
	 * Return a {@link List} of {@link GroupDTO} which matches the criteria from the
	 * database.
	 * 
	 * @param criteria The object which holds all the filters, which the entities
	 *                 should match.
	 * @return the matching entities.
	 */
	@Transactional(readOnly = true)
	@AccessFilter(clientAccessFlag = true, companyAccessFlag = true, branchAccessFlag = false)
	public List<GroupDTO> findByCriteria(GroupCriteria criteria) {
		log.debug("find by criteria : {}", criteria);
		final Specification<Group> specification = createSpecification(criteria);
		List<GroupDTO> dtos = groupMapper.toDto(groupRepository.findAll(specification));
		prepareChildList(dtos);
		return dtos;
	}

	/**
	 * Return a {@link Page} of {@link GroupDTO} which matches the criteria from the
	 * database.
	 * 
	 * @param criteria The object which holds all the filters, which the entities
	 *                 should match.
	 * @param page     The page, which should be returned.
	 * @return the matching entities.
	 */
	@Transactional(readOnly = true)
	@AccessFilter(clientAccessFlag = true, companyAccessFlag = true, branchAccessFlag = false)
	public Page<GroupDTO> findByCriteria(GroupCriteria criteria, Pageable page) {
		log.debug("find by criteria : {}, page: {}", criteria, page);
		final Specification<Group> specification = createSpecification(criteria);
		Page<Group> groups = groupRepository.findAll(specification, page);
		List<GroupDTO> listOfGroups = groupMapper.toDto(groups.getContent());
		prepareChildList(listOfGroups);
		return new PageImpl<>(listOfGroups, page, groups.getTotalElements());
	}

	private void prepareChildList(List<GroupDTO> listOfGroups) {
		log.debug("groups received in prepareChildList {} ", listOfGroups);
		if (!CollectionUtils.isEmpty(listOfGroups)) {
			List<Long> groupIdList = listOfGroups.stream().filter(groupDTO -> groupDTO.getId() != null)
					.map(GroupDTO::getId).collect(Collectors.toList());
			Map<Long, Set<ObjectAliasDTO>> aliasMap = aliasService
					.getListOfAliasByReferanceIdListAndReferenceName(groupIdList, ReferenceNameConstant.GROUP);
			if (!CollectionUtils.isEmpty(aliasMap)) {
				listOfGroups.forEach(groupDTO -> groupDTO.setAliases(aliasMap.get(groupDTO.getId())));
			}
		}
	}

	/**
	 * Return the number of matching entities in the database.
	 * 
	 * @param criteria The object which holds all the filters, which the entities
	 *                 should match.
	 * @return the number of matching entities.
	 */
	@Transactional(readOnly = true)
	public long countByCriteria(GroupCriteria criteria) {
		log.debug("count by criteria : {}", criteria);
		final Specification<Group> specification = createSpecification(criteria);
		return groupRepository.count(specification);
	}

	/**
	 * Function to convert {@link GroupCriteria} to a {@link Specification}
	 * 
	 * @param criteria The object which holds all the filters, which the entities
	 *                 should match.
	 * @return the matching {@link Specification} of the entity.
	 */
	protected Specification<Group> createSpecification(GroupCriteria criteria) {
		Specification<Group> specification = Specification.where(null);
		if (criteria != null) {
			specification = specification.and(getIdSpec(criteria)).and(getCodeSpec(criteria)).and(getNameSpec(criteria))
					.and(getNatureOfGroupSpec(criteria)).and(getAliasSpec(criteria)).and(getStatusSpec(criteria))
					.and(getActiveFlagSpec(criteria)).and(getParentGroupNameSpec(criteria));
		}
		return specification;
	}

	private Specification<Group> getIdSpec(GroupCriteria criteria) {
		if (criteria.getId() != null)
			return buildRangeSpecification(criteria.getId(), Group_.id);
		return Specification.where(null);
	}

	private Specification<Group> getCodeSpec(GroupCriteria criteria) {
		if (criteria.getCode() != null)
			return buildStringSpecification(criteria.getCode(), Group_.code);
		return Specification.where(null);
	}

	private Specification<Group> getNameSpec(GroupCriteria criteria) {
		if (criteria.getName() != null)
			return buildStringSpecification(criteria.getName(), Group_.name);
		return Specification.where(null);
	}

	private Specification<Group> getNatureOfGroupSpec(GroupCriteria criteria) {
		if (criteria.getNatureOfGroup() != null) {
			return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(Group_.natureOfGroup),
					NatureOfGroup.valueOf(criteria.getNatureOfGroup()));
		}
		return Specification.where(null);
	}

	private Specification<Group> getAliasSpec(GroupCriteria criteria) {
		if (criteria.getAlias() != null) {
			return (root, query, criteriaBuilder) -> {
				Root<ObjectAlias> aliasroot = query.from(ObjectAlias.class);
				return criteriaBuilder.and(criteriaBuilder.equal(aliasroot.get(ObjectAlias_.name), criteria.getAlias()),
						criteriaBuilder.equal(aliasroot.get(ObjectAlias_.referenceName), ReferenceNameConstant.GROUP),
						criteriaBuilder.equal(aliasroot.get(ObjectAlias_.referenceId), root.get(Group_.id)));
			};
		}
		return Specification.where(null);
	}

	private Specification<Group> getStatusSpec(GroupCriteria criteria) {
		if (criteria.getStatus() != null) {
			return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(Group_.status),
					DomainStatus.valueOf(criteria.getStatus()));
		}
		return Specification.where(null);
	}

	private Specification<Group> getActiveFlagSpec(GroupCriteria criteria) {
		if (criteria.getActiveFlag() != null)
			return buildSpecification(criteria.getActiveFlag(), Group_.activeFlag);
		return Specification.where(null);
	}

	private Specification<Group> getParentGroupNameSpec(GroupCriteria criteria) {
		if (criteria.getParentGroupName() != null)
			return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get(Group_.parentGroup).get(Group_.name),
					"%" + criteria.getParentGroupName() + "%");
		return Specification.where(null);
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<GroupDTO> findOne(Long id) {
		log.debug("Request to get Group : {}", id);
		GroupCriteria criteria = new GroupCriteria();
		LongFilter filter = new LongFilter();
		filter.setEquals(id);
		criteria.setId(filter);
		List<GroupDTO> groups = findByCriteria(criteria);
		return (!CollectionUtils.isEmpty(groups)) ? Optional.of(groups.get(0)) : Optional.ofNullable(null);
	}

	@Override
	@Transactional(readOnly = true)
	@AccessFilter(clientAccessFlag = true, companyAccessFlag = true, branchAccessFlag = false)
	public List<BaseDTO> getParentGroups() {
		List<GroupDTO> groups = groupRepository.findByParentGroupIsNull().stream().map(groupMapper::toDto)
				.collect(Collectors.toList());
		return groups.stream().filter(groupDTO -> groupDTO.getId() != null && groupDTO.getName() != null)
				.map(groupDTO -> new BaseDTO(groupDTO.getId(), groupDTO.getName())).collect(Collectors.toList());
	}

	@Override
	@Transactional(readOnly = true)
	@AccessFilter(clientAccessFlag = true, companyAccessFlag = true, branchAccessFlag = false)
	public List<BaseDTO> getChildGroups(Long id) {
		List<GroupDTO> groups = groupRepository.findByTreeIdContaining(id + ".").stream().map(groupMapper::toDto)
				.collect(Collectors.toList());
		return groups.stream().filter(groupDTO -> groupDTO.getId() != null && groupDTO.getName() != null)
				.map(groupDTO -> new BaseDTO(groupDTO.getId(), groupDTO.getName())).collect(Collectors.toList());
	}
}
