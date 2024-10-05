package com.grt.elogfrieght.services.user.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.grt.elogfrieght.services.user.domain.Deactivate;
import com.grtship.core.dto.DeactivationDTO;

@Mapper(componentModel = "spring")
public interface DeactivationMapper {

	@Mapping(source = "deactivationWefDate", target = "wefDate")
	@Mapping(source = "deactivatedDate", target = "deactivatedDate")
	@Mapping(source = "deactivationReason", target = "reason")
	public abstract Deactivate toEntity(DeactivationDTO deactivationdto);

	@Mapping(source = "wefDate", target = "deactivationWefDate")
	@Mapping(source = "deactivatedDate", target = "deactivatedDate")
	@Mapping(source = "reason", target = "deactivationReason")
	public abstract DeactivationDTO toDto(Deactivate deactivate);

}
