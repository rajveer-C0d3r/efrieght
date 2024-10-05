package com.grtship.mdm.mapper;

import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

import com.grtship.core.dto.EquipmentDTO;
import com.grtship.mdm.domain.Equipment;
import com.grtship.mdm.interfaces.EntityMapper;

/**
 * Mapper for the entity {@link Equipment} and its DTO {@link EquipmentDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
@Component
public interface EquipmentMapper extends EntityMapper<EquipmentDTO, Equipment> {

	Equipment toEntity(EquipmentDTO equipmentDTO);

	default Equipment fromId(Long id) {
		if (id == null) {
			return null;
		}
		Equipment equipment = new Equipment();
		equipment.setId(id);
		return equipment;
	}
}
