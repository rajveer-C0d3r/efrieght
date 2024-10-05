package com.grtship.mdm.mapper;

import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

import com.grtship.core.dto.ContainerPackageDTO;
import com.grtship.mdm.domain.ContainerPackage;
import com.grtship.mdm.interfaces.EntityMapper;

/**
 * Mapper for the entity {@link ContainerPackage} and its DTO
 * {@link ContainerPackageDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
@Component
public interface ContainerPackageMapper extends EntityMapper<ContainerPackageDTO, ContainerPackage> {

	default ContainerPackage fromId(Long id) {
		if (id == null) {
			return null;
		}
		ContainerPackage containerPackage = new ContainerPackage();
		containerPackage.setId(id);
		return containerPackage;
	}
}
