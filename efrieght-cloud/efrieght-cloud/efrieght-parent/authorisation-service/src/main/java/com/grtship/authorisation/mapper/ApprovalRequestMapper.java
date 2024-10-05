package com.grtship.authorisation.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.grtship.core.dto.ApprovalRequestDTO;
import com.grtship.core.dto.AuthorizationObjectDTO;

@Mapper(componentModel = "spring")
public abstract class ApprovalRequestMapper {

	@Mapping(target = "moduleName",expression = "java(this.setModuleName(authorizationObjectDTO))")
	public abstract ApprovalRequestDTO toRequestDto(AuthorizationObjectDTO authorizationObjectDTO);
	
	protected String setModuleName(AuthorizationObjectDTO authorizationObjectDTO) {
		return authorizationObjectDTO.getModuleName().name();
	}
}
