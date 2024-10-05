package com.grtship.mdm.mapper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.BeforeMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.grtship.core.constant.ReferenceNameConstant;
import com.grtship.core.dto.CreditTermsDTO;
import com.grtship.core.dto.ExternalEntityDTO;
import com.grtship.core.dto.ExternalEntityRequestDTO;
import com.grtship.core.dto.ObjectAliasDTO;
import com.grtship.core.enumeration.DomainStatus;
import com.grtship.core.enumeration.EntityType;
import com.grtship.mdm.domain.ExternalEntity;
import com.grtship.mdm.interfaces.EntityMapper;
import com.grtship.mdm.service.CodeGeneratorService;
import com.grtship.mdm.service.ObjectAliasQueryService;

/**
 * Mapper for the entity {@link ExternalEntity} and its DTO
 * {@link ExternalEntityDTO}.
 */
@Mapper(componentModel = "spring", uses = { EntityBranchTaxMapper.class, CreditTermsMapper.class,
		EntityGroupMapper.class, CurrencyMapper.class, AddressMapper.class, ObjectAliasMapper.class })
@Component
public abstract class ExternalEntityMapper implements EntityMapper<ExternalEntityDTO, ExternalEntity> {

	@Autowired
	private CodeGeneratorService codeGeneratorService;
	
	@Autowired
	private ObjectAliasQueryService aliasFilterService;

	@Mapping(source = "currency.id", target = "currencyId")
	@Mapping(source = "currency.name", target = "currencyName")
	@Mapping(source = "address", target = "address")
	@Mapping(target = "code", source = "code")
	@Mapping(target = "externalEntityAlias", expression = "java(this.setExternalEntityAlias(externalEntity))")
	public abstract ExternalEntityDTO toDto(ExternalEntity externalEntity);

	@Mapping(source = "currencyId", target = "currency.id")
	@Mapping(source = "address", target = "address")
	@Mapping(source = "currencyName", target = "currency.name")
	@Mapping(target = "activeFlag", source = "activeFlag", defaultValue = "false")
	@Mapping(target = "customerFlag", source = "customerFlag", defaultValue = "false")
	@Mapping(target = "vendorFlag", source = "vendorFlag", defaultValue = "false")
	@Mapping(target = "submittedForApproval", expression = "java(this.setSubmittedForApproval(externalEntityDto.getStatus()))")
	public abstract ExternalEntity toEntity(ExternalEntityDTO externalEntityDto);

	@Mapping(source = "currencyId", target = "currency.id")
	@Mapping(source = "address", target = "address")
	@Mapping(source = "status", target = "status")
	@Mapping(target = "activeFlag", constant = "false")
	@Mapping(target = "customerFlag", source = "customerFlag", defaultValue = "false")
	@Mapping(target = "vendorFlag", source = "vendorFlag", defaultValue = "false")
	@Mapping(target = "code", expression = "java(this.setCode(externalEntityDto))")
	@Mapping(target = "submittedForApproval", expression = "java(this.setSubmittedForApproval(externalEntityDto.getStatus()))")
	public abstract ExternalEntity toEntity(ExternalEntityRequestDTO externalEntityDto);

	String setCode(ExternalEntityRequestDTO externalEntityDto) {
		System.out.println("Inside set code");
		String code = externalEntityDto.getCode();
		if (StringUtils.isEmpty(externalEntityDto.getCode()))
			code = codeGeneratorService.generateCode(ReferenceNameConstant.ENTITY, null);
		externalEntityDto.setCode(code); 
		return code;
	}

	Boolean setSubmittedForApproval(DomainStatus status) {
		return (status != null && !status.equals(DomainStatus.DRAFT)) ? Boolean.TRUE : Boolean.FALSE;
	}

	/** prepare credit term dto on save... */
	@BeforeMapping
	private void prepareCreditTermsDto(ExternalEntityRequestDTO externalEntityDto) {
		List<CreditTermsDTO> creditTermDtoList = new ArrayList<>();
		if (externalEntityDto.getCustomerFlag().equals(Boolean.TRUE)) {
			CreditTermsDTO customerCreditTerm = CreditTermsDTO.builder().entityType(EntityType.CUSTOMER)
					.creditAmount(externalEntityDto.getCustomerCreditAmount())
					.creditDays(externalEntityDto.getCustomerCreditDays()).status(DomainStatus.PENDING).build();
			creditTermDtoList.add(customerCreditTerm);
		}
		if (externalEntityDto.getVendorFlag().equals(Boolean.TRUE)) {
			CreditTermsDTO vendorCreditTerm = CreditTermsDTO.builder().entityType(EntityType.VENDOR)
					.creditAmount(externalEntityDto.getVendorCreditAmount())
					.creditDays(externalEntityDto.getVendorCreditDays()).status(DomainStatus.PENDING).build();
			creditTermDtoList.add(vendorCreditTerm);
		}
		externalEntityDto.setCreditTermsList(creditTermDtoList);
	}

	/** prepare credit term dto on update... */
	@BeforeMapping
	private void prepareCreditTermsDto(ExternalEntityDTO entityDto) {
		List<CreditTermsDTO> creditTermDtoList = new ArrayList<>();
		if (entityDto.getCustomerFlag().equals(Boolean.TRUE)) {
			CreditTermsDTO customerCreditTerm = CreditTermsDTO.builder().entityType(EntityType.CUSTOMER)
					.creditAmount(entityDto.getCustomerCreditAmount()).creditDays(entityDto.getCustomerCreditDays())
					.status(entityDto.getCustomerApprovalStatus()).build();
			customerCreditTerm.setId(entityDto.getCustomerCreditTermId());
			creditTermDtoList.add(customerCreditTerm);
		}
		if (entityDto.getVendorFlag().equals(Boolean.TRUE)) {
			CreditTermsDTO vendorCreditTerm = CreditTermsDTO.builder().entityType(EntityType.VENDOR)
					.creditAmount(entityDto.getVendorCreditAmount()).creditDays(entityDto.getVendorCreditDays())
					.status(entityDto.getCustomerApprovalStatus()).build();
			vendorCreditTerm.setId(entityDto.getVendorCreditTermId());
			creditTermDtoList.add(vendorCreditTerm);
		}
		entityDto.setCreditTermsList(creditTermDtoList);
	}

	ExternalEntity fromId(Long id) {
		if (id == null) {
			return null;
		}
		ExternalEntity externalEntity = new ExternalEntity();
		externalEntity.setId(id);
		return externalEntity;
	}
	
	Set<ObjectAliasDTO> setExternalEntityAlias(ExternalEntity externalEntity){
		if(ObjectUtils.isNotEmpty(externalEntity.getId())) {
		return new HashSet<>(aliasFilterService
				.getListOfAliasByReferanceIdAndReferenceName(externalEntity.getId(), ReferenceNameConstant.ENTITY));
		} else {
			return new HashSet<>();
		}
	}
}
