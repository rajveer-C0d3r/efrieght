package com.grtship.account.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.grtship.account.criteria.TdsCriteria;
import com.grtship.core.dto.TdsDTO;

public interface TdsFilterService {

	List<TdsDTO> findByCriteria(TdsCriteria criteria);

	Page<TdsDTO> findByCriteria(TdsCriteria criteria, Pageable page);

	long countByCriteria(TdsCriteria criteria);
}
