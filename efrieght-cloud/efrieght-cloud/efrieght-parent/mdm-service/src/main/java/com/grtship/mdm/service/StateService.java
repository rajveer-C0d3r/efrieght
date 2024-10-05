package com.grtship.mdm.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.grtship.core.dto.StateDTO;
import com.grtship.mdm.domain.State;

/**
 * Service Interface for managing {@link com.grt.efreight.domain.State}.
 */
public interface StateService {

	/**
	 * Save a state.
	 *
	 * @param stateDTO the entity to save.
	 * @return the persisted entity.
	 */
	StateDTO save(StateDTO stateDTO);

	/**
	 * Get all the states.
	 *
	 * @param pageable the pagination information.
	 * @return the list of entities.
	 */
	Page<StateDTO> findAll(Pageable pageable);

	/**
	 * Get the "id" state.
	 *
	 * @param id the id of the entity.
	 * @return the entity.
	 */
	Optional<StateDTO> findOne(Long id);

	/**
	 * Delete the "id" state.
	 *
	 * @param id the id of the entity.
	 */
	void delete(Long id);

	List<State> saveAll(Set<State> states);

	List<StateDTO> getStatesByIds(List<Long> ids);

	List<Long> getStatesIdsByCountryId(Long id);

	String getStateNameById(Long id);

	List<StateDTO> getByCountryId(Long countryId);
}
