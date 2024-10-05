package com.grtship.mdm.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.grtship.core.dto.GstDTO;
import com.grtship.mdm.criteria.GstCriteria;

public interface GstFilterService {

	public List<GstDTO> findByCriteria(GstCriteria criteria);

	public Page<GstDTO> findByCriteria(GstCriteria criteria, Pageable page);

}
