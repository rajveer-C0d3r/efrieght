package com.grtship.client.mapper;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

import com.grtship.client.domain.CompanyBranch;
import com.grtship.core.dto.CompanyBranchCreationDTO;
import com.grtship.core.dto.CompanyBranchDTO;
import com.grtship.core.enumeration.DomainStatus;

/**
 * Mapper for the entity {@link CompanyBranch} and its DTO {@link CompanyBranchDTO}.
 */
@Mapper(componentModel = "spring", uses = {AddressMapper.class, CompanyMapper.class, BranchGstDetailsMapper.class})
@Component
public interface CompanyBranchMapper extends EntityMapper<CompanyBranchDTO, CompanyBranch> {

    @Mapping(source = "company.id", target = "companyId")
    @Mapping(source = "company.name", target = "companyName")
    @Mapping(source = "branchGstDetails", target = "branchGstDetails")
    CompanyBranchDTO toDto(CompanyBranch companyBranch);

    @Mapping(source = "companyId", target = "company.id")
    @Mapping(source = "branchGstDetails", target = "branchGstDetails")
    @Mapping(target = "submittedForApproval",  expression = "java(this.setSubmittedForApproval(companyBranchDto.getStatus()))")
    CompanyBranch toEntity(CompanyBranchDTO companyBranchDto);

    @Mapping(source = "companyId", target = "company.id")
    @Mapping(source = "branchGstDetails", target = "branchGstDetails")
    @Mapping(target = "submittedForApproval",  expression = "java(this.setSubmittedForApproval(companyBranchDto.getStatus()))")
    CompanyBranch toEntity(CompanyBranchCreationDTO companyBranchDto);
    
    
    default Boolean setSubmittedForApproval(DomainStatus status){
    	return (status!=null && !status.equals(DomainStatus.DRAFT)) ? Boolean.TRUE : Boolean.FALSE;
    }

    default CompanyBranch fromId(Long id) {
        if (id == null) {
            return null;
        }
        CompanyBranch companyBranch = new CompanyBranch();
        companyBranch.setId(id);
        return companyBranch;
    }
}
