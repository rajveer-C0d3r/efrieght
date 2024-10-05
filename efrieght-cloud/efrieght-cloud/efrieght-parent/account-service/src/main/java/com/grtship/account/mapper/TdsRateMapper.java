package com.grtship.account.mapper;

import org.mapstruct.Mapper;

import com.grtship.account.domain.TdsRate;
import com.grtship.account.interfaces.EntityMapper;
import com.grtship.core.dto.TdsRateDTO;

/**
 * Mapper for the entity {@link TdsRate} and its DTO {@link TdsRateDTO}.
 */
@Mapper(componentModel = "spring", uses = { TdsMapper.class })
public interface TdsRateMapper extends EntityMapper<TdsRateDTO, TdsRate> {

	TdsRateDTO toDto(TdsRate tdsRate);

	TdsRate toEntity(TdsRateDTO tdsRateDTO);

	default TdsRate fromId(Long id) {
		if (id == null) {
			return null;
		}
		TdsRate tdsRate = new TdsRate();
		tdsRate.setId(id);
		return tdsRate;
	}
}
