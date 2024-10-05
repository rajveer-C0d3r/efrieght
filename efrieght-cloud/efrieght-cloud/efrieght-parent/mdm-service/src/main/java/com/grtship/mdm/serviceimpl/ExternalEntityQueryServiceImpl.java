package com.grtship.mdm.serviceimpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.grtship.core.annotation.AccessFilter;
import com.grtship.core.constant.ReferenceNameConstant;
import com.grtship.core.dto.CreditTermsDTO;
import com.grtship.core.dto.EntityBranchDTO;
import com.grtship.core.dto.EntityMultiDropDownDTO;
import com.grtship.core.dto.ExternalEntityDTO;
import com.grtship.core.dto.ObjectAliasDTO;
import com.grtship.core.enumeration.EntityType;
import com.grtship.mdm.criteria.EntityBranchCriteria;
import com.grtship.mdm.criteria.ExternalEntityCriteria;
import com.grtship.mdm.criteria.ObjectAliasCriteria;
import com.grtship.mdm.mapper.ExternalEntityMapper;
import com.grtship.mdm.repository.ExternalEntityRepository;
import com.grtship.mdm.service.CreditTermsService;
import com.grtship.mdm.service.DocumentQueryService;
import com.grtship.mdm.service.EntityBranchQueryService;
import com.grtship.mdm.service.EntityBusinessTypeService;
import com.grtship.mdm.service.ExternalEntityQueryService;
import com.grtship.mdm.service.ObjectAliasQueryService;
import com.grtship.mdm.specs.ExternalEntitySpecs;

@Service
@Transactional(readOnly = true)

public class ExternalEntityQueryServiceImpl implements ExternalEntityQueryService {

	@Autowired
	ExternalEntityRepository externalEntityRepository;

	@Autowired
	ExternalEntityMapper externalEntityMapper;

	@Autowired
	private DocumentQueryService documentQueryService;

	@Autowired
	private EntityBusinessTypeService entityBusinessTypeService;

	@Autowired
	private CreditTermsService creditTermsService;

	@Autowired
	private EntityBranchQueryService branchFilterService;

	@Autowired
	private ExternalEntityMapper entityMapper;

	@Autowired
	private ObjectAliasQueryService aliasFilterService;

	// Only for Landing page of External Entity
	@AccessFilter(clientAccessFlag = true, companyAccessFlag = true, branchAccessFlag = false)
	@Override
	public Page<ExternalEntityDTO> findAll(ExternalEntityCriteria entityCriteria, Pageable pageable) {
		return externalEntityRepository.findAll(ExternalEntitySpecs.getExternalEntityBySpecs(entityCriteria), pageable)
				.map(externalEntityMapper::toDto);
	}

	@AccessFilter(clientAccessFlag = true, companyAccessFlag = true, branchAccessFlag = true)
	@Override
	public ExternalEntityDTO findByCriteria(ExternalEntityCriteria entityCriteria) {
		List<ExternalEntityDTO> allListOfExternalEntity = entityMapper
				.toDto(externalEntityRepository.findAll(ExternalEntitySpecs.getExternalEntityBySpecs(entityCriteria)));
		if (!CollectionUtils.isEmpty(allListOfExternalEntity)) {
			EntityBranchCriteria branchCriteria = new EntityBranchCriteria();
			branchCriteria.setEntiyId(entityCriteria.getId());
			List<EntityBranchDTO> entityBranchList = branchFilterService.findByCriteria(branchCriteria);
			return prepareChildList(allListOfExternalEntity.get(0), entityBranchList);
		}
		return null;
	}

	@Override
	public Optional<ExternalEntityDTO> findOne(Long id) {
		ExternalEntityCriteria entityCriteria = new ExternalEntityCriteria();
		entityCriteria.setId(id);
		ExternalEntityDTO entityDto = findByCriteria(entityCriteria);
		return (findByCriteria(entityCriteria) != null) ? Optional.of(entityDto) : Optional.ofNullable(null);
	}

	/** used to prepare entity child list... */
	private ExternalEntityDTO prepareChildList(ExternalEntityDTO entityDto, List<EntityBranchDTO> entityBranchList) {
		entityDto.setExternalEntityAlias(new HashSet<>(aliasFilterService
				.getListOfAliasByReferanceIdAndReferenceName(entityDto.getId(), ReferenceNameConstant.ENTITY)));
		entityDto.setEntityDetails(entityBusinessTypeService.getEntityBusinessTypesByEntityId(entityDto.getId()));
		entityDto.setEntityDocuments(
				documentQueryService.getDocumentsOnEdit(entityDto.getId(), ReferenceNameConstant.ENTITY));
		Set<CreditTermsDTO> creditTermsDTO = creditTermsService.getCreditTermsByReferenceIdsAndReferenceName(
				Arrays.asList(entityDto.getId()), ReferenceNameConstant.ENTITY);
		setCreditTermsData(entityDto, creditTermsDTO);
		Map<Long, List<EntityBranchDTO>> entityBranchMap = entityBranchList.stream()
				.filter(obj -> obj.getExternalEntityId() != null)
				.collect(Collectors.groupingBy(EntityBranchDTO::getExternalEntityId, Collectors.toList()));
		entityDto.setBranchDetailsDto(entityBranchMap.get(entityDto.getId()));
		return entityDto;
	}

	/**
	 * method to set credit terms data
	 */
	private void setCreditTermsData(ExternalEntityDTO externalEntityDTO, Set<CreditTermsDTO> creditTermsDTO) {
		if (!CollectionUtils.isEmpty(creditTermsDTO)) {
			creditTermsDTO.forEach(creditTerm -> {
				if (creditTerm.getEntityType().equals(EntityType.CUSTOMER)) {
					externalEntityDTO.setCustomerCreditAmount(creditTerm.getCreditAmount());
					externalEntityDTO.setCustomerCreditDays(creditTerm.getCreditDays());
					externalEntityDTO.setCustomerApprovalStatus(creditTerm.getStatus());
					externalEntityDTO.setCustomerCreditTermId(creditTerm.getId());
				}
				if (creditTerm.getEntityType().equals(EntityType.VENDOR)) {
					externalEntityDTO.setVendorCreditAmount(creditTerm.getCreditAmount());
					externalEntityDTO.setVendorCreditDays(creditTerm.getCreditDays());
					externalEntityDTO.setVendorApprovalStatus(creditTerm.getStatus());
					externalEntityDTO.setVendorCreditTermId(creditTerm.getId());
				}
			});
		}
	}

	/**
	 * service to fetch entity list for multi DD.....
	 */
	@Override
	public Page<EntityMultiDropDownDTO> getAllExternalEntitiesForMultiDD(ExternalEntityCriteria entityCriteria,
			Pageable pageable) {
		Page<EntityMultiDropDownDTO> entityList = new PageImpl<>(new ArrayList<>());
		if (entityCriteria.getCustomerFlag() != null && entityCriteria.getCustomerFlag())
			entityList = externalEntityRepository.findPagedProjectedByCustomerFlagAndActiveFlag(
					entityCriteria.getCustomerFlag(), Boolean.TRUE, pageable);
		if (entityCriteria.getVendorFlag() != null && entityCriteria.getVendorFlag())
			entityList = externalEntityRepository.findPagedProjectedByVendorFlagAndActiveFlag(
					entityCriteria.getVendorFlag(), Boolean.TRUE, pageable);
		if (entityCriteria.getGroupId() != null)
			return externalEntityRepository.findPagedProjectedByGroups_IdAndActiveFlag(entityCriteria.getGroupId(),
					Boolean.TRUE, pageable);

		if (!CollectionUtils.isEmpty(entityList.getContent())) {
			List<Long> entityIds = entityList.stream().filter(obj -> obj.getId() != null)
					.map(EntityMultiDropDownDTO::getId).collect(Collectors.toList());
			ObjectAliasCriteria aliasCriteria = new ObjectAliasCriteria();
			aliasCriteria.setReferenceIds(entityIds);
			aliasCriteria.setReferenceName(ReferenceNameConstant.ENTITY);
			List<ObjectAliasDTO> aliasDtoList = aliasFilterService.findByCriteria(aliasCriteria);
			Map<Long, Set<ObjectAliasDTO>> aliasDtoMap = aliasDtoList.stream()
					.filter(obj -> obj != null && obj.getReferenceId() != null && obj.getReferenceName() != null)
					.collect(Collectors.groupingBy(ObjectAliasDTO::getReferenceId, Collectors.toSet()));

			if (!CollectionUtils.isEmpty(aliasDtoMap))
				entityList.getContent().forEach(entity -> {
					if (aliasDtoMap.get(entity.getId()) != null)
						entity.setAlias(aliasDtoMap.get(entity.getId()).stream().map(ObjectAliasDTO::getName)
								.collect(Collectors.toSet()));
				});
		}
		return entityList;
	}

	@Override
	public boolean existById(Long externalEntityId) {
		return (externalEntityId != null) ? externalEntityRepository.existsById(externalEntityId) : false;
	}

}
