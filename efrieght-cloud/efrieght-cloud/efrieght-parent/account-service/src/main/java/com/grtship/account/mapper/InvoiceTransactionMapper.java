package com.grtship.account.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.grtship.account.domain.InvoiceTransaction;
import com.grtship.account.interfaces.EntityMapper;
import com.grtship.core.dto.InvoiceTransactionDTO;

/**
 * Mapper for the entity {@link InvoiceTransaction} and its DTO
 * {@link InvoiceTransactionDTO}.
 */
@Mapper(componentModel = "spring", uses = { InvoiceMapper.class, BankReceiptMapper.class })
public interface InvoiceTransactionMapper extends EntityMapper<InvoiceTransactionDTO, InvoiceTransaction> {

	@Mapping(source = "invoice.id", target = "invoiceId")
	@Mapping(source = "bankReceipt.id", target = "bankReceiptId")
	@Mapping(source = "invoice.version", target = "invoiceVersion")
	InvoiceTransactionDTO toDto(InvoiceTransaction invoiceTransaction);

	@Mapping(source = "invoiceId", target = "invoice.id")
	@Mapping(source = "invoiceVersion", target = "invoice.version")
	@Mapping(source = "bankReceiptId", target = "bankReceipt.id")
	InvoiceTransaction toEntity(InvoiceTransactionDTO invoiceTransactionDto);

	default InvoiceTransaction fromId(Long id) {
		if (id == null) {
			return null;
		}
		InvoiceTransaction invoiceTransaction = new InvoiceTransaction();
		invoiceTransaction.setId(id);
		return invoiceTransaction;
	}
}
