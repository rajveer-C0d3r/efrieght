package com.grtship.mdm.serviceimpl;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.grtship.core.annotation.Auditable;
import com.grtship.core.annotation.Auditable.ActionType;
import com.grtship.core.dto.EquipmentDTO;
import com.grtship.mdm.domain.Equipment;
import com.grtship.mdm.mapper.EquipmentMapper;
import com.grtship.mdm.repository.EquipmentRepository;
import com.grtship.mdm.service.EquipmentService;

/**
 * Service Implementation for managing {@link Equipment}.
 */
@Service
@Transactional
public class EquipmentServiceImpl implements EquipmentService {

	private final Logger log = LoggerFactory.getLogger(EquipmentServiceImpl.class);

	private final EquipmentRepository equipmentRepository;

	private final EquipmentMapper equipmentMapper;

	public EquipmentServiceImpl(EquipmentRepository equipmentRepository, EquipmentMapper equipmentMapper) {
		this.equipmentRepository = equipmentRepository;
		this.equipmentMapper = equipmentMapper;
	}

	@Override
	@Auditable(action = ActionType.SAVE, module = com.grtship.core.annotation.Auditable.Module.EQUIPMENT)
	public EquipmentDTO save(EquipmentDTO equipmentDTO) {
		log.debug("Request to save Equipment : {}", equipmentDTO);
		Equipment equipment = equipmentMapper.toEntity(equipmentDTO);
		equipment = equipmentRepository.save(equipment);
		return equipmentMapper.toDto(equipment);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<EquipmentDTO> findAll(Pageable pageable) {
		log.debug("Request to get all Equipment");
		return equipmentRepository.findAll(pageable).map(equipmentMapper::toDto);
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<EquipmentDTO> findOne(Long id) {
		log.debug("Request to get Equipment : {}", id);
		return equipmentRepository.findById(id).map(equipmentMapper::toDto);
	}

	@Override
	@Auditable(action = ActionType.DELETE, module = com.grtship.core.annotation.Auditable.Module.EQUIPMENT)
	public void delete(Long id) {
		log.debug("Request to delete Equipment : {}", id);
		equipmentRepository.deleteById(id);
	}
}
