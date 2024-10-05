package com.grtship.account.mapper;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import com.grtship.account.adaptor.MasterModuleAdapter;
import com.grtship.account.criteria.BankReceiptCriteria;
import com.grtship.account.criteria.InvoiceCriteria;
import com.grtship.account.criteria.LedgerCriteria;
import com.grtship.account.domain.BankReceipt;
import com.grtship.account.domain.BankReceiptLedger;
import com.grtship.account.domain.InvoiceTransaction;
import com.grtship.account.domain.ShipmentReference;
import com.grtship.account.interfaces.EntityMapper;
import com.grtship.account.service.BankReceiptService;
import com.grtship.account.service.InvoiceFilterService;
import com.grtship.account.service.LedgerQueryService;
import com.grtship.core.dto.BankReceiptLedgerDTO;
import com.grtship.core.dto.BankReceiptResponseDTO;
import com.grtship.core.dto.BaseDTO;
import com.grtship.core.dto.CurrencyDTO;
import com.grtship.core.dto.InvoiceDTO;
import com.grtship.core.dto.InvoiceTransactionResponseDTO;
import com.grtship.core.dto.LedgerDTO;
import com.grtship.core.dto.ShipmentReferenceDTO;
import com.grtship.core.enumeration.InvoiceTransactionType;
import com.grtship.core.enumeration.PaymentType;
import com.grtship.core.enumeration.ReferenceType;

@Mapper(componentModel = "spring")
public abstract class BankReceiptResponseMapper implements EntityMapper<BankReceiptResponseDTO, BankReceipt> {

	@Autowired
	private LedgerQueryService ledgerFilterService;

	@Autowired
	private InvoiceFilterService invoiceFilterService;

	@Autowired
	private MasterModuleAdapter masterModuleAdapter;

	@Autowired
	private BankReceiptService bankReceiptService;

	@Override
	public BankReceiptResponseDTO toDto(BankReceipt bankReceipt) {
		BankReceiptResponseDTO dto = new BankReceiptResponseDTO();
		prepareDto(bankReceipt, dto);
		if (bankReceipt.getPaymentType() != null
				&& bankReceipt.getPaymentType().equals(PaymentType.PAYMENT_AGAINST_BILL)
				&& !CollectionUtils.isEmpty(bankReceipt.getInvoiceTransaction())) {
			prepareInvoiceTransactions(bankReceipt, dto);
			preparePreviousUnadjustedAmountsDetails(bankReceipt, dto);
		}

		if (bankReceipt.getPaymentType() != null && bankReceipt.getPaymentType().equals(PaymentType.ADVANCE_PAYMENT)
				&& !CollectionUtils.isEmpty(bankReceipt.getShipmentReference()))
			prepareShipmentReference(bankReceipt, dto);
		if (bankReceipt.getPaymentType() != null && bankReceipt.getPaymentType().equals(PaymentType.ADVANCE_PAYMENT)
				&& !CollectionUtils.isEmpty(bankReceipt.getBankReceiptLedger()))
			prepareLedgerDetails(bankReceipt, dto);
		return dto;
	}

	private void preparePreviousUnadjustedAmountsDetails(BankReceipt bankReceipt, BankReceiptResponseDTO dto) {
		BankReceiptCriteria criteria = new BankReceiptCriteria();
		criteria.setExternalEntityId(bankReceipt.getExternalEntityId());
		bankReceiptService.getPreviousBankReceiptDetails(criteria);
		dto.setUnadjustedAmountsDetails(new HashSet<>(bankReceiptService.getPreviousBankReceiptDetails(criteria)));
	}

	private void prepareDto(BankReceipt bankReceipt, BankReceiptResponseDTO dto) {
		Map<Long, CurrencyDTO> currencyMap = masterModuleAdapter.getAllCurrenciesByIdList(
				Arrays.asList(bankReceipt.getTxnCurrencyId(), bankReceipt.getBaseCurrencyId()));
		dto.setId(bankReceipt.getId());
		dto.setVoucherNo(bankReceipt.getVoucherNo());
		dto.setVoucherDate(bankReceipt.getVoucherDate());
		dto.setVersion(bankReceipt.getVersion());
		dto.setInstrumentNo(bankReceipt.getInstrumentNo());
		dto.setInstrumentDate(bankReceipt.getInstrumentDate());
		dto.setInstrumentType(bankReceipt.getInstrumentType());
		dto.setTransactionType(bankReceipt.getTransactionType());
		dto.setBankName(bankReceipt.getBankName());
		dto.setAmountBaseCcy(bankReceipt.getAmountBaseCcy());
		dto.setAmountTxnCcy(bankReceipt.getAmountTxnCcy());
		if (bankReceipt.getTxnCurrencyId() != null) {
			dto.setTxnCurrency(currencyMap.get(bankReceipt.getTxnCurrencyId()).getCode());
		}
		if (bankReceipt.getBaseCurrencyId() != null) {
			dto.setBaseCurrency(currencyMap.get(bankReceipt.getBaseCurrencyId()).getCode());
		}
		dto.setExchangeRate(bankReceipt.getExchangeRate());
		dto.setPartyType(bankReceipt.getPartyType());
		dto.setTdsDeductedFlag(bankReceipt.getTdsDeductedFlag());
		dto.setExternalEntityId(bankReceipt.getExternalEntityId());
		dto.setEntityBranchId(bankReceipt.getEntityBranchId());
		dto.setTaxReferenceNo(bankReceipt.getTaxReferenceNo());
		dto.setMainModuleId(bankReceipt.getMainModuleId());
		dto.setSubModuleId(bankReceipt.getSubModuleId());
		dto.setTdsAmount(bankReceipt.getTdsAmount());
		dto.setPaymentType(bankReceipt.getPaymentType());
		dto.setAdjustmentType(bankReceipt.getAdjustmentType());
		dto.setNarration(bankReceipt.getNarration());
		dto.setAdjustedAmount(bankReceipt.getAdjustedAmount());
		dto.setUnadjustedAmount(bankReceipt.getUnadjustedAmount());

	}

	private void prepareShipmentReference(BankReceipt bankReceipt, BankReceiptResponseDTO dto) {
		Set<ShipmentReferenceDTO> shipmentReferenceDtos = new HashSet<>();
		Set<ShipmentReference> shipmentReferences = bankReceipt.getShipmentReference();
		Set<ReferenceType> referenceTypes = shipmentReferences.stream().filter(obj -> obj.getReferenceType() != null)
				.map(ShipmentReference::getReferenceType).collect(Collectors.toSet());
		referenceTypes.forEach(type -> {
			ShipmentReferenceDTO shipmentReferenceDto = new ShipmentReferenceDTO();
			shipmentReferenceDto.setReferenceType(type);
			Set<BaseDTO> nos = new HashSet<>();
			shipmentReferences.forEach(shipmentReference -> {
				if (type.equals(shipmentReference.getReferenceType()))
					nos.add(new BaseDTO(shipmentReference.getId(), shipmentReference.getReferenceNo()));
			});
			shipmentReferenceDto.setResponseReferenceNos(nos);
			shipmentReferenceDtos.add(shipmentReferenceDto);
		});
		dto.setShipmentReference(shipmentReferenceDtos);
	}

	private void prepareInvoiceTransactions(BankReceipt bankReceipt, BankReceiptResponseDTO dto) {
		Set<InvoiceTransactionResponseDTO> invoiceTransactionResponseDtos = new HashSet<>();
		Set<Long> invoiceIds = bankReceipt.getInvoiceTransaction().stream()
				.filter(obj -> obj.getInvoice().getId() != null).map(obj -> obj.getInvoice().getId())
				.collect(Collectors.toSet());
		InvoiceCriteria criteria = new InvoiceCriteria();
		criteria.setIds(invoiceIds);
		Map<Long, InvoiceDTO> invoiceMap = invoiceFilterService.findByCriteria(criteria).stream()
				.filter(invoiceDto -> invoiceDto.getId() != null)
				.collect(Collectors.toMap(InvoiceDTO::getId, value -> value));
		Set<InvoiceTransaction> invoiceTransaction = bankReceipt.getInvoiceTransaction();
		invoiceIds.forEach(invoiceId -> {
			List<InvoiceTransaction> invoices = invoiceTransaction.stream()
					.filter(obj -> obj.getInvoice().getId().equals(invoiceId)).collect(Collectors.toList());
			if (invoices.size() > 1) {
				InvoiceTransactionResponseDTO invoiceTransactionResponseDto = new InvoiceTransactionResponseDTO();
				invoiceTransactionResponseDto.setInvoiceRefNo(invoiceMap.get(invoiceId).getRefNo());
				invoiceTransactionResponseDto.setInvoiceCurrency(invoiceMap.get(invoiceId).getCurrencyCode());
				invoiceTransactionResponseDto.setInvoiceDate(invoiceMap.get(invoiceId).getDate());
				invoiceTransactionResponseDto.setInvoiceAmount(invoiceMap.get(invoiceId).getAmount());
				invoiceTransactionResponseDto.setAdjustedAmount(invoiceMap.get(invoiceId).getAdjustedAmount());
				invoices.forEach(obj -> {
					if (obj.getTransactionType().equals(InvoiceTransactionType.ADJUSTMENT)) {
						invoiceTransactionResponseDto.setAmount(obj.getTransactionAmount());
						invoiceTransactionResponseDto.setBalanceAmount(invoiceMap.get(invoiceId).getAmount()
								- obj.getTransactionAmount() - invoiceTransactionResponseDto.getAdjustedAmount());
					} else
						invoiceTransactionResponseDto.setExcessShortAmount(obj.getTransactionAmount());
				});
				invoiceTransactionResponseDto.setWriteOff(true);
				invoiceTransactionResponseDtos.add(invoiceTransactionResponseDto);
			} else {
				InvoiceTransactionResponseDTO invoiceTransactionResponseDto = new InvoiceTransactionResponseDTO();
				invoiceTransactionResponseDto.setInvoiceRefNo(invoiceMap.get(invoiceId).getRefNo());
				invoiceTransactionResponseDto.setInvoiceCurrency(invoiceMap.get(invoiceId).getCurrencyCode());
				invoiceTransactionResponseDto.setInvoiceDate(invoiceMap.get(invoiceId).getDate());
				invoiceTransactionResponseDto.setInvoiceAmount(invoiceMap.get(invoiceId).getAmount());
				invoiceTransactionResponseDto.setAdjustedAmount(invoiceMap.get(invoiceId).getAdjustedAmount());
				invoiceTransactionResponseDto.setBalanceAmount(invoiceMap.get(invoiceId).getAmount()
						- invoices.get(0).getTransactionAmount() - invoiceTransactionResponseDto.getAdjustedAmount());
				invoiceTransactionResponseDto.setAmount(invoices.get(0).getTransactionAmount());
				invoiceTransactionResponseDto.setExcessShortAmount(invoiceMap.get(invoiceId).getAmount()
						- invoices.get(0).getTransactionAmount() - invoiceTransactionResponseDto.getAdjustedAmount());
				invoiceTransactionResponseDto.setWriteOff(false);
				invoiceTransactionResponseDtos.add(invoiceTransactionResponseDto);
			}
		});
		dto.setInvoiceTransaction(invoiceTransactionResponseDtos);
	}

	private void prepareLedgerDetails(BankReceipt bankReceipt, BankReceiptResponseDTO dto) {
		Set<BankReceiptLedgerDTO> bankReceiptLedgerDtos = new HashSet<>();
		List<Long> ledgerIds = bankReceipt.getBankReceiptLedger().stream().filter(obj -> obj.getLedgerId() != null)
				.map(BankReceiptLedger::getLedgerId).collect(Collectors.toList());
		LedgerCriteria criteria = new LedgerCriteria();
		criteria.setIds(ledgerIds);
		Map<Long, LedgerDTO> ledgerMap = ledgerFilterService.findByCriteria(criteria).stream()
				.filter(ledgerDto -> ledgerDto.getId() != null)
				.collect(Collectors.toMap(LedgerDTO::getId, value -> value));
		bankReceipt.getBankReceiptLedger().forEach(ledger -> {
			BankReceiptLedgerDTO bankReceiptLedgerDto = new BankReceiptLedgerDTO();
			bankReceiptLedgerDto.setLedgerId(ledger.getLedgerId());
			bankReceiptLedgerDto.setLedgerCode(ledgerMap.get(ledger.getLedgerId()).getCode());
			bankReceiptLedgerDto.setLedgerName(ledgerMap.get(ledger.getLedgerId()).getName());
			bankReceiptLedgerDto.setGroupName(ledgerMap.get(ledger.getLedgerId()).getGroupName());
			bankReceiptLedgerDto.setAmount(ledger.getAmount());
			bankReceiptLedgerDtos.add(bankReceiptLedgerDto);
		});
		dto.setBankReceiptLedger(bankReceiptLedgerDtos);
	}

	@Override
	public List<BankReceiptResponseDTO> toDto(List<BankReceipt> bankReceiptList) {
		return bankReceiptList.stream().filter(bankReceipt -> bankReceipt.getId() != null).map(this::toDto)
				.collect(Collectors.toList());
	}

}
