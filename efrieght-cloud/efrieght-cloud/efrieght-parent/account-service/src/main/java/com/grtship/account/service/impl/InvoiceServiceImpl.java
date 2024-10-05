package com.grtship.account.service.impl;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.grtship.account.criteria.InvoiceCriteria;
import com.grtship.account.domain.Invoice;
import com.grtship.account.mapper.InvoiceMapper;
import com.grtship.account.repository.InvoiceRepository;
import com.grtship.account.service.InvoiceFilterService;
import com.grtship.account.service.InvoiceService;
import com.grtship.core.annotation.Auditable;
import com.grtship.core.annotation.Auditable.ActionType;
import com.grtship.core.dto.InvoiceDTO;

/**
 * Service Implementation for managing {@link Invoice}.
 */
@Service
@Transactional
public class InvoiceServiceImpl implements InvoiceService {

	private final Logger log = LoggerFactory.getLogger(InvoiceServiceImpl.class);

	@Autowired
	private InvoiceRepository invoiceRepository;

	@Autowired
	private InvoiceMapper invoiceMapper;

	@Autowired
	private InvoiceFilterService invoiceFilterService;

	@Override
	@Auditable(action = ActionType.SAVE, module = com.grtship.core.annotation.Auditable.Module.INVOICE)
	public InvoiceDTO save(InvoiceDTO invoiceDto) {
		log.debug("Request to save Invoice : {}", invoiceDto);
		Invoice invoice = invoiceMapper.toEntity(invoiceDto);
		invoice = invoiceRepository.save(invoice);
		return invoiceMapper.toDto(invoice);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<InvoiceDTO> findAll(Pageable pageable) {
		log.debug("Request to get all Invoices");
		return invoiceRepository.findAll(pageable).map(invoiceMapper::toDto);
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<InvoiceDTO> findOne(Long id) {
		log.debug("Request to get Invoice : {}", id);
		InvoiceCriteria criteria = new InvoiceCriteria();
		criteria.setId(id);
		List<InvoiceDTO> invoiceList = invoiceFilterService.findByCriteria(criteria);
		return (!CollectionUtils.isEmpty(invoiceList)) ? Optional.of(invoiceList.get(0)) : Optional.empty();
	}

	@Override
	@Auditable(action = ActionType.DELETE, module = com.grtship.core.annotation.Auditable.Module.INVOICE)
	public void delete(Long id) {
		log.debug("Request to delete Invoice : {}", id);
		invoiceRepository.deleteById(id);
	}

	@Override
	public Map<Long, InvoiceDTO> getInvoiceMap(Collection<Long> ids) {
		return invoiceRepository.findByIdIn(ids).stream()
				.collect(Collectors.toMap(Invoice::getId, invoiceMapper::toDto));
	}

	@Override
	public void saveAll(List<InvoiceDTO> invoiceDtos) {
		invoiceRepository.saveAll(invoiceMapper.toEntity(invoiceDtos));
	}
}
