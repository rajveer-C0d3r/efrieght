package com.grtship.mdm.serviceimpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.grtship.core.annotation.AccessFilter;
import com.grtship.core.constant.ReferenceNameConstant;
import com.grtship.core.dto.BranchContactDTO;
import com.grtship.core.dto.CreditTermsDTO;
import com.grtship.core.dto.DocumentDownloadDTO;
import com.grtship.core.dto.EntityBranchDTO;
import com.grtship.core.dto.EntityBranchTaxDTO;
import com.grtship.core.enumeration.EntityType;
import com.grtship.mdm.criteria.EntityBranchCriteria;
import com.grtship.mdm.domain.EntityBranch;
import com.grtship.mdm.dto.EntityBranchMultiDropDownDTO;
import com.grtship.mdm.mapper.EntityBranchMapper;
import com.grtship.mdm.repository.EntityBranchRepository;
import com.grtship.mdm.service.BranchContactService;
import com.grtship.mdm.service.CreditTermsService;
import com.grtship.mdm.service.DocumentQueryService;
import com.grtship.mdm.service.EntityBranchQueryService;
import com.grtship.mdm.service.EntityBranchTaxService;
import com.grtship.mdm.specs.EntityBranchSpecs;

@Service
@Transactional(readOnly = true)
public class EntityBranchQueryServiceImpl implements EntityBranchQueryService {

	@Autowired
	private EntityBranchRepository entityBranchRepository;

	@Autowired
	private EntityBranchMapper entityBranchMapper;

	@Autowired
	private CreditTermsService creditTermsService;

	@Autowired
	private EntityBranchTaxService entityBranchTaxService;

	@Autowired
	private DocumentQueryService documentQueryService;

	@Autowired
	private BranchContactService branchContactsService;

	@Autowired
	private EntityBranchRepository branchDetailsRepository;

	/** find all branches with criteria and pageable.. */
	@AccessFilter(clientAccessFlag = true, companyAccessFlag = true, branchAccessFlag = true)
	@Override
	public Page<EntityBranchDTO> findAll(EntityBranchCriteria entityBranchCriteria, Pageable pageable) {
		List<EntityBranchDTO> entityBranchDtoList = new ArrayList<>();
		List<EntityBranch> branchList = entityBranchRepository
				.findAll(EntityBranchSpecs.getEntityBranchBySpecs(entityBranchCriteria), pageable).getContent();
		if (!CollectionUtils.isEmpty(branchList)) {
			setCellNumberAndLandMartks(branchList, entityBranchDtoList);
		}
		return new PageImpl<>(entityBranchDtoList, pageable, entityBranchDtoList.size());
	}

	/** find all branches with criteria */
	@AccessFilter(clientAccessFlag = true, companyAccessFlag = true, branchAccessFlag = true)
	@Override
	public List<EntityBranchDTO> findByCriteria(EntityBranchCriteria entityBranchCriteria) {
		List<EntityBranchDTO> entityBranchDtoList = new ArrayList<>();
		List<EntityBranch> branchList = entityBranchRepository
				.findAll(EntityBranchSpecs.getEntityBranchBySpecs(entityBranchCriteria));
		if (!CollectionUtils.isEmpty(branchList)) {
			setCellNumberAndLandMartks(branchList, entityBranchDtoList);
		}
		return prepareChildListData(entityBranchDtoList);
	}

	private void setCellNumberAndLandMartks(List<EntityBranch> branchList, List<EntityBranchDTO> entityBranchDtoList) {
		branchList.forEach(branch -> {
			EntityBranchDTO branchDto = entityBranchMapper.toDto(branch);
			if (branch.getAddress() != null && branch.getAddress().getLandMarks() != null) {
				branchDto.getAddress().setLandMarkSet(
						Stream.of(branch.getAddress().getLandMarks().split(",", -1)).collect(Collectors.toSet()));
			}
			entityBranchDtoList.add(branchDto);
		});
	}

	/**
	 * used to prepare child list data for getList and getById....
	 **/
	private List<EntityBranchDTO> prepareChildListData(List<EntityBranchDTO> entityBranchDtoList) {
		List<Long> branchIdList = entityBranchDtoList.stream().filter(obj -> obj != null && obj.getId() != null)
				.map(EntityBranchDTO::getId).collect(Collectors.toList());
		if (!CollectionUtils.isEmpty(branchIdList)) {
			Set<DocumentDownloadDTO> documentDtoSet = documentQueryService.getDocumentsOnEdit(branchIdList.get(0),
					ReferenceNameConstant.ENTITY_BRANCH);
			Map<Long, Set<DocumentDownloadDTO>> documentMap = documentDtoSet.stream()
					.filter(obj -> obj.getReferenceId() != null && obj.getReferenceName() != null)
					.collect(Collectors.groupingBy(DocumentDownloadDTO::getReferenceId, Collectors.toSet()));

			Set<CreditTermsDTO> creditTerms = creditTermsService
					.getCreditTermsByReferenceIdsAndReferenceName(branchIdList, ReferenceNameConstant.ENTITY_BRANCH);
			Map<Long, Set<CreditTermsDTO>> creditTermsMap = creditTerms.stream()
					.filter(obj -> obj.getReferenceId() != null && obj.getReferenceName() != null)
					.collect(Collectors.groupingBy(CreditTermsDTO::getReferenceId, Collectors.toSet()));

			List<BranchContactDTO> contactsList = branchContactsService.getBranchContactsByBranchId(branchIdList);
			Map<Long, List<BranchContactDTO>> contactsMap = contactsList.stream()
					.filter(obj -> obj.getEntityBranchId() != null)
					.collect(Collectors.groupingBy(BranchContactDTO::getEntityBranchId, Collectors.toList()));

			List<EntityBranchTaxDTO> taxList = entityBranchTaxService.getBranchTaxDetailsByBranchId(branchIdList);
			Map<Long, List<EntityBranchTaxDTO>> taxMap = taxList.stream().filter(obj -> obj.getEntityBranchId() != null)
					.collect(Collectors.groupingBy(EntityBranchTaxDTO::getEntityBranchId, Collectors.toList()));

			for (EntityBranchDTO entityBranchDto : entityBranchDtoList) {
				entityBranchDto.setEntityBranchDocuments(documentMap.get(entityBranchDto.getId()));
				Set<CreditTermsDTO> creditTermsDTO = creditTermsMap.get(entityBranchDto.getId());
				setCreditTermsData(entityBranchDto, creditTermsDTO);
				entityBranchDto.setContactDetailsDto(contactsMap.get(entityBranchDto.getId()));
				entityBranchDto.setTaxDetialsDto(taxMap.get(entityBranchDto.getId()));
			}
			return entityBranchDtoList;
		}
		return Collections.emptyList();
	}

	/** this function used to set credit terms in edit */
	private void setCreditTermsData(EntityBranchDTO entityBranchDto, Set<CreditTermsDTO> creditTermsDTO) {
		if (!CollectionUtils.isEmpty(creditTermsDTO)) {
			creditTermsDTO.forEach(creditTerm -> {
				if (creditTerm.getEntityType().equals(EntityType.CUSTOMER)) {
					entityBranchDto.setCustomerCreditAmount(creditTerm.getCreditAmount());
					entityBranchDto.setCustomerCreditDays(creditTerm.getCreditDays());
					entityBranchDto.setCustomerApprovalStatus(creditTerm.getStatus());
					entityBranchDto.setCustomerCreditTermId(creditTerm.getId());
				}
				if (creditTerm.getEntityType().equals(EntityType.VENDOR)) {
					entityBranchDto.setVendorCreditAmount(creditTerm.getCreditAmount());
					entityBranchDto.setVendorCreditDays(creditTerm.getCreditDays());
					entityBranchDto.setVendorApprovalStatus(creditTerm.getStatus());
					entityBranchDto.setVendorCreditTermId(creditTerm.getId());
				}
			});
		}
	}

	/*
	 * find by id
	 */
	@Override
	public Optional<EntityBranchDTO> findOne(Long id) {
		EntityBranchCriteria branchCriteria = new EntityBranchCriteria();
		branchCriteria.setId(id);
		List<EntityBranchDTO> entityBranchDtos = findByCriteria(branchCriteria);

		if (!CollectionUtils.isEmpty(entityBranchDtos))
			return Optional.of(entityBranchDtos.get(0));
		return Optional.ofNullable(null);
	}

	/**
	 * service to get list of branch by projection which returns branches with
	 * gstNos for multi drop down..
	 */
	@Override
	public Page<EntityBranchMultiDropDownDTO> getAllBranchesForMultiDD(EntityBranchCriteria branchCriteria,
			Pageable pageable) {
		Page<EntityBranchMultiDropDownDTO> entityBranchList = new PageImpl<>(new ArrayList<>());
		List<EntityBranchMultiDropDownDTO> branchList = new ArrayList<>();
		if (branchCriteria.getEntiyId() != null)
			entityBranchList = branchDetailsRepository.findPagedProjectedByExternalEntityId(branchCriteria.getEntiyId(),
					pageable);

		if (!CollectionUtils.isEmpty(entityBranchList.getContent())) {
			List<Long> branchIdList = entityBranchList.stream().filter(obj -> obj.getId() != null)
					.map(EntityBranchMultiDropDownDTO::getId).collect(Collectors.toList());
			List<EntityBranchTaxDTO> taxList = entityBranchTaxService.getBranchTaxDetailsByBranchId(branchIdList);
			Map<Long, Set<EntityBranchTaxDTO>> gstDetailsMap = taxList.stream()
					.filter(obj -> obj.getEntityBranchId() != null)
					.collect(Collectors.groupingBy(EntityBranchTaxDTO::getEntityBranchId, Collectors.toSet()));

			for (EntityBranchMultiDropDownDTO branch : entityBranchList.getContent()) {
				Set<EntityBranchTaxDTO> entityBranchTaxList = gstDetailsMap.get(branch.getId());
				if (!CollectionUtils.isEmpty(entityBranchTaxList)) {
					entityBranchTaxList.forEach(gst -> {
						EntityBranchMultiDropDownDTO entityBranch = EntityBranchMultiDropDownDTO.newInstance(branch);
						entityBranch.setGstId(gst.getId());
						entityBranch.setGstNo(gst.getGstNo());
						branchList.add(entityBranch);
					});
					entityBranchList = new PageImpl<>(branchList);
				}
			}
		}
		return entityBranchList;
	}

	@Override
	public List<Long> getBranchIdsByEntityIdAndActiveFlag(Long entityId, Boolean activeFlag) {
		return branchDetailsRepository.getIdsByExternalEntityIdAndActiveFlag(entityId, activeFlag);
	}
}
