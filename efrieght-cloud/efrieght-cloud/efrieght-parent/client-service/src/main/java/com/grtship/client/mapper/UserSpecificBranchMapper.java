package com.grtship.client.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

import com.grtship.client.domain.CompanyBranch;
import com.grtship.core.dto.UserSpecificBranchDTO;
import com.grtship.core.enumeration.DomainStatus;

@Mapper(componentModel = "spring")
@Component
public abstract class UserSpecificBranchMapper implements EntityMapper<UserSpecificBranchDTO,CompanyBranch>{

	@Mapping(target = "isBranchDeactivated",expression = "java(this.getBranchDeactivationFlag(companyBranch.getStatus()))")
	@Mapping(source = "company.id", target = "companyId")
	@Mapping(source = "id", target = "branchId")
	public abstract UserSpecificBranchDTO toDto(CompanyBranch companyBranch);
	
	public abstract List<UserSpecificBranchDTO> toDto(List<CompanyBranch> companyBranches);
	
	public Boolean getBranchDeactivationFlag(DomainStatus status){
		return (status!=null && (status.equals(DomainStatus.DEACTIVATED))) ? Boolean.TRUE : Boolean.FALSE;
	}
}
