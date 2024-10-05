package com.grtship.mdm.serviceimpl;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.grtship.core.annotation.AccessFilter;
import com.grtship.core.annotation.Auditable;
import com.grtship.core.annotation.Auditable.ActionType;
import com.grtship.core.dto.EntityGroupCriteriaDTO;
import com.grtship.core.dto.EntityGroupDTO;
import com.grtship.mdm.domain.EntityGroup;
import com.grtship.mdm.mapper.EntityGroupMapper;
import com.grtship.mdm.repository.EntityGroupRepository;
import com.grtship.mdm.service.CodeGeneratorService;
import com.grtship.mdm.service.EntityGroupService;
import com.grtship.mdm.specs.EntityGroupSpec;

@Service
@Transactional
public class EntityGroupServiceImpl implements EntityGroupService {

	private final EntityGroupRepository entityGroupsRepository;

	private final EntityGroupMapper entityGroupsMapper;

	@Autowired
	private CodeGeneratorService codeGeneratorService;

	public EntityGroupServiceImpl(EntityGroupRepository entityGroupsRepository, EntityGroupMapper entityGroupsMapper) {
		this.entityGroupsRepository = entityGroupsRepository;
		this.entityGroupsMapper = entityGroupsMapper;
	}

	/**
	 * Save a entityGroups.
	 *
	 * @param entityGroupsDTO the entity to save.
	 * @return the persisted entity.
	 */
	@Override
	@Auditable(action = ActionType.SAVE, module = com.grtship.core.annotation.Auditable.Module.ENTITY_GROUP)
	public EntityGroupDTO save(EntityGroupDTO entityGroupsDto) {
		if (StringUtils.isEmpty(entityGroupsDto.getCode()))
			generateEntityCode(entityGroupsDto);
		EntityGroup entityGroupByName = entityGroupsRepository.getByName(entityGroupsDto.getName());
		if (entityGroupByName != null)
			return entityGroupsMapper.toDto(entityGroupByName);
		EntityGroup entityGroups = entityGroupsMapper.toEntity(entityGroupsDto);
		entityGroups = entityGroupsRepository.save(entityGroups);
		return entityGroupsMapper.toDto(entityGroups);
	}

	private void generateEntityCode(EntityGroupDTO entityGroupsDto) {
		String entityGroupCode = codeGeneratorService.generateCode("EntityGroup", null);
		entityGroupsDto.setCode(entityGroupCode);
	}

	/**
	 * Get all the entityGroups.
	 *
	 * @return the list of entities.
	 */
	@AccessFilter(clientAccessFlag = true, companyAccessFlag = false, branchAccessFlag = false)
	@Transactional(readOnly = true)
	@Override
	public List<EntityGroupDTO> findAll(EntityGroupCriteriaDTO entityGroupsCriteria) {

		return entityGroupsRepository.findAll(EntityGroupSpec.getEntityGroupsBySpecs(entityGroupsCriteria)).stream()
				.map(entityGroupsMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
	}

	/**
	 * Get one entityGroups by id.
	 *
	 * @param id the id of the entity.
	 * @return the entity.
	 */
	@Transactional(readOnly = true)
	@Override
	public Optional<EntityGroupDTO> findOne(Long id) {
		return entityGroupsRepository.findById(id).map(entityGroupsMapper::toDto);
	}

	/**
	 * Delete the entityGroups by id.
	 *
	 * @param id the id of the entity.
	 */
	@Override
	@Auditable(action = ActionType.DELETE, module = com.grtship.core.annotation.Auditable.Module.ENTITY_GROUP)
	public void delete(Long id) {
		entityGroupsRepository.deleteById(id);
	}

}
