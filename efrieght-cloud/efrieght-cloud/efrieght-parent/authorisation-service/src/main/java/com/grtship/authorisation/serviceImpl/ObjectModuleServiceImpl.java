package com.grtship.authorisation.serviceImpl;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.Gson;
import com.grtship.authorisation.domain.ObjectApproval;
import com.grtship.authorisation.domain.ObjectApprovalSequence;
import com.grtship.authorisation.domain.ObjectModule;
import com.grtship.authorisation.feignclient.UserFeignClient;
import com.grtship.authorisation.mapper.ApprovalRequestMapper;
import com.grtship.authorisation.mapper.ObjectApprovalMapper;
import com.grtship.authorisation.mapper.ObjectModuleMapper;
import com.grtship.authorisation.repository.ObjectModuleRepository;
import com.grtship.authorisation.service.ObjectModuleService;
import com.grtship.authorisation.util.SecurityUtils;
import com.grtship.common.service.KafkaProducerService;
import com.grtship.core.annotation.AccessFilter;
import com.grtship.core.annotation.Validate;
import com.grtship.core.dto.ApprovalRequestDTO;
import com.grtship.core.dto.AuthorizationContainerDTO;
import com.grtship.core.dto.AuthorizationObjectDTO;
import com.grtship.core.dto.ObjectModuleDTO;
import com.grtship.core.enumeration.DomainStatus;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@AllArgsConstructor
public class ObjectModuleServiceImpl implements ObjectModuleService {

	private static final String APPROVAL = "-APPROVAL";
	private final ObjectModuleRepository objectModuleRepository;
	private final ObjectModuleMapper objectModuleMapper;
	private final KafkaProducerService kafkaProducerService;
	private final UserFeignClient userFeignClient;
	private final ObjectApprovalMapper objectApprovalMapper;
	private final ApprovalRequestMapper approvalRequestMapper;

	@Override
	@Validate(validator = "objectModuleValidator",action = "save")
	public ObjectModuleDTO save(ObjectModuleDTO objectModuleDTO) {
		log.debug("Request to save ObjectModule : {}", objectModuleDTO);
		ObjectModule objectModule = objectModuleMapper.toEntity(objectModuleDTO);
		objectModule = objectModuleRepository.save(objectModule);
		return objectModuleMapper.toDto(objectModule);
	}

	@Override
	@Validate(validator = "objectModuleValidator",action = "update")
	public ObjectModuleDTO update(ObjectModuleDTO objectModuleDTO) {
		log.debug("Request to save ObjectModule : {}", objectModuleDTO);
		ObjectModule objectModule = objectModuleMapper.toEntity(objectModuleDTO);
		objectModule.setId(objectModuleDTO.getId());
		objectModule = objectModuleRepository.save(objectModule);
		return objectModuleMapper.toDto(objectModule);
	}

	@Override
	public void delete(Long id) {
		log.debug("Request to delete ObjectModule : {}", id);
		objectModuleRepository.deleteById(id);
	}
	
	@Override
	@AccessFilter(clientAccessFlag = true,companyAccessFlag = true)
	public void getApprovalRequirement(AuthorizationContainerDTO containerDTO) {
		    if(ObjectUtils.isNotEmpty(containerDTO.getAuthorizationObjectDTO())) {
		         authorizationObjectPresentThen(containerDTO);
		    }
	}

	private void authorizationObjectPresentThen(AuthorizationContainerDTO containerDTO) {
		final AuthorizationObjectDTO authorizationObjectDTO = extractedAuthorizationObject(containerDTO);	
		 checkApprovalRequirement(getObjectModuleDetails(authorizationObjectDTO),authorizationObjectDTO);
	}

	private Optional<ObjectModule> getObjectModuleDetails(final AuthorizationObjectDTO authorizationObjectDTO) {
		return objectModuleRepository
				.findByModuleNameAndAction(authorizationObjectDTO.getModuleName().name(), authorizationObjectDTO.getAction());
	}

	private AuthorizationObjectDTO extractedAuthorizationObject(AuthorizationContainerDTO containerDTO) {
		return containerDTO.getAuthorizationObjectDTO();
	}

	private void checkApprovalRequirement(Optional<ObjectModule> optionalModule,
			AuthorizationObjectDTO authorizationObjectDTO) {
		isObjectModulePresent(optionalModule, authorizationObjectDTO);
	}

	private void isObjectModulePresent(Optional<ObjectModule> optionalModule, AuthorizationObjectDTO authorizationObjectDTO) {
		objectModulePresentThen(optionalModule, authorizationObjectDTO);
	}

	private void objectModulePresentThen(Optional<ObjectModule> optionalModule, AuthorizationObjectDTO authorizationObjectDTO) {
		if (optionalModule.isPresent()) {
			extractObjectModule(optionalModule, authorizationObjectDTO);
		}
	}

	private void extractObjectModule(Optional<ObjectModule> optionalModule, AuthorizationObjectDTO authorizationObjectDTO) {
		executeApproval(authorizationObjectDTO, optionalModule.get());
	}

	private void executeApproval(AuthorizationObjectDTO authorizationObjectDTO, ObjectModule module) {
		if (module.getApprovalRequired()) {
			needAnApproval(authorizationObjectDTO, getLoggedInUser(), module);
		} else {
			noApprovalNeeded(authorizationObjectDTO, module);
		}
	}

	private Optional<String> getLoggedInUser() {
		Optional<String> user = SecurityUtils.getCurrentUserLogin();
		return user;
	}

	private void needAnApproval(AuthorizationObjectDTO authorizationObjectDTO, Optional<String> user, ObjectModule module) {
		saveObjectApprovalDetails(authorizationObjectDTO, user, module);
		sendNotificationToAuthorizeUsers(authorizationObjectDTO, module);
	}

	private void sendNotificationToAuthorizeUsers(AuthorizationObjectDTO authorizationObjectDTO, ObjectModule module) {
		userFeignClient.getUsersByRoleIds(getApprovalRequestDto(authorizationObjectDTO, module));
	}

	private void noApprovalNeeded(AuthorizationObjectDTO authorizationObjectDTO, ObjectModule module) {
		setStatusAsApproved(authorizationObjectDTO);
		activateRequestedData(authorizationObjectDTO, module);
	}

	private void activateRequestedData(AuthorizationObjectDTO authorizationObjectDTO, ObjectModule module) {
		kafkaProducerService.sendMessage(createApprovalTopic(module), createJsonDataForKafka(authorizationObjectDTO));
	}

	private String createApprovalTopic(ObjectModule module) {
		return module.getModuleName() + APPROVAL;
	}

	private String createJsonDataForKafka(AuthorizationObjectDTO authorizationObjectDTO) {
		return new Gson().toJson(authorizationObjectDTO);
	}

	private void setStatusAsApproved(AuthorizationObjectDTO authorizationObjectDTO) {
		authorizationObjectDTO.setStatus(DomainStatus.APPROVED);
	}

	private ApprovalRequestDTO getApprovalRequestDto(AuthorizationObjectDTO authorizationObjectDTO, ObjectModule module) {
		return setApprovalRequestData(authorizationObjectDTO, module);
	}

	private ApprovalRequestDTO setApprovalRequestData(AuthorizationObjectDTO authorizationObjectDTO, ObjectModule module) {
		ApprovalRequestDTO approvalRequestDTO = getApprovalRequestDto(authorizationObjectDTO);
//		setRoles(module, approvalRequestDTO);
		setMakerApprovalAsTrue(module, approvalRequestDTO);
		return approvalRequestDTO;
	}

//	private void setRoles(ObjectModule module, ApprovalRequestDTO approvalRequestDTO) {
//		approvalRequestDTO.setRoleIds(extractedPermissionCodes(module));
//	}

	private ApprovalRequestDTO getApprovalRequestDto(AuthorizationObjectDTO authorizationObjectDTO) {
		return approvalRequestMapper.toRequestDto(authorizationObjectDTO);
	}

	private void saveObjectApprovalDetails(AuthorizationObjectDTO authorizationObjectDTO, Optional<String> user, ObjectModule module) {
		ObjectApproval objectApproval = getObjectApproval(module);
		setCurrentUserEmail(user, objectApproval);
		setReferenceId(authorizationObjectDTO, objectApproval);
		saveObjectApproval(objectApproval);
	}

	private void saveObjectApproval(ObjectApproval objectApproval) {
		objectModuleRepository.save(objectApproval);
	}

	private ObjectApproval getObjectApproval(ObjectModule module) {
		ObjectApproval objectApproval = objectApprovalMapper.toObjectApproval(module);
		return objectApproval;
	}

	private void setReferenceId(AuthorizationObjectDTO authorizationObjectDTO, ObjectApproval objectApproval) {
		objectApproval.setReferenceId(authorizationObjectDTO.getReferenceId());
	}

	private void setCurrentUserEmail(Optional<String> user, ObjectApproval objectApproval) {
		objectApproval.setInitiaterEmail(user.get());
	}

//	private Set<Long> extractedRoleIds(ObjectModule module) {
//		Set<Long> roleIds = module.getObjectApprovalSequences().stream().map(ObjectApprovalSequence::getRoleId)
//				.collect(Collectors.toSet());
//		return roleIds;
//	}
	
	private Set<String> extractedPermissionCodes(ObjectModule module) {
		Set<String> permissionCodes = module.getObjectApprovalSequences().stream().map(ObjectApprovalSequence::getPermissionCode)
				.collect(Collectors.toSet());
		return permissionCodes;
	}

	private void setMakerApprovalAsTrue(ObjectModule module, ApprovalRequestDTO approvalRequestDTO) {
		if (module.getMakerAsApprover().equals(Boolean.TRUE)) {
			approvalRequestDTO.setMakerAsApprover(Boolean.TRUE);
		}
	}
	
}
