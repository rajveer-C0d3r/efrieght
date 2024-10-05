
package com.grtship.account.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;

import com.grtship.account.domain.BankReceipt;
import com.grtship.account.interfaces.EntityMapper;
import com.grtship.core.dto.PreviousBankReceiptResponseDTO;

@Mapper(componentModel = "spring")
public abstract class PreviousBankReceiptResponseMapper
		implements EntityMapper<PreviousBankReceiptResponseDTO, BankReceipt> {

	@Override
	public PreviousBankReceiptResponseDTO toDto(BankReceipt bankReceipt) {
		PreviousBankReceiptResponseDTO dto = new PreviousBankReceiptResponseDTO();
		dto.setId(bankReceipt.getId());
		dto.setVoucherNo(bankReceipt.getVoucherNo());
		dto.setVoucherDate(bankReceipt.getVoucherDate());
		dto.setInstrumentType(bankReceipt.getInstrumentType());
		dto.setInstrumentNo(bankReceipt.getInstrumentNo());
		dto.setInstrumentDate(bankReceipt.getInstrumentDate());
		dto.setBankName(bankReceipt.getBankName());
		dto.setUnadjustedAmount(bankReceipt.getUnadjustedAmount());
		dto.setInvoiceType(bankReceipt.getPaymentType());
		return dto;
	}

	@Override
	public List<PreviousBankReceiptResponseDTO> toDto(List<BankReceipt> bankReceiptList) {
		return bankReceiptList.stream().filter(bankReceipt -> bankReceipt.getId() != null).map(this::toDto)
				.collect(Collectors.toList());
	}

}
