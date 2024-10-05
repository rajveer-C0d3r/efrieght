package com.grtship.account.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import com.grtship.account.adaptor.MasterModuleAdapter;
import com.grtship.account.domain.Group;
import com.grtship.account.interfaces.EntityMapper;
import com.grtship.core.constant.ReferenceNameConstant;
import com.grtship.core.dto.GroupCreationDTO;
import com.grtship.core.dto.GroupDTO;
import com.grtship.core.enumeration.DomainStatus;

/**
 * Mapper for the entity {@link Group} and its DTO {@link GroupDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public abstract class GroupMapper implements EntityMapper<GroupDTO, Group> {

	@Autowired
	private MasterModuleAdapter masterModuleAdapter;

	@Mapping(source = "parentGroup.id", target = "parentGroupId")
//	@Mapping(source = "parentGroup.name", target = "parentGroupName")
	@Mapping(source = "reactivate.wefDate", target = "reactivate.reactivationWefDate")
	@Mapping(source = "reactivate.reactivatedDate", target = "reactivate.reactivatedDate")
	@Mapping(source = "reactivate.reason", target = "reactivate.reactivationReason")
	@Mapping(source = "deactivate.wefDate", target = "deactivate.deactivationWefDate")
	@Mapping(source = "deactivate.deactivatedDate", target = "deactivate.deactivatedDate")
	@Mapping(source = "deactivate.reason", target = "deactivate.deactivationReason")
	@Mapping(source = "name", target = "name")
	public abstract GroupDTO toDto(Group group);

	@Mapping(source = "parentGroupId", target = "parentGroup.id")
	@Mapping(source = "reactivate.reactivationWefDate", target = "reactivate.wefDate")
	@Mapping(source = "reactivate.reactivatedDate", target = "reactivate.reactivatedDate")
	@Mapping(source = "reactivate.reactivationReason", target = "reactivate.reason")
	@Mapping(source = "deactivate.deactivationWefDate", target = "deactivate.wefDate")
	@Mapping(source = "deactivate.deactivatedDate", target = "deactivate.deactivatedDate")
	@Mapping(source = "deactivate.deactivationReason", target = "deactivate.reason")
	@Mapping(target = "submittedForApproval", constant = "true")
	@Mapping(source = "name", target = "name")
	public abstract Group toEntity(GroupDTO groupDTO);

	@Mapping(source = "parentGroupId", target = "parentGroup.id")
	@Mapping(target = "activeFlag", constant = "false")
	@Mapping(target = "submittedForApproval", constant = "true")
	@Mapping(target = "code", expression = "java(this.setCode(groupDto))")
	@Mapping(source = "name", target = "name")
	public abstract Group toEntity(GroupCreationDTO groupDto);
	
	@Mapping(target = "submittedForApproval",constant = "false")
	@Mapping(target = "activeFlag",constant = "true")
	@Mapping(target = "status",expression = "java(this.setStatus())")
	public abstract Group toApprovedGroup(Group group);
	
	public DomainStatus setStatus() {
		return DomainStatus.APPROVED;
	}

	public String setCode(GroupCreationDTO groupDto) {
		return (StringUtils.hasText(groupDto.getCode())) ? masterModuleAdapter.generateCode(ReferenceNameConstant.GROUP, null) : groupDto.getCode();
	}

	Group fromId(Long id) {
		if (id == null) {
			return null;
		}
		Group group = new Group();
		group.setId(id);
		return group;
	}
	
	protected DomainStatus getStatus() {
		return DomainStatus.PENDING;
	}
}
