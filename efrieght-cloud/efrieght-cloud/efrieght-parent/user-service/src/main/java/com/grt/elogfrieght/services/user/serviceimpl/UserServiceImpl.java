package com.grt.elogfrieght.services.user.serviceimpl;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.google.gson.Gson;
import com.grt.elogfrieght.services.user.domain.Authority;
import com.grt.elogfrieght.services.user.domain.User;
import com.grt.elogfrieght.services.user.domain.UserAccess;
import com.grt.elogfrieght.services.user.feignclient.ClientModule;
import com.grt.elogfrieght.services.user.mapper.UserAccessMapper;
import com.grt.elogfrieght.services.user.mapper.UserApprovalRequestMapper;
import com.grt.elogfrieght.services.user.mapper.UserMapper;
import com.grt.elogfrieght.services.user.repository.AuthorityRepository;
import com.grt.elogfrieght.services.user.repository.UserApprovalRequestRepository;
import com.grt.elogfrieght.services.user.repository.UserRepository;
import com.grt.elogfrieght.services.user.service.UserService;
import com.grt.elogfrieght.services.user.util.RandomUtil;
import com.grt.elogfrieght.services.user.util.SecurityUtils;
import com.grt.elogfrieght.services.user.validator.impl.UserValidatorImpl;
import com.grtship.common.exception.EmailAlreadyUsedException;
import com.grtship.common.exception.InvalidDataException;
import com.grtship.common.exception.UsernameAlreadyUsedException;
import com.grtship.common.service.KafkaProducerService;
import com.grtship.core.annotation.AccessFilter;
import com.grtship.core.annotation.Auditable;
import com.grtship.core.annotation.Auditable.ActionType;
import com.grtship.core.annotation.Auditable.Module;
import com.grtship.core.annotation.Validate;
import com.grtship.core.constant.AuthoritiesConstants;
import com.grtship.core.constant.ErrorCode;
import com.grtship.core.constant.KafkaTopicConstant;
import com.grtship.core.constant.SystemMessageConstant;
import com.grtship.core.dto.ApprovalRequestDTO;
import com.grtship.core.dto.ClientIdDTO;
import com.grtship.core.dto.CompanyDTO;
import com.grtship.core.dto.CsaDetailsDTO;
import com.grtship.core.dto.EmailPresentDto;
import com.grtship.core.dto.GsaDetailsDTO;
import com.grtship.core.dto.NotificationDTO;
import com.grtship.core.dto.UserAccessCompanyBranchDTO;
import com.grtship.core.dto.UserApprovalRequestDTO;
import com.grtship.core.dto.UserCreationRequestDTO;
import com.grtship.core.dto.UserDTO;
import com.grtship.core.dto.UserLogDTO;
import com.grtship.core.dto.UserSpecificBranchDTO;
import com.grtship.core.dto.UserSpecificCBRequest;
import com.grtship.core.dto.UserSpecificCBResponse;
import com.grtship.core.dto.UserSpecificCompanyDTO;
import com.grtship.core.enumeration.UserDeactivationType;
import com.grtship.core.enumeration.UserLogType;
import com.grtship.core.enumeration.UserType;

import javassist.tools.rmi.ObjectNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
@Transactional
@AllArgsConstructor
public class UserServiceImpl implements UserService {

	private final UserRepository repository;
	private final PasswordEncoder passwordEncoder;
	private final AuthorityRepository authorityRepository;
	private final UserMapper userMapper;
	private final UserValidatorImpl userValidator;
	private final UserAccessMapper userAccessMapper;
	private final KafkaProducerService kafkaProducerService;
	private final UserApprovalRequestRepository approvalRequestRepository;
	private final UserApprovalRequestMapper approvalRequestMapper;
	private final UserAuditor userAuditor;

	private final ClientModule clientModule;

	public Optional<User> activateRegistration(String key) {
		log.debug("Activating user for activation key {}", key);
		return repository.findOneByActivationKey(key).map(user -> {
			// activate given user for the registration key.
			user.setActivated(true);
			user.setActivationKey(null);
			log.debug("Activated user: {}", user);
			return user;
		});
	}

	public Optional<User> completePasswordReset(String newPassword, String key) {
		log.debug("Reset user password for reset key {}", key);
		return repository.findOneByResetKey(key)
				.filter(user -> user.getResetDate().isAfter(Instant.now().minusSeconds(86400))).map(user -> {
					user.setPassword(passwordEncoder.encode(newPassword));
					user.setResetKey(null);
					user.setResetDate(null);
					return user;
				});
	}

	public Optional<User> requestPasswordReset(String mail) {
		return repository.findOneByEmailIgnoreCase(mail).filter(User::isActivated).map(user -> {
			user.setResetKey(RandomUtil.generateResetKey());
			user.setResetDate(Instant.now());
			user.setVerificationCode(RandomUtil.generateSixDigitRandomAlphanumericString());
			return user;
		});
	}

	public User registerUser(UserDTO userDTO, String password) {
		if (userDTO.getLogin() != null) {
			repository.findOneByLogin(userDTO.getLogin().toLowerCase()).ifPresent(existingUser -> {
				boolean removed = removeNonActivatedUser(existingUser);
				if (!removed) {
					throw new UsernameAlreadyUsedException();
				}
			});
		}
		repository.findOneByEmailIgnoreCase(userDTO.getEmail()).ifPresent(existingUser -> {
			boolean removed = removeNonActivatedUser(existingUser);
			if (!removed) {
				throw new EmailAlreadyUsedException();
			}
		});
		User newUser = new User();
		String encryptedPassword = passwordEncoder.encode(password);
		newUser.setLogin(userDTO.getEmail().toLowerCase());
		// new user gets initially a generated password
		newUser.setPassword(encryptedPassword);
		newUser.setName(userDTO.getName());
		newUser.setAllCompanies(userDTO.getAllCompanies());
		newUser.setAllowLogin(userDTO.getAllowLogin());
		if (userDTO.getEmail() != null) {
			newUser.setEmail(userDTO.getEmail().toLowerCase());
		}
		newUser.setImageUrl(userDTO.getImageUrl());
		newUser.setLangKey(userDTO.getLangKey());
		// new user is not active
		newUser.setActivated(false);
		// new user gets registration key
		newUser.setActivationKey(RandomUtil.generateActivationKey());
		Set<Authority> authorities = new HashSet<>();
		authorityRepository.findById(AuthoritiesConstants.USER).ifPresent(authorities::add);
		newUser.setAuthorities(authorities);
		repository.save(newUser);
		log.debug("Created Information for User: {}", newUser);
		return newUser;
	}

	private boolean removeNonActivatedUser(User existingUser) {
		if (existingUser.isActivated()) {
			return false;
		}
		repository.delete(existingUser);
		repository.flush();
		return true;
	}

	@Transactional(readOnly = false)
	@Auditable(action = ActionType.SAVE,module = Module.USER)
	@Validate(validator = "userValidatorImpl",action = "save")
	public User createUser(UserCreationRequestDTO userDTO) throws ObjectNotFoundException {
		User user = userMapper.toEntity(userDTO);
		String encryptedPassword = passwordEncoder.encode(RandomUtil.generatePassword());
		user.setPassword(encryptedPassword);
		user.setResetKey(RandomUtil.generateResetKey());
		user.setActivationKey(RandomUtil.generateRandomAlphanumericString());
		user.setResetDate(Instant.now());
		user.setActivated(false);
		user.setVerificationCode(RandomUtil.generateSixDigitRandomAlphanumericString());
		user.setUserAccess(getUserAccess(user));
//		userValidator.isUserValid(user);
		repository.save(user);

		log.debug("Created Information for User: {}", user);
		return user;
	}

	private Set<UserAccess> getUserAccess(User user) {
		Set<UserAccess> userAccess = new HashSet<>();
		if (user.getUserType() != null && user.getUserType().equals(UserType.CLIENT)
				&& user.getAllCompanies().equals(Boolean.FALSE)) {
			userAccess = userAccessMapper.userToUserAccess(user);
		}
		return userAccess;
	}

	/**
	 * Update all information for a specific user, and return the modified user.
	 *
	 * @param userDTO user to update.
	 * @return updated user.
	 * @throws InvalidDataException
	 */
	@Transactional(readOnly = false)
	public Optional<UserDTO> updateUser(UserCreationRequestDTO userDTO) throws InvalidDataException {
		log.debug("Request to update userDTO {}", userDTO);
		User userEntity = userMapper.toEntity(userDTO);
		Optional<User> oldUserOptional = repository.findById(userDTO.getId());
		if (oldUserOptional.isPresent()) {
			User oldUser = oldUserOptional.get();
			userEntity.setUserType((oldUser.getUserType() != null) ? oldUser.getUserType() : userDTO.getUserType());// userType
																													// should
																													// not
																													// update
			userEntity.setPassword(oldUser.getPassword());
			userEntity.setAuthorities(oldUser.getAuthorities());
//			userValidator.isUserValid(userEntity);
			userEntity.setUserAccess(getUserAccess(userEntity));
			repository.save(userEntity);
			log.debug("user updated {}", userEntity);
		}
		return Optional.of(userMapper.toDto(userEntity));
	}

	public void deleteUser(Long id) {
		repository.findById(id).ifPresent(user -> {
			repository.delete(user);
			log.debug("Deleted User: {}", user);
		});
	}

	@Transactional
	public void changePassword(String currentClearTextPassword, String newPassword) {
		SecurityUtils.getCurrentUserLogin().flatMap(repository::findOneByLogin).ifPresent(user -> {
			String currentEncryptedPassword = user.getPassword();
			if (!passwordEncoder.matches(currentClearTextPassword, currentEncryptedPassword)) {
				throw new InvalidDataException(ErrorCode.INVALID_DATA_ERROR, "Password does not match");
			}
			String encryptedPassword = passwordEncoder.encode(newPassword);
			user.setPassword(encryptedPassword);
			log.debug("Changed password for User: {}", user);
		});
	}

	@Transactional
	public Boolean generateGsaUsers(List<GsaDetailsDTO> gsaUserDetails) {
		Boolean status = Boolean.FALSE;
		List<User> userList = userMapper.toEntity(gsaUserDetails);
		log.debug("userList {}", userList);
		userList.stream().forEach(user -> user.setPassword(passwordEncoder.encode(user.getName())));
		List<User> savedUserList = new ArrayList<>();
		List<UserLogDTO> userLogDTOs = new ArrayList<>();
		for (User user : userList) {
			try {
				user.setVerificationCode(RandomUtil.generateSixDigitRandomAlphanumericString());
				User savedUser = repository.save(user);
				savedUserList.add(savedUser);
				pushUserInKafkaAfterSuccess(userLogDTOs, user);
				status = Boolean.TRUE;
			} catch (Exception e) {
				status = Boolean.TRUE;
				pushUserInKafkaAfterFailure(userLogDTOs, user, e);
			}
		}

		log.debug("Saved users {}", savedUserList);
		if (!CollectionUtils.isEmpty(savedUserList)) {
			this.sendActivationMailByKafka(savedUserList, KafkaTopicConstant.KAFKA_GSA_NOTIFICATION_TOPIC);
			status = Boolean.TRUE;
		}
		if(!CollectionUtils.isEmpty(savedUserList)) {
			log.info("user data found for audit : {} ",savedUserList);
			auditUser(savedUserList);
		}
		return status;
	}

	private void pushUserInKafkaAfterFailure(List<UserLogDTO> userLogDTOs, User user, Exception e) {
		UserLogDTO userLogDTO = new UserLogDTO();
		userLogDTO.setAction(UserLogType.CREATION);
		userLogDTO.setClientId(user.getClientId());
		userLogDTO.setEmail(user.getEmail());
		userLogDTO.setUserId(user.getId());
		userLogDTO.setStatus(Boolean.FALSE);
		userLogDTO.setMessage(e.getMessage());
		userLogDTOs.add(userLogDTO);
		kafkaProducerService.sendMessage(KafkaTopicConstant.KAFKA_USER_CREATION_LOG_TOPIC,
				new Gson().toJson(userLogDTOs));
	}

	private void pushUserInKafkaAfterSuccess(List<UserLogDTO> userLogDTOs, User user) {
		UserLogDTO userLogDTO = new UserLogDTO();
		userLogDTO.setAction(UserLogType.CREATION);
		userLogDTO.setClientId(user.getClientId());
		userLogDTO.setEmail(user.getEmail());
		userLogDTO.setUserId(user.getId());
		userLogDTO.setStatus(Boolean.TRUE);
		userLogDTO.setMessage(SystemMessageConstant.USER_CREATION_MESSAGE);
		userLogDTOs.add(userLogDTO);
		kafkaProducerService.sendMessage(KafkaTopicConstant.KAFKA_USER_CREATION_LOG_TOPIC,
				new Gson().toJson(userLogDTOs));
	}

	@Transactional
	public Boolean generateCsaUsers(List<CsaDetailsDTO> csaUserDetails) {
		Boolean status = Boolean.FALSE;
		log.debug("CSA Users: {}", csaUserDetails);
		List<User> userList = userMapper.csaToEntity(csaUserDetails);
		userList.stream().filter(user -> user.getName() != null)
				.forEach(user -> log.debug("converted csa User list {}", user));
		List<User> savedUserList = new ArrayList<>();
		List<UserLogDTO> userLogDTOs = new ArrayList<>();
		status = setCsaUserDetails(status, userList, savedUserList, userLogDTOs);
		log.debug("Saved users {}", savedUserList);
		if (!CollectionUtils.isEmpty(savedUserList)) {
			sendActivationMailByKafka(savedUserList, KafkaTopicConstant.KAFKA_CSA_NOTIFICATION_TOPIC);
		}

		return status;
	}

	private Boolean setCsaUserDetails(Boolean status, List<User> userList, List<User> savedUserList,
			List<UserLogDTO> userLogDTOs) {
		for (User user : userList) {
			try {
				User userToSave = user;
				Optional<User> userByEmail = repository.findByEmailAndClientId(user.getEmail(), user.getClientId());
				if (userByEmail.isPresent()) {
					User optionalUser = userByEmail.get();
					optionalUser.getUserAccess()
							.add(UserAccess.builder().companyId(user.getCompanyId()).allBranches(Boolean.TRUE).build());
					optionalUser.setSubmittedForApproval(Boolean.TRUE);
					userToSave = optionalUser;
				} else {
					userToSave.setVerificationCode(RandomUtil.generateSixDigitRandomAlphanumericString());
				}
				User savedUser = repository.save(userToSave);
				if (!userByEmail.isPresent()) {
					savedUserList.add(savedUser);
				}
				pushUserInKafkaAfterSuccess(userLogDTOs, userToSave);
				status = Boolean.TRUE;
			} catch (Exception e) {
				status = Boolean.TRUE;
				pushUserInKafkaAfterFailure(userLogDTOs, user, e);
			}
		}
		log.debug("Saved users {}", savedUserList);
		if (!CollectionUtils.isEmpty(savedUserList)) {
			sendActivationMailByKafka(savedUserList, KafkaTopicConstant.KAFKA_CSA_NOTIFICATION_TOPIC);
		}

		if (!CollectionUtils.isEmpty(savedUserList)) {
			log.info("user data found for audit : {} ",savedUserList);
			auditUser(savedUserList);
		}
		return status;
	}
	public void auditUser(List<User> savedUser) {
		for(User user :savedUser) {
			executeAuditUser(user);
		}
	}
//	@Auditable(action = ActionType.SAVE,module = Module.USER)
	public User executeAuditUser(User user) {
		log.info("audit user data : {} ",user);
		return userAuditor.getAuditUser(user);
	}

	private void sendActivationMailByKafka(List<User> savedUserList, String kafkaTopicConstant) {
		List<NotificationDTO> notificationDtos = new ArrayList<>();
		if (!CollectionUtils.isEmpty(savedUserList)) {
			notificationDtos = userMapper.toNotificationDtos(savedUserList);
		}

		if (!CollectionUtils.isEmpty(notificationDtos)) {
			kafkaProducerService.sendMessage(kafkaTopicConstant, new Gson().toJson(notificationDtos));
		}
	}

	@Transactional
	public void lockUser(Long userId, Boolean locked) {
		Optional.of(repository.findById(userId)).filter(Optional::isPresent).map(Optional::get).map(user -> {
			user.setAccountLocked(locked);
			log.debug("User Deactivated: {}", user);
			return user;
		});
	}

	@Override
	public void deactivate(Long userId, boolean isPermanentDeactivate) {
		log.debug("Deactivating user {} {} ", userId, isPermanentDeactivate);
		Optional.of(repository.findById(userId)).filter(Optional::isPresent).map(Optional::get).map(user -> {
			if (!isPermanentDeactivate) {
				user.setDeactivationType(UserDeactivationType.TEMPORARY);
				user.setSubmittedForApproval(Boolean.TRUE);
				user.setDeactivationWefDate(LocalDate.now());
				/** Deactivation date does not set because it is a temporary deactivation */
			} else {
				String newEmail = getNewEmail(user.getEmail(),user.getId());
				log.debug("Changing email {} to new email {} for permanent deactivation", user.getEmail(), newEmail);
				log.debug("Changing login {} to new login {} for permanent deactivation", user.getLogin(), newEmail);
				user.setEmail(newEmail);
				user.setLogin(newEmail);
				user.setDeactivationType(UserDeactivationType.PERMANENT);
				user.setActivated(Boolean.FALSE);
				user.setSubmittedForApproval(Boolean.TRUE);
				user.setDeactivationWefDate(LocalDate.now());
			}
			log.debug("User Deactivated: {}", user);
			return user;
		});
	}

	/**
	 * Generate new unique email with padded 'x' char for deactivation.
	 * 
	 * @param email
	 * @return email
	 */
	protected String getNewEmail(String email, Long userId) {
		String newEmail = email;
		newEmail="x"+userId+"."+email;
		return newEmail;
	}

	@Override
	@Auditable(action = ActionType.REACTIVATE,module = Module.USER)
	public void reactivate(Long userId) {
		Optional.of(repository.findById(userId)).filter(Optional::isPresent).map(Optional::get).map(user -> {
			user.setSubmittedForApproval(Boolean.TRUE);
			user.setDeactivationType(null);
			user.setActivated(Boolean.TRUE);
//			user.setStatus(DomainStatus.APPROVED);
			user.setReactivationWefDate(LocalDate.now());
			log.debug("User Reactivated: {}", user);
			return user;
		});

	}

	@Override
	public Optional<User> completePasswordByResetKeyAndVerficationCode(String newPassword, String key,
			String verficationCode) {
		log.debug("Reset user password for reset key {}", key);
		return repository.findOneByResetKeyAndVerificationCode(key, verficationCode)
				.filter(user -> user.getResetDate().isAfter(Instant.now().minusSeconds(86400))).map(user -> {
					user.setPassword(passwordEncoder.encode(newPassword));
					user.setResetKey(null);
					user.setResetDate(null);
					user.setVerificationCode(null);
					return user;
				});
	}

	@Override
	public Optional<User> findOneWithAuthoritiesByEmailIgnoreCase(String username) {
		return repository.findOneWithAuthoritiesByEmailIgnoreCase(username);
	}

	@Override
	public Optional<User> findById(Long userId) {
		return repository.findById(userId);
	}

	@Override
	public User findByEmail(String email) {
		return repository.findByEmail(email);
	}

	@Override
	public Optional<User> findOneWithAuthoritiesByLogin(String login) {
		return repository.findOneWithAuthoritiesByLogin(login);
	}

	@Override
	public UserSpecificCBResponse getUserSpecificCompanyDetails() {
		log.info("preparing user specific company detail data ");
		Optional<String> currentUserLogin = SecurityUtils.getCurrentUserLogin();
		UserSpecificCBRequest detailDto = new UserSpecificCBRequest();
		if(currentUserLogin.isPresent()) {
			String email = currentUserLogin.get();
			Optional<User> optionalUser = repository.findOneWithAuthoritiesByEmailIgnoreCase(email);
			if(optionalUser.isPresent()) {
				User user = optionalUser.get();
				detailDto.setAllCompanies(user.getAllCompanies());
				detailDto.setClientId(user.getClientId());
				detailDto.setUserAccess(userAccessMapper.toDto(user.getUserAccess()));
			}
		}
		return clientModule.userSpecificCBDetails(detailDto);
	}

	@Override
	public ClientIdDTO getClientIdAsPerLogggedInUser() {
		log.info("preparing client data as per the logged in client");
		Optional<String> currentUserLogin=SecurityUtils.getCurrentUserLogin();
		ClientIdDTO clientDTO=new ClientIdDTO();
		if(currentUserLogin.isPresent()) {
			String email=currentUserLogin.get();
			Optional<User> optionalUser = repository.findOneWithAuthoritiesByEmailIgnoreCase(email);
			if(optionalUser.isPresent())
			{
				User user=optionalUser.get();
				clientDTO.setClientId(user.getClientId());
			}
		}
		return clientDTO;
	}

	
	@Override
	public EmailPresentDto checkEmailPresent(List<String> emailIds) {
		EmailPresentDto emailPresentDto = new EmailPresentDto();
		List<String> emails = new ArrayList<>();
		emailPresentDto.setEmailIds(emails);
		if (!CollectionUtils.isEmpty(emailIds)) {
			emailIds.stream().forEach(mail -> {
				if (repository.existsByEmailIgnoreCase(mail)) {
					emails.add(mail);
				}
			});

		}
		return emailPresentDto;
	}

	@Override
	public List<UserSpecificCompanyDTO> getUserSpecificCompanies() {
		UserAccessCompanyBranchDTO companyBranchDTO = findCurrentLoginAndSetCompanyDetails();
		return clientModule.userSpecificCompanyDetails(companyBranchDTO);
	}

	@Override
	public List<UserSpecificBranchDTO> getUserSpecificBranches(Long companyId) {
		UserAccessCompanyBranchDTO companyBranchDTO = findCurrentLoginAndSetBranchDetails(companyId);
		return clientModule.userSpecificBranchDetails(companyBranchDTO);
	}

	private UserAccessCompanyBranchDTO findCurrentLoginAndSetCompanyDetails() {
		Optional<String> currentUserLogin = SecurityUtils.getCurrentUserLogin();
		UserAccessCompanyBranchDTO detailDto = new UserAccessCompanyBranchDTO();
		if(currentUserLogin.isPresent()) {
			String email = currentUserLogin.get();
			Optional<User> optionalUser = repository.findOneWithAuthoritiesByEmailIgnoreCase(email);
			if(optionalUser.isPresent()) {
				User user = optionalUser.get();
				detailDto.setAllCompanies(user.getAllCompanies());
				detailDto.setClientId(user.getClientId());
				Set<Long> companyIds=user.getUserAccess().stream().map(UserAccess::getCompanyId).collect(Collectors.toSet());
				detailDto.setCompanyIds(companyIds);;
			}
		}
		return detailDto;
	}

	private UserAccessCompanyBranchDTO findCurrentLoginAndSetBranchDetails(Long companyId) {
		Optional<String> currentUserLogin = SecurityUtils.getCurrentUserLogin();
		UserAccessCompanyBranchDTO detailDto = new UserAccessCompanyBranchDTO();
		if (currentUserLogin.isPresent()) {
			String email = currentUserLogin.get();
			Optional<User> optionalUser = repository.findOneWithAuthoritiesByEmailIgnoreCase(email);
			if (optionalUser.isPresent()) {
				User user = optionalUser.get();
				detailDto.setClientId(user.getClientId());
				detailDto.setCompanyId(companyId);
				Set<Long> branchIds = new TreeSet<>();
				if (user.getAllCompanies()) {
					detailDto.setAllBranches(Boolean.TRUE);
				}
				user.getUserAccess().stream().forEach(accessObj -> {
					setUserBranchAccess(companyId, detailDto, branchIds, accessObj);
				});
				detailDto.setBranchIds(branchIds);
			}
		}
		return detailDto;
	}

	private void setUserBranchAccess(Long companyId, UserAccessCompanyBranchDTO detailDto, Set<Long> branchIds,
			UserAccess accessObj) {
		if (accessObj.getCompanyId().equals(companyId)) {
			if (accessObj.getAllBranches())
				detailDto.setAllBranches(accessObj.getAllBranches());
			else
				branchIds.add(accessObj.getBranchId());
		}
	}

	@Override
	public String validateCsaUsers(CompanyDTO companyDTO) {
		if(!CollectionUtils.isEmpty(companyDTO.getCsaDetails())){
			return userValidator.validateCsaUsersEmail(companyDTO.getCsaDetails(),companyDTO.getClientId(),companyDTO.getId());
		}
		return null;
	}

	@Override
	@AccessFilter(allowAdminData = false, clientAccessFlag = true)
	public void getUsersByRoleIds(ApprovalRequestDTO approvalRequestDTO) {
//		if (checkForRoleIds(approvalRequestDTO)) {
//			saveUserRequestSendDataAndPrepareNotificationDtos(
//					extractedUsersAsRequested(approvalRequestDTO, getAccessControlDto()), approvalRequestDTO,
//					new ArrayList<>());
//		}
//		sendNotification(approvalRequestDTO);
	}

	@Override
	public void saveUserApprovalRequestSentData(UserApprovalRequestDTO userApprovalRequestDTO) {
		// TODO Auto-generated method stub
		
	}

//	private boolean checkForRoleIds(ApprovalRequestDTO approvalRequestDTO) {
//		return org.apache.commons.collections4.CollectionUtils.isNotEmpty(approvalRequestDTO.getRoleIds());
//	}
//
//	private AccessControlDTO getAccessControlDto() {
//		return SpringBeans.getAccessDTO();
//	}
//
//	private List<User> extractedUsersAsRequested(ApprovalRequestDTO approvalRequestDTO, AccessControlDTO accessDTO) {
//		List<User> users;
//		if (checkWithClientAndCompany(accessDTO)) {
//			users = getUserWithCompanyAndClient(approvalRequestDTO, accessDTO);
//		} else {
//			users = getUserByRoleIds(approvalRequestDTO);
//		}
//		return users;
//	}
//
//	private List<User> getUserByRoleIds(ApprovalRequestDTO approvalRequestDTO) {
//		return repository.findAllByUserRolesRolesIdIn(approvalRequestDTO.getRoleIds());
//	}
//
//	private void sendNotification(ApprovalRequestDTO approvalRequestDTO) {
//		if (isNotificationDtoEmpty(approvalRequestDTO)) {
//			kafkaProducerService.sendMessage(KafkaTopicConstant.KAFKA_SEND_APPROVAL_REQUEST,
//					generateJsonDataForNotification(approvalRequestDTO));
//		}
//	}
//
//	private String generateJsonDataForNotification(ApprovalRequestDTO approvalRequestDTO) {
//		return new Gson().toJson(approvalRequestDTO);
//	}
//
//	private boolean isNotificationDtoEmpty(ApprovalRequestDTO approvalRequestDTO) {
//		return !CollectionUtils.isEmpty(approvalRequestDTO.getNotificationDTOs());
//	}
//
//	private boolean checkWithClientAndCompany(AccessControlDTO accessDTO) {
//		return ObjectUtils.isNotEmpty(accessDTO.getClientId()) && ObjectUtils.isNotEmpty(accessDTO.getCompanyId());
//	}
//
//	private List<User> getUserWithCompanyAndClient(ApprovalRequestDTO approvalRequestDTO, AccessControlDTO accessDTO) {
//		List<User> users;
//		users = repository.findAllByUserRolesRolesIdInAndUserAccessCompanyId(approvalRequestDTO.getRoleIds(),
//				accessDTO.getCompanyId());
//		return users;
//	}
//
//	private void saveUserRequestSendDataAndPrepareNotificationDtos(List<User> users,
//			ApprovalRequestDTO approvalRequestDTO, List<NotificationDTO> notificationDtos) {
//		IsMakerApproverExists(users, approvalRequestDTO);
//		users.stream().forEach(user -> {
//			filterUsersForNotification(approvalRequestDTO, notificationDtos, user);
//		});
//		approvalRequestDTO.setNotificationDTOs(notificationDtos);
//	}
//
//	private void filterUsersForNotification(ApprovalRequestDTO approvalRequestDTO,
//			List<NotificationDTO> notificationDtos, User user) {
//		Set<Long> roleIds = extractRoleIds(user);
//		roleIds.retainAll(approvalRequestDTO.getRoleIds());
//		insertUserNotificationDetails(approvalRequestDTO, notificationDtos, user, roleIds);
//	}
//
//	private void insertUserNotificationDetails(ApprovalRequestDTO approvalRequestDTO,
//			List<NotificationDTO> notificationDtos, User user, Set<Long> roleIds) {
//		roleIds.stream()
//				.forEach(roleId -> saveUserNotificationDetails(approvalRequestDTO, notificationDtos, user, roleId));
//	}
//
//	private void saveUserNotificationDetails(ApprovalRequestDTO approvalRequestDTO,
//			List<NotificationDTO> notificationDtos, User user, Long roleId) {
//		UserApprovalRequest approvalRequest = buildUserApprovalRequest(approvalRequestDTO, user, roleId);
//		addNotificationDetailsInList(notificationDtos, user,roleId);
//		approvalRequestRepository.save(approvalRequest);
//	}
//
//	private void addNotificationDetailsInList(List<NotificationDTO> notificationDtos, User user, Long roleId) {
//		NotificationDTO notificationDTO=userMapper.toNotificationDto(user);
//		notificationDTO.setRoleId(roleId);		
//		notificationDtos.add(notificationDTO);
//	}
//
//	private UserApprovalRequest buildUserApprovalRequest(ApprovalRequestDTO approvalRequestDTO, User user,
//			Long roleId) {
//		UserApprovalRequest approvalRequest = UserApprovalRequest.builder().action(approvalRequestDTO.getAction())
//				.email(user.getEmail()).userId(user.getId()).moduleName(approvalRequestDTO.getModuleName())
//				.status(DomainStatus.PENDING).referenceId(approvalRequestDTO.getReferenceId()).roleId(roleId).build();
//		return approvalRequest;
//	}
//
//	private Set<Long> extractRoleIds(User user) {
//		Set<Long> roleIds = user.getUserRoles().stream().map(UserRoles::getRoleId).collect(Collectors.toSet());
//		return roleIds;
//	}
//
//	private void IsMakerApproverExists(List<User> users, ApprovalRequestDTO approvalRequestDTO) {
//		if (!CollectionUtils.isEmpty(users)) {
//			Optional<String> email = SecurityUtils.getCurrentUserLogin();
//			if (checkForMakerApproval(approvalRequestDTO, email)) {
//				removeMakerUser(users, email);
//			}
//		}
//	}
//
//	private void removeMakerUser(List<User> users, Optional<String> email) {
//		users.removeIf(userObj -> userObj.getEmail().equals(email.get()));
//	}
//
//	private boolean checkForMakerApproval(ApprovalRequestDTO approvalRequestDTO, Optional<String> email) {
//		return approvalRequestDTO.getMakerAsApprover() && email.isPresent();
//	}
//
//	@Override
//	public void saveUserApprovalRequestSentData(UserApprovalRequestDTO userApprovalRequestDTO) {
//		if (ObjectUtils.isNotEmpty(userApprovalRequestDTO)) {
//			approvalRequestRepository.save(approvalRequestMapper.toEntity(userApprovalRequestDTO));
//		}
//	}

}