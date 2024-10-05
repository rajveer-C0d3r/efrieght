package com.grtship.client.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

import com.grtship.client.domain.Company;
import com.grtship.core.dto.UserSpecificCompanyDTO;
import com.grtship.core.enumeration.DomainStatus;

@Mapper(componentModel = "spring")
@Component
public abstract class UserSpecificCompanyMapper implements EntityMapper<UserSpecificCompanyDTO,Company> {

	@Mapping(target = "isCompanyDeactivated",expression = "java(this.getCompanyDeactivationFlag(company.getStatus()))")
	@Mapping(source = "client.id", target = "clientId")
	@Mapping(source = "id", target = "companyId")
	public abstract UserSpecificCompanyDTO toDto(Company company);
	
	public abstract List<UserSpecificCompanyDTO> toDto(List<Company> companies);
	
	public Boolean getCompanyDeactivationFlag(DomainStatus status){
		return (status!=null && (status.equals(DomainStatus.DEACTIVATED))) ? Boolean.TRUE : Boolean.FALSE;
	}
}
