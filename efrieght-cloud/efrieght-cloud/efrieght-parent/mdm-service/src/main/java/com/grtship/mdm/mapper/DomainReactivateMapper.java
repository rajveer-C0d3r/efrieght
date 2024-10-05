package com.grtship.mdm.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

import com.grtship.core.dto.DeactivationDTO;
import com.grtship.core.dto.ReactivationDTO;
import com.grtship.mdm.domain.DomainDeactivate;
import com.grtship.mdm.domain.DomainReactivate;
import com.grtship.mdm.interfaces.EntityMapper;

@Mapper(componentModel = "spring",uses = {})
@Component
public interface DomainReactivateMapper extends EntityMapper<ReactivationDTO,DomainReactivate> {

	@Mapping(source = "reactivationDto.reactivationReason", target = "reactivationReason")
	@Mapping(source = "reactivationDto.reactivationWefDate", target = "reactivationWefDate")	
	DomainReactivate toEntity(ReactivationDTO reactivationDto);
}
