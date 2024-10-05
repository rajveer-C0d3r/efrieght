package com.grtship.mdm.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.grtship.core.dto.EntityBranchTaxDTO;
import com.grtship.mdm.domain.EntityBranchTax;

/**
 * Service Implementation for managing {@link EntityBranchTax}.
 */
public interface EntityBranchTaxService {

	/**
	 * Save a taxDetails.
	 *
	 * @param taxDetailsDTO the entity to save.
	 * @return the persisted entity.
	 */
	public EntityBranchTaxDTO save(EntityBranchTaxDTO taxDetailsDTO);

	/**
	 * Get all the taxDetails.
	 *
	 * @return the list of entities.
	 */
	public List<EntityBranchTaxDTO> findAll();

	/**
	 * Get one taxDetails by id.
	 *
	 * @param id the id of the entity.
	 * @return the entity.
	 */
	public Optional<EntityBranchTaxDTO> findOne(Long id);

	/**
	 * Delete the taxDetails by id.
	 *
	 * @param id the id of the entity.
	 */
	public void delete(Long id);

	public List<EntityBranchTax> saveAll(List<EntityBranchTax> gstDetails);

	public List<EntityBranchTaxDTO> getBranchTaxDetailsByBranchId(List<Long> branchIdList);

	public Set<Long> getBranchTaxDetailsIdsByBranchId(Long branchId);

	public void deleteByIdIn(Set<Long> ids);

	public void saveAll(List<EntityBranchTaxDTO> branchTaxDtoList, Long branchId);

	public void deleteTaxDetailsOnUpdate(List<EntityBranchTaxDTO> taxDetialsDto, Long branchId);

}
