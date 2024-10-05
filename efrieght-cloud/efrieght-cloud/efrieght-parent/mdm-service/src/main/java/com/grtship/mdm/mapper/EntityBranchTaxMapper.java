package com.grtship.mdm.mapper;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

import com.grtship.core.dto.EntityBranchTaxDTO;
import com.grtship.mdm.domain.EntityBranchTax;
import com.grtship.mdm.interfaces.EntityMapper;

/**
 * Mapper for the entity {@link EntityBranchTax} and its DTO {@link EntityBranchTaxDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
@Component
public interface EntityBranchTaxMapper extends EntityMapper<EntityBranchTaxDTO, EntityBranchTax> {

	@Mapping(source = "entityBranch.id", target = "entityBranchId")
	EntityBranchTaxDTO toDto(EntityBranchTax entityBranchTax);


	@Mapping(source = "entityBranchId", target = "entityBranch.id")
	EntityBranchTax toEntity(EntityBranchTaxDTO entityBranchtaxDto);

    default EntityBranchTax fromId(Long id) {
        if (id == null) {
            return null;
        }
        EntityBranchTax taxDetails = new EntityBranchTax();
        taxDetails.setId(id);
        return taxDetails;
    }
    
}
