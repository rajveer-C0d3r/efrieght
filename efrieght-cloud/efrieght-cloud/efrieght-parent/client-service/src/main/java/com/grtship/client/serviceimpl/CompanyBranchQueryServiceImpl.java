package com.grtship.client.serviceimpl;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.grtship.client.criteria.CompanyBranchCriteria;
import com.grtship.client.filter.AccessFilter;
import com.grtship.client.mapper.CompanyBranchMapper;
import com.grtship.client.repository.CompanyBranchRepository;
import com.grtship.client.service.CompanyBranchQueryService;
import com.grtship.client.service.CompanyService;
import com.grtship.client.specs.CompanyBranchSpecs;
import com.grtship.core.dto.CompanyBranchBaseDTO;
import com.grtship.core.dto.CompanyBranchDTO;
import com.grtship.core.dto.UserCompanyDTO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(readOnly = true)
public class CompanyBranchQueryServiceImpl implements CompanyBranchQueryService {

	@Autowired CompanyBranchRepository branchRepository;

	@Autowired CompanyBranchMapper branchMapper;
	@Autowired CompanyService companyService;

	@Override
	@AccessFilter(clientAccessFlag = true,companyAccessFlag = true,branchAccessFlag = false)
	public List<CompanyBranchDTO> findByCriteria(CompanyBranchCriteria criteria) {
		return branchMapper.toDto( branchRepository.findAll(CompanyBranchSpecs.getCompanyBranchBySpecs(criteria)) );
	}

	@Override
	@AccessFilter(clientAccessFlag = true,companyAccessFlag = true,branchAccessFlag = false)
	public Page<CompanyBranchDTO> findByCriteria(CompanyBranchCriteria criteria, Pageable page) {
		return branchRepository.findAll(CompanyBranchSpecs.getCompanyBranchBySpecs(criteria), page)
				.map(branchMapper::toDto);
	}

	/**
     * Get the "id" companyBranch.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
	@Override
	public Optional<CompanyBranchDTO> findOne(Long id) {
		CompanyBranchCriteria criteria = new CompanyBranchCriteria();
		criteria.setId(id);
		List<CompanyBranchDTO> companyBrachDto = findByCriteria(criteria);
		return (!CollectionUtils.isEmpty(companyBrachDto)) ? Optional.of(companyBrachDto.get(0))
				: Optional.ofNullable(null);
	}
	
	@Override
	public List<CompanyBranchBaseDTO> findByCompanyIdForMultiDropdown(List<Long> companyIdList) {
		log.debug("Request to fetch company & branch data from {}",companyIdList);
		return branchRepository.findByCompanyIdForMultiDropdown(companyIdList);
	}

	@Override
	public List<CompanyBranchBaseDTO> findByIdForMultiDropdown(List<Long> branchIdList) {
		return branchRepository.findByIdForMultiDropdown(branchIdList);
	}
	
	@Override
	public List<Long> getBranchIdsByCompanyIdAndActiveFlag(Long companyId, Boolean activeFlag) {
		return branchRepository.findByCompanyIdAndActiveFlag(companyId, activeFlag);
	}

	@Override
	public List<Long> getBranchIdsByCompanyIdAndActiveFlagAndDeactivateAutoGenerateId(Long referenceId,Boolean activeFlag, String deactivateAutoGenerateId) {
		return branchRepository.findByCompanyIdAndActiveFlagAndDeactivateDtlsDeactivateAutoGenerateId(
				referenceId, activeFlag, deactivateAutoGenerateId);
	}

	@Override
	public List<UserCompanyDTO> findByCompanyIdWithAllBranches(List<Long> companyIdList) {
		if(CollectionUtils.isEmpty(companyIdList)) {
			return Collections.emptyList();
		}
		return branchRepository.findByCompanyIdWithAllBranches(companyIdList);
	}

	@Override
	public List<UserCompanyDTO> findByIdWithSpecificBranches(List<Long> branchIdList) {
		if(CollectionUtils.isEmpty(branchIdList)) {
			return Collections.emptyList();
		}
		return branchRepository.findByIdWithSpecificBranches(branchIdList);
	}

	@Override
	public List<UserCompanyDTO> getByCompanyIdWithAllBranches(List<Long> companyIdList) {
		if(CollectionUtils.isEmpty(companyIdList)) {
			return Collections.emptyList();
		}
		return branchRepository.getByCompanyIdWithAllBranches(companyIdList);
	}
}
