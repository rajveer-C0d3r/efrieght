package com.grtship.account.validator;

import java.util.List;

import com.grtship.common.interfaces.ValidationError;
import com.grtship.core.dto.DeactivationDTO;
import com.grtship.core.dto.GroupCreationDTO;
import com.grtship.core.dto.ReactivationDTO;

public interface GroupValidator {

	void validateGroup(GroupCreationDTO groupDTO,List<ValidationError> errors);

	void deleteValidations(Long id);

	void deactivateValidations(DeactivationDTO deactivateDto,List<ValidationError> errors);

	void reactivateValidations(ReactivationDTO reactivationDto,List<ValidationError> errors);

	void isApprovalPending(Boolean submittedForApproval);
}
