package com.grtship.mdm.serviceimpl;

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
import com.grtship.core.dto.EquipmentSizeDTO;
import com.grtship.mdm.domain.EquipmentSize;
import com.grtship.mdm.mapper.EquipmentSizeMapper;
import com.grtship.mdm.repository.EquipmentSizeRepository;
import com.grtship.mdm.service.EquipmentSizeService;

/**
 * Service Implementation for managing {@link EquipmentSize}.
 */
@Service
@Transactional
public class EquipmentSizeServiceImpl implements EquipmentSizeService {

	private final Logger log = LoggerFactory.getLogger(EquipmentSizeServiceImpl.class);

	private final EquipmentSizeRepository equipmentSizeRepository;

	private final EquipmentSizeMapper equipmentSizeMapper;

	public EquipmentSizeServiceImpl(EquipmentSizeRepository equipmentSizeRepository,
			EquipmentSizeMapper equipmentSizeMapper) {
		this.equipmentSizeRepository = equipmentSizeRepository;
		this.equipmentSizeMapper = equipmentSizeMapper;
	}

	@Override
	@Auditable(action = ActionType.SAVE, module = com.grtship.core.annotation.Auditable.Module.EQUIPMENT_SIZE)
	public EquipmentSizeDTO save(EquipmentSizeDTO equipmentSizeDTO) {
		log.debug("Request to save EquipmentSize : {}", equipmentSizeDTO);
		EquipmentSize equipmentSize = equipmentSizeMapper.toEntity(equipmentSizeDTO);
		equipmentSize = equipmentSizeRepository.save(equipmentSize);
		return equipmentSizeMapper.toDto(equipmentSize);
	}

	@Override
	@Transactional(readOnly = true)
	public List<EquipmentSizeDTO> findAll() {
		log.debug("Request to get all EquipmentSizes");
		return equipmentSizeRepository.findAll().stream().map(equipmentSizeMapper::toDto)
				.collect(Collectors.toCollection(LinkedList::new));
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<EquipmentSizeDTO> findOne(Long id) {
		log.debug("Request to get EquipmentSize : {}", id);
		return equipmentSizeRepository.findById(id).map(equipmentSizeMapper::toDto);
	}

	@Override
	@Auditable(action = ActionType.DELETE, module = com.grtship.core.annotation.Auditable.Module.EQUIPMENT_SIZE)
	public void delete(Long id) {
		log.debug("Request to delete EquipmentSize : {}", id);
		equipmentSizeRepository.deleteById(id);
	}
}
