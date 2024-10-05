package com.grtship.account.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.grtship.account.domain.DomainDeactivate;
import com.grtship.account.interfaces.EntityMapper;
import com.grtship.core.dto.DeactivationDTO;

@Mapper(componentModel = "spring", uses = {})
public interface DomainDeactivateMapper extends EntityMapper<DeactivationDTO, DomainDeactivate> {

	@Mapping(source = "deactivateDto.deactivationWefDate", target = "wefDate")
	@Mapping(source = "deactivateDto.deactivationReason", target = "reason")
	@Mapping(source = "deactivateDto.deactivateAutoGenerateId", target = "deactivateAutoGenerateId")
	DomainDeactivate toEntity(DeactivationDTO deactivateDto);
}
