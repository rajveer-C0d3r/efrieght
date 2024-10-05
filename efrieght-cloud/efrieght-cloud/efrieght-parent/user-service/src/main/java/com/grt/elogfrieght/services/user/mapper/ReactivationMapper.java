package com.grt.elogfrieght.services.user.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.grt.elogfrieght.services.user.domain.Reactivate;
import com.grtship.core.dto.ReactivationDTO;

@Mapper(componentModel = "spring")
public interface ReactivationMapper {

	@Mapping(source = "reactivationWefDate", target = "wefDate")
	@Mapping(source = "reactivatedDate", target = "reactivatedDate")
	@Mapping(source = "reactivationReason", target = "reason")
	public abstract Reactivate toEntity(ReactivationDTO reactivationdto);

	@Mapping(source = "wefDate", target = "reactivationWefDate")
	@Mapping(source = "reactivatedDate", target = "reactivatedDate")
	@Mapping(source = "reason", target = "reactivationReason")
	public abstract ReactivationDTO toDto(Reactivate reactivate);

}
