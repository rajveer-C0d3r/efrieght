package com.grtship.mdm.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

import com.grtship.core.dto.DeactivationDTO;
import com.grtship.mdm.domain.DomainDeactivate;
import com.grtship.mdm.interfaces.EntityMapper;

@Mapper(componentModel = "spring", uses = {})
@Component
public interface DomainDeactivateMapper extends EntityMapper<DeactivationDTO, DomainDeactivate>{
	
	@Mapping(source = "deactivationDto.deactivationReason", target = "deactivationReason")
	@Mapping(source = "deactivationDto.deactivationWefDate", target = "deactivationWefDate")
	@Mapping(source = "deactivationDto.deactivateAutoGenerateId", target = "deactivateAutoGenerateId")//use this id to reactivate entity and its branches.
	DomainDeactivate toEntity(DeactivationDTO deactivationDto);

}
