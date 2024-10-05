package com.grtship.client.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.grtship.client.criteria.CompanyCriteria;
import com.grtship.core.dto.BaseDTO;
import com.grtship.core.dto.CompanyBranchBaseDTO;
import com.grtship.core.dto.CompanyDTO;
import com.grtship.core.dto.UserCompanyDTO;

public interface CompanyQueryService {

	public List<CompanyDTO> findByCriteria(CompanyCriteria criteria);
	
	public Page<CompanyDTO> findByCriteria(CompanyCriteria criteria, Pageable page);
	
	Optional<CompanyDTO> findOne(Long id);
	
	Set<CompanyBranchBaseDTO> getCompanyAndBanchesByUserId(Long userId);
	
	List<BaseDTO> findByClientIdIn(List<Long> clientIds);
	
	Set<UserCompanyDTO> getByUserAccess(List<UserCompanyDTO> userCompanies);
}
