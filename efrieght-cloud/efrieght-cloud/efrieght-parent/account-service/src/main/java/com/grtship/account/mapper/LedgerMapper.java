package com.grtship.account.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.grtship.account.domain.Ledger;
import com.grtship.account.interfaces.EntityMapper;
import com.grtship.core.dto.LedgerCreationDTO;
import com.grtship.core.dto.LedgerDTO;
import com.grtship.core.enumeration.DomainStatus;

/**
 * Mapper for the entity {@link Ledger} and its DTO {@link LedgerDTO}.
 */
@Mapper(componentModel = "spring", uses = { GroupMapper.class })
public interface LedgerMapper extends EntityMapper<LedgerDTO, Ledger> {

	@Mapping(source = "group.id", target = "groupId")
	@Mapping(source = "group.name", target = "groupName")
	@Mapping(source = "reactivateDtls.wefDate", target = "reactivateDtls.reactivationWefDate")
	@Mapping(source = "reactivateDtls.reactivatedDate", target = "reactivateDtls.reactivatedDate")
	@Mapping(source = "reactivateDtls.reason", target = "reactivateDtls.reactivationReason")
	@Mapping(source = "deactivateDtls.wefDate", target = "deactivateDtls.deactivationWefDate")
	@Mapping(source = "deactivateDtls.deactivatedDate", target = "deactivateDtls.deactivatedDate")
	@Mapping(source = "deactivateDtls.reason", target = "deactivateDtls.deactivationReason")
	LedgerDTO toDto(Ledger ledger);

	@Mapping(source = "groupId", target = "group.id")
	@Mapping(source = "groupName", target = "group.name")
	@Mapping(source = "activeFlag", target = "activeFlag", defaultValue = "false")
	@Mapping(source = "alias", target = "alias")
	@Mapping(target = "submittedForApproval", source = "submittedForApproval", defaultValue = "true")
	@Mapping(source = "reactivateDtls.reactivationWefDate", target = "reactivateDtls.wefDate")
	@Mapping(source = "reactivateDtls.reactivatedDate", target = "reactivateDtls.reactivatedDate")
	@Mapping(source = "reactivateDtls.reactivationReason", target = "reactivateDtls.reason")
	@Mapping(source = "deactivateDtls.deactivationWefDate", target = "deactivateDtls.wefDate")
	@Mapping(source = "deactivateDtls.deactivatedDate", target = "deactivateDtls.deactivatedDate")
	@Mapping(source = "deactivateDtls.deactivationReason", target = "deactivateDtls.reason")
	Ledger toEntity(LedgerDTO ledgerDto);

	@Mapping(source = "groupId", target = "group.id")
	@Mapping(target = "activeFlag", constant = "false")
	@Mapping(source = "alias", target = "alias")
	@Mapping(target = "submittedForApproval", constant = "true")
	Ledger toEntity(LedgerCreationDTO ledgerDto);

	default Ledger fromId(Long id) {
		if (id == null) {
			return null;
		}
		Ledger ledger = new Ledger();
		ledger.setId(id);
		return ledger;
	}
	
    default DomainStatus getStatus() {
		return DomainStatus.PENDING;
	}

}
