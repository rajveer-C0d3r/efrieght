package com.grtship.mdm.serviceimpl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.grtship.common.exception.InvalidDataException;
import com.grtship.core.annotation.Auditable;
import com.grtship.core.annotation.Auditable.ActionType;
import com.grtship.core.annotation.Validate;
import com.grtship.core.constant.ErrorCode;
import com.grtship.core.constant.ReferenceNameConstant;
import com.grtship.core.dto.CreditTermsDTO;
import com.grtship.core.dto.DeactivationDTO;
import com.grtship.core.dto.ExternalEntityDTO;
import com.grtship.core.dto.ExternalEntityRequestDTO;
import com.grtship.core.dto.ReactivationDTO;
import com.grtship.core.enumeration.DomainStatus;
import com.grtship.core.enumeration.EntityType;
import com.grtship.mdm.domain.ExternalEntity;
import com.grtship.mdm.mapper.DomainDeactivateMapper;
import com.grtship.mdm.mapper.DomainReactivateMapper;
import com.grtship.mdm.mapper.EntityGroupMapper;
import com.grtship.mdm.mapper.ExternalEntityMapper;
import com.grtship.mdm.repository.ExternalEntityRepository;
import com.grtship.mdm.service.CreditTermsService;
import com.grtship.mdm.service.EntityBranchQueryService;
import com.grtship.mdm.service.EntityBranchService;
import com.grtship.mdm.service.EntityBusinessTypeService;
import com.grtship.mdm.service.EntityGroupService;
import com.grtship.mdm.service.ExternalEntityService;
import com.grtship.mdm.service.ObjectAliasService;

/**
 * Service Implementation for managing {@link ExternalEntity}.
 */
@Service
@Transactional
public class ExternalEntityServiceImpl implements ExternalEntityService {

	private static final String ENTITY_NOT_FOUND_FOR_GIVEN_ID = "Entity Not Found For Given Id.";

	public final SimpleDateFormat dateTimeFormate = new SimpleDateFormat("yyyyMMddHHmmss");

	@Autowired
	private ExternalEntityRepository externalEntityRepository;

	@Autowired
	private ExternalEntityMapper externalEntityMapper;

	@Autowired
	private ObjectAliasService aliasService;

	@Autowired
	private EntityBusinessTypeService entityBusinessTypeService;

	@Autowired
	private EntityBranchService entityBranchService;
	@Autowired
	private EntityBranchQueryService branchQueryService;

	@Autowired
	private EntityGroupService entityGroupsService;

	@Autowired
	private EntityGroupMapper entityGroupMapper;

	@Autowired
	private CreditTermsService creditTermsService;

	@Autowired
	private DomainDeactivateMapper domainDeactivateMapper;

	@Autowired
	private DomainReactivateMapper domainReactivateMapper;

	/**
	 * save entity
	 **/
	@Override
	@Transactional
	@Auditable(action = ActionType.SAVE, module = com.grtship.core.annotation.Auditable.Module.EXTERNAL_ENTITY)
	@Validate(validator = "externalEntityValidator", action = "save")
	public ExternalEntityDTO save(ExternalEntityRequestDTO entityDto) {
		ExternalEntity externalEntity = externalEntityMapper.toEntity(entityDto);
		if (entityDto.getGroups() != null && !StringUtils.isEmpty(entityDto.getGroups().getName())) {
			entityDto.getGroups().setClientId(entityDto.getClientId());
			externalEntity.setGroups(entityGroupMapper.toEntity(entityGroupsService.save(entityDto.getGroups())));
		}
		ExternalEntity savedEntity = externalEntityRepository.save(externalEntity);
		prepareCreditTermsDtoForSave(entityDto);
		saveChildData(entityDto, savedEntity);
		entityBranchService.saveDefaultBranch(savedEntity);
		return externalEntityMapper.toDto(savedEntity);
	}

	private void prepareCreditTermsDtoForSave(ExternalEntityRequestDTO externalEntityDto) {
		List<CreditTermsDTO> creditTermDtoList = new ArrayList<>();
		if (externalEntityDto.getCustomerFlag().equals(Boolean.TRUE)) {
			CreditTermsDTO customerCreditTerm = CreditTermsDTO.builder().entityType(EntityType.CUSTOMER)
					.creditAmount(externalEntityDto.getCustomerCreditAmount())
					.creditDays(externalEntityDto.getCustomerCreditDays()).status(DomainStatus.PENDING).build();
			creditTermDtoList.add(customerCreditTerm);
		}
		if (externalEntityDto.getVendorFlag().equals(Boolean.TRUE)) {
			CreditTermsDTO vendorCreditTerm = CreditTermsDTO.builder().entityType(EntityType.VENDOR)
					.creditAmount(externalEntityDto.getVendorCreditAmount())
					.creditDays(externalEntityDto.getVendorCreditDays()).status(DomainStatus.PENDING).build();
			creditTermDtoList.add(vendorCreditTerm);
		}
		externalEntityDto.setCreditTermsList(creditTermDtoList);
	}

	private void prepareCreditTermsDtoForUpdate(ExternalEntityDTO entityDto) {
		List<CreditTermsDTO> creditTermDtoList = new ArrayList<>();
		if (entityDto.getCustomerFlag().equals(Boolean.TRUE)) {
			CreditTermsDTO customerCreditTerm = CreditTermsDTO.builder().entityType(EntityType.CUSTOMER)
					.creditAmount(entityDto.getCustomerCreditAmount()).creditDays(entityDto.getCustomerCreditDays())
					.status(entityDto.getCustomerApprovalStatus() != null ? entityDto.getCustomerApprovalStatus()
							: DomainStatus.PENDING)
					.build();
			customerCreditTerm.setId(entityDto.getCustomerCreditTermId());
			creditTermDtoList.add(customerCreditTerm);
		}
		if (entityDto.getVendorFlag().equals(Boolean.TRUE)) {
			CreditTermsDTO vendorCreditTerm = CreditTermsDTO.builder().entityType(EntityType.VENDOR)
					.creditAmount(entityDto.getVendorCreditAmount()).creditDays(entityDto.getVendorCreditDays())
					.status(entityDto.getVendorApprovalStatus() != null ? entityDto.getVendorApprovalStatus()
							: DomainStatus.PENDING)
					.build();
			vendorCreditTerm.setId(entityDto.getVendorCreditTermId());
			creditTermDtoList.add(vendorCreditTerm);
		}
		entityDto.setCreditTermsList(creditTermDtoList);
	}

	/**
	 * update entity
	 **/
	@Override
	@Transactional
	@Validate(validator = "externalEntityValidator",action = "update")
	@Auditable(action = ActionType.UPDATE, module = com.grtship.core.annotation.Auditable.Module.EXTERNAL_ENTITY)
	public ExternalEntityDTO update(ExternalEntityDTO entityDto) {
		ExternalEntity externalEntity = externalEntityMapper.toEntity(entityDto);
		updateChildList(entityDto, entityDto.getCreditTermsList());
		if (!StringUtils.isEmpty(entityDto.getGroups().getName())) {
			entityDto.getGroups().setClientId(entityDto.getClientId());
			externalEntity.setGroupsId(entityGroupsService.save(entityDto.getGroups()).getId());
		}
		ExternalEntity updatedEntity = externalEntityRepository.save(externalEntity);
		prepareCreditTermsDtoForUpdate(entityDto);
		saveChildData(entityDto, updatedEntity);
		return externalEntityMapper.toDto(updatedEntity);
	}

	private void saveChildData(ExternalEntityRequestDTO entityDto, ExternalEntity savedEntity) {
		aliasService.saveAll(entityDto.getExternalEntityAlias(), savedEntity.getId(), ReferenceNameConstant.ENTITY,
				entityDto.getClientId(), entityDto.getCompanyId(), entityDto.getBranchId());
		entityBusinessTypeService.saveAll(entityDto.getEntityDetails(), savedEntity);
		creditTermsService.saveAll(entityDto.getCreditTermsList(), ReferenceNameConstant.ENTITY, savedEntity.getId());
	}

	/**
	 * this function will do hard delete for entity child objects.
	 * 
	 * @param creditTermsList
	 */
	private void updateChildList(ExternalEntityRequestDTO entityDto, List<CreditTermsDTO> creditTermsList) {
		if (entityDto.getId() != null) {
			entityBusinessTypeService.deleteEntityBusinessTypeOnUpdate(entityDto.getEntityDetails(), entityDto.getId());
			aliasService.deleteAliasOnUpdate(entityDto.getExternalEntityAlias(), ReferenceNameConstant.ENTITY,
					entityDto.getId());
			creditTermsService.deleteCreditTermOnUpdate(creditTermsList, ReferenceNameConstant.ENTITY,
					entityDto.getId());
		}
	}

	/**
	 * service to deactivate entity...
	 **/
	@Override
	@Auditable(action = ActionType.DEACTIVATE, module = com.grtship.core.annotation.Auditable.Module.EXTERNAL_ENTITY)
	@Validate(validator = "externalEntityValidator", action = "deactivate")
	public ExternalEntityDTO deactivate(@Valid DeactivationDTO deactivateDto) {
		dateTimeFormate.format(new Date());
//		entityValidator.deactivateValidation(deactivateDto);

		Optional<ExternalEntity> externalEntity = externalEntityRepository.findById(deactivateDto.getReferenceId());
		if (!externalEntity.isPresent())
			throw new InvalidDataException(ErrorCode.INVALID_DATA_ERROR, ENTITY_NOT_FOUND_FOR_GIVEN_ID);

		deactivateDto
				.setDeactivateAutoGenerateId(deactivateDto.getReferenceId() + "-" + dateTimeFormate.format(new Date()));
		ExternalEntity entity = externalEntity.get();
		entity.setSubmittedForApproval(Boolean.TRUE);
		entity.setDeactivateDtls(domainDeactivateMapper.toEntity(deactivateDto));
		entity = externalEntityRepository.save(entity);

		List<Long> branchIds = branchQueryService.getBranchIdsByEntityIdAndActiveFlag(deactivateDto.getReferenceId(),
				Boolean.TRUE);
		if (!CollectionUtils.isEmpty(branchIds)) {
			branchIds.forEach(id -> {
				deactivateDto.setReferenceId(id);
				entityBranchService.deactivate(deactivateDto);
			});
		}
		return externalEntityMapper.toDto(entity);
	}

	@Override
	@Auditable(action = ActionType.REACTIVATE, module = com.grtship.core.annotation.Auditable.Module.EXTERNAL_ENTITY)
	@Validate(validator = "externalEntityValidator", action = "reactivate")
	public ExternalEntityDTO reactivate(@Valid ReactivationDTO reactivateDto) {
		dateTimeFormate.format(new Date());
//		entityValidator.reactivateValidation(reactivateDto);

		Optional<ExternalEntity> externalEntity = externalEntityRepository.findById(reactivateDto.getReferenceId());
		if (!externalEntity.isPresent())
			throw new InvalidDataException(ErrorCode.INVALID_DATA_ERROR, ENTITY_NOT_FOUND_FOR_GIVEN_ID);

		ExternalEntity entity = externalEntity.get();
		entity.setReactivateDtls(domainReactivateMapper.toEntity(reactivateDto));
		entity.setSubmittedForApproval(Boolean.TRUE);
		entity = externalEntityRepository.save(entity);

		List<Long> branchIds = branchQueryService.getBranchIdsByEntityIdAndActiveFlag(reactivateDto.getReferenceId(),
				Boolean.TRUE);
		if (!CollectionUtils.isEmpty(branchIds)) {
			branchIds.forEach(id -> {
				reactivateDto.setReferenceId(id);
				entityBranchService.reactivate(reactivateDto);
			});
		}
		return externalEntityMapper.toDto(entity);
	}

}
