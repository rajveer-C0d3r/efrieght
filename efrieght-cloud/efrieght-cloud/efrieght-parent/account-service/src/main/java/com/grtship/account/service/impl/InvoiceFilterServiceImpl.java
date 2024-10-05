package com.grtship.account.service.impl;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.grtship.account.criteria.InvoiceCriteria;
import com.grtship.account.domain.Invoice;
import com.grtship.account.domain.Invoice_;
import com.grtship.account.feignclient.MasterModule;
import com.grtship.account.mapper.InvoiceMapper;
import com.grtship.account.repository.InvoiceRepository;
import com.grtship.account.service.InvoiceFilterService;
import com.grtship.core.dto.CurrencyDTO;
import com.grtship.core.dto.InvoiceDTO;
import com.grtship.core.enumeration.TransactionStatus;

@Service
@Transactional(readOnly = true)
public class InvoiceFilterServiceImpl implements InvoiceFilterService {

	private final Logger log = LoggerFactory.getLogger(InvoiceFilterServiceImpl.class);

	@Autowired
	private InvoiceRepository invoiceRepository;

	@Autowired
	private InvoiceMapper invoiceMapper;

	@Autowired
	private MasterModule masterModule;

	@Override
	@Transactional(readOnly = true)
	public List<InvoiceDTO> findByCriteria(InvoiceCriteria criteria) {
		log.debug("find by criteria : {}", criteria);
		final Specification<Invoice> specification = createSpecification(criteria);
		List<InvoiceDTO> invoiceDtoList = invoiceMapper.toDto(invoiceRepository.findAll(specification));
		prepareCurrency(invoiceDtoList);
		return invoiceDtoList;
	}

	@Override
	@Transactional(readOnly = true)
	public Page<InvoiceDTO> findByCriteria(InvoiceCriteria criteria, Pageable page) {
		log.debug("find by criteria : {}, page: {}", criteria, page);
		final Specification<Invoice> specification = createSpecification(criteria);
		Page<Invoice> invoices = invoiceRepository.findAll(specification, page);
		List<InvoiceDTO> invoiceDtoList = invoiceMapper.toDto(invoices.getContent());
		prepareCurrency(invoiceDtoList);
		return new PageImpl<>(invoiceDtoList, page, invoices.getTotalElements());
	}

	private void prepareCurrency(List<InvoiceDTO> invoiceDtoList) {
		if (!CollectionUtils.isEmpty(invoiceDtoList)) {
			List<Long> currencyIdList = invoiceDtoList.stream()
					.filter(invoiceDto -> invoiceDto != null && invoiceDto.getId() != null
							&& invoiceDto.getCurrencyId() != null)
					.map(InvoiceDTO::getCurrencyId).collect(Collectors.toList());
			Map<Long, CurrencyDTO> currencyMap = masterModule.getAllCurrenciesByIdList(currencyIdList);
			if (!CollectionUtils.isEmpty(currencyMap)) {
				invoiceDtoList.forEach(invoiceDto -> invoiceDto
						.setCurrencyCode(currencyMap.get(invoiceDto.getCurrencyId()).getCode()));
			}
		}
	}

	private Specification<Invoice> createSpecification(InvoiceCriteria criteria) {
		Specification<Invoice> specification = Specification.where(null);
		if (criteria != null) {
			specification = specification.and(getIdSpec(criteria.getId()))
					.and(getExternalEntityIdSpec(criteria.getExternalEntityId()))
					.and(getInvoiceStatusSpec(criteria.getInvoiceStatus()))
					.and(getCurrencyIdSpec(criteria.getCurrencyId())).and(getIdsSpec(criteria.getIds()));
		}
		return specification;
	}

	private Specification<Invoice> getIdsSpec(Collection<Long> ids) {
		return (!CollectionUtils.isEmpty(ids)) ? (root, query, criteriaBuilder) -> root.get("id").in(ids)
				: Specification.where(null);
	}

	private Specification<Invoice> getCurrencyIdSpec(Long currencyId) {
		return (currencyId != null)
				? (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(Invoice_.currencyId), currencyId)
				: Specification.where(null);
	}

	private Specification<Invoice> getInvoiceStatusSpec(String invoiceStatus) {
		for (TransactionStatus status : TransactionStatus.values()) {
			if (status.name().equals(invoiceStatus)) {
				return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(Invoice_.invoiceStatus),
						TransactionStatus.valueOf(invoiceStatus));
			}
		}
		return Specification.where(null);
	}

	private Specification<Invoice> getIdSpec(Long id) {
		return (id != null) ? (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(Invoice_.id), id)
				: Specification.where(null);
	}

	private Specification<Invoice> getExternalEntityIdSpec(Long externalEntityId) {
		return (externalEntityId != null) ? (root, query, criteriaBuilder) -> criteriaBuilder
				.equal(root.get(Invoice_.externalEntityId), externalEntityId) : Specification.where(null);
	}

}
