package com.grtship.account.service.impl;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.grtship.account.domain.InvoiceTransaction;
import com.grtship.account.mapper.InvoiceTransactionMapper;
import com.grtship.account.repository.InvoiceTransactionRepository;
import com.grtship.account.service.InvoiceTransactionService;
import com.grtship.core.annotation.Auditable;
import com.grtship.core.annotation.Auditable.ActionType;
import com.grtship.core.dto.InvoiceTransactionDTO;

/**
 * Service Implementation for managing {@link InvoiceTransaction}.
 */
@Service
@Transactional
public class InvoiceTransactionServiceImpl implements InvoiceTransactionService {

	private final Logger log = LoggerFactory.getLogger(InvoiceTransactionServiceImpl.class);

	private final InvoiceTransactionRepository invoiceTransactionRepository;

	private final InvoiceTransactionMapper invoiceTransactionMapper;

	public InvoiceTransactionServiceImpl(InvoiceTransactionRepository invoiceTransactionRepository,
			InvoiceTransactionMapper invoiceTransactionMapper) {
		this.invoiceTransactionRepository = invoiceTransactionRepository;
		this.invoiceTransactionMapper = invoiceTransactionMapper;
	}

	@Override
	@Auditable(action = ActionType.SAVE, module = com.grtship.core.annotation.Auditable.Module.INVOICE_TRANSACTION)
	public InvoiceTransactionDTO save(InvoiceTransactionDTO invoiceTransactionDTO) {
		log.debug("Request to save InvoiceTransaction : {}", invoiceTransactionDTO);
		InvoiceTransaction invoiceTransaction = invoiceTransactionMapper.toEntity(invoiceTransactionDTO);
		invoiceTransaction = invoiceTransactionRepository.save(invoiceTransaction);
		return invoiceTransactionMapper.toDto(invoiceTransaction);
	}

	@Override
	public void saveAll(Set<InvoiceTransactionDTO> dtos) {
		invoiceTransactionRepository
				.saveAll(dtos.stream().map(invoiceTransactionMapper::toEntity).collect(Collectors.toList()));
	}

	@Override
	@Transactional(readOnly = true)
	public List<InvoiceTransactionDTO> findAll() {
		log.debug("Request to get all InvoiceTransactions");
		return invoiceTransactionRepository.findAll().stream().map(invoiceTransactionMapper::toDto)
				.collect(Collectors.toCollection(LinkedList::new));
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<InvoiceTransactionDTO> findOne(Long id) {
		log.debug("Request to get InvoiceTransaction : {}", id);
		return invoiceTransactionRepository.findById(id).map(invoiceTransactionMapper::toDto);
	}

	@Override
	@Auditable(action = ActionType.DELETE, module = com.grtship.core.annotation.Auditable.Module.INVOICE_TRANSACTION)
	public void delete(Long id) {
		log.debug("Request to delete InvoiceTransaction : {}", id);
		invoiceTransactionRepository.deleteById(id);
	}
}
