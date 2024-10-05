package com.grtship.account.service.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.grtship.account.adaptor.MasterModuleAdapter;
import com.grtship.account.criteria.BankReceiptCriteria;
import com.grtship.account.domain.BankReceipt;
import com.grtship.account.domain.InvoiceTransaction;
import com.grtship.account.mapper.BankReceiptMapper;
import com.grtship.account.mapper.PreviousBankReceiptResponseMapper;
import com.grtship.account.repository.BankReceiptRepository;
import com.grtship.account.service.BankReceiptService;
import com.grtship.account.service.InvoiceService;
import com.grtship.account.service.InvoiceTransactionService;
import com.grtship.account.validator.BankReceiptValidator;
import com.grtship.core.annotation.Auditable;
import com.grtship.core.annotation.Auditable.ActionType;
import com.grtship.core.dto.BankReceiptDTO;
import com.grtship.core.dto.BankReceiptResponseDTO;
import com.grtship.core.dto.CurrencyDTO;
import com.grtship.core.dto.InvoiceDTO;
import com.grtship.core.dto.InvoiceTransactionDTO;
import com.grtship.core.dto.PreviousBankReceiptResponseDTO;
import com.grtship.core.dto.ShipmentReferenceDTO;
import com.grtship.core.enumeration.DomainStatus;
import com.grtship.core.enumeration.InvoiceTransactionType;
import com.grtship.core.enumeration.TransactionStatus;

/**
 * Service Implementation for managing {@link BankReceipt}.
 */
@Service
@Transactional
public class BankReceiptServiceImpl implements BankReceiptService {

	private static final String BANK_RECEIPT = "Bank Receipt";

	private final Logger log = LoggerFactory.getLogger(BankReceiptServiceImpl.class);

	@Autowired
	private BankReceiptRepository bankReceiptRepository;

	@Autowired
	private BankReceiptMapper bankReceiptMapper;

	@Autowired
	private BankReceiptValidator bankReceiptValidator;

	@Autowired
	private InvoiceService invoiceService;

	@Autowired
	private InvoiceTransactionService invoiceTransactionService;

	@Autowired
	private PreviousBankReceiptResponseMapper previousBankReceiptResponseMapper;

	@Autowired
	private BankReceiptFilterServiceImpl bankReceiptFilterService;

	@Autowired
	private MasterModuleAdapter masterModuleAdapter;

	@Override
	@Auditable(action = ActionType.SAVE, module = com.grtship.core.annotation.Auditable.Module.BANK_RECEIPT)
	public BankReceiptDTO save(BankReceiptDTO bankReceiptDto) {
		bankReceiptDto.setVoucherNo(masterModuleAdapter.generateCode(BANK_RECEIPT, LocalDate.now().toString() + "-"));
		bankReceiptValidator.saveValidations(bankReceiptDto);
		log.debug("Request to save BankReceipt : {}", bankReceiptDto);
		prepareShipmentReference(bankReceiptDto);
		prepareInvoiceTransaction(bankReceiptDto);
		prepareInvoice(bankReceiptDto);
		if (bankReceiptDto.getUnadjustedAmount() != null && bankReceiptDto.getUnadjustedAmount() == 0)
			bankReceiptDto.setStatus(TransactionStatus.CLOSED);
		else
			bankReceiptDto.setStatus(TransactionStatus.OPEN);
		BankReceipt bankReceipt = bankReceiptMapper.toEntity(bankReceiptDto);
		bankReceipt.setApprovalStatus(DomainStatus.PENDING);
		approveBankReceipt(bankReceipt);
		bankReceipt = bankReceiptRepository.save(bankReceipt);
		saveInvoiceTransactions(bankReceiptDto.getInvoiceTransaction(), bankReceipt.getId());
		return bankReceiptMapper.toDto(bankReceipt);
	}

	private void approveBankReceipt(BankReceipt bankReceipt) {
		bankReceipt.setApprovalStatus(DomainStatus.APPROVED);
	}

	private void saveInvoiceTransactions(Set<InvoiceTransactionDTO> invoiceTransactions, Long bankReceiptId) {
		if (!CollectionUtils.isEmpty(invoiceTransactions)) {
			invoiceTransactions.forEach(invoiceTransactionDto -> {
				if (!(invoiceTransactionDto.getTransactionAmount() < 0
						&& invoiceTransactionDto.getTransactionType().equals(InvoiceTransactionType.WRITE_OFF)))
					invoiceTransactionDto.setBankReceiptId(bankReceiptId);
				else
					invoiceTransactionDto.setBankReceiptId(null);
			});
		}
		invoiceTransactionService.saveAll(invoiceTransactions);
	}

	private void prepareInvoiceTransaction(BankReceiptDTO bankReceiptDto) {
		if (!CollectionUtils.isEmpty(bankReceiptDto.getInvoiceTransaction())) {
			Set<InvoiceTransactionDTO> invoiceTransactionDtos = new HashSet<>();
			Double adjustedAmount = 0d;
			for (InvoiceTransactionDTO invoiceTransactionDto : bankReceiptDto.getInvoiceTransaction()) {
				// invoiceService.findOne(invoiceTransactionDto.getInvoiceId());
				adjustedAmount += getAdustedAmount(invoiceTransactionDto);
				if (invoiceTransactionDto.getWriteOff().equals(Boolean.TRUE)) {
					InvoiceTransactionDTO writeOffDto = new InvoiceTransactionDTO();
					if (invoiceTransactionDto.getExcessShortAmount() < 0) {
						writeOffDto.setBankReceiptId(null);
						writeOffDto.setInvoiceId(invoiceTransactionDto.getInvoiceId());
					} else {
						writeOffDto.setInvoiceId(null);
					}
					writeOffDto.setTransactionType(InvoiceTransactionType.WRITE_OFF);
					writeOffDto.setTransactionDate(LocalDate.now());
					writeOffDto.setTransactionAmount(invoiceTransactionDto.getExcessShortAmount());
					writeOffDto.setAuthorisedBy(invoiceTransactionDto.getAuthorisedBy());
					writeOffDto.setPostingId(invoiceTransactionDto.getPostingId());
					invoiceTransactionDtos.add(writeOffDto);
				}
				invoiceTransactionDto.setTransactionType(InvoiceTransactionType.ADJUSTMENT);
				invoiceTransactionDto.setTransactionDate(LocalDate.now());
				invoiceTransactionDtos.add(invoiceTransactionDto);
			}
			bankReceiptDto.setInvoiceTransaction(invoiceTransactionDtos);
			bankReceiptDto.setAdjustedAmount(adjustedAmount);
			bankReceiptDto.setUnadjustedAmount(bankReceiptDto.getAmountBaseCcy() - adjustedAmount);
		}
	}

	private Double getAdustedAmount(InvoiceTransactionDTO invoiceTransactionDto) {
		Double adjustedAmount = 0d;
		if (invoiceTransactionDto.getExcessShortAmount() < 0 || (invoiceTransactionDto.getExcessShortAmount() > 0
				&& invoiceTransactionDto.getWriteOff().equals(Boolean.FALSE)))
			adjustedAmount += invoiceTransactionDto.getTransactionAmount();
		if (invoiceTransactionDto.getExcessShortAmount() > 0
				&& invoiceTransactionDto.getWriteOff().equals(Boolean.TRUE))
			adjustedAmount += (invoiceTransactionDto.getTransactionAmount()
					- invoiceTransactionDto.getExcessShortAmount());
		return adjustedAmount;
	}

	private void prepareInvoice(BankReceiptDTO bankReceiptDto) {
		if (!CollectionUtils.isEmpty(bankReceiptDto.getInvoiceTransaction())) {
			List<Long> invoiceIdList = bankReceiptDto.getInvoiceTransaction().stream()
					.filter(invoiceTransactionDto -> invoiceTransactionDto.getInvoiceId() != null)
					.map(InvoiceTransactionDTO::getInvoiceId).collect(Collectors.toList());
			Map<Long, InvoiceDTO> invoiceMap = invoiceService.getInvoiceMap(invoiceIdList);
			List<InvoiceDTO> invoiceDtos = new ArrayList<>();
			bankReceiptDto.getInvoiceTransaction().stream()
					.filter(obj -> obj.getTransactionType().equals(InvoiceTransactionType.ADJUSTMENT))
					.forEach(invoiceTransactionDto -> {
						InvoiceDTO invoiceDto = invoiceMap.get(invoiceTransactionDto.getInvoiceId());
						invoiceDto.setAdjustedAmount(
								invoiceDto.getAdjustedAmount() + invoiceTransactionDto.getTransactionAmount());
						invoiceDto.setBalanceAmount(invoiceDto.getAmount() - invoiceDto.getAdjustedAmount());
						if (invoiceDto.getBalanceAmount() <= 0d)
							invoiceDto.setInvoiceStatus(TransactionStatus.CLOSED);
						if (invoiceTransactionDto.getExcessShortAmount() <= 0
								&& invoiceTransactionDto.getWriteOff().equals(Boolean.TRUE))
							invoiceDto.setInvoiceStatus(TransactionStatus.CLOSED);
						invoiceDtos.add(invoiceDto);

					});
			invoiceService.saveAll(invoiceDtos);
		}
	}

	private void prepareShipmentReference(BankReceiptDTO bankReceiptDto) {
		if (!CollectionUtils.isEmpty(bankReceiptDto.getShipmentReference())) {
			Set<ShipmentReferenceDTO> dtos = new HashSet<>();
			bankReceiptDto.getShipmentReference().forEach(shipmentReferenceDto -> {
				if (!CollectionUtils.isEmpty(shipmentReferenceDto.getReferenceNos()))
					shipmentReferenceDto.getReferenceNos().forEach(referenceNo -> {
						ShipmentReferenceDTO dto = new ShipmentReferenceDTO();
						dto.setReferenceType(shipmentReferenceDto.getReferenceType());
						dto.setReferenceNo(referenceNo);
						dtos.add(dto);
					});
			});
			bankReceiptDto.setShipmentReference(dtos);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public Page<BankReceiptResponseDTO> findAll(BankReceiptCriteria criteria, Pageable pageable) {
		log.debug("Request to get all BankReceipts");
		return bankReceiptFilterService.findByCriteria(criteria, pageable);
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<BankReceiptResponseDTO> findOne(Long id) {
		log.debug("Request to get BankReceipt : {}", id);
		BankReceiptCriteria criteria = new BankReceiptCriteria();
		criteria.setId(id);
		List<BankReceiptResponseDTO> bankReceipts = bankReceiptFilterService.findByCriteria(criteria);
		if (!CollectionUtils.isEmpty(bankReceipts)) {
			return Optional.of(bankReceipts.get(0));
		}
		return Optional.ofNullable(null);
	}

	@Override
	public List<PreviousBankReceiptResponseDTO> getPreviousBankReceiptDetails(BankReceiptCriteria criteria) {
		List<BankReceipt> bankReceipts;
		List<PreviousBankReceiptResponseDTO> response = new ArrayList<>();
		if (criteria.getExternalEntityId() != null) {
			bankReceipts = bankReceiptRepository.findByExternalEntityIdAndStatus(criteria.getExternalEntityId(),
					TransactionStatus.OPEN);
			response = previousBankReceiptResponseMapper.toDto(bankReceipts);

		} else if (criteria.getInvoiceId() != null) {
			bankReceipts = bankReceiptRepository.findByInvoiceTransactionInvoiceId(criteria.getInvoiceId());
			response = previousBankReceiptResponseMapper.toDto(bankReceipts);
			prepareBankReceiptResponseDto(response, criteria.getInvoiceId(), bankReceipts);
		}

		return response;
	}

	private void prepareBankReceiptResponseDto(List<PreviousBankReceiptResponseDTO> response, Long invoiceId,
			List<BankReceipt> bankReceipts) {
		bankReceipts.forEach(bankReceipt -> response.forEach(dto -> {
			if (bankReceipt.getId().equals(dto.getId())) {
				Map<Long, CurrencyDTO> currencyMap = masterModuleAdapter.getAllCurrenciesByIdList(
						Arrays.asList(bankReceipt.getTxnCurrencyId(), bankReceipt.getBaseCurrencyId()));
				Double adjustedAmount = getInvoiceAdjustedAmount(bankReceipt, invoiceId);
				if (bankReceipt.getTxnCurrencyId() != null) {
					dto.setCurrency(currencyMap.get(bankReceipt.getTxnCurrencyId()).getCode());
					dto.setExchangeRate(bankReceipt.getExchangeRate());
					dto.setInvoiceAdjustedAmountTxnCcy(adjustedAmount);
					dto.setInvoiceAdjustedAmountBaseCcy(adjustedAmount * bankReceipt.getExchangeRate());
				} else {
					dto.setInvoiceAdjustedAmountBaseCcy(adjustedAmount);
					dto.setCurrency(currencyMap.get(bankReceipt.getBaseCurrencyId()).getCode());
				}
			}
		}));
	}

	private Double getInvoiceAdjustedAmount(BankReceipt bankReceipt, Long invoiceId) {
		Double adjustedAmount = 0d;
		for (InvoiceTransaction invoiceTransaction : bankReceipt.getInvoiceTransaction()) {
			if (invoiceTransaction.getInvoice().getId().equals(invoiceId))
				return invoiceTransaction.getTransactionAmount();
		}
		return adjustedAmount;
	}
}
