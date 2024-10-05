package com.grtship.client.serviceimpl;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
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

import com.grtship.client.adaptor.MasterModuleAdapter;
import com.grtship.client.adaptor.OAuthModuleAdapter;
import com.grtship.client.criteria.CompanyCriteria;
import com.grtship.client.domain.Address_;
import com.grtship.client.domain.Company;
import com.grtship.client.domain.Company_;
import com.grtship.client.feignclient.OAuthModule;
import com.grtship.client.filter.AccessFilter;
import com.grtship.client.mapper.CompanyMapper;
import com.grtship.client.repository.CompanyRepository;
import com.grtship.client.service.CompanyBranchQueryService;
import com.grtship.client.service.CompanyQueryService;
import com.grtship.core.dto.BaseDTO;
import com.grtship.core.dto.CompanyBranchBaseDTO;
import com.grtship.core.dto.CompanyDTO;
import com.grtship.core.dto.CountryDTO;
import com.grtship.core.dto.CsaDetailsDTO;
import com.grtship.core.dto.UserAccessDTO;
import com.grtship.core.dto.UserCompanyDTO;
import com.grtship.core.enumeration.DomainStatus;

@Service
@Transactional(readOnly = true)
public class CompanyQueryServiceImpl implements CompanyQueryService {

	private final Logger log = LoggerFactory.getLogger(CompanyQueryServiceImpl.class);

	@Autowired
	private CompanyRepository companyRepository;

	@Autowired
	private CompanyMapper companyMapper;
	
	@Autowired
	private CompanyBranchQueryService companyBranchQueryService;

	@Autowired
	private OAuthModuleAdapter oAuthModuleAdapter;

	@Autowired
	private MasterModuleAdapter masterModuleAdapter;
	
	@Autowired
	private OAuthModule authClient;
	
	@Autowired
	private CompanyQueryService companyQueryService;

	@Transactional(readOnly = true)
	@Override
	@AccessFilter(clientAccessFlag = true, companyAccessFlag = false, branchAccessFlag = false)
	public List<CompanyDTO> findByCriteria(CompanyCriteria criteria) {
		log.debug("find by criteria : {}", criteria);
		final Specification<Company> specification = createSpecification(criteria);
		List<CompanyDTO> companyDtos = companyMapper.toDto(companyRepository.findAll(specification));
		prepareCompanies(companyDtos);
		return companyDtos;
	}

	@Transactional(readOnly = true)
	@Override
	@AccessFilter(clientAccessFlag = true, companyAccessFlag = false, branchAccessFlag = false)
	public Page<CompanyDTO> findByCriteria(CompanyCriteria criteria, Pageable page) {
		log.debug("find by criteria : {}, page: {}", criteria, page);
		final Specification<Company> specification = createSpecification(criteria);
		Page<Company> company = companyRepository.findAll(specification, page);
		List<CompanyDTO> companyDtos = companyMapper.toDto(company.getContent());
		prepareCompanies(companyDtos);
		return new PageImpl<>(companyDtos, page, company.getTotalElements());
	}

	private void prepareCompanies(List<CompanyDTO> companyDtos) {
		List<Long> companyIdList = companyDtos.stream().filter(companyDto -> companyDto.getId() != null)
				.map(CompanyDTO::getId).collect(Collectors.toList());
		Set<Long> countryIdList = companyDtos.stream()
				.filter(companyDto -> companyDto.getId() != null && companyDto.getAddress() != null
						&& companyDto.getAddress().getCountryId() != null)
				.map(companyDto -> companyDto.getAddress().getCountryId()).collect(Collectors.toSet());
		Map<Long, List<CsaDetailsDTO>> csaUserMap = oAuthModuleAdapter.getCsaUsersByCompanyIdList(companyIdList);
		List<CountryDTO> countriesByIds = masterModuleAdapter.getCountriesByIds(countryIdList);
		companyDtos.forEach(companyDto -> {
			companyDto.setCsaDetails(csaUserMap.get(companyDto.getId()));
			countriesByIds.forEach(baseDto -> {
				if (companyDto.getAddress() != null && companyDto.getAddress().getCountryId() != null
						&& baseDto.getId().equals(companyDto.getAddress().getCountryId())) {
					companyDto.getAddress().setCountryName(baseDto.getName());
					companyDto.setCountryName(baseDto.getName());
				}
			});
		});
	}
	
	@Override
	@Transactional(readOnly = true)
	public Optional<CompanyDTO> findOne(Long id) {
		log.debug("Request to get Company : {}", id);
		CompanyCriteria criteria = new CompanyCriteria();
		criteria.setId(id);
		List<CompanyDTO> companies = companyQueryService.findByCriteria(criteria);
		return (!CollectionUtils.isEmpty(companies)) ? Optional.of(companies.get(0)) : Optional.ofNullable(null);
	}

	@Override
	public Set<CompanyBranchBaseDTO> getCompanyAndBanchesByUserId(Long userId) {
		Set<CompanyBranchBaseDTO> finalList = new HashSet<>();
		List<UserAccessDTO> userCompanies = authClient.getCurrentUserCompanyBranchIds(userId).getBody();
		log.debug("USer company fetced {}", userCompanies);
		if (!CollectionUtils.isEmpty(userCompanies)) {
			List<Long> companyIdList = userCompanies.stream().filter(obj -> obj.getCompanyId() != null && obj.getBranchId() == null).map(UserAccessDTO::getCompanyId).collect(Collectors.toList());
			log.debug("companyIdList: {}",companyIdList);
			List<Long> branchIdList = userCompanies.stream().filter(obj -> obj.getCompanyId() != null && obj.getBranchId() != null).map(UserAccessDTO::getBranchId).collect(Collectors.toList());
			log.debug("branchIdList {}",branchIdList);
			List<CompanyBranchBaseDTO> branchListByCompanyIds = prepareListByCompanyIds(companyIdList);
			List<CompanyBranchBaseDTO> companiesByCompanyIds = companyRepository.getCompaniesByIds(companyIdList);
			List<CompanyBranchBaseDTO> branchListByBranchIds = companyBranchQueryService.findByIdForMultiDropdown(branchIdList);

			finalList.addAll(companiesByCompanyIds);
			finalList.addAll(branchListByCompanyIds);
			finalList.addAll(branchListByBranchIds);
		}

		return finalList;

	}

	private List<CompanyBranchBaseDTO> prepareListByCompanyIds(List<Long> companyIdList) {
		return companyBranchQueryService.findByCompanyIdForMultiDropdown(companyIdList);

	}

	@Override
	public List<BaseDTO> findByClientIdIn(List<Long> clientIds) {
		return (!CollectionUtils.isEmpty(clientIds)) ? companyRepository.findByClientIdIn(clientIds).stream()
				.map(obj -> new BaseDTO(obj.getId(), obj.getName())).collect(Collectors.toList()): Collections.emptyList();
	}
	
	@Override
	public Set<UserCompanyDTO> getByUserAccess(List<UserCompanyDTO> userCompanies) {
		List<Long> companyIdsWithAllBranches = userCompanies.stream().filter(userCompany->userCompany.getAllBranches().equals(Boolean.TRUE)).map(UserCompanyDTO::getCompanyId).collect(Collectors.toList());
		List<Long> companyIdsWithSpecificBranches = userCompanies.stream().filter(userCompany->userCompany.getAllBranches().equals(Boolean.FALSE)).map(UserCompanyDTO::getBranchId).collect(Collectors.toList());
		
		List<UserCompanyDTO> companiesWithAllBranches = companyBranchQueryService.getByCompanyIdWithAllBranches(companyIdsWithAllBranches);
		List<UserCompanyDTO> companiesWithSpecificBranches = companyBranchQueryService.findByIdWithSpecificBranches(companyIdsWithSpecificBranches);
		
		Set<UserCompanyDTO> companies = new HashSet<>();
		companies.addAll(companiesWithAllBranches);
		companies.addAll(companiesWithSpecificBranches);
		return companies;
	}

	private Specification<Company> createSpecification(CompanyCriteria criteria) {
		Specification<Company> specification = Specification.where(null);
		if (criteria != null) {
			specification = specification.and(getIdSpec(criteria.getId())).and(getCodeSpec(criteria.getCode()))
					.and(getNameSpec(criteria.getName())).and(getCountryIdsSpec(criteria.getCountryIds()))
					.and(getStatusSpec(criteria.getStatus()).and(getActiveSpec(criteria.getActiveFlag())));
		}
		return specification;
	}

	private Specification<Company> getIdSpec(Long id) {
		return (id != null) ? (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(Company_.id), id)
				: Specification.where(null);
	}

	private Specification<Company> getCodeSpec(String code) {
		return (!StringUtils.isBlank(code))
				? (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get(Company_.code), "%" + code + "%")
				: Specification.where(null);
	}

	private Specification<Company> getNameSpec(String name) {
		return (!StringUtils.isBlank(name))
				? (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get(Company_.name), "%" + name + "%")
				: Specification.where(null);
	}

	private Specification<Company> getCountryIdsSpec(List<Long> countryIds) {
		if (countryIds != null) {
			return (root, query, criteriaBuilder) -> root.get(Company_.address).get(Address_.countryId)
					.in(countryIds);
		}
		return Specification.where(null);
	}

	private Specification<Company> getStatusSpec(String status) {
		if (status != null) {
			for (DomainStatus domainStaus : DomainStatus.values()) {
				if (domainStaus.name().equals(status)) {
					return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(Company_.status),
							DomainStatus.valueOf(status));
				}
			}
		}
		return Specification.where(null);
	}
	
	private Specification<Company> getActiveSpec(Boolean activeFlag) {
		return (activeFlag!=null)
				? (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(Company_.activeFlag),activeFlag)
				: Specification.where(null);
	}

}
