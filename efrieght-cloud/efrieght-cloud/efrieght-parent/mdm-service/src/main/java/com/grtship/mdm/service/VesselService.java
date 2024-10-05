package com.grtship.mdm.service;

import java.util.Optional;

import com.grtship.core.dto.VesselCreationRequest;
import com.grtship.core.dto.VesselDTO;
import com.grtship.core.dto.VesselDeactivationDto;
import com.grtship.core.dto.VesselUpdateRequest;

/**
 * Service Interface for managing {@link com.grt.efreight.domain.Vessel}.
 */
public interface VesselService {

	/**
	 * Save a vessel.
	 *
	 * @param vesselDTO the entity to save.
	 * @return the persisted entity.
	 */
	VesselDTO save(VesselCreationRequest vesselDTO);

	VesselDTO update(VesselUpdateRequest vesselDTO);

	/**
	 * Get the "id" vessel.
	 *
	 * @param id the id of the entity.
	 * @return the entity.
	 */
	Optional<VesselDTO> findOne(Long id);

	void deactivate(VesselDeactivationDto deactivationDto);

}
