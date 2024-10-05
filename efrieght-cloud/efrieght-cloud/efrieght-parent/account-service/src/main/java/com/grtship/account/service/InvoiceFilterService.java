package com.grtship.account.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.grtship.account.criteria.InvoiceCriteria;
import com.grtship.core.dto.InvoiceDTO;

public interface InvoiceFilterService {

	List<InvoiceDTO> findByCriteria(InvoiceCriteria criteria);

	Page<InvoiceDTO> findByCriteria(InvoiceCriteria criteria, Pageable page);
}
