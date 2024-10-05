package com.grtship.account.validator.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.grtship.account.validator.BankReceiptValidator;
import com.grtship.common.exception.InvalidDataException;
import com.grtship.core.constant.ErrorCode;
import com.grtship.core.dto.BankReceiptDTO;
import com.grtship.core.enumeration.InstrumentType;
import com.grtship.core.enumeration.PaymentType;

@Component
@Transactional(readOnly = true)
public class BankReceiptValidatorImpl implements BankReceiptValidator {

	private static final String IF_PAYMENT_DETAILS_IS_ADVANCE_PAYMENT_THEN_ADVANCE_LEDGER_GRID_MUST_NOT_EMPTY = "If Payment Details is Advance Payment, then Advance ledger grid must not empty.";
	private static final String LEDGER_CODE_IS_MANDATORY = "Ledger code is mandatory.";
	private static final String IF_PAYMENT_DETAILS_IS_PAYMENT_AGAINST_BILL_THEN_ADJUST_AGAINST_PENDING_INVOICES_GRID_MUST_NOT_EMPTY = "If Payment Details is Payment Against Bill, then Adjust against pending invoices grid must not empty.";
	private static final String AMOUNT_IS_MANDATORY = "Amount is mandatory";
	private static final String IF_INSTRUMENT_TYPE_IS_CHEQUE_OR_DEMAND_DRAFT_THEN_INSTRUMENT_NO_IS_MANDATORY = "If ‘Instrument Type’ is Cheque or Demand Draft then Instrument No is mandatory.";
	private static final String IF_INSTRUMENT_TYPE_IS_INWARD_REMITTANCE_FOREIGN_PARTY_OR_INWARD_REMITTANCE_LOCAL_PARTY_THEN_FOREX_AMOUNT_IS_MANDATORY = "If ‘Instrument Type’ is Inward remittance-foreign party or Inward remittance-local party then Forex Amount is mandatory.";
	private static final String IF_INSTRUMENT_TYPE_IS_INWARD_REMITTANCE_FOREIGN_PARTY_OR_INWARD_REMITTANCE_LOCAL_PARTY_THEN_CURRENCY_IS_MANDATORY = "If ‘Instrument Type’ is Inward remittance-foreign party or Inward remittance-local party then Currency is mandatory.";
	private static final String IF_INSTRUMENT_TYPE_IS_INWARD_REMITTANCE_FOREIGN_PARTY_OR_INWARD_REMITTANCE_LOCAL_PARTY_THEN_EXCHANGE_RATE_IS_MANDATORY = "If ‘Instrument Type’ is Inward remittance-foreign party or Inward remittance-local party then Exchange Rate is mandatory.";
	private static final String IF_TDS_DEDUCTED_CHECK_BOX_IS_CHECKED_THEN_TDS_AMOUNT_IS_MANDATORY = "If Tds Deducted check box is checked, then Tds Amount is mandatory.";
	private static final String IF_SHIPMENT_REFERENCE_TYPE_IS_SELECTED_THEN_SHIPMENT_REFERENCE_NO_IS_MANDATORY = "If Shipment Reference Type is selected then Shipment Reference No is mandatory.";

	@Override
	public void saveValidations(BankReceiptDTO bankReceiptDto) {
		validateInstrumentNo(bankReceiptDto);
		validateForexAmount(bankReceiptDto);
		validateTxnCurrencyId(bankReceiptDto);
		validateExchangeRate(bankReceiptDto);
		validateTdsAmount(bankReceiptDto);
		validateShipmentReference(bankReceiptDto);
		validateInvoiceTransaction(bankReceiptDto);
	}

	private void validateInvoiceTransaction(BankReceiptDTO bankReceiptDto) {
		if (bankReceiptDto.getPaymentType().equals(PaymentType.PAYMENT_AGAINST_BILL)
				&& CollectionUtils.isEmpty(bankReceiptDto.getInvoiceTransaction()))
			throw new InvalidDataException(ErrorCode.INVALID_DATA_ERROR,
					IF_PAYMENT_DETAILS_IS_PAYMENT_AGAINST_BILL_THEN_ADJUST_AGAINST_PENDING_INVOICES_GRID_MUST_NOT_EMPTY);

		if (bankReceiptDto.getPaymentType().equals(PaymentType.PAYMENT_AGAINST_BILL)
				&& !CollectionUtils.isEmpty(bankReceiptDto.getInvoiceTransaction())) {
			bankReceiptDto.getInvoiceTransaction().forEach(invoiceTransactionsDto -> {
				if (invoiceTransactionsDto.getTransactionAmount() == null)
					throw new InvalidDataException(ErrorCode.INVALID_DATA_ERROR,AMOUNT_IS_MANDATORY);
			});
		}

	}

	private void validateShipmentReference(BankReceiptDTO bankReceiptDto) {
		if (bankReceiptDto.getPaymentType().equals(PaymentType.ADVANCE_PAYMENT)
				&& !CollectionUtils.isEmpty(bankReceiptDto.getShipmentReference())) {
			bankReceiptDto.getShipmentReference().forEach(shipmentReferenceDto -> {
				if (shipmentReferenceDto.getReferenceType() != null
						&& CollectionUtils.isEmpty(shipmentReferenceDto.getReferenceNos()))
					throw new InvalidDataException(ErrorCode.INVALID_DATA_ERROR,
							IF_SHIPMENT_REFERENCE_TYPE_IS_SELECTED_THEN_SHIPMENT_REFERENCE_NO_IS_MANDATORY);
			});
		}

		if (bankReceiptDto.getPaymentType().equals(PaymentType.ADVANCE_PAYMENT)
				&& CollectionUtils.isEmpty(bankReceiptDto.getBankReceiptLedger()))
			throw new InvalidDataException(ErrorCode.INVALID_DATA_ERROR,
					IF_PAYMENT_DETAILS_IS_ADVANCE_PAYMENT_THEN_ADVANCE_LEDGER_GRID_MUST_NOT_EMPTY);

		validateLedgerAndAmount(bankReceiptDto);
	}

	private void validateLedgerAndAmount(BankReceiptDTO bankReceiptDto) {
		if (bankReceiptDto.getPaymentType().equals(PaymentType.ADVANCE_PAYMENT)
				&& !CollectionUtils.isEmpty(bankReceiptDto.getBankReceiptLedger())) {
			bankReceiptDto.getBankReceiptLedger().forEach(bankReceiptLedgerDto -> {
				if (bankReceiptLedgerDto.getLedgerId() == null)
					throw new InvalidDataException(ErrorCode.INVALID_DATA_ERROR,LEDGER_CODE_IS_MANDATORY);
				if (bankReceiptLedgerDto.getAmount() == null)
					throw new InvalidDataException(ErrorCode.INVALID_DATA_ERROR,AMOUNT_IS_MANDATORY);
			});
		}
	}

	private void validateInstrumentNo(BankReceiptDTO bankReceiptDto) {
		if ((bankReceiptDto.getInstrumentType().equals(InstrumentType.CHEQUE)
				|| bankReceiptDto.getInstrumentType().equals(InstrumentType.DEMAND_DRAFT))
				&& StringUtils.isBlank(bankReceiptDto.getInstrumentNo()))
			throw new InvalidDataException(ErrorCode.INVALID_DATA_ERROR,IF_INSTRUMENT_TYPE_IS_CHEQUE_OR_DEMAND_DRAFT_THEN_INSTRUMENT_NO_IS_MANDATORY);
	}

	private void validateForexAmount(BankReceiptDTO bankReceiptDto) {
		if ((bankReceiptDto.getInstrumentType().equals(InstrumentType.INWARD_REMITTANCE_FOREIGN_PARTY)
				|| bankReceiptDto.getInstrumentType().equals(InstrumentType.INWARD_REMITTANCE_LOCAL_PARTY))
				&& bankReceiptDto.getAmountTxnCcy() == null)
			throw new InvalidDataException(ErrorCode.INVALID_DATA_ERROR,
					IF_INSTRUMENT_TYPE_IS_INWARD_REMITTANCE_FOREIGN_PARTY_OR_INWARD_REMITTANCE_LOCAL_PARTY_THEN_FOREX_AMOUNT_IS_MANDATORY);
	}

	private void validateTxnCurrencyId(BankReceiptDTO bankReceiptDto) {
		if ((bankReceiptDto.getInstrumentType().equals(InstrumentType.INWARD_REMITTANCE_FOREIGN_PARTY)
				|| bankReceiptDto.getInstrumentType().equals(InstrumentType.INWARD_REMITTANCE_LOCAL_PARTY))
				&& bankReceiptDto.getTxnCurrencyId() == null)
			throw new InvalidDataException(ErrorCode.INVALID_DATA_ERROR,
					IF_INSTRUMENT_TYPE_IS_INWARD_REMITTANCE_FOREIGN_PARTY_OR_INWARD_REMITTANCE_LOCAL_PARTY_THEN_CURRENCY_IS_MANDATORY);
	}

	private void validateExchangeRate(BankReceiptDTO bankReceiptDto) {
		if ((bankReceiptDto.getInstrumentType().equals(InstrumentType.INWARD_REMITTANCE_FOREIGN_PARTY)
				|| bankReceiptDto.getInstrumentType().equals(InstrumentType.INWARD_REMITTANCE_LOCAL_PARTY))
				&& bankReceiptDto.getExchangeRate() == null)
			throw new InvalidDataException(ErrorCode.INVALID_DATA_ERROR,
					IF_INSTRUMENT_TYPE_IS_INWARD_REMITTANCE_FOREIGN_PARTY_OR_INWARD_REMITTANCE_LOCAL_PARTY_THEN_EXCHANGE_RATE_IS_MANDATORY);
	}

	private void validateTdsAmount(BankReceiptDTO bankReceiptDto) {
		if (bankReceiptDto.getTdsDeductedFlag().equals(Boolean.TRUE) && bankReceiptDto.getTdsAmount() == null)
			throw new InvalidDataException(ErrorCode.INVALID_DATA_ERROR,IF_TDS_DEDUCTED_CHECK_BOX_IS_CHECKED_THEN_TDS_AMOUNT_IS_MANDATORY);
	}

}
