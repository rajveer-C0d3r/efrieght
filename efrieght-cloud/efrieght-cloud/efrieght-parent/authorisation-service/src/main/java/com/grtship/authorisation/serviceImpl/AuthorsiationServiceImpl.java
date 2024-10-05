package com.grtship.authorisation.serviceImpl;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.grtship.authorisation.domain.ObjectApproval;
import com.grtship.authorisation.domain.ObjectApprovalAction;
import com.grtship.authorisation.feignclient.UserFeignClient;
import com.grtship.authorisation.repository.ObjectApprovalActionRepository;
import com.grtship.authorisation.repository.ObjectApprovalRepository;
import com.grtship.authorisation.service.AuthorisationService;
import com.grtship.authorisation.util.SecurityUtils;
import com.grtship.authorisation.validator.AuthorisationValidator;
import com.grtship.common.exception.InvalidDataException;
import com.grtship.common.service.KafkaProducerService;
import com.grtship.core.constant.ErrorCode;
import com.grtship.core.dto.ApprovedRequestDataDTO;
import com.grtship.core.dto.AuthorizationObjectDTO;
import com.grtship.core.dto.UserApprovalRequestDTO;
import com.grtship.core.enumeration.DomainStatus;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class AuthorsiationServiceImpl implements AuthorisationService {

	private static final String APPROVAL = "-APPROVAL";
	private final ObjectApprovalRepository objectApprovalRepository;
	private final AuthorisationValidator authorisationValidator;
	private final UserFeignClient userFeignClient;
	private final ObjectApprovalActionRepository approvalActionRepository;
	private final KafkaProducerService kafkaProducerService;
	
	@Override
	public Boolean saveApprovedData(ApprovedRequestDataDTO approvedRequestDataDTO) {
		log.debug("REST request to check for previous validation {}", approvedRequestDataDTO);
		String currentEmail=getCurrentUserLogin();
		Optional<ObjectApproval> optionalObjectApproval = getObjectApproval(approvedRequestDataDTO);
		if (optionalObjectApproval.isPresent()) {
			executeObjectApproval(approvedRequestDataDTO, optionalObjectApproval, buildUserApprovalRequest(approvedRequestDataDTO,currentEmail));
		} else {
			exceptionNoDataToApprove(approvedRequestDataDTO);
		}
		return Boolean.TRUE;
	}

	private String getCurrentUserLogin() {
		Optional<String> email = SecurityUtils.getCurrentUserLogin();
		if (email.isPresent()) {
			return email.get();
		}
		return null;
	}

	private Optional<ObjectApproval> getObjectApproval(ApprovedRequestDataDTO approvedRequestDataDTO) {
		return objectApprovalRepository.findByReferenceIdAndObjectName(
				approvedRequestDataDTO.getReferenceId(), approvedRequestDataDTO.getModuleName());
	}

	private UserApprovalRequestDTO buildUserApprovalRequest(ApprovedRequestDataDTO approvedRequestDataDTO,
			String email) {
		return UserApprovalRequestDTO.builder().moduleName(approvedRequestDataDTO.getModuleName()).email(email)
				.referenceId(approvedRequestDataDTO.getReferenceId()).permissionCode(approvedRequestDataDTO.getPermissionCode())
				.build();
	}

	private void exceptionNoDataToApprove(ApprovedRequestDataDTO approvedRequestDataDTO) {
		throw new InvalidDataException(ErrorCode.INVALID_DATA_ERROR,
				"There is no " + approvedRequestDataDTO.getModuleName() + " with Id "
						+ approvedRequestDataDTO.getReferenceId() + " is Pending for Approval");
	}

	private void executeObjectApproval(ApprovedRequestDataDTO approvedRequestDataDTO,
			Optional<ObjectApproval> optionalObjectApproval, UserApprovalRequestDTO approvalRequestDTO) {
		ObjectApproval objectApproval = optionalObjectApproval.get();
		UserApprovalRequestDTO userApprovalRequestDTO = getUserApprovalData(approvalRequestDTO);
		authorisationValidator.validateApprovedRequest(objectApproval, userApprovalRequestDTO,
				approvedRequestDataDTO.getPermissionCode());
		objectApproval.getApprovalActions().stream()
				.forEach(IsObjectRejected(approvedRequestDataDTO, objectApproval, userApprovalRequestDTO));
		updateDataIfAllConditionsTrue(approvedRequestDataDTO, objectApproval, userApprovalRequestDTO);
	}

	private UserApprovalRequestDTO getUserApprovalData(UserApprovalRequestDTO approvalRequestDTO) {
		return userFeignClient
				.getUserApprovalRequestData(approvalRequestDTO);
	}

	private Consumer<? super ObjectApprovalAction> IsObjectRejected(ApprovedRequestDataDTO approvedRequestDataDTO,
			ObjectApproval objectApproval, UserApprovalRequestDTO userApprovalRequestDTO) {
		return approvalAction -> {
			if (checkUserHaveRole(userApprovalRequestDTO, approvalAction)) {
				saveApprovalActionData(approvedRequestDataDTO, userApprovalRequestDTO, approvalAction);
				prepareAndSaveUserApprovalRequest(approvedRequestDataDTO, userApprovalRequestDTO);
				if (IsDataRejected(approvedRequestDataDTO)) {
					objectDataIsRejected(approvedRequestDataDTO, objectApproval,userApprovalRequestDTO);
				}
			}
		};
	}

	private void prepareAndSaveUserApprovalRequest(ApprovedRequestDataDTO approvedRequestDataDTO,
			UserApprovalRequestDTO userApprovalRequestDTO) {
		userApprovalRequestDTO.setStatus(approvedRequestDataDTO.getStatus());
		userFeignClient.saveUserApprovalRequest(userApprovalRequestDTO);
	}

	private boolean IsDataRejected(ApprovedRequestDataDTO approvedRequestDataDTO) {
		return approvedRequestDataDTO.getStatus().equals(DomainStatus.REJECTED);
	}

	private void saveApprovalActionData(ApprovedRequestDataDTO approvedRequestDataDTO, UserApprovalRequestDTO userApprovalRequestDTO,
			ObjectApprovalAction approvalAction) {
		setApprovalActionData(approvedRequestDataDTO, userApprovalRequestDTO, approvalAction);
		approvalActionRepository.save(approvalAction);
	}

	private void setApprovalActionData(ApprovedRequestDataDTO approvedRequestDataDTO, UserApprovalRequestDTO userApprovalRequestDTO,
			ObjectApprovalAction approvalAction) {
		approvalAction.setAction(approvedRequestDataDTO.getStatus());
		approvalAction.setActionComment(approvedRequestDataDTO.getComment());
		approvalAction.setActionedBy(userApprovalRequestDTO.getUserId());
		approvalAction.setActioner(userApprovalRequestDTO.getEmail());
		approvalAction.setActionDatetime(LocalDateTime.now());
	}

	private boolean checkUserHaveRole(UserApprovalRequestDTO userApprovalRequestDTO, ObjectApprovalAction approvalAction) {
		return approvalAction.getPermissionCode().equals(userApprovalRequestDTO.getPermissionCode());
	}

	private void updateDataIfAllConditionsTrue(ApprovedRequestDataDTO approvedRequestDataDTO,
			ObjectApproval objectApproval, UserApprovalRequestDTO userApprovalRequestDTO) {
		if (objectApproval.getApprovalActions().stream()
				.allMatch(IsFinallyApproved())) {
			makeObjectAvailableToUser(approvedRequestDataDTO, objectApproval, userApprovalRequestDTO);
		}
	}

	private void makeObjectAvailableToUser(ApprovedRequestDataDTO approvedRequestDataDTO, ObjectApproval objectApproval,
			UserApprovalRequestDTO userApprovalRequestDTO) {
		objectDataIsRejected(approvedRequestDataDTO, objectApproval, userApprovalRequestDTO);
		pushDataToKafka(approvedRequestDataDTO, objectApproval, userApprovalRequestDTO);
	}

	private Predicate<? super ObjectApprovalAction> IsFinallyApproved() {
		return approvalAction -> approvalAction.getAction().equals(DomainStatus.APPROVED)
				&& approvalAction.getActioner() != null;
	}

	private void pushDataToKafka(ApprovedRequestDataDTO approvedRequestDataDTO, ObjectApproval objectApproval,
			UserApprovalRequestDTO userApprovalRequestDTO) {
		sendDataForAuthorization(approvedRequestDataDTO, objectApproval, userApprovalRequestDTO);
	}

	private void sendDataForAuthorization(ApprovedRequestDataDTO approvedRequestDataDTO, ObjectApproval objectApproval,
			UserApprovalRequestDTO userApprovalRequestDTO) {
		kafkaProducerService.sendMessage(generateApprovalTopic(objectApproval),
				generateJsonDataForAuthorization(approvedRequestDataDTO, userApprovalRequestDTO));
	}

	private String generateApprovalTopic(ObjectApproval objectApproval) {
		return objectApproval.getObjectName() + APPROVAL;
	}

	private String generateJsonDataForAuthorization(ApprovedRequestDataDTO approvedRequestDataDTO,
			UserApprovalRequestDTO userApprovalRequestDTO) {
		return new Gson().toJson(buildAuthorizationObject(approvedRequestDataDTO, userApprovalRequestDTO));
	}

	private AuthorizationObjectDTO buildAuthorizationObject(ApprovedRequestDataDTO approvedRequestDataDTO,
			UserApprovalRequestDTO userApprovalRequestDTO) {
		return AuthorizationObjectDTO.builder().action(userApprovalRequestDTO.getAction())
				.status(approvedRequestDataDTO.getStatus()).referenceId(approvedRequestDataDTO.getReferenceId())
				.build();
	}

	private void objectDataIsRejected(ApprovedRequestDataDTO approvedRequestDataDTO, ObjectApproval objectApproval,
			UserApprovalRequestDTO userApprovalRequestDTO) {
		objectApproval.setApprovalStatus(approvedRequestDataDTO.getStatus());
		objectApproval.setObjectStatus(approvedRequestDataDTO.getStatus());
		objectApprovalRepository.save(objectApproval);
		pushDataToKafka(approvedRequestDataDTO, objectApproval, userApprovalRequestDTO);
	}
}
