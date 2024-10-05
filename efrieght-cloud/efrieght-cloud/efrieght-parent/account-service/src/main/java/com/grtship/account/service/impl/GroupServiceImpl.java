
package com.grtship.account.service.impl;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.grtship.account.domain.Group;
import com.grtship.account.mapper.DomainDeactivateMapper;
import com.grtship.account.mapper.DomainReactivateMapper;
import com.grtship.account.mapper.GroupMapper;
import com.grtship.account.repository.GroupRepository;
import com.grtship.account.service.GroupService;
import com.grtship.account.service.ObjectAliasService;
import com.grtship.account.validator.GroupValidator;
import com.grtship.common.exception.InvalidDataException;
import com.grtship.core.annotation.Auditable;
import com.grtship.core.annotation.Auditable.ActionType;
import com.grtship.core.annotation.Authorize;
import com.grtship.core.annotation.Validate;
import com.grtship.core.constant.ErrorCode;
import com.grtship.core.constant.ReferenceNameConstant;
import com.grtship.core.dto.AuthorizationObjectDTO;
import com.grtship.core.dto.DeactivationDTO;
import com.grtship.core.dto.DefaultChartofAccountDTO;
import com.grtship.core.dto.GroupCreationDTO;
import com.grtship.core.dto.GroupDTO;
import com.grtship.core.dto.ObjectAliasDTO;
import com.grtship.core.dto.ReactivationDTO;
import com.grtship.core.enumeration.DomainStatus;
import com.grtship.core.enumeration.ModuleName;

/**
 * Service Implementation for managing {@link Group}.
 */
@Service
@Transactional
public class GroupServiceImpl implements GroupService {

	private static final String INVALID_ID = "Invalid Id.";

	private static final String GROUP_NOT_FOUND_FOR_GIVEN_ID = "Group not found for given Id..!!";

	private static final String GROUP = "Group";

	@Autowired
	private GroupRepository groupRepository;

	@Autowired
	private GroupMapper groupMapper;

	@Autowired
	private GroupValidator groupValidator;

	@Autowired
	private ObjectAliasService aliasService;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private DomainDeactivateMapper domainDeactivateMapper;

	@Autowired
	private DomainReactivateMapper domainReactivateMapper;

	@Value("classpath:config/groups.json")
	private Resource resourceFile;

	@Override
	@Authorize(moduleName = ModuleName.GROUP,action = com.grtship.core.enumeration.ActionType.SAVE)
	@Auditable(action = ActionType.SAVE, module = com.grtship.core.annotation.Auditable.Module.GROUP)
	@Validate(validator = "groupValidatorImpl", action = "save")
	public GroupDTO save(GroupCreationDTO groupDto) {
		Group savedGroup = groupRepository.save(groupMapper.toEntity(groupDto));
		if (groupDto.getAliases() != null)
			aliasService.saveAll(groupDto.getAliases(), savedGroup.getId(), GROUP);
		return groupMapper.toDto(prepareAndSaveTreeId(savedGroup));
	}

	/** this is used to generate treeId... **/
	private Group prepareAndSaveTreeId(Group savedGroup) {
		GroupDTO dto = groupMapper.toDto(savedGroup);
		if (dto.getParentGroupId() == null) {
			dto.setTreeId(dto.getId().toString());
			return groupRepository.save(groupMapper.toEntity(dto));
		} else {
			Optional<Group> parentIdGroup = groupRepository.findById(dto.getParentGroupId());
			if (parentIdGroup.isPresent()) {
				Group parentGroup = parentIdGroup.get();
				String treeId = parentGroup.getTreeId() + "." + dto.getId();
				dto.setTreeId(treeId);
				return groupRepository.save(groupMapper.toEntity(dto));
			}
		}
		return savedGroup;
	}

	@Override
	@Auditable(action = ActionType.UPDATE, module = com.grtship.core.annotation.Auditable.Module.GROUP)
	@Validate(validator = "groupValidatorImpl", action = "update")
	public GroupDTO update(GroupDTO groupDto) {
		Long oldParentGroupId = null;
		Optional<Group> optionalGroup = groupRepository.findById(groupDto.getId());
		if (optionalGroup.isPresent()) {
			groupValidator.isApprovalPending(groupDto.getSubmittedForApproval()); // check update is valid or not
			groupDto.setCode(optionalGroup.get().getCode());
			if(optionalGroup.get().getParentGroup()!=null)
				oldParentGroupId = optionalGroup.get().getParentGroup().getId();
			aliasService.deleteAliasOnUpdate(groupDto.getAliases(), ReferenceNameConstant.GROUP, groupDto.getId());
			Group savedGroup = prepareAndSaveTreeId(groupRepository.save(groupMapper.toEntity(groupDto)));
			if (oldParentGroupId != null && groupDto.getParentGroupId() != null
					&& !oldParentGroupId.equals(groupDto.getParentGroupId()))
				updateChildTreeId(groupDto);
			if (groupDto.getAliases() != null)
				aliasService.saveAll(groupDto.getAliases(), savedGroup.getId(), GROUP);

			return groupMapper.toDto(savedGroup);
		} else {
			throw new InvalidDataException(ErrorCode.INVALID_DATA_ERROR, INVALID_ID);
		}
	}

	/*** this method is used to update tree id.... ***/
	private void updateChildTreeId(GroupCreationDTO groupDto) {
		List<Group> groups = groupRepository.findByTreeIdContaining(groupDto.getId() + ".");
		if (!CollectionUtils.isEmpty(groups))
			groups.stream().forEach(this::prepareAndSaveTreeId);
	}

	@Override
	@Auditable(action = ActionType.DELETE, module = com.grtship.core.annotation.Auditable.Module.GROUP)
	public void delete(Long id) {
		Optional<GroupDTO> dto = groupRepository.findById(id).map(groupMapper::toDto);
		if (dto.isPresent() && !CollectionUtils.isEmpty(dto.get().getAliases())) {
			List<Long> aliasIdsToDelete = dto.get().getAliases().stream().filter(alias -> alias.getId() != null)
					.map(ObjectAliasDTO::getId).collect(Collectors.toList());
			aliasService.deleteByReferenceNameAndIdIn(GROUP, aliasIdsToDelete);
		}
		groupRepository.deleteById(id);
	}

	@Override
	@Transactional
	@Auditable(action = ActionType.DEACTIVATE, module = com.grtship.core.annotation.Auditable.Module.GROUP)
	@Validate(validator = "groupValidatorImpl", action = "deactivate")
	public GroupDTO deactivate(DeactivationDTO deactivateDto) {
		Optional<Group> groupById = groupRepository.findById(deactivateDto.getReferenceId());
		if (!groupById.isPresent())
			throw new InvalidDataException(ErrorCode.INVALID_DATA_ERROR, GROUP_NOT_FOUND_FOR_GIVEN_ID);
		Group group = groupById.get();
		group.setSubmittedForApproval(Boolean.TRUE);
		group.setDeactivate(domainDeactivateMapper.toEntity(deactivateDto));
		group = groupRepository.save(group);
		return groupMapper.toDto(group);
	}

	@Override
	@Transactional
	@Auditable(action = ActionType.REACTIVATE, module = com.grtship.core.annotation.Auditable.Module.GROUP)
	@Validate(validator = "groupValidatorImpl", action = "reactivate")
	public GroupDTO reactivate(ReactivationDTO reactivationDto) {
		Optional<Group> groupById = groupRepository.findById(reactivationDto.getReferenceId());
		if (!groupById.isPresent())
			throw new InvalidDataException(ErrorCode.INVALID_DATA_ERROR, GROUP_NOT_FOUND_FOR_GIVEN_ID);
		Group group = groupById.get();
		group.setSubmittedForApproval(Boolean.TRUE);
		group.setReactivate(domainReactivateMapper.toEntity(reactivationDto));
		group = groupRepository.save(group);
		return groupMapper.toDto(group);
	}

	public void createBaseGroups(Long companyId) throws IOException {
		objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
		JsonNode jsonNode = objectMapper.readTree(resourceFile.getFile());
		Iterator<JsonNode> nodes = jsonNode.get("deafultGroups").elements();
		nodes.forEachRemaining(obj -> {
			JsonNode node = nodes.next();
			DefaultChartofAccountDTO group = new DefaultChartofAccountDTO(node.get("name").asText(),
					node.get("code").asText(), node.get("natureOfGroup").asText());
			Group convertedGroup = convertToGroup(group);
			convertedGroup.setCompanyId(companyId);
			saveChartOfAccount(convertedGroup);

		});
	}

	private void saveChartOfAccount(Group group) {
		groupRepository.save(group);
		if (group.getId() != null) {
			group.setTreeId(group.getId().toString());
			groupRepository.save(group);
		}
	}

	private Group convertToGroup(DefaultChartofAccountDTO group) {
		return new Group().name(group.getName()).code(group.getCode()).natureOfGroup(group.getNatureOfGroup());
	}

	@Override
	public Boolean approveGroup(AuthorizationObjectDTO authorizationObjectDTO) {
		if(authorizationObjectDTO.getAction().equals(ActionType.SAVE)) {
			return prepareDataForSave(authorizationObjectDTO);
		}
		return false;
	}

	private Boolean prepareDataForSave(AuthorizationObjectDTO authorizationObjectDTO) {
		Optional<Group> optionalGroup=groupRepository.findById(authorizationObjectDTO.getReferenceId());
		if(optionalGroup.isPresent()) {
		   Group groupToSave=new Group();
		   if(authorizationObjectDTO.getStatus().equals(DomainStatus.APPROVED)) {
			   groupToSave=groupMapper.toApprovedGroup(optionalGroup.get());	   
		   } else {
			   groupToSave=optionalGroup.get();
			   groupToSave.setStatus(authorizationObjectDTO.getStatus());
		   }
		   Group updatedGroup=groupRepository.save(groupToSave);
		   if(ObjectUtils.isNotEmpty(updatedGroup)) {
			   return true;
		   }
		}
		return false;
	}
}
