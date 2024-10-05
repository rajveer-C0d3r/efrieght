package com.grtship.mdm.service;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.grtship.core.annotation.Auditable;
import com.grtship.core.annotation.Auditable.ActionType;
import com.grtship.core.dto.UnitDTO;
import com.grtship.mdm.domain.Unit;
import com.grtship.mdm.mapper.UnitMapper;
import com.grtship.mdm.repository.UnitRepository;

/**
 * Service Implementation for managing {@link Unit}.
 */
@Service
@Transactional
public class UnitService {

	private final Logger log = LoggerFactory.getLogger(UnitService.class);

	private final UnitRepository unitRepository;

	private final UnitMapper unitMapper;

	public UnitService(UnitRepository unitRepository, UnitMapper unitMapper) {
		this.unitRepository = unitRepository;
		this.unitMapper = unitMapper;
	}

	/**
	 * Save a unit.
	 *
	 * @param unitDTO the entity to save.
	 * @return the persisted entity.
	 */
	@Auditable(action = ActionType.SAVE, module = com.grtship.core.annotation.Auditable.Module.UNIT)
	public UnitDTO save(UnitDTO unitDTO) {
		log.debug("Request to save Unit : {}", unitDTO);
		Unit unit = unitMapper.toEntity(unitDTO);
		unit = unitRepository.save(unit);
		return unitMapper.toDto(unit);
	}

	/**
	 * Get all the units.
	 *
	 * @return the list of entities.
	 */
	@Transactional(readOnly = true)
	public List<UnitDTO> findAll() {
		log.debug("Request to get all Units");
		return unitRepository.findAll().stream().map(unitMapper::toDto)
				.collect(Collectors.toCollection(LinkedList::new));
	}

	/**
	 * Get one unit by id.
	 *
	 * @param id the id of the entity.
	 * @return the entity.
	 */
	@Transactional(readOnly = true)
	public Optional<UnitDTO> findOne(Long id) {
		log.debug("Request to get Unit : {}", id);
		return unitRepository.findById(id).map(unitMapper::toDto);
	}

	/**
	 * Delete the unit by id.
	 *
	 * @param id the id of the entity.
	 */
	@Auditable(action = ActionType.DELETE, module = com.grtship.core.annotation.Auditable.Module.UNIT)
	public void delete(Long id) {
		log.debug("Request to delete Unit : {}", id);
		unitRepository.deleteById(id);
	}
}
