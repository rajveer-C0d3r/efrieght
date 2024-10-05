package com.grtship.client.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

import com.grtship.client.domain.DomainDeactivate;
import com.grtship.core.dto.DeactivationDTO;

@Mapper(componentModel = "spring", uses = {})
@Component
public interface DomainDeactivateMapper extends EntityMapper<DeactivationDTO, DomainDeactivate> {

	@Mapping(source = "deactivateDto.deactivationWefDate", target = "deactivationWefDate")
	@Mapping(source = "deactivateDto.deactivationReason", target = "deactivationReason")
	@Mapping(source = "deactivateDto.deactivateAutoGenerateId", target = "deactivateAutoGenerateId")
	DomainDeactivate toEntity(DeactivationDTO deactivateDto);
}
