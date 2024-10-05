package com.grtship.client.mapper;


import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.grtship.client.adaptor.MasterModuleAdapter;
import com.grtship.client.domain.Company;
import com.grtship.core.dto.CompanyDTO;
import com.grtship.core.enumeration.DomainStatus;

/**
 * Mapper for the entity {@link Company} and its DTO {@link CompanyDTO}.
 */
@Mapper(componentModel = "spring", uses = {AddressMapper.class, ClientMapper.class})
@Component
public abstract class CompanyMapper implements EntityMapper<CompanyDTO, Company> {

	@Autowired
	public MasterModuleAdapter masterModuleAdapter;

	private static final String COMPANY = "Company";

	@Mapping(source = "company.id", target = "id")
	@Mapping(source = "client.id", target = "clientId")
	@Mapping(source = "client.code", target = "clientCode")
	@Mapping(source = "client.name", target = "clientName")
	public abstract CompanyDTO toDto(Company company);

	@Mapping(source = "clientId", target = "client.id")
	@Mapping(target = "submittedForApproval", expression ="java(this.setSubmittedForApproval(companyDto.getStatus()))")
	@Mapping(target = "code", expression ="java(this.setCode(companyDto.getCode(),companyDto.getId()))")
	public abstract Company toEntity(CompanyDTO companyDto);


	public Boolean setSubmittedForApproval(DomainStatus status){
		return (status!=null && (!status.equals(DomainStatus.DRAFT))) ? Boolean.TRUE : Boolean.FALSE;
	}

	public String setCode(String code, Long id) {
		if (StringUtils.isEmpty(code) && id==null)
			return masterModuleAdapter.generateCode(COMPANY, null);
		return code;
	}

	public Company fromId(Long id) {
		if (id == null) {
			return null;
		}
		Company company = new Company();
		company.setId(id);
		return company;
	}
}
