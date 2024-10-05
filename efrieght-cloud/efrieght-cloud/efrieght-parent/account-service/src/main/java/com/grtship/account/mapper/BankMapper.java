package com.grtship.account.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.grtship.account.domain.Bank;
import com.grtship.account.interfaces.EntityMapper;
import com.grtship.core.dto.BankDTO;

/**
 * Mapper for the entity {@link Bank} and its DTO {@link BankDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface BankMapper extends EntityMapper<BankDTO, Bank> {

	@Mapping(source = "addressId", target = "address.id")
	BankDTO toDto(Bank entity);

	Bank toEntity(BankDTO dto);

	default Bank fromId(Long id) {
		if (id == null) {
			return null;
		}
		Bank bank = new Bank();
		bank.setId(id);
		return bank;
	}
}
