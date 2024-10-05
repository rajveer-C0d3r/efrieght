package com.grtship.mdm.validator;

import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.grtship.common.exception.InvalidDataException;
import com.grtship.common.interfaces.ValidationError;
import com.grtship.common.interfaces.Validator;
import com.grtship.common.interfaces.impl.ObjectValidationError;
import com.grtship.core.constant.ErrorCode;
import com.grtship.core.dto.SectorDTO;
import com.grtship.core.enumeration.ValidationErrorType;
import com.grtship.mdm.domain.Sector;
import com.grtship.mdm.repository.SectorRepository;

@Component
public class SectorValidator implements Validator<SectorDTO> {

	private static final String ENTITY_NAME = "Sector";
	private static final String NAME = "name";
	private static final String SECTOR_NAME_CAN_T_BE_DUPLICATE = "Sector Name can't be duplicate!";
	private static final String SECTOR_CODE_CAN_T_BE_DUPLICATE = "Sector Code can't be duplicate!";
	private static final String CODE = "code";

	@Autowired
	private SectorRepository sectorRepository;
	
	@Override
	public List<ValidationError> validate(SectorDTO sectorDTO, String action) {
		List<ValidationError> errors=new LinkedList<>();
		if(action.equals("save") || action.equals("update")) {
			saveValidation(sectorDTO, errors);
		}
		return errors;
	}

	public void saveValidation(SectorDTO sectorDto,List<ValidationError> errors) {
		addNonEmpty(errors,validateCode(sectorDto));
		addNonEmpty(errors,validateName(sectorDto));
	}

	private ValidationError validateName(SectorDTO sectorDto) {
		if (sectorDto.getName() != null) {
			List<Sector> sectors = sectorRepository.findByNameAndClientIdAndCompanyId(sectorDto.getName(),
					sectorDto.getClientId(), sectorDto.getCompanyId());
			Boolean sectorPresent = sectors.stream().filter(
					obj -> obj.getId() != null && sectorDto.getId() != null && !(obj.getId().equals(sectorDto.getId())))
					.map(obj -> obj).findAny().isPresent();
			if ((sectorDto.getId() == null && !CollectionUtils.isEmpty(sectors))
					|| sectorPresent.equals(Boolean.TRUE)) {
				return ObjectValidationError.builder().type(ValidationErrorType.ERROR)
						.errorCode(ErrorCode.INVALID_DATA_ERROR).message(SECTOR_NAME_CAN_T_BE_DUPLICATE)
						.referenceId(sectorDto.getId() == null ? "" : String.valueOf(sectorDto.getId())).build();
			}
		}
		return null;
	}

	private ValidationError validateCode(SectorDTO sectorDto) {
		if (sectorDto.getCode() != null) {
			List<Sector> sectors = sectorRepository.findByCodeAndClientIdAndCompanyId(sectorDto.getCode(),
					sectorDto.getClientId(), sectorDto.getCompanyId());
			Boolean sectorPresent = sectors.stream().filter(
					obj -> obj.getId() != null && sectorDto.getId() != null && !(obj.getId().equals(sectorDto.getId())))
					.map(obj -> obj).findAny().isPresent();
			if ((sectorDto.getId() == null && !CollectionUtils.isEmpty(sectors))
					|| sectorPresent.equals(Boolean.TRUE)) {
				return ObjectValidationError.builder().type(ValidationErrorType.ERROR)
						.errorCode(ErrorCode.INVALID_DATA_ERROR).message(SECTOR_CODE_CAN_T_BE_DUPLICATE)
						.referenceId(sectorDto.getId() == null ? "" : String.valueOf(sectorDto.getId())).build();
			}
		}
		return null;
	}
	
	private void addNonEmpty(List list, Object o) {
		if (null != o) {
			list.add(o);
		}
	}
}
