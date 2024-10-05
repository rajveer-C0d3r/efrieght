package com.grt.elogfrieght.services.user.controller;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.validation.Valid;
import javax.xml.bind.ValidationException;

import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.MessagingException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.google.gson.Gson;
import com.grt.elogfrieght.services.user.criteria.UserCriteria;
import com.grt.elogfrieght.services.user.domain.User;
import com.grt.elogfrieght.services.user.dto.PageableEntityDTO;
import com.grt.elogfrieght.services.user.mapper.UserMapper;
import com.grt.elogfrieght.services.user.repository.UserRepository;
import com.grt.elogfrieght.services.user.service.UserService;
import com.grt.elogfrieght.services.user.serviceimpl.UserQueryServiceImpl;
import com.grt.elogfrieght.services.user.util.HeaderUtil;
import com.grt.elogfrieght.services.user.util.PaginationUtil;
import com.grt.elogfrieght.services.user.util.ResponseUtil;
import com.grtship.common.exception.BadRequestAlertException;
import com.grtship.common.exception.EmailAlreadyUsedException;
import com.grtship.common.exception.LoginAlreadyUsedException;
import com.grtship.common.service.KafkaProducerService;
import com.grtship.core.constant.AuthoritiesConstants;
import com.grtship.core.constant.KafkaTopicConstant;
import com.grtship.core.dto.ApprovalRequestDTO;
import com.grtship.core.dto.BaseDTO;
import com.grtship.core.dto.ClientIdDTO;
import com.grtship.core.dto.CompanyDTO;
import com.grtship.core.dto.CsaDetailsDTO;
import com.grtship.core.dto.EmailPresentDto;
import com.grtship.core.dto.GsaDetailsDTO;
import com.grtship.core.dto.NotificationDTO;
import com.grtship.core.dto.UserAccessDTO;
import com.grtship.core.dto.UserApprovalRequestDTO;
import com.grtship.core.dto.UserCreationRequestDTO;
import com.grtship.core.dto.UserDTO;
import com.grtship.core.dto.UserDeactivationRequest;
import com.grtship.core.dto.UserSpecificBranchDTO;
import com.grtship.core.dto.UserSpecificCBResponse;
import com.grtship.core.dto.UserSpecificCompanyDTO;
import com.grtship.core.enumeration.UserType;

import javassist.tools.rmi.ObjectNotFoundException;

/**
 * REST controller for managing users.
 * <p>
 * This class accesses the {@link User} entity, and needs to fetch its
 * collection of authorities.
 * <p>
 * For a normal use-case, it would be better to have an eager relationship
 * between User and Authority, and send everything to the client side: there
 * would be no View Model and DTO, a lot less code, and an outer-join which
 * would be good for performance.
 * <p>
 * We use a View Model and a DTO for 3 reasons:
 * <ul>
 * <li>We want to keep a lazy association between the user and the authorities,
 * because people will quite often do relationships with the user, and we don't
 * want them to get the authorities all the time for nothing (for performance
 * reasons). This is the #1 goal: we should not impact our users' application
 * because of this use-case.</li>
 * <li>Not having an outer join causes n+1 requests to the database. This is not
 * a real issue as we have by default a second-level cache. This means on the
 * first HTTP call we do the n+1 requests, but then all authorities come from
 * the cache, so in fact it's much better than doing an outer join (which will
 * get lots of data from the database, for each HTTP call).</li>
 * <li>As this manages users, for security reasons, we'd rather have a DTO
 * layer.</li>
 * </ul>
 * <p>
 * Another option would be to have a specific JPA entity graph to handle this
 * case.
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

	private final Logger log = LoggerFactory.getLogger(UserController.class);

	@Value("${spring.application.name}")
	private String applicationName;

	@Autowired
	private UserQueryServiceImpl userQueryService;

	private final UserService userService;
	private final UserRepository userRepository;
	private final UserMapper userMapper;
	private final KafkaProducerService kafkaProducerService;

	public UserController(UserService userService, UserRepository userRepository,
			UserMapper userMapper, KafkaProducerService kafkaProducerService) {
		super();
		this.userService = userService;
		this.userRepository = userRepository;
		this.userMapper = userMapper;
		this.kafkaProducerService = kafkaProducerService;
	}

	/**
	 * {@code POST  /users} : Creates a new user.
	 * <p>
	 * Creates a new user if the login and email are not already used, and sends an
	 * mail with an activation link. The user needs to be activated on creation.
	 *
	 * @param userDTO the user to create.
	 * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with
	 *         body the new user, or with status {@code 400 (Bad Request)} if the
	 *         login or email is already in use.
	 * @throws URISyntaxException       if the Location URI syntax is incorrect.
	 * @throws ObjectNotFoundException
	 * @throws MessagingException 
	 * @throws BadRequestAlertException {@code 400 (Bad Request)} if the login or
	 *                                  email is already in use.
	 */
	@PostMapping
	@PreAuthorize("hasAnyAuthority(\"" + AuthoritiesConstants.USER_ADD + ", " + AuthoritiesConstants.GSA + ", "
			+ AuthoritiesConstants.CSA + "\")")
	public ResponseEntity<User> createUser(@Valid @RequestBody UserCreationRequestDTO userDTO)
			throws URISyntaxException, ObjectNotFoundException, MessagingException {
		log.debug("REST request to save User : {}", userDTO);

		if (userDTO.getId() != null) {
			throw new BadRequestAlertException("A new user cannot already have an ID", "userManagement", "id");
		} else {
			User newUser = userService.createUser(userDTO);
			sendEmailByKafka(newUser);
			return ResponseEntity.created(new URI("/api/users/" + newUser.getLogin()))
					.headers(HeaderUtil.createAlert(applicationName, "userManagement.created", newUser.getLogin()))
					.body(newUser);

		}
	}

	private void sendEmailByKafka(User newUser) {
		NotificationDTO notificationDto = new NotificationDTO();
		if (!ObjectUtils.isEmpty(newUser)) {
			notificationDto=userMapper.toNotificationDto(newUser);
		}

		if (!ObjectUtils.isEmpty(notificationDto)) {
			kafkaProducerService.sendMessage(KafkaTopicConstant.KAFKA_USER_NOTIFICATION_TOPIC,
					new Gson().toJson(notificationDto));
		}
	}

	/**
	 * {@code PUT /users} : Updates an existing User.
	 *
	 * @param userDTO the user to update.
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
	 *         the updated user.
	 * @throws EmailAlreadyUsedException {@code 400 (Bad Request)} if the email is
	 *                                   already in use.
	 * @throws LoginAlreadyUsedException {@code 400 (Bad Request)} if the login is
	 *                                   already in use.
	 */
	@PutMapping
	@PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.USER_EDIT + "\")")
	public ResponseEntity<UserDTO> updateUser(@Valid @RequestBody UserCreationRequestDTO userDTO) {
		log.debug("REST request to update User : {}", userDTO);
		Optional<User> existingUser = userRepository.findOneByEmailIgnoreCase(userDTO.getEmail());
		if (existingUser.isPresent() && (!existingUser.get().getId().equals(userDTO.getId()))) {
			throw new EmailAlreadyUsedException();
		}
		existingUser = userRepository.findOneByLogin(userDTO.getEmail().toLowerCase());
		if (existingUser.isPresent() && (!existingUser.get().getId().equals(userDTO.getId()))) {
			throw new LoginAlreadyUsedException();
		}

		Optional<UserDTO> updatedUser = userService.updateUser(userDTO);

		return ResponseUtil.wrapOrNotFound(updatedUser,
				HeaderUtil.createAlert(applicationName, "userManagement.updated", userDTO.getEmail()));
	}

	/**
	 * {@code GET /users} : get all users.
	 *
	 * @param pageable the pagination information.
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
	 *         all users.
	 * @throws ValidationException 
	 */
	@GetMapping
	@PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.USER_VIEW + "\")")
	public ResponseEntity<PageableEntityDTO<UserDTO>> getAllUsers(UserCriteria userCriteria, Pageable pageable) throws ValidationException {
		final Page<UserDTO> page = userQueryService.findByCriteria(userCriteria, pageable);
		HttpHeaders headers = PaginationUtil
				.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
		return ResponseEntity.ok().headers(headers)
				.body(new PageableEntityDTO<UserDTO>(page.getContent(), page.getTotalElements()));
	}

	/**
	 * Gets a list of all roles.
	 * 
	 * @return a string list of all roles.
	 */
	@GetMapping("authorities")
	@PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
	public List<String> getAuthorities() {
		return userQueryService.getAuthorities();
	}

	/**
	 * {@code DELETE /users/:login} : delete the "login" User.
	 *
	 * @param login the login of the user to delete.
	 * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
	 */
	@DeleteMapping("{id}")
	@PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.USER_DELETE + "\")")
	public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
		log.debug("REST request to delete User: {}", id);
		userService.deleteUser(id);
		return ResponseEntity.noContent()
				.headers(HeaderUtil.createAlert(applicationName, "userManagement.deleted", id.toString())).build();
	}

	@PostMapping("createGsaUsers")
	@PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
	public ResponseEntity<Void> generateGsaUsers(@RequestBody List<GsaDetailsDTO> gsaUserDetails) throws MessagingException {
		userService.generateGsaUsers(gsaUserDetails);
		return ResponseEntity.ok().body(null);
	}

	@PostMapping("createCsaUsers")
	@PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
	public ResponseEntity<Void> generateCsaUsers(@RequestBody List<CsaDetailsDTO> csaUserDetails) throws MessagingException {
		userService.generateCsaUsers(csaUserDetails);

		return ResponseEntity.noContent()
				.headers(HeaderUtil.createAlert(applicationName, "userManagement.csaCreated", "")).build();
	}

	@GetMapping("getCurrentUserCompanyBranchIds/{userId}")
	public ResponseEntity<List<UserAccessDTO>> getCurrentUserCompanyBranchIds(@PathVariable Long userId) {
		return ResponseEntity.ok().body(userQueryService.getUserAccessByUserId(userId));
	}

	/** GET ONLY ID AND NAME LIST OF USER DROPDOWNS */
	@GetMapping("dropdownlist")
	public ResponseEntity<PageableEntityDTO<BaseDTO>> getUsersForDropdown(Pageable pageable) {
		final Page<BaseDTO> page = userQueryService.getUsersForDropdown(pageable);
		HttpHeaders headers = PaginationUtil
				.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
		return ResponseEntity.ok().headers(headers)
				.body(new PageableEntityDTO<BaseDTO>(page.getContent(), page.getTotalElements()));
	}

	@PostMapping("getGsaUsersByClientIdList")
	public Map<Long, List<GsaDetailsDTO>> getGsaUsersByClientIdList(@RequestBody List<Long> idList) {
		return userQueryService.getGsaUsersByClientIdList(idList);
	}

	@GetMapping("getUserType/{login}")
	public Optional<UserType> getUserType(@PathVariable String login) {
		log.debug("REST request to get User Type : {}", login);
		return userQueryService.getUserType(login);
	}

	@PostMapping("getCsaUsersByCompanyIdList")
	public Map<Long, List<CsaDetailsDTO>> getCsaUsersByCompanyIdList(@RequestBody List<Long> companyIdList) {
		return userQueryService.getCsaUsersByCompanyIdList(companyIdList);
	}

	@GetMapping("{id}")
	@PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.USER_VIEW + "\")")
	public ResponseEntity<UserDTO> getUser(@PathVariable Long id) {
		log.debug("REST request to get Role : {}", id);
		Optional<UserDTO> userDTO = userQueryService.findOne(id);
		return ResponseUtil.wrapOrNotFound(userDTO);
	}

	@PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.USER_DEACTIVATE + "\")")
	@PutMapping("deactivate")
	public void deactivate(@RequestBody UserDeactivationRequest userDeactivationRequest) {
		log.debug("Request to deactivate user {}", userDeactivationRequest);
		userService.deactivate(userDeactivationRequest.getUserId(), userDeactivationRequest.getIsPermanent());
	}
 
	@PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.USER_REACTIVATE + "\")")
	@PutMapping("reactivate/{id}")
	public void reactivate(@PathVariable Long id) {
		log.debug("Request to deactivate user {}", id);
		userService.reactivate(id);
	}
	
	@GetMapping(value = "/getUserSpecificCompanyDetails")
	public UserSpecificCBResponse getUserSpecificCompanyDetails() {
		log.info("Request to getUserSpecificCompanyDetails api");
		return userService.getUserSpecificCompanyDetails();
	}
	 
	@GetMapping("/getClientIdByLoggedInUser")
	public ClientIdDTO getClientIdAsPerLogin() {
		log.info("Request to get Client Id as per logged in User");
		return userService.getClientIdAsPerLogggedInUser();
	}
	
	@PostMapping("/checkEmailPresent")
	public EmailPresentDto checkEmailPresent(@RequestBody List<String> emailIds) {
		log.info("list of email to check : ", emailIds);
		return userService.checkEmailPresent(emailIds);
	}
	
	@GetMapping(value = "/getUserSpecificCompanies")
	public List<UserSpecificCompanyDTO> getUserSpecificCompanies() {
		log.info("Request to getUserSpecificCompanies");
		return userService.getUserSpecificCompanies();
	}
	
	@GetMapping(value = "/getUserSpecificBranches/{companyId}")
	public List<UserSpecificBranchDTO> getUserSpecificBranches(@PathVariable Long companyId) {
		log.info("Request to getUserSpecificBranches {}",companyId);
		return userService.getUserSpecificBranches(companyId);
	}
	
	@PostMapping(value = "/getUsersByRoleIds")
	public void getUsersByRoleIds(@RequestBody ApprovalRequestDTO approvalRequestDTO) {
		log.info("Request to getUsersByRoleIds {}",approvalRequestDTO);
		userService.getUsersByRoleIds(approvalRequestDTO);
	}
	
	@PostMapping("/getUserApprovalRequestData") 
	public UserApprovalRequestDTO getUserApprovalRequestData(@RequestBody UserApprovalRequestDTO userApprovalRequestDTO){
		log.info("REST to getUserApprovalRequestData {}",userApprovalRequestDTO);
		return userQueryService.getUserApprovalRequestSentData(userApprovalRequestDTO);	
	}
	
	@PostMapping("/saveUserApprovalRequestData") 
	public void saveUserApprovalRequestData(@RequestBody UserApprovalRequestDTO userApprovalRequestDTO){
		log.info("REST to saveUserApprovalRequestData {}",userApprovalRequestDTO);
		userService.saveUserApprovalRequestSentData(userApprovalRequestDTO);	
	}
	@PostMapping("validateCsaUsers")
	public String validateCsaUsers(@RequestBody CompanyDTO companyDTO) throws MessagingException {
	    return userService.validateCsaUsers(companyDTO);
	}
	
}
