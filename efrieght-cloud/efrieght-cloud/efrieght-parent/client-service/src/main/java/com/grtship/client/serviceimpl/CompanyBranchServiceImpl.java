package com.grtship.client.serviceimpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.grtship.client.adaptor.MasterModuleAdapter;
import com.grtship.client.domain.CompanyBranch;
import com.grtship.client.domain.DomainDeactivate;
import com.grtship.client.domain.DomainReactivate;
import com.grtship.client.mapper.CompanyBranchMapper;
import com.grtship.client.mapper.UserSpecificBranchMapper;
import com.grtship.client.repository.BranchGstDetailsRepository;
import com.grtship.client.repository.CompanyBranchRepository;
import com.grtship.client.service.CompanyBranchService;
import com.grtship.common.exception.InvalidDataException;
import com.grtship.core.annotation.Auditable;
import com.grtship.core.annotation.Auditable.ActionType;
import com.grtship.core.annotation.Validate;
import com.grtship.core.constant.ErrorCode;
import com.grtship.core.constant.ReferenceNameConstant;
import com.grtship.core.dto.BranchResponse;
import com.grtship.core.dto.CompanyBranchCreationDTO;
import com.grtship.core.dto.CompanyBranchDTO;
import com.grtship.core.dto.CompanyBranchResponse;
import com.grtship.core.dto.DeactivationDTO;
import com.grtship.core.dto.ReactivationDTO;
import com.grtship.core.dto.UserAccessCompanyBranchDTO;
import com.grtship.core.dto.UserAccessDTO;
import com.grtship.core.dto.UserSpecificBranchDTO;
import com.grtship.core.dto.UserSpecificCBRequest;
import com.grtship.core.dto.UserSpecificCBResponse;
import com.grtship.core.enumeration.DomainStatus;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation for managing {@link CompanyBranch}.
 */
@Service
@Transactional
@Slf4j
public class CompanyBranchServiceImpl implements CompanyBranchService {

	private static final String BRANCH_NOT_FOUND_FOR_THE_GIVEN_ID = "Branch Not Found For the given Id.";

	private final CompanyBranchRepository companyBranchRepository;

	private final CompanyBranchMapper companyBranchMapper;

	@Autowired
	private MasterModuleAdapter masterModuleAdapter;

	@Autowired
	private BranchGstDetailsRepository branchGstDetailsRepository;

	@Autowired
	private UserSpecificBranchMapper userSpecificBranchMapper;

	public CompanyBranchServiceImpl(CompanyBranchRepository companyBranchRepository,
			CompanyBranchMapper companyBranchMapper) {
		this.companyBranchRepository = companyBranchRepository;
		this.companyBranchMapper = companyBranchMapper;
	}

	/**
	 * company branch save.....
	 **/
	@Override
	@Auditable(action = ActionType.SAVE, module = com.grtship.core.annotation.Auditable.Module.COMPANY_BRANCH)
	@Validate(validator = "companyBranchValidator", action = "save")
	public CompanyBranchDTO save(CompanyBranchCreationDTO companyBranchDto) {
		CompanyBranch companyBranch = companyBranchMapper.toEntity(companyBranchDto);
		if (StringUtils.isEmpty(companyBranch.getCode()))
			companyBranch.setCode(masterModuleAdapter.generateCode(ReferenceNameConstant.COMPANY_BRANCH, null));

//		branchValidator.validateSave(companyBranch);// on draft only database-level and uniqueness validations are
		// applicable.
		return companyBranchMapper.toDto(companyBranchRepository.save(companyBranch));
	}

	/**
	 * company branch update.....
	 **/
	@Override
	@Auditable(action = ActionType.UPDATE, module = com.grtship.core.annotation.Auditable.Module.COMPANY_BRANCH)
	@Validate(validator = "companyBranchValidator", action = "update")
	public CompanyBranchDTO update(CompanyBranchDTO companyBranchDto) {
		CompanyBranch companyBranch = companyBranchMapper.toEntity(companyBranchDto);
		branchGstDetailsRepository.deleteAll(companyBranch.getBranchGstDetails());
		if (!companyBranch.getStatus().equals(DomainStatus.DRAFT))
			companyBranch.setSubmittedForApproval(Boolean.TRUE); // submited for update approval

//		branchValidator.validateUpdate(companyBranch);
		return companyBranchMapper.toDto(companyBranchRepository.save(companyBranch));
	}

	/**
	 * service to deactivate company branch...
	 **/
	@Override
	@Auditable(action = ActionType.DEACTIVATE, module = com.grtship.core.annotation.Auditable.Module.COMPANY_BRANCH)
	@Validate(validator = "companyBranchValidator", action = "deactivate")
	public CompanyBranchDTO deactivate(@Valid DeactivationDTO deactivateDto) {
//		branchValidator.deactivateValidation(deactivateDto);
		Optional<CompanyBranch> branch = companyBranchRepository.findById(deactivateDto.getReferenceId());
		if (!branch.isPresent())
			throw new InvalidDataException(ErrorCode.INVALID_DATA_ERROR, BRANCH_NOT_FOUND_FOR_THE_GIVEN_ID);

		CompanyBranch companyBranch = branch.get();
		DomainDeactivate deactivate = new DomainDeactivate();
		deactivate.setDeactivationReason(deactivateDto.getDeactivationReason());
		deactivate.setDeactivationWefDate(deactivateDto.getDeactivationWefDate());
		deactivate.setDeactivateAutoGenerateId(deactivateDto.getDeactivateAutoGenerateId());
		companyBranch.setDeactivateDtls(deactivate);
		companyBranch.setSubmittedForApproval(Boolean.TRUE);// Submited For Deactivation Approval.
		companyBranch = companyBranchRepository.save(companyBranch);
		return companyBranchMapper.toDto(companyBranch);
	}

	/**
	 * service to Reactivate company branch...
	 **/
	@Override
	@Auditable(action = ActionType.REACTIVATE, module = com.grtship.core.annotation.Auditable.Module.COMPANY_BRANCH)
	@Validate(validator = "companyBranchValidator", action = "reactivate")
	public CompanyBranchDTO reactivate(ReactivationDTO reactivationDto) {
//		branchValidator.reactivateValidations(reactivationDto);
		Optional<CompanyBranch> branch = companyBranchRepository.findById(reactivationDto.getReferenceId());
		if (!branch.isPresent())
			throw new InvalidDataException(ErrorCode.INVALID_DATA_ERROR, BRANCH_NOT_FOUND_FOR_THE_GIVEN_ID);

		CompanyBranch companyBranch = branch.get();
		DomainReactivate reactivate = new DomainReactivate();
		reactivate.setReactivationWefDate(reactivationDto.getReactivationWefDate());
		reactivate.setReactivationReason(reactivationDto.getReactivationReason());
		companyBranch.setSubmittedForApproval(Boolean.TRUE);// Submited For Reactivation Approval..s
		companyBranch.setReactivateDtls(reactivate);
		companyBranch = companyBranchRepository.save(companyBranch);
		return companyBranchMapper.toDto(companyBranch);
	}

	@Override
	public UserSpecificCBResponse getUserSpecificCompanyBranchResponseByClientId(Long clientId) {
		log.info("user have all company access called for all branch response with client id :{} ", clientId);
		final UserSpecificCBResponse cbResponse = new UserSpecificCBResponse();
		final List<CompanyBranch> companyBranchs = companyBranchRepository.findByClientId(clientId);
		List<CompanyBranchResponse> companyBranchList = getCompanyBranchList(companyBranchs);
		log.info("company With branch list : {} ", companyBranchList);
		cbResponse.setContent(companyBranchList);
		log.info("user specific branch specific response with client id is : {} ", cbResponse);
		return cbResponse;
	}

	private List<CompanyBranchResponse> getCompanyBranchList(final List<CompanyBranch> companyBranchs) {
		List<CompanyBranchResponse> branchResponses = new ArrayList<>();
		Map<Long, List<CompanyBranch>> branchMap = companyBranchs.stream().filter(obj -> obj.getCompanyId() != null)
				.collect(Collectors.groupingBy(CompanyBranch::getCompanyId));
		log.info("stream map for type company with branchs : {} ", branchMap);
		branchMap.keySet().stream().forEach(obj -> {
			if (ObjectUtils.isNotEmpty(obj)) {
				CompanyBranch companyBranch = branchMap.get(obj).get(0);
				branchResponses.add(getCompanyBranchResponseObject(companyBranch, branchMap));
			}
		});
		return branchResponses;
	}

	private BranchResponse getBranchObject(final CompanyBranch filterBranch) {
		return BranchResponse.builder().companyId(filterBranch.getCompanyId()).branchId(filterBranch.getId())
				.code(filterBranch.getCode()).name(filterBranch.getName())
				.isBranchDeactivated(
						filterBranch.getStatus().equals(DomainStatus.DEACTIVATED) ? Boolean.TRUE : Boolean.FALSE)
				.build();
	}

	private CompanyBranchResponse getCompanyBranchResponseObject(final CompanyBranch companyBranch,
			Map<Long, List<CompanyBranch>> branchMap) {
		List<CompanyBranch> companyBranchList = branchMap.get(companyBranch.getCompanyId());
		log.info("data from map with companyId {} and  branches are {} ", companyBranch.getCompanyId(),
				companyBranchList);
		List<BranchResponse> branchResponses = companyBranchList.stream().map(this::getBranchObject)
				.collect(Collectors.toList());
		log.info("company Branches : {} ", branchResponses);
		return CompanyBranchResponse.builder().clientId(companyBranch.getClientId())
				.code(companyBranch.getCompany().getCode()).companyId(companyBranch.getCompanyId())
				.name(companyBranch.getCompany().getName())
				.isCompanyDeactivated(
						companyBranch.getCompany().getStatus().equals(DomainStatus.DEACTIVATED) ? Boolean.TRUE
								: Boolean.FALSE)
				.branches(branchResponses).build();
	}

	@Override
	public UserSpecificCBResponse getUserSpecificCompanyBranchDetails(UserSpecificCBRequest cbRequest) {
		log.info("user specific data with respective branch permission", cbRequest);
		final UserSpecificCBResponse cbResponse = new UserSpecificCBResponse();
		List<Long> companyIdsWithAllBranches = cbRequest.getUserAccess().stream().filter(UserAccessDTO::isAllBranches)
				.map(UserAccessDTO::getCompanyId).collect(Collectors.toList());
		if (!CollectionUtils.isEmpty(companyIdsWithAllBranches)) {
			cbResponse.setContent(getCompanyBranchList(cbRequest.getClientId(), companyIdsWithAllBranches, null, true));
		}
		List<Long> companyIds = cbRequest.getUserAccess().stream().filter(b -> !b.isAllBranches())
				.map(UserAccessDTO::getCompanyId).collect(Collectors.toList());
		List<Long> branchIds = cbRequest.getUserAccess().stream().filter(b -> !b.isAllBranches())
				.map(UserAccessDTO::getBranchId).collect(Collectors.toList());
		if (!CollectionUtils.isEmpty(companyIds) && !CollectionUtils.isEmpty(branchIds)) {
			cbResponse.setContent(getCompanyBranchList(cbRequest.getClientId(), companyIds, branchIds, false));
		}
		log.info("user specific data with respective branch permission data is {}", cbResponse);
		return cbResponse;
	}

	private List<CompanyBranchResponse> getCompanyBranchList(Long clientId, List<Long> companyIds, List<Long> branchIds,
			boolean isAllBranches) {
		List<CompanyBranch> companyList = new ArrayList<>();
		if (isAllBranches) {
			companyList.addAll(companyBranchRepository.findByClientIdAndCompany_IdIn(clientId, companyIds));
		} else {
			companyList.addAll(
					companyBranchRepository.findByClientIdAndCompany_IdInAndIdIn(clientId, companyIds, branchIds));
		}
		return getCompanyBranchList(companyList);
	}

	@Override
	public List<UserSpecificBranchDTO> getUserSpecificBranchDetails(UserAccessCompanyBranchDTO companyBranchDTO) {
		log.info("user specific company request details data : {} ", companyBranchDTO);
		if (ObjectUtils.isNotEmpty(companyBranchDTO.getAllBranches())) {
			return userSpecificBranchMapper
					.toDto(companyBranchRepository.findByCompany_Id(companyBranchDTO.getCompanyId()));
		}
		if (!CollectionUtils.isEmpty(companyBranchDTO.getBranchIds())) {
			return userSpecificBranchMapper.toDto(companyBranchRepository.findAllById(companyBranchDTO.getBranchIds()));
		}
		return null;
	}
}
