package com.grtship.account.mapper;

import org.mapstruct.Mapper;

import com.grtship.account.domain.TdsType;
import com.grtship.account.interfaces.EntityMapper;
import com.grtship.core.dto.TdsTypeDTO;

/**
 * Mapper for the entity {@link TdsType} and its DTO {@link TdsTypeDTO}.
 */
@Mapper(componentModel = "spring", uses = { TdsMapper.class })
public interface TdsTypeMapper extends EntityMapper<TdsTypeDTO, TdsType> {

	TdsTypeDTO toDto(TdsType tdsType);

	TdsType toEntity(TdsTypeDTO tdsTypeDTO);

	default TdsType fromId(Long id) {
		if (id == null) {
			return null;
		}
		TdsType tdsType = new TdsType();
		tdsType.setId(id);
		return tdsType;
	}
}
