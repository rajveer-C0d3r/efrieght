package com.grtship.mdm.mapper;


import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.BeforeMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.grtship.core.constant.ReferenceNameConstant;
import com.grtship.core.dto.CreditTermsDTO;
import com.grtship.core.dto.EntityBranchDTO;
import com.grtship.core.dto.EntityBranchRequestDTO;
import com.grtship.core.enumeration.DomainStatus;
import com.grtship.core.enumeration.EntityType;
import com.grtship.mdm.domain.EntityBranch;
import com.grtship.mdm.interfaces.EntityMapper;
import com.grtship.mdm.service.CodeGeneratorService;

/**
 * Mapper for the entity {@link EntityBranch} and its DTO {@link EntityBranchDTO}.
 */
@Mapper(componentModel = "spring", uses = {AddressMapper.class})
@Component
public abstract class EntityBranchMapper implements EntityMapper<EntityBranchDTO, EntityBranch> {

	
	@Autowired private CodeGeneratorService codeGeneratorService;
	
	@Mapping(source = "externalEntity.id", target = "externalEntityId")
	@Mapping(target = "cellNumbers",expression = "java(this.setCellNumbers(entityBranch))")
	public abstract EntityBranchDTO toDto(EntityBranch entityBranch);


	@Mapping(source = "externalEntityId", target = "externalEntity.id")
	@Mapping(target = "sez", source = "sez",defaultValue = "false")
	@Mapping(target = "activeFlag", source = "activeFlag", defaultValue = "false")
	@Mapping(target = "customerFlag", source = "customerFlag", defaultValue = "false")
	@Mapping(target = "vendorFlag", source = "vendorFlag", defaultValue = "false")
	@Mapping(target ="cellNumbers", expression = "java(this.setCellNumber(entityBranchDto.getCellNumbers()))")
	@Mapping(target = "submittedForApproval", expression = "java(this.setSubmittedForApproval(entityBranchDto.getStatus()))")
	public abstract EntityBranch toEntity(EntityBranchDTO entityBranchDto);
	
	@Mapping(source = "externalEntityId", target = "externalEntity.id")
	@Mapping(target = "sez", source = "sez",defaultValue = "false")
	@Mapping(target = "activeFlag", constant = "false")
	@Mapping(target = "customerFlag", source = "customerFlag", defaultValue = "false")
	@Mapping(target = "vendorFlag", source = "vendorFlag", defaultValue = "false")
	@Mapping(target ="cellNumbers", expression = "java(this.setCellNumber(entityBranchDto.getCellNumbers()))")
	@Mapping(target = "submittedForApproval", expression = "java(this.setSubmittedForApproval(entityBranchDto.getStatus()))")
	@Mapping(target = "code", expression = "java(this.setCode(entityBranchDto.getCode()))")
	public abstract EntityBranch toEntity(EntityBranchRequestDTO entityBranchDto);
	
	
	String setCode(String code) {
		if(StringUtils.isEmpty(code))
			 return codeGeneratorService.generateCode(ReferenceNameConstant.ENTITY_BRANCH, null);
		return code;
	}
	
	 Boolean setSubmittedForApproval(DomainStatus status) {
		return (status!=null && !status.equals(DomainStatus.DRAFT)) ? Boolean.TRUE : Boolean.FALSE;
	}
	
	 String setCellNumber(Set<String> cellNumbers) {
		return (!CollectionUtils.isEmpty(cellNumbers))? (cellNumbers.stream().map(Object::toString).collect(Collectors.joining(","))) : null;
	}
	
	/** prepare credit term dto on save... */
	@BeforeMapping
	private void prepareCreditTermsDto(EntityBranchRequestDTO branchDetailsDto) {
		List<CreditTermsDTO> creditTermDtoList = new ArrayList<>();
		if(branchDetailsDto.getCustomerFlag().equals(Boolean.TRUE)) {
			CreditTermsDTO customerCreditTerm = CreditTermsDTO.builder().entityType(EntityType.CUSTOMER).creditAmount(branchDetailsDto.getCustomerCreditAmount())
					.creditDays(branchDetailsDto.getCustomerCreditDays()).status(DomainStatus.PENDING).build();
			creditTermDtoList.add(customerCreditTerm);
		}
		if(branchDetailsDto.getVendorFlag().equals(Boolean.TRUE)) {
			CreditTermsDTO vendorCreditTerm = CreditTermsDTO.builder().entityType(EntityType.VENDOR).creditAmount(branchDetailsDto.getVendorCreditAmount())
					.creditDays(branchDetailsDto.getVendorCreditDays()).status(DomainStatus.PENDING).build();
			creditTermDtoList.add(vendorCreditTerm);
		}
		branchDetailsDto.setCreditTermsList(creditTermDtoList);
	}
	
	/** prepare credit term dto on update... */
	@BeforeMapping
	private void prepareCreditTermsDto(EntityBranchDTO branchDetailsDto) {
		List<CreditTermsDTO> creditTermDtoList = new ArrayList<>();
		if(branchDetailsDto.getCustomerFlag().equals(Boolean.TRUE)) {
			CreditTermsDTO customerCreditTerm = CreditTermsDTO.builder().entityType(EntityType.CUSTOMER).creditAmount(branchDetailsDto.getCustomerCreditAmount())
					.creditDays(branchDetailsDto.getCustomerCreditDays()).status(branchDetailsDto.getCustomerApprovalStatus()).build();
			customerCreditTerm.setId(branchDetailsDto.getCustomerCreditTermId());
			creditTermDtoList.add(customerCreditTerm);
		}
		if(branchDetailsDto.getVendorFlag().equals(Boolean.TRUE)) {
			CreditTermsDTO vendorCreditTerm = CreditTermsDTO.builder().entityType(EntityType.VENDOR).creditAmount(branchDetailsDto.getVendorCreditAmount())
					.creditDays(branchDetailsDto.getVendorCreditDays()).status(branchDetailsDto.getCustomerApprovalStatus()).build();
			vendorCreditTerm.setId(branchDetailsDto.getVendorCreditTermId());
			creditTermDtoList.add(vendorCreditTerm);
		}
		branchDetailsDto.setCreditTermsList(creditTermDtoList);
	}
	
	
     EntityBranch fromId(Long id) {
        if (id == null) {
            return null;
        }
        EntityBranch branchDetails = new EntityBranch();
        branchDetails.setId(id);
        return branchDetails;
    }
     
     Set<String> setCellNumbers(EntityBranch entityBranch){
 		if (!StringUtils.isEmpty(entityBranch.getCellNumbers())) {
			return Stream.of(entityBranch.getCellNumbers().split(",", -1)).collect(Collectors.toSet());
		}
 		return null;
 	}
}
