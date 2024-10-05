package com.grtship.mdm.validator.impl;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.grtship.common.exception.InvalidDataException;
import com.grtship.core.dto.VesselDeactivationDto;
import com.grtship.mdm.criteria.VesselCriteria;
import com.grtship.mdm.domain.Vessel;
import com.grtship.mdm.service.VesselQueryService;

@Component
public class VesselValidatorImpl {

	private static final String NAME_ALREADY_EXIST = "Name already exist";
	@Autowired
	private VesselQueryService vesselQueryService;

	@Transactional(readOnly = true)
	public void isValidForSave(Vessel vessel) {
		isNameValid(vessel);
	}

	private void isNameValid(Vessel vessel) {
		VesselCriteria criteria = new VesselCriteria();
		criteria.setEqualsToName(vessel.getName());
		if (vesselQueryService.countByCriteria(criteria) > 0) {
			throw new InvalidDataException(NAME_ALREADY_EXIST);
		}
	}

	@Transactional(readOnly = true)
	public void isValidForUpdate(Vessel vessel) {
		isNameValidForUpdate(vessel);
	}

	private void isNameValidForUpdate(Vessel vessel) {
		VesselCriteria criteria = new VesselCriteria();
		criteria.setNotEqualToId(vessel.getId());
		criteria.setEqualsToName(vessel.getName());
		if (vesselQueryService.countByCriteria(criteria) > 0) {
			throw new InvalidDataException(NAME_ALREADY_EXIST);
		}
	}

	public void isValidForDeactivate(VesselDeactivationDto deactivationDto) {

		if (deactivationDto.getDeactivationWefDate().isBefore(LocalDate.now())) {
			throw new InvalidDataException("Deactivate WEF Date can't be past date");
		}
	}

}
