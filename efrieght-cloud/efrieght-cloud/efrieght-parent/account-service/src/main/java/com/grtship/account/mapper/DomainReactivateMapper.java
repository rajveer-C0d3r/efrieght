package com.grtship.account.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.grtship.account.domain.DomainReactivate;
import com.grtship.account.interfaces.EntityMapper;
import com.grtship.core.dto.ReactivationDTO;

@Mapper(componentModel = "spring", uses = {})
public interface DomainReactivateMapper extends EntityMapper<ReactivationDTO, DomainReactivate> {

	@Mapping(source = "reactivateDto.reactivationWefDate", target = "wefDate")
	@Mapping(source = "reactivateDto.reactivationReason", target = "reason")
	DomainReactivate toEntity(ReactivationDTO reactivateDto);
}
