package com.grtship.account.mapper;

import org.mapstruct.Mapper;

import com.grtship.account.domain.ShipmentReference;
import com.grtship.account.interfaces.EntityMapper;
import com.grtship.core.dto.ShipmentReferenceDTO;

/**
 * Mapper for the entity {@link ShipmentReference} and its DTO
 * {@link ShipmentReferenceDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface ShipmentReferenceMapper extends EntityMapper<ShipmentReferenceDTO, ShipmentReference> {

	default ShipmentReference fromId(Long id) {
		if (id == null) {
			return null;
		}
		ShipmentReference shipmentReference = new ShipmentReference();
		shipmentReference.setId(id);
		return shipmentReference;
	}
}
