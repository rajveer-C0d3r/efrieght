package com.grtship.mdm.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.grtship.core.dto.CreditTermsDTO;
import com.grtship.mdm.domain.CreditTerms;

/**
 * Service Interface for managing {@link com.grt.efreight.domain.CreditTerms}.
 */
public interface CreditTermsService {

	/**
	 * Save a creditTerms.
	 *
	 * @param creditTermsDTO the entity to save.
	 * @return the persisted entity.
	 */
	CreditTermsDTO save(CreditTermsDTO creditTermsDTO);

	/**
	 * Get all the creditTerms.
	 *
	 * @param pageable the pagination information.
	 * @return the list of entities.
	 */
	Page<CreditTermsDTO> findAll(Pageable pageable);

	/**
	 * Get the "id" creditTerms.
	 *
	 * @param id the id of the entity.
	 * @return the entity.
	 */
	Optional<CreditTermsDTO> findOne(Long id);

	/**
	 * Delete the "id" creditTerms.
	 *
	 * @param id the id of the entity.
	 */
	void delete(Long id);

	List<CreditTerms> saveAll(List<CreditTerms> creditTermsList);

	Set<CreditTermsDTO> getCreditTermsByReferenceIdsAndReferenceName(List<Long> enittyIdList, String referenceName);

	Set<Long> getCreditTermsIdByReferenceIdAndReferenceName(Long referenceId, String refereceName);

	void deleteByReferenceNameAndIdIn(String referenceName, Set<Long> savedCreditTermsIdsForentity);

	void saveAll(List<CreditTermsDTO> list, String referenceName, Long referenceId);

	void deleteCreditTermOnUpdate(List<CreditTermsDTO> creditTermsList, String referenceName, Long referenceId);
}
