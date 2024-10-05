package com.grtship.mdm.mapper;

import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

import com.grtship.core.dto.UnitDTO;
import com.grtship.mdm.domain.Unit;
import com.grtship.mdm.interfaces.EntityMapper;

/**
 * Mapper for the entity {@link Unit} and its DTO {@link UnitDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
@Component
public interface UnitMapper extends EntityMapper<UnitDTO, Unit> {

	default Unit fromId(Long id) {
		if (id == null) {
			return null;
		}
		Unit unit = new Unit();
		unit.setId(id);
		return unit;
	}
}
