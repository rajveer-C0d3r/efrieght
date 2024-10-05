package com.grtship.mdm.serviceimpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.grtship.core.dto.EntityBranchTaxDTO;
import com.grtship.mdm.domain.EntityBranchTax;
import com.grtship.mdm.mapper.EntityBranchTaxMapper;
import com.grtship.mdm.repository.EntityBranchTaxRepository;
import com.grtship.mdm.service.EntityBranchTaxService;

/**
 * Service Interface for managing for managing {@link EntityBranchTax}.
 */
@Service
@Transactional
public class EntityBranchTaxServiceImpl implements EntityBranchTaxService {

	private final EntityBranchTaxRepository taxDetailsRepository;

	private final EntityBranchTaxMapper taxDetailsMapper;

	public EntityBranchTaxServiceImpl(EntityBranchTaxRepository taxDetailsRepository,
			EntityBranchTaxMapper taxDetailsMapper) {
		this.taxDetailsRepository = taxDetailsRepository;
		this.taxDetailsMapper = taxDetailsMapper;
	}

	/**
	 * Save a taxDetails.
	 *
	 * @param taxDetailsDTO the entity to save.
	 * @return the persisted entity.
	 */
	@Override
	public EntityBranchTaxDTO save(EntityBranchTaxDTO taxDetailsDTO) {
		EntityBranchTax taxDetails = taxDetailsMapper.toEntity(taxDetailsDTO);
		taxDetails = taxDetailsRepository.save(taxDetails);
		return taxDetailsMapper.toDto(taxDetails);
	}

	/**
	 * Get all the taxDetails.
	 *
	 * @return the list of entities.
	 */
	@Transactional(readOnly = true)
	@Override
	public List<EntityBranchTaxDTO> findAll() {
		return taxDetailsRepository.findAll().stream().map(taxDetailsMapper::toDto)
				.collect(Collectors.toCollection(LinkedList::new));
	}

	/**
	 * Get one taxDetails by id.
	 *
	 * @param id the id of the entity.
	 * @return the entity.
	 */
	@Transactional(readOnly = true)
	@Override
	public Optional<EntityBranchTaxDTO> findOne(Long id) {
		return taxDetailsRepository.findById(id).map(taxDetailsMapper::toDto);
	}

	/**
	 * Delete the taxDetails by id.
	 *
	 * @param id the id of the entity.
	 */
	@Override
	public void delete(Long id) {
		taxDetailsRepository.deleteById(id);
	}

	@Override
	public List<EntityBranchTax> saveAll(List<EntityBranchTax> gstDetails) {
		return taxDetailsRepository.saveAll(gstDetails);
	}

	@Override
	public List<EntityBranchTaxDTO> getBranchTaxDetailsByBranchId(List<Long> branchIdList) {
		return (!CollectionUtils.isEmpty(branchIdList))
				? taxDetailsMapper.toDto(taxDetailsRepository.findByEntityBranch_IdIn(branchIdList))
				: Collections.emptyList();
	}

	@Override
	public Set<Long> getBranchTaxDetailsIdsByBranchId(Long branchId) {
		return taxDetailsRepository.findIdsByEntityBranchId(branchId);
	}

	@Override
	public void deleteByIdIn(Set<Long> ids) {
		taxDetailsRepository.deleteByIdIn(ids);
	}

	/**** this service used to save all branch tax... ****/
	@Override
	public void saveAll(List<EntityBranchTaxDTO> branchTaxDtoList, Long branchId) {
		if (!CollectionUtils.isEmpty(branchTaxDtoList)) {
			List<EntityBranchTax> taxDetails = new ArrayList<>();
			branchTaxDtoList.forEach(tax -> {
				EntityBranchTax branchTax = taxDetailsMapper.toEntity(tax);
				branchTax.setEntityBranchId(branchId);
				taxDetails.add(branchTax);
			});
			saveAll(taxDetails);
		}
	}

	/*** used to delete data on Edit... ***/
	@Override
	public void deleteTaxDetailsOnUpdate(List<EntityBranchTaxDTO> taxDetialsDto, Long branchId) {
		Set<Long> savedBranchTaxIdsForBranch = getBranchTaxDetailsIdsByBranchId(branchId);
		if (!CollectionUtils.isEmpty(taxDetialsDto)) {
			Set<Long> branhTaxIdsToSaveOrUpdate = taxDetialsDto.stream().filter(obj -> obj.getId() != null)
					.map(EntityBranchTaxDTO::getId).collect(Collectors.toSet());
			if (!CollectionUtils.isEmpty(branhTaxIdsToSaveOrUpdate))
				savedBranchTaxIdsForBranch.removeAll(branhTaxIdsToSaveOrUpdate);
		}
		if (!CollectionUtils.isEmpty(savedBranchTaxIdsForBranch)) {
			deleteByIdIn(savedBranchTaxIdsForBranch);
		}
	}

}
