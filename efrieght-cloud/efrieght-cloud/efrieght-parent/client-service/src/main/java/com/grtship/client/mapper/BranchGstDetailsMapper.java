package com.grtship.client.mapper;


import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

import com.grtship.client.domain.BranchGstDetails;
import com.grtship.core.dto.BranchGstDetailsDTO;

/**
 * Mapper for the entity {@link BranchGstDetails} and its DTO {@link BranchGstDetailsDTO}.
 */
@Mapper(componentModel = "spring", uses = {CompanyBranchMapper.class})
@Component
public interface BranchGstDetailsMapper extends EntityMapper<BranchGstDetailsDTO, BranchGstDetails> {

    BranchGstDetailsDTO toDto(BranchGstDetails branchGstDetails);

    BranchGstDetails toEntity(BranchGstDetailsDTO branchGstDetailsDTO);

    default BranchGstDetails fromId(Long id) {
        if (id == null) {
            return null;
        }
        BranchGstDetails branchGstDetails = new BranchGstDetails();
        branchGstDetails.setId(id);
        return branchGstDetails;
    }
}
