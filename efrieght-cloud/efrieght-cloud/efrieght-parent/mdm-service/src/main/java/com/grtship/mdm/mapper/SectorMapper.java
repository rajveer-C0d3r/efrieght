package com.grtship.mdm.mapper;

import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

import com.grtship.core.dto.SectorDTO;
import com.grtship.mdm.domain.Sector;
import com.grtship.mdm.interfaces.EntityMapper;

/**
 * Mapper for the entity {@link Sector} and its DTO {@link SectorDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
@Component
public interface SectorMapper extends EntityMapper<SectorDTO, Sector> {

	SectorDTO toDto(Sector sector);

	Sector toEntity(SectorDTO sectorDto);

	default Sector fromId(Long id) {
		if (id == null) {
			return null;
		}
		Sector sector = new Sector();
		sector.setId(id);
		return sector;
	}
}
