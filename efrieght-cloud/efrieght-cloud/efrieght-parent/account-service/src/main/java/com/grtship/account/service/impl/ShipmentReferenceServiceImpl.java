package com.grtship.account.service.impl;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.grtship.account.domain.ShipmentReference;
import com.grtship.account.mapper.ShipmentReferenceMapper;
import com.grtship.account.repository.ShipmentReferenceRepository;
import com.grtship.account.service.ShipmentReferenceService;
import com.grtship.core.annotation.Auditable;
import com.grtship.core.annotation.Auditable.ActionType;
import com.grtship.core.dto.ShipmentReferenceDTO;

/**
 * Service Implementation for managing {@link ShipmentReference}.
 */
@Service
@Transactional
public class ShipmentReferenceServiceImpl implements ShipmentReferenceService {

	private final Logger log = LoggerFactory.getLogger(ShipmentReferenceServiceImpl.class);

	@Autowired
	private ShipmentReferenceRepository shipmentReferenceRepository;

	@Autowired
	private ShipmentReferenceMapper shipmentReferenceMapper;

	/**
	 * Save a shipmentReference.
	 *
	 * @param shipmentReferenceDto the entity to save.
	 * @return the persisted entity.
	 */
	@Override
	@Auditable(action = ActionType.SAVE,module = com.grtship.core.annotation.Auditable.Module.SHIPMENT_REFERENCE)
	public ShipmentReferenceDTO save(ShipmentReferenceDTO shipmentReferenceDto) {
		log.debug("Request to save ShipmentReference : {}", shipmentReferenceDto);
		ShipmentReference shipmentReference = shipmentReferenceMapper.toEntity(shipmentReferenceDto);
		shipmentReference = shipmentReferenceRepository.save(shipmentReference);
		return shipmentReferenceMapper.toDto(shipmentReference);
	}

	/**
	 * Get all the shipmentReferences.
	 *
	 * @return the list of entities.
	 */
	@Override
	@Transactional(readOnly = true)
	public List<ShipmentReferenceDTO> findAll() {
		log.debug("Request to get all ShipmentReferences");
		return shipmentReferenceRepository.findAll().stream().map(shipmentReferenceMapper::toDto)
				.collect(Collectors.toCollection(LinkedList::new));
	}

	/**
	 * Get one shipmentReference by id.
	 *
	 * @param id the id of the entity.
	 * @return the entity.
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<ShipmentReferenceDTO> findOne(Long id) {
		log.debug("Request to get ShipmentReference : {}", id);
		return shipmentReferenceRepository.findById(id).map(shipmentReferenceMapper::toDto);
	}

	/**
	 * Delete the shipmentReference by id.
	 *
	 * @param id the id of the entity.
	 */
	@Override
	@Auditable(action = ActionType.DELETE,module = com.grtship.core.annotation.Auditable.Module.SHIPMENT_REFERENCE)
	public void delete(Long id) {
		log.debug("Request to delete ShipmentReference : {}", id);
		shipmentReferenceRepository.deleteById(id);
	}
}
