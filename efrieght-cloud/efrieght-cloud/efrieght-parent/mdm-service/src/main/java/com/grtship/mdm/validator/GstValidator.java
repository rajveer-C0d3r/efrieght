package com.grtship.mdm.validator;

import java.util.List;

import com.grtship.common.interfaces.ValidationError;
import com.grtship.core.dto.GstDTO;

public interface GstValidator {

	void saveValidations(GstDTO gstDto,List<ValidationError> errors);
}
