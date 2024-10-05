package com.grtship.account.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.grtship.account.criteria.LedgerCriteria;
import com.grtship.core.dto.LedgerDTO;

public interface LedgerQueryService {
	public List<LedgerDTO> findByCriteria(LedgerCriteria criteria);

	public Page<LedgerDTO> findByCriteria(LedgerCriteria criteria, Pageable page);

	public long countByCriteria(LedgerCriteria criteria);

	public Optional<LedgerDTO> findOne(Long id);

}
