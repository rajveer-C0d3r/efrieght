package com.grtship.account.mapper;

import org.mapstruct.Mapper;

import com.grtship.account.domain.Invoice;
import com.grtship.account.interfaces.EntityMapper;
import com.grtship.core.dto.InvoiceDTO;

/**
 * Mapper for the entity {@link Invoice} and its DTO {@link InvoiceDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface InvoiceMapper extends EntityMapper<InvoiceDTO, Invoice> {

	default Invoice fromId(Long id) {
		if (id == null) {
			return null;
		}
		Invoice invoice = new Invoice();
		invoice.setId(id);
		return invoice;
	}
}
