package com.grtship.authorisation.validator;

import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Component;

import com.grtship.authorisation.domain.ObjectApproval;
import com.grtship.authorisation.domain.ObjectApprovalAction;
import com.grtship.common.exception.InvalidDataException;
import com.grtship.core.constant.ErrorCode;
import com.grtship.core.dto.UserApprovalRequestDTO;
import com.grtship.core.enumeration.DomainStatus;

@Component
public class AuthorisationValidator {

	private static final String ACTION_ON_DATA_AT_YOUR_LEVEL_IS_ALREADY_TAKEN = "Action on Data at Your Level is Already Taken";
	private static final String YOU_ARE_NOT_ALLOWED_TO_TAKE_ACTION_ON_CURRENT_DATA = "You are not allowed to take action on current Data";
	private static final String OBJECT_IS_ALREADY_REJECTED = "Data is Already Rejected";
	private static final String PREVIOUS_APPROVAL_IS_PENDING = "Previous Approval is Pending";

	public void validateApprovedRequest(ObjectApproval objectApproval, UserApprovalRequestDTO userApprovalRequestDTO,
			String permissionCode) {
		if (checkForRejection(objectApproval)) {
			exceptionDataAlreadyRejected();
		}
		if (ObjectUtils.isEmpty(userApprovalRequestDTO)) {
			exceptionNotAllowedToTakeAction();
		}
		checkIfAlreadyApprovedByPermissionCode(objectApproval, permissionCode);
		if (IsParallelApprovalThereAndObjectIsPresent(objectApproval)) {
			executeApproval(objectApproval, permissionCode);
		}
	}

	private void checkIfAlreadyApprovedByPermissionCode(ObjectApproval objectApproval, String permissionCode) {
		Optional<ObjectApprovalAction> approvalAction = findAction(permissionCode, objectApproval.getApprovalActions());
		if (approvalAction.isPresent()) {
			ObjectApprovalAction objectApprovalAction = approvalAction.get();
			if (!objectApprovalAction.getAction().equals(DomainStatus.PENDING)) {
				exceptionIfAlreadyTakenActionByPermissionCode();
			}
		}
	}

	private void exceptionIfAlreadyTakenActionByPermissionCode() {
		throw new InvalidDataException(ErrorCode.INVALID_DATA_ERROR, ACTION_ON_DATA_AT_YOUR_LEVEL_IS_ALREADY_TAKEN);
	}

	private void exceptionNotAllowedToTakeAction() {
		throw new InvalidDataException(ErrorCode.INVALID_DATA_ERROR,
				YOU_ARE_NOT_ALLOWED_TO_TAKE_ACTION_ON_CURRENT_DATA);
	}

	private void executeApproval(ObjectApproval objectApproval, String permissionCode) {
		Set<ObjectApprovalAction> approvalActions = objectApproval.getApprovalActions();
		Optional<ObjectApprovalAction> approvalAction = findAction(permissionCode, approvalActions);
		if (approvalAction.isPresent()) {
			checkForPreviousApproval(approvalActions, approvalAction);
		}
	}

	private void checkForPreviousApproval(Set<ObjectApprovalAction> approvalActions,
			Optional<ObjectApprovalAction> approvalAction) {
		ObjectApprovalAction objectApprovalAction = approvalAction.get();
		approvalActions.stream().filter(checkPreviousApproval(objectApprovalAction)).findAny()
				.ifPresent(IsApprovalPendingForPreviousUser());
	}

	private Consumer<? super ObjectApprovalAction> IsApprovalPendingForPreviousUser() {
		return actionObj -> {
			if (checkForPendingAction(actionObj)) {
				exceptionPreviousApprovalPending();
			}
		};
	}

	private Predicate<? super ObjectApprovalAction> checkPreviousApproval(ObjectApprovalAction objectApprovalAction) {
		return obj -> obj.getApprovalSequence().equals(objectApprovalAction.getApprovalSequence() - 1);
	}

	private void exceptionPreviousApprovalPending() {
		throw new InvalidDataException(ErrorCode.INVALID_DATA_ERROR, PREVIOUS_APPROVAL_IS_PENDING);
	}

	private boolean checkForPendingAction(ObjectApprovalAction actionObj) {
		return actionObj.getAction() != null && actionObj.getAction().equals(DomainStatus.PENDING);
	}

	private Optional<ObjectApprovalAction> findAction(String permissionCode, Set<ObjectApprovalAction> approvalActions) {
		return approvalActions.stream().filter(action -> action.getPermissionCode().equals(permissionCode)).findAny();
	}

	private boolean IsParallelApprovalThereAndObjectIsPresent(ObjectApproval objectApproval) {
		return objectApproval.getIsParallel().equals(Boolean.FALSE);
	}

	private void exceptionDataAlreadyRejected() {
		throw new InvalidDataException(ErrorCode.INVALID_DATA_ERROR, OBJECT_IS_ALREADY_REJECTED);
	}

	private boolean checkForRejection(ObjectApproval objectApproval) {
		return objectApproval.getApprovalStatus().equals(DomainStatus.REJECTED);
	}
}
