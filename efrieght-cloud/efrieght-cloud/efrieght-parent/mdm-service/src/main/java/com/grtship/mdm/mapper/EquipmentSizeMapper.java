package com.grtship.mdm.mapper;

import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

import com.grtship.core.dto.EquipmentSizeDTO;
import com.grtship.mdm.domain.EquipmentSize;
import com.grtship.mdm.interfaces.EntityMapper;

/**
 * Mapper for the entity {@link EquipmentSize} and its DTO
 * {@link EquipmentSizeDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
@Component
public interface EquipmentSizeMapper extends EntityMapper<EquipmentSizeDTO, EquipmentSize> {

	default EquipmentSize fromId(Long id) {
		if (id == null) {
			return null;
		}
		EquipmentSize equipmentSize = new EquipmentSize();
		equipmentSize.setId(id);
		return equipmentSize;
	}
}
