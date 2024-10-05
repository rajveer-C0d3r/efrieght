package com.grtship.mdm.serviceimpl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.grtship.common.exception.InvalidDataException;
import com.grtship.core.annotation.Auditable;
import com.grtship.core.annotation.Auditable.ActionType;
import com.grtship.core.annotation.Validate;
import com.grtship.core.constant.ErrorCode;
import com.grtship.core.constant.ReferenceNameConstant;
import com.grtship.core.dto.BranchContactDTO;
import com.grtship.core.dto.CreditTermsDTO;
import com.grtship.core.dto.DeactivationDTO;
import com.grtship.core.dto.EntityBranchDTO;
import com.grtship.core.dto.EntityBranchRequestDTO;
import com.grtship.core.dto.EntityBranchTaxDTO;
import com.grtship.core.dto.ReactivationDTO;
import com.grtship.core.enumeration.DomainStatus;
import com.grtship.core.enumeration.EntityType;
import com.grtship.mdm.domain.CreditTerms;
import com.grtship.mdm.domain.EntityBranch;
import com.grtship.mdm.domain.EntityBranchTax;
import com.grtship.mdm.domain.ExternalEntity;
import com.grtship.mdm.mapper.BranchContactMapper;
import com.grtship.mdm.mapper.CreditTermsMapper;
import com.grtship.mdm.mapper.DomainDeactivateMapper;
import com.grtship.mdm.mapper.DomainReactivateMapper;
import com.grtship.mdm.mapper.EntityBranchMapper;
import com.grtship.mdm.mapper.EntityBranchTaxMapper;
import com.grtship.mdm.repository.EntityBranchRepository;
import com.grtship.mdm.service.BranchContactService;
import com.grtship.mdm.service.CodeGeneratorService;
import com.grtship.mdm.service.CreditTermsService;
import com.grtship.mdm.service.EntityBranchService;
import com.grtship.mdm.service.EntityBranchTaxService;
import com.grtship.mdm.validator.EntityBranchValidator;

/**
 * Service Implementation for managing {@link EntityBranch}.
 */
@Service
@Transactional
public class EntityBranchServiceImpl implements EntityBranchService {

	private static final String ENTITY_BRANCH_NOT_FOUND_FOR_GIVEN_ID = "Entity Branch not found for given Id";

	private final Logger log = LoggerFactory.getLogger(EntityBranchServiceImpl.class);

	@Autowired
	private EntityBranchRepository branchDetailsRepository;

	@Autowired
	private EntityBranchMapper branchDetailsMapper;

	@Autowired
	private CreditTermsService creditTermsService;

	@Autowired
	private EntityBranchTaxMapper entityBranchTaxMapper;

	@Autowired
	private EntityBranchTaxService entityBranchTaxService;

	@Autowired
	private BranchContactService branchContactsService;

	@Autowired
	private BranchContactMapper branchContactsMapper;

	public final SimpleDateFormat dateTimeFormate = new SimpleDateFormat("yyyyMMddHHmmss");

	@Autowired
	private CodeGeneratorService codeGeneratorService;

	@Autowired
	private EntityBranchValidator branchValidator;
	@Autowired
	CreditTermsMapper creditermsMapper;

	@Autowired
	private DomainDeactivateMapper domainDeactivateMapper;

	@Autowired
	private DomainReactivateMapper domainReactivateMapper;

	/**
	 * save Entity Branch...
	 */
	@Override
	@Transactional
	@Auditable(action = ActionType.SAVE, module = com.grtship.core.annotation.Auditable.Module.ENTITY_BRANCH)
	@Validate(validator = "entityBranchValidator",action = "save")
	public EntityBranchDTO save(EntityBranchRequestDTO branchDetailsDto) {
//		branchValidator.creditTermsValidation(branchDetailsDto);
		EntityBranch branchDetails = branchDetailsMapper.toEntity(branchDetailsDto);
//		branchValidator.saveValidate(branchDetails);
		EntityBranch savedBranchDetails = branchDetailsRepository.save(branchDetails);
		prepareCreditTermsDtoForSave(branchDetailsDto);
		saveChildData(savedBranchDetails, branchDetailsDto);
		return branchDetailsMapper.toDto(branchDetails);
	}

	private void prepareCreditTermsDtoForSave(EntityBranchRequestDTO branchDetailsDto) {
		List<CreditTermsDTO> creditTermDtoList = new ArrayList<>();
		if (branchDetailsDto.getCustomerFlag().equals(Boolean.TRUE)) {
			CreditTermsDTO customerCreditTerm = CreditTermsDTO.builder().entityType(EntityType.CUSTOMER)
					.creditAmount(branchDetailsDto.getCustomerCreditAmount())
					.creditDays(branchDetailsDto.getCustomerCreditDays()).status(DomainStatus.PENDING).build();
			creditTermDtoList.add(customerCreditTerm);
		}
		if (branchDetailsDto.getVendorFlag().equals(Boolean.TRUE)) {
			CreditTermsDTO vendorCreditTerm = CreditTermsDTO.builder().entityType(EntityType.VENDOR)
					.creditAmount(branchDetailsDto.getVendorCreditAmount())
					.creditDays(branchDetailsDto.getVendorCreditDays()).status(DomainStatus.PENDING).build();
			creditTermDtoList.add(vendorCreditTerm);
		}
		branchDetailsDto.setCreditTermsList(creditTermDtoList);
	}

	private void prepareCreditTermsDtoForUpdate(EntityBranchRequestDTO branchDetailsDto) {
		List<CreditTermsDTO> creditTermDtoList = new ArrayList<>();
		if (branchDetailsDto.getCustomerFlag().equals(Boolean.TRUE)) {
			CreditTermsDTO customerCreditTerm = CreditTermsDTO.builder().entityType(EntityType.CUSTOMER)
					.creditAmount(branchDetailsDto.getCustomerCreditAmount())
					.creditDays(branchDetailsDto.getCustomerCreditDays())
					.status(branchDetailsDto.getCustomerApprovalStatus() != null
							? branchDetailsDto.getCustomerApprovalStatus()
							: DomainStatus.PENDING)
					.build();
			creditTermDtoList.add(customerCreditTerm);
		}
		if (branchDetailsDto.getVendorFlag().equals(Boolean.TRUE)) {
			CreditTermsDTO vendorCreditTerm = CreditTermsDTO.builder().entityType(EntityType.VENDOR)
					.creditAmount(branchDetailsDto.getVendorCreditAmount())
					.creditDays(branchDetailsDto.getVendorCreditDays())
					.status(branchDetailsDto.getVendorApprovalStatus() != null
							? branchDetailsDto.getVendorApprovalStatus()
							: DomainStatus.PENDING)
					.build();
			creditTermDtoList.add(vendorCreditTerm);
		}
		branchDetailsDto.setCreditTermsList(creditTermDtoList);
	}

	/**
	 * update Entity Branch...
	 */
	@Override
	@Transactional
	@Auditable(action = ActionType.UPDATE, module = com.grtship.core.annotation.Auditable.Module.ENTITY_BRANCH)
	@Validate(validator = "entityBranchValidator",action = "update")
	public EntityBranchDTO update(EntityBranchDTO branchDetailsDto) {
		List<CreditTerms> creditTermsList = new ArrayList<>();
//		branchValidator.creditTermsValidation(branchDetailsDto);
		EntityBranch branchDetails = branchDetailsMapper.toEntity(branchDetailsDto);
//		branchValidator.updateValidate(branchDetails);

		updateChildList(branchDetailsDto, creditTermsList);
		EntityBranch savedBranchDetails = branchDetailsRepository.save(branchDetails);
		prepareCreditTermsDtoForUpdate(branchDetailsDto);
		saveChildData(savedBranchDetails, branchDetailsDto);
		return branchDetailsMapper.toDto(branchDetails);
	}

	private void saveChildData(EntityBranch savedBranchDetails, EntityBranchRequestDTO branchDetailsDto) {
		creditTermsService.saveAll(branchDetailsDto.getCreditTermsList(), ReferenceNameConstant.ENTITY_BRANCH,
				savedBranchDetails.getId());
		entityBranchTaxService.saveAll(branchDetailsDto.getTaxDetialsDto(), savedBranchDetails.getId());
		branchContactsService.saveAll(branchDetailsDto.getContactDetailsDto(), savedBranchDetails.getId());
	}

	/**
	 * update child lists..
	 * 
	 * @param creditTermsList
	 **/
	private void updateChildList(EntityBranchRequestDTO branchDetailsDto, List<CreditTerms> creditTermsList) {
		if (branchDetailsDto.getId() != null) {
			entityBranchTaxService.deleteTaxDetailsOnUpdate(branchDetailsDto.getTaxDetialsDto(),
					branchDetailsDto.getId());
			creditTermsService.deleteCreditTermOnUpdate(creditermsMapper.toDto(creditTermsList),
					ReferenceNameConstant.ENTITY_BRANCH, branchDetailsDto.getId());
			branchContactsService.deleteBranchContactsOnUpdate(branchDetailsDto.getContactDetailsDto(),
					branchDetailsDto.getId());
		}
	}

	/**
	 * save default branch
	 */
	@Transactional
	@Override
	public EntityBranchDTO saveDefaultBranch(ExternalEntity entityObj) {
		log.debug("Request to save BranchDetails : {}", entityObj);
		EntityBranchDTO entityBranchDto = new EntityBranchDTO();
		entityBranchDto.setName(entityObj.getName());
		String branchCode = codeGeneratorService.generateCode(ReferenceNameConstant.ENTITY_BRANCH, null);
		entityBranchDto.setCode(branchCode);
		entityBranchDto.setStatus(DomainStatus.DRAFT);
		EntityBranch branchDetails = branchDetailsMapper.toEntity(entityBranchDto);
		branchDetails.setAddress(entityObj.getAddress());
		branchDetails.setExternalEntity(entityObj);
		branchDetails.setDefaultBranchFlag(Boolean.TRUE);
		branchDetails.setClientId(entityObj.getClientId());
		branchDetails.setCompanyId(entityObj.getCompanyId());
		EntityBranch savedBranchDetails = branchDetailsRepository.save(branchDetails);
		return branchDetailsMapper.toDto(savedBranchDetails);
	}

	/**
	 * service to deactivate branch... *
	 */
	@Override
	@Auditable(action = ActionType.DEACTIVATE, module = com.grtship.core.annotation.Auditable.Module.ENTITY_BRANCH)
	@Validate(validator = "entityBranchValidator",action = "deactivate")
	public EntityBranchDTO deactivate(@Valid DeactivationDTO deactivateDto) {
//		branchValidator.branchDeactivateValidation(deactivateDto);
		Optional<EntityBranch> entityBranchyId = branchDetailsRepository.findById(deactivateDto.getReferenceId());
		if (!entityBranchyId.isPresent())
			throw new InvalidDataException(ErrorCode.INVALID_DATA_ERROR, ENTITY_BRANCH_NOT_FOUND_FOR_GIVEN_ID);

		deactivateDto
				.setDeactivateAutoGenerateId(deactivateDto.getReferenceId() + "-" + dateTimeFormate.format(new Date()));
		EntityBranch branch = entityBranchyId.get();
		branch.setDeactivateDtls(domainDeactivateMapper.toEntity(deactivateDto));
		branch.setSubmittedForApproval(Boolean.TRUE);
		branch = branchDetailsRepository.save(branch);
		return branchDetailsMapper.toDto(branch);
	}

	/**
	 * service to reactivate branch... *
	 */
	@Override
	@Auditable(action = ActionType.REACTIVATE, module = com.grtship.core.annotation.Auditable.Module.ENTITY_BRANCH)
	@Validate(validator = "entityBranchValidator",action = "reactivate")
	public EntityBranchDTO reactivate(@Valid ReactivationDTO reactivateDto) {
//		branchValidator.branchReactivateValidation(reactivateDto);
		Optional<EntityBranch> entityBranchyId = branchDetailsRepository.findById(reactivateDto.getReferenceId());
		if (!entityBranchyId.isPresent())
			throw new InvalidDataException(ErrorCode.INVALID_DATA_ERROR, ENTITY_BRANCH_NOT_FOUND_FOR_GIVEN_ID);

		EntityBranch branch = entityBranchyId.get();
		branch.setReactivateDtls(domainReactivateMapper.toEntity(reactivateDto));
		branch.setSubmittedForApproval(Boolean.TRUE);
		branch = branchDetailsRepository.save(branch);
		return branchDetailsMapper.toDto(branch);
	}

	/**
	 * service to save and update branch tax details...
	 */
	@Override
	@Auditable(action = ActionType.SAVE, module = com.grtship.core.annotation.Auditable.Module.ENTITY_BRANCH_TAX)
	public List<EntityBranchTaxDTO> saveBranchTax(@Valid List<EntityBranchTaxDTO> branchTaxDtos) {
		entityBranchTaxService.deleteTaxDetailsOnUpdate(branchTaxDtos, branchTaxDtos.get(0).getId());
		List<EntityBranchTax> entityBranchTax = entityBranchTaxMapper.toEntity(branchTaxDtos);
		return entityBranchTaxMapper.toDto(entityBranchTaxService.saveAll(entityBranchTax));
	}

	/**
	 * service to update branch contacts...
	 */
	@Override
	@Auditable(action = ActionType.SAVE, module = com.grtship.core.annotation.Auditable.Module.ENTITY_BRANCH_CONTACT)
	public List<BranchContactDTO> saveBranchContacts(@Valid List<BranchContactDTO> branchContactDtos) {
		branchContactsService.deleteBranchContactsOnUpdate(branchContactDtos,
				branchContactDtos.get(0).getEntityBranchId());
		return branchContactsMapper
				.toDto(branchContactsService.saveAll(branchContactsMapper.toEntity(branchContactDtos)));
	}
}
