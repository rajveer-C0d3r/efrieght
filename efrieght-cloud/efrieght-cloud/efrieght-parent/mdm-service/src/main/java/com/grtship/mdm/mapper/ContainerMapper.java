package com.grtship.mdm.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

import com.grtship.core.dto.ContainerDTO;
import com.grtship.mdm.domain.Container;
import com.grtship.mdm.interfaces.EntityMapper;

/**
 * Mapper for the entity {@link Container} and its DTO {@link ContainerDTO}.
 */
@Mapper(componentModel = "spring", uses = { EquipmentMapper.class, EquipmentSizeMapper.class })
@Component
public interface ContainerMapper extends EntityMapper<ContainerDTO, Container> {

	@Mapping(source = "equipmentType.id", target = "equipmentTypeId")
	@Mapping(source = "size.id", target = "sizeId")
	ContainerDTO toDto(Container container);

	@Mapping(source = "equipmentTypeId", target = "equipmentType")
	@Mapping(source = "sizeId", target = "size")
	Container toEntity(ContainerDTO containerDTO);

	default Container fromId(Long id) {
		if (id == null) {
			return null;
		}
		Container container = new Container();
		container.setId(id);
		return container;
	}
}
