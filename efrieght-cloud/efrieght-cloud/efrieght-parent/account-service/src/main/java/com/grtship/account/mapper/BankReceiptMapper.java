package com.grtship.account.mapper;

import org.mapstruct.Mapper;

import com.grtship.account.domain.BankReceipt;
import com.grtship.account.interfaces.EntityMapper;
import com.grtship.core.dto.BankReceiptDTO;

/**
 * Mapper for the entity {@link BankReceipt} and its DTO {@link BankReceiptDTO}.
 */
@Mapper(componentModel = "spring", uses = { InvoiceTransactionMapper.class })
public interface BankReceiptMapper extends EntityMapper<BankReceiptDTO, BankReceipt> {

	default BankReceipt fromId(Long id) {
		if (id == null) {
			return null;
		}
		BankReceipt bankReceipt = new BankReceipt();
		bankReceipt.setId(id);
		return bankReceipt;
	}
}
