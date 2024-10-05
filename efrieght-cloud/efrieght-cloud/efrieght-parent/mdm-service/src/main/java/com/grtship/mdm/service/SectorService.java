package com.grtship.mdm.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.grtship.core.dto.SectorDTO;

/**
 * Service Interface for managing {@link com.grt.efreight.domain.Sector}.
 */
public interface SectorService {

	/**
	 * Save a sector.
	 *
	 * @param sectorDTO the entity to save.
	 * @return the persisted entity.
	 */
	SectorDTO save(SectorDTO sectorDTO);

	/**
	 * Get all the sectors.
	 *
	 * @return the list of entities.
	 */
	List<SectorDTO> findAll();

	/**
	 * Get the "id" sector.
	 *
	 * @param id the id of the entity.
	 * @return the entity.
	 */
	Optional<SectorDTO> findOne(Long id);

	/**
	 * Delete the "id" sector.
	 *
	 * @param id the id of the entity.
	 */
	void delete(Long id);

	Map<Long, String> findSectorNameByIdList(Set<Long> idList);

	Optional<SectorDTO> getByCountryId(Long countryId);

}
