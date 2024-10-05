package com.grtship.mdm.mapper;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

import com.grtship.core.dto.BranchContactDTO;
import com.grtship.mdm.domain.BranchContact;
import com.grtship.mdm.interfaces.EntityMapper;

/**
 * Mapper for the entity {@link BranchContact} and its DTO {@link BranchContactDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
@Component
public interface BranchContactMapper extends EntityMapper<BranchContactDTO, BranchContact> {

	@Mapping(source = "entityBranch.id", target = "entityBranchId")
	BranchContactDTO toDto(BranchContact branchContacts);


	@Mapping(source = "entityBranchId", target = "entityBranch.id")
	BranchContact toEntity(BranchContactDTO branchContactDto);

    default BranchContact fromId(Long id) {
        if (id == null) {
            return null;
        }
        BranchContact contactDetails = new BranchContact();
        contactDetails.setId(id);
        return contactDetails;
    }
}
