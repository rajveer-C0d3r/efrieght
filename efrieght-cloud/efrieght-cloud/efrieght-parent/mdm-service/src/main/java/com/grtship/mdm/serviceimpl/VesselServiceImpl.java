package com.grtship.mdm.serviceimpl;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.grtship.core.annotation.Auditable;
import com.grtship.core.annotation.Auditable.ActionType;
import com.grtship.core.dto.VesselCreationRequest;
import com.grtship.core.dto.VesselDTO;
import com.grtship.core.dto.VesselDeactivationDto;
import com.grtship.core.dto.VesselUpdateRequest;
import com.grtship.mdm.domain.Deactivate;
import com.grtship.mdm.domain.Vessel;
import com.grtship.mdm.mapper.VesselMapper;
import com.grtship.mdm.repository.VesselRepository;
import com.grtship.mdm.service.VesselService;
import com.grtship.mdm.validator.impl.VesselValidatorImpl;

/**
 * Service Implementation for managing {@link Vessel}.
 */
@Service
@Transactional
public class VesselServiceImpl implements VesselService {

	private final Logger log = LoggerFactory.getLogger(VesselServiceImpl.class);

	private final VesselRepository vesselRepository;
	private final VesselMapper vesselMapper;
	private final VesselValidatorImpl vesselValidator;

	public VesselServiceImpl(VesselRepository vesselRepository, VesselMapper vesselMapper,
			VesselValidatorImpl vesselValidator) {
		this.vesselRepository = vesselRepository;
		this.vesselMapper = vesselMapper;
		this.vesselValidator = vesselValidator;
	}

	@Override
	@Auditable(action = ActionType.SAVE, module = com.grtship.core.annotation.Auditable.Module.VESSEL)
	public VesselDTO save(VesselCreationRequest vesselDTO) {
		log.debug("Request to save Vessel : {}", vesselDTO);
		Vessel vessel = vesselMapper.toEntity(vesselDTO);
		vesselValidator.isValidForSave(vessel);
		vessel = vesselRepository.save(vessel);
		return vesselMapper.toDto(vessel);
	}

	@Override
	@Auditable(action = ActionType.UPDATE, module = com.grtship.core.annotation.Auditable.Module.VESSEL)
	public VesselDTO update(VesselUpdateRequest vesselDTO) {
		log.debug("Request to save Vessel : {}", vesselDTO);
		Vessel vessel = vesselMapper.toEntity(vesselDTO);
		vesselValidator.isValidForUpdate(vessel);
		vessel = vesselRepository.save(vessel);
		return vesselMapper.toDto(vessel);
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<VesselDTO> findOne(Long id) {
		log.debug("Request to get Vessel : {}", id);
		return vesselRepository.findById(id).map(vesselMapper::toDto);
	}

	@Override
	@Auditable(action = ActionType.DEACTIVATE, module = com.grtship.core.annotation.Auditable.Module.VESSEL)
	public void deactivate(VesselDeactivationDto deactivationDto) {
		vesselValidator.isValidForDeactivate(deactivationDto);
		Optional.of(vesselRepository.findById(deactivationDto.getReferenceId())).filter(Optional::isPresent)
				.map(Optional::get).map(vessel -> {
					Deactivate deactivate = new Deactivate();
					deactivate.setReason(deactivationDto.getDeactivationReason());
					deactivate.setWefDate(deactivationDto.getDeactivationWefDate());
					vessel.setDeactivate(deactivate);
					vessel.setSubmittedForApproval(Boolean.TRUE);
					return vessel;
				});
	}

}
