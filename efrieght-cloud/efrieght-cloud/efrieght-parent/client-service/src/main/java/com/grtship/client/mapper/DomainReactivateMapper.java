package com.grtship.client.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

import com.grtship.client.domain.DomainReactivate;
import com.grtship.core.dto.ReactivationDTO;

@Mapper(componentModel = "spring", uses = {})
@Component
public interface DomainReactivateMapper extends EntityMapper<ReactivationDTO, DomainReactivate>{

	@Mapping(source = "reactivateDto.reactivationWefDate", target = "reactivationWefDate")
	@Mapping(source = "reactivateDto.reactivationReason", target = "reactivationReason")
	DomainReactivate toEntity(ReactivationDTO reactivateDto);
}
