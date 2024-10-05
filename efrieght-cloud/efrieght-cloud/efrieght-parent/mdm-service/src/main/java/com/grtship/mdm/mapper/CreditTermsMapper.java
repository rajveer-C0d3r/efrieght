package com.grtship.mdm.mapper;

import java.util.Set;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

import com.grtship.core.dto.CreditTermsDTO;
import com.grtship.mdm.domain.CreditTerms;
import com.grtship.mdm.interfaces.EntityMapper;

/**
 * Mapper for the entity {@link CreditTerms} and its DTO {@link CreditTermsDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
@Component
public interface CreditTermsMapper extends EntityMapper<CreditTermsDTO, CreditTerms> {

	@Mapping(source = "referenceName", target = "referenceName")
	@Mapping(source = "referenceId", target = "referenceId")
	@Mapping(source = "status", target = "status")
	@Mapping(source = "creditDays", target = "creditDays")
	@Mapping(source = "creditAmount", target = "creditAmount")
	@Mapping(source = "entityType", target = "entityType")
	CreditTermsDTO toDto(CreditTerms creditTerms);

	@Mapping(source = "referenceName", target = "referenceName")
	@Mapping(source = "referenceId", target = "referenceId")
	@Mapping(source = "status", target = "status")
	@Mapping(source = "creditDays", target = "creditDays")
	@Mapping(source = "creditAmount", target = "creditAmount")
	@Mapping(source = "entityType", target = "entityType")
	CreditTerms toEntity(CreditTermsDTO creditTermsDto);

	Set<CreditTermsDTO> toDto(Set<CreditTerms> creditTerms);

	default CreditTerms fromId(Long id) {
		if (id == null) {
			return null;
		}
		CreditTerms creditTerms = new CreditTerms();
		creditTerms.setId(id);
		return creditTerms;
	}
}
