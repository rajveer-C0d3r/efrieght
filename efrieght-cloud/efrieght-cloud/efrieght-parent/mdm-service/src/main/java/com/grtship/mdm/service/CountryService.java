package com.grtship.mdm.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.grtship.core.dto.CountryDTO;
import com.grtship.core.dto.KeyLabelBeanDTO;
import com.grtship.mdm.criteria.CountryCriteria;

/**
 * Service Interface for managing {@link com.grt.efreight.domain.Country}.
 */
public interface CountryService {

	/**
	 * Save a country.
	 *
	 * @param countryDTO the entity to save.
	 * @return the persisted entity.
	 */
	CountryDTO save(CountryDTO countryDTO);

	/**
	 * Get all the countries.
	 * 
	 * @param countryCriteria
	 *
	 * @param pageable        the pagination information.
	 * @return the list of entities.
	 */
	Page<CountryDTO> findAll(CountryCriteria countryCriteria, Pageable pageable);

	/**
	 * Get the "id" country.
	 *
	 * @param id the id of the entity.
	 * @return the entity.
	 */
	Optional<CountryDTO> findOne(Long id);

	/**
	 * Delete the "id" country.
	 *
	 * @param id the id of the entity.
	 */
//    void delete(Long id);

	String getCountryNameById(Long id);

	List<CountryDTO> getCountriesByIdList(Set<Long> idList);

	Long getSectorIdByCountryId(Long countryId);

	Boolean isStateMandatoryForGivenCountry(Long id);

	KeyLabelBeanDTO getGstVatType(Long countryId);
}
