package com.grt.elogfrieght.services.user.validator.impl;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.google.gson.Gson;
import com.grt.elogfrieght.services.user.domain.User;
import com.grt.elogfrieght.services.user.mapper.UserMapper;
import com.grt.elogfrieght.services.user.repository.UserRepository;
import com.grtship.common.exception.InvalidDataException;
import com.grtship.common.interfaces.ValidationError;
import com.grtship.common.interfaces.Validator;
import com.grtship.common.interfaces.impl.ObjectValidationError;
import com.grtship.common.service.KafkaProducerService;
import com.grtship.core.constant.ErrorCode;
import com.grtship.core.constant.KafkaTopicConstant;
import com.grtship.core.constant.SystemMessageConstant;
import com.grtship.core.dto.CsaDetailsDTO;
import com.grtship.core.dto.UserCreationRequestDTO;
import com.grtship.core.dto.UserLogDTO;
import com.grtship.core.enumeration.UserLogType;
import com.grtship.core.enumeration.UserType;
import com.grtship.core.enumeration.ValidationErrorType;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Transactional(readOnly = true)
public class UserValidatorImpl implements Validator<UserCreationRequestDTO> {

	private static final String EMAIL_ID_ALREADY_EXIST = "Email id already exist ";
	
	private static final String ROLES_ARE_MANDATORY = "Roles are Mandatory";
	
	@Autowired
	UserRepository userRepository;

	@Autowired
	private KafkaProducerService kafkaProducerService;
	
	@Autowired 
	private UserMapper userMapper;
	
	@Override
	public List<ValidationError> validate(UserCreationRequestDTO userDTO, String action) {
		List<ValidationError> errors=new LinkedList<>();
		if(action.equals("save") || action.equals("update")) {
			isUserValid(userMapper.toEntity(userDTO), errors);
		}
		return errors;
	}

	public void isUserValid(User user,List<ValidationError> errors) throws InvalidDataException {
		addNonEmpty(errors,validateEmail(user));
		addNonEmpty(errors,validateRoles(user));
	}

	private ValidationError validateRoles(User user) throws InvalidDataException {
		if (user.getUserType() != null && user.getUserType().equals(UserType.CLIENT)
				&& CollectionUtils.isEmpty(user.getUserRoles())) {
			return ObjectValidationError.builder().type(ValidationErrorType.ERROR)
					.errorCode(ErrorCode.INVALID_DATA_ERROR).message(ROLES_ARE_MANDATORY)
					.referenceId(user.getId() == null ? "" : String.valueOf(user.getId())).build();
		}
		return null;
	}

	private ValidationError validateEmail(User user) throws InvalidDataException {
		if (user.getId() != null) {
			boolean emailExist = userRepository.existsByEmailIgnoreCaseAndIdNot(user.getEmail(), user.getId());
			log.debug("Validating user email {} ", emailExist);
			if (emailExist) {
				return returnEmailValidationError(user);
			}
		} else {
			boolean emailExist = userRepository.existsByEmailIgnoreCase(user.getEmail());
			log.debug("Validating user email {} ", emailExist);
			if (emailExist) {
				return returnEmailValidationError(user);
			}
		}
		return null;
	}

	private ValidationError returnEmailValidationError(User user) {
		return ObjectValidationError.builder().type(ValidationErrorType.ERROR).errorCode(ErrorCode.INVALID_DATA_ERROR)
				.message(EMAIL_ID_ALREADY_EXIST).referenceId(user.getId() == null ? "" : String.valueOf(user.getId()))
				.build();
	}

	public void validateEmail(List<String> email) throws InvalidDataException {
		List<User> usersByEmail = userRepository.findAllByEmailIn(email);
		if (!CollectionUtils.isEmpty(usersByEmail)) {
			throw new InvalidDataException(ErrorCode.INVALID_DATA_ERROR, "Email Already Used");
		}
	}

	/**
	 * 
	 * @throws InvalidDataException 
	 * @implNote this method is created for validate email with respect to clientId
	 *           to broadcast message in kafka for client user logs
	 * 
	 */
	public void validateEmailWithClientId(List<String> email, Long clientId) throws InvalidDataException {
		List<User> usersByEmail = userRepository.findAllByEmailIn(email);
		if (!CollectionUtils.isEmpty(usersByEmail)) {
			List<UserLogDTO> userLogs = new ArrayList<>(usersByEmail.size());
			UserLogDTO userLog = null;
			for (User user : usersByEmail) {
				if (StringUtils.isNotEmpty(user.getEmail())) {
					userLog = new UserLogDTO();
					userLog.setClientId(clientId);
					userLog.setEmail(user.getEmail());
					userLog.setStatus(Boolean.FALSE);
					userLog.setAction(UserLogType.CREATION);
					userLog.setUserId(user.getId());
					userLog.setMessage(SystemMessageConstant.DUPLICATE_EMAIL_ERROR);
					userLogs.add(userLog);
				}
			}
			if (!CollectionUtils.isEmpty(userLogs)) {
				kafkaProducerService.sendMessage(KafkaTopicConstant.KAFKA_USER_CREATION_LOG_TOPIC,
						new Gson().toJson(userLogs));
			}
			throw new InvalidDataException(ErrorCode.INVALID_DATA_ERROR, "Email Already Used");
		}
	}
	
	public String validateCsaUsersEmail(List<CsaDetailsDTO> csaDetailsDTOs,Long clientId,Long id) {
		if(ObjectUtils.isEmpty(id)) {
			List<User> usersPresent = userRepository.findAllByEmailInAndClientIdNot(csaDetailsDTOs.stream().
					filter(csa -> csa.getEmail()!=null).map(CsaDetailsDTO::getEmail).collect(Collectors.toList()),clientId);
			return throwCsaEmailsPresentException(usersPresent);
		} else {
			List<String> existingEmails=csaDetailsDTOs.stream().filter(csaObj -> csaObj.getId()!=null)
					                              .map(CsaDetailsDTO::getEmail).collect(Collectors.toList());
			List<String> newEmails=csaDetailsDTOs.stream().filter(csaObj -> csaObj.getId()==null)
                    .map(CsaDetailsDTO::getEmail).collect(Collectors.toList());
			List<User> usersPresent = userRepository.findAllByEmailInAndClientIdNot(newEmails,clientId);
			return throwCsaEmailsPresentException(usersPresent);
		}
	}

	private String throwCsaEmailsPresentException(List<User> usersByEmail) {
		if(!CollectionUtils.isEmpty(usersByEmail)) {
			String duplicateEmails = usersByEmail.stream().map(User::getEmail).collect(Collectors.joining(","));
			return duplicateEmails;
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private void addNonEmpty(@SuppressWarnings("rawtypes") List list,Object o) {
		if(null!=o) {
			list.add(o);
		}
	}
}
