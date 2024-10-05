package com.grtship.client.serviceimpl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.google.gson.Gson;
import com.grtship.client.domain.Company;
import com.grtship.client.feignclient.OAuthModule;
import com.grtship.client.mapper.CompanyMapper;
import com.grtship.client.mapper.DomainDeactivateMapper;
import com.grtship.client.mapper.DomainReactivateMapper;
import com.grtship.client.mapper.UserSpecificCompanyMapper;
import com.grtship.client.repository.CompanyRepository;
import com.grtship.client.service.CompanyBranchQueryService;
import com.grtship.client.service.CompanyBranchService;
import com.grtship.client.service.CompanyService;
import com.grtship.common.exception.InvalidDataException;
import com.grtship.common.service.KafkaProducerService;
import com.grtship.core.annotation.Auditable;
import com.grtship.core.annotation.Auditable.ActionType;
import com.grtship.core.annotation.Validate;
import com.grtship.core.constant.ErrorCode;
import com.grtship.core.constant.KafkaTopicConstant;
import com.grtship.core.dto.CompanyDTO;
import com.grtship.core.dto.CsaDetailsDTO;
import com.grtship.core.dto.DeactivationDTO;
import com.grtship.core.dto.ReactivationDTO;
import com.grtship.core.dto.UserAccessCompanyBranchDTO;
import com.grtship.core.dto.UserSpecificCBRequest;
import com.grtship.core.dto.UserSpecificCBResponse;
import com.grtship.core.dto.UserSpecificCompanyDTO;

/**
 * Service Implementation for managing {@link Company}.
 */
@Service
@Transactional
public class CompanyServiceImpl implements CompanyService {

	private static final String INVALID_ID = "Invalid Id";

	private static final Logger log = LoggerFactory.getLogger(CompanyServiceImpl.class);

	private static final String COMPANY_NOT_FOUND_FOR_THE_GIVEN_ID = "Company Not Fount for a given id";

	public final SimpleDateFormat dateTimeFormate = new SimpleDateFormat("yyyyMMddHHmmss");

	@Autowired
	private CompanyRepository companyRepository;

	@Autowired
	private DomainDeactivateMapper domainDeactivateMapper;

	@Autowired
	private DomainReactivateMapper domainReactivateMapper;

	@Autowired
	private CompanyMapper companyMapper;

	@Autowired
	private OAuthModule authClient;

	@Autowired
	private CompanyBranchService companyBranchService;

	@Autowired
	private CompanyBranchQueryService companyBranchQueryService;

	@Autowired
	private KafkaProducerService kafkaProducerService;

	@Autowired
	private UserSpecificCompanyMapper userSpecificCompanyMapper;

	@Override
	@Auditable(action = ActionType.SAVE, module = com.grtship.core.annotation.Auditable.Module.COMPANY)
	@Validate(validator = "companyValidator", action = "save")
	public CompanyDTO save(CompanyDTO companyDto) throws Exception {
		log.debug("Request to save Company : {}", companyDto);
		Company company = companyMapper.toEntity(companyDto);
		Company savedCompany = companyRepository.save(company);
		saveCsaDetails(companyDto, savedCompany);
		return companyMapper.toDto(savedCompany);
	}

	@Override
	@Auditable(action = ActionType.UPDATE, module = com.grtship.core.annotation.Auditable.Module.COMPANY)
	@Validate(validator = "companyValidator", action = "update")
	public CompanyDTO update(CompanyDTO companyDto) throws Exception {
		log.debug("Request to update Company : {}", companyDto);
		Optional<Company> optionalCompany = companyRepository.findById(companyDto.getId());
		if (optionalCompany.isPresent()) {
			Company company = companyMapper.toEntity(companyDto);
			companyDto.setCode(optionalCompany.get().getCode());
			Company savedCompany = companyRepository.save(company);
			updateCsaList(companyDto, savedCompany);
			return companyMapper.toDto(savedCompany);
		} else {
			throw new InvalidDataException(ErrorCode.INVALID_DATA_ERROR, INVALID_ID);
		}
	}

	private void updateCsaList(CompanyDTO companyDto, Company savedCompany) {
		List<CsaDetailsDTO> csaDetails = companyDto.getCsaDetails();
		if (!CollectionUtils.isEmpty(csaDetails)) {
			csaDetails = csaDetails.stream().filter(obj -> obj.getId() == null).collect(Collectors.toList());
			csaDetails.forEach(csaDetailsDTO -> {
				csaDetailsDTO.setClientId(companyDto.getClientId());
				csaDetailsDTO.setCompanyId(savedCompany.getId());
			});
			authClient.generateCsaUsers(csaDetails);
		}
	}

	private void saveCsaDetails(CompanyDTO companyDto, Company savedCompany) {
		List<CsaDetailsDTO> csaUserDetails = companyDto.getCsaDetails();
		if (!CollectionUtils.isEmpty(csaUserDetails) && csaUserDetails.stream()
				.noneMatch(dto -> dto.getEmail() == null || dto.getName() == null || dto.getContactNo() == null)) {
			csaUserDetails.forEach(csaDetailsDTO -> {
				csaDetailsDTO.setClientId(companyDto.getClientId());
				csaDetailsDTO.setCompanyId(savedCompany.getId());
			});
			kafkaProducerService.sendMessage(KafkaTopicConstant.KAFKA_CSA_USER_CREATION_TOPIC,
					new Gson().toJson(csaUserDetails));
		}
	}

	@Override
	@Auditable(action = ActionType.DEACTIVATE, module = com.grtship.core.annotation.Auditable.Module.COMPANY)
	@Validate(validator = "companyValidator", action = "deactivate")
	public CompanyDTO deactivate(DeactivationDTO deactivateDto) {
		Optional<Company> companyById = companyRepository.findById(deactivateDto.getReferenceId());
		if (!companyById.isPresent())
			throw new InvalidDataException(ErrorCode.INVALID_DATA_ERROR, COMPANY_NOT_FOUND_FOR_THE_GIVEN_ID);
		Company company = companyById.get();
		deactivateDto.setDeactivateAutoGenerateId(dateTimeFormate.format(new Date()));
		company.setDeactivateDtls(domainDeactivateMapper.toEntity(deactivateDto));
		company.setSubmittedForApproval(Boolean.TRUE);
		company = companyRepository.save(company);
		List<Long> branchIds = companyBranchQueryService
				.getBranchIdsByCompanyIdAndActiveFlag(deactivateDto.getReferenceId(), Boolean.TRUE);
		branchIds.forEach(branchId -> {
			deactivateDto.setReferenceId(branchId);
			companyBranchService.deactivate(deactivateDto);
		});
		return companyMapper.toDto(company);
	}

	@Override
	@Auditable(action = ActionType.REACTIVATE, module = com.grtship.core.annotation.Auditable.Module.COMPANY)
	@Validate(validator = "companyValidator", action = "reactivate")
	public CompanyDTO reactivate(ReactivationDTO activateDto) {
		Optional<Company> companyById = companyRepository.findById(activateDto.getReferenceId());
		if (!companyById.isPresent())
			throw new InvalidDataException(ErrorCode.INVALID_DATA_ERROR, COMPANY_NOT_FOUND_FOR_THE_GIVEN_ID);
		Company company = companyById.get();
		company.setReactivateDtls(domainReactivateMapper.toEntity(activateDto));
		company.setSubmittedForApproval(Boolean.TRUE);
		company = companyRepository.save(company);
		if (company.getReactivateDtls() != null) {
			List<Long> branchIds = companyBranchQueryService
					.getBranchIdsByCompanyIdAndActiveFlagAndDeactivateAutoGenerateId(activateDto.getReferenceId(),
							Boolean.FALSE, company.getDeactivateDtls().getDeactivateAutoGenerateId());
			branchIds.forEach(branchId -> {
				activateDto.setReferenceId(branchId);
				companyBranchService.reactivate(activateDto);
			});
		}
		return companyMapper.toDto(company);
	}

	/**
	 *
	 * @apiNote If all company access is true then,
	 *
	 *          find all company details by client id and then,
	 *
	 *          check userAccess for respective companyId for branches.
	 *
	 *          if(all branches access is true then get all branches details)
	 *
	 *          else( iterate over user access and get branchIds for respective
	 *          company)
	 *
	 */
	@Override
	public UserSpecificCBResponse userSpecificCBDetails(UserSpecificCBRequest cbRequest) {
		log.info("user specific company branch request details data : {} ", cbRequest);
		if (ObjectUtils.isNotEmpty(cbRequest)) {
			if (cbRequest.getAllCompanies()) {
				return companyBranchService.getUserSpecificCompanyBranchResponseByClientId(cbRequest.getClientId());
			}
			if (!CollectionUtils.isEmpty(cbRequest.getUserAccess())) {
				return companyBranchService.getUserSpecificCompanyBranchDetails(cbRequest);
			}
		}
		return null;
	}

	@Override
	public List<UserSpecificCompanyDTO> userSpecificCompanyDetails(UserAccessCompanyBranchDTO companyBranchDTO) {
		log.info("user specific company request details data : {} ", companyBranchDTO);
		if (ObjectUtils.isNotEmpty(companyBranchDTO)) {
			if (companyBranchDTO.getAllCompanies()) {
				List<Company> companies = companyRepository.findByClientId(companyBranchDTO.getClientId());
				log.info("user specific company dto details : {} ", companies);
				return userSpecificCompanyMapper.toDto(companies);
			} else if (!CollectionUtils.isEmpty(companyBranchDTO.getCompanyIds())) {
				List<Company> companies = companyRepository.findAllById(companyBranchDTO.getCompanyIds());
				log.info("user specific company dto details : {} ", companies);
				return userSpecificCompanyMapper.toDto(companies);
			}
		}
		return null;
	}

}
