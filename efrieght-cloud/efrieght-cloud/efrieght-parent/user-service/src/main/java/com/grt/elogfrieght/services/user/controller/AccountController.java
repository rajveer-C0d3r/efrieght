package com.grt.elogfrieght.services.user.controller;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.MessagingException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.grt.elogfrieght.services.user.domain.User;
import com.grt.elogfrieght.services.user.mapper.UserMapper;
import com.grt.elogfrieght.services.user.service.UserService;
import com.grt.elogfrieght.services.user.serviceimpl.UserQueryServiceImpl;
import com.grt.elogfrieght.services.user.vm.KeyAndPasswordVM;
import com.grt.elogfrieght.services.user.vm.ManagedUserVM;
import com.grtship.common.exception.EmailAlreadyUsedException;
import com.grtship.common.exception.InvalidPasswordException;
import com.grtship.common.exception.LoginAlreadyUsedException;
import com.grtship.common.service.KafkaProducerService;
import com.grtship.core.constant.KafkaTopicConstant;
import com.grtship.core.dto.NotificationDTO;
import com.grtship.core.dto.PasswordChangeDTO;
import com.grtship.core.dto.PasswordChangeRequest;
import com.grtship.core.dto.StringResponse;
import com.grtship.core.dto.UserDTO;

/**
 * REST controller for managing the current user's account.
 */
@RestController
@RequestMapping("/api")
public class AccountController {

	private static class AccountResourceException extends RuntimeException {
		private static final long serialVersionUID = 904423376289911934L;

		private AccountResourceException(String message) {
			super(message);
		}
	}

	private final Logger log = LoggerFactory.getLogger(AccountController.class);

	private final UserService userService;

	
	private final KafkaProducerService kafkaProducerService;
	
	private final UserMapper userMapper;

	@Autowired
	private UserQueryServiceImpl userQueryService;

	public AccountController(UserService userService, KafkaProducerService kafkaProducerService,
			UserMapper userMapper) {

		this.userService = userService;
		this.kafkaProducerService=kafkaProducerService;
		this.userMapper=userMapper;
	}

	/**
	 * {@code POST  /register} : register the user.
	 *
	 * @param managedUserVM the managed user View Model.
	 * @throws MessagingException
	 * @throws InvalidPasswordException  {@code 400 (Bad Request)} if the password
	 *                                   is incorrect.
	 * @throws EmailAlreadyUsedException {@code 400 (Bad Request)} if the email is
	 *                                   already used.
	 * @throws LoginAlreadyUsedException {@code 400 (Bad Request)} if the login is
	 *                                   already used.
	 */
	@PostMapping("/register")
	@ResponseStatus(HttpStatus.CREATED)
	public void registerAccount(@Valid @RequestBody ManagedUserVM managedUserVM) throws MessagingException {
		if (!checkPasswordLength(managedUserVM.getPassword())) {
			throw new InvalidPasswordException();
		}
		User user = userService.registerUser(managedUserVM, managedUserVM.getPassword());
		sendEmailByKafka(user,KafkaTopicConstant.KAFKA_USER_NOTIFICATION_TOPIC);
	}

	/**
	 * {@code GET  /activate} : activate the registered user.
	 *
	 * @param key the activation key.
	 * @throws RuntimeException {@code 500 (Internal Server Error)} if the user
	 *                          couldn't be activated.
	 */
	@GetMapping(value = "/activate", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<StringResponse> activateAccount(@RequestParam(value = "key") String key) {
		Optional<User> user = userService.activateRegistration(key);
		if (!user.isPresent()) {
			throw new AccountResourceException("No user was found for this activation key");
		}
		return ResponseEntity.ok().body(new StringResponse(user.get().getResetKey()));
	}

	/**
	 * {@code GET  /authenticate} : check if the user is authenticated, and return
	 * its login.
	 *
	 * @param request the HTTP request.
	 * @return the login if the user is authenticated.
	 */
	@GetMapping(value = "/authenticate", produces = MediaType.TEXT_PLAIN_VALUE)
	public ResponseEntity<String> isAuthenticated(HttpServletRequest request) {
		log.debug("REST request to check if the current user is authenticated");
		return ResponseEntity.ok().body(request.getRemoteUser());
	}

	/**
	 * {@code GET  /account} : get the current user.
	 *
	 * @return the current user.
	 * @throws RuntimeException {@code 500 (Internal Server Error)} if the user
	 *                          couldn't be returned.
	 */
	@GetMapping("/account")
	public UserDTO getAccount() {
		return userQueryService.getUserWithAuthorities()
				.orElseThrow(() -> new AccountResourceException("User could not be found"));
	}

	/**
	 * {@code POST  /account/change-password} : changes the current user's password.
	 *
	 * @param passwordChangeDto current and new password.
	 * @throws InvalidPasswordException {@code 400 (Bad Request)} if the new
	 *                                  password is incorrect.
	 */
	@PostMapping(path = "/account/change-password")
	public ResponseEntity<StringResponse> changePassword(@RequestBody PasswordChangeDTO passwordChangeDto) {
		if (!checkPasswordLength(passwordChangeDto.getNewPassword())) {
			throw new InvalidPasswordException();
		}
		userService.changePassword(passwordChangeDto.getCurrentPassword(), passwordChangeDto.getNewPassword());
		return ResponseEntity.ok().body(new StringResponse("password changes successfully"));
	}

	/**
	 * {@code POST   /account/reset-password/init} : Send an email to reset the
	 * password of the user.
	 *
	 * @param mail the mail of the user.
	 * @throws MessagingException
	 */
	@PostMapping(path = "/account/reset-password/init")
	public ResponseEntity<StringResponse> requestPasswordReset(@RequestBody PasswordChangeRequest passwordChangeRequest)
			throws MessagingException {
		log.debug("Request to change password {}", passwordChangeRequest.getMail());
		Optional<User> user = userService.requestPasswordReset(passwordChangeRequest.getMail());
		log.debug("User found by mail {} {}", user.isPresent(), user);
		if (!user.isPresent()) {
			log.warn("Password reset requested for non existing mail");
			throw new UsernameNotFoundException("User not found with given email Id " + passwordChangeRequest.getMail());
		}
		sendEmailByKafka(user.get(),KafkaTopicConstant.KAFKA_RESET_PASSWORD_TOPIC);
		return ResponseEntity.ok().body(new StringResponse("forget password mail send successfully"));
	}
	

	/**
	 * {@code POST   /account/reset-password/finish} : Finish to reset the password
	 * of the user.
	 *
	 * @param keyAndPassword the generated key and the new password.
	 * @throws InvalidPasswordException {@code 400 (Bad Request)} if the password is
	 *                                  incorrect.
	 * @throws RuntimeException         {@code 500 (Internal Server Error)} if the
	 *                                  password could not be reset.
	 */
	@PostMapping(path = "/account/reset-password/finish")
	public ResponseEntity<StringResponse> finishPasswordReset(@RequestBody KeyAndPasswordVM keyAndPassword) {
		if (!checkPasswordLength(keyAndPassword.getNewPassword())) {
			throw new InvalidPasswordException();
		}
		Optional<User> user = userService.completePasswordByResetKeyAndVerficationCode(keyAndPassword.getNewPassword(),
				keyAndPassword.getKey(), keyAndPassword.getVerficationCode());
		if (!user.isPresent()) {
			throw new AccountResourceException("No user was found for this set key");
		}
		return ResponseEntity.ok().body(new StringResponse("password set successfully"));
	}
	

	private static boolean checkPasswordLength(String password) {
		return !StringUtils.isEmpty(password) && password.length() >= ManagedUserVM.PASSWORD_MIN_LENGTH
				&& password.length() <= ManagedUserVM.PASSWORD_MAX_LENGTH;
	}
	
	private void sendEmailByKafka(User newUser,String kafkaTopicConstant) {
		NotificationDTO notificationDto=new NotificationDTO();
		if (!ObjectUtils.isEmpty(newUser)) {
			notificationDto=userMapper.toNotificationDto(newUser);
		}

		if (!ObjectUtils.isEmpty(notificationDto)) {
			kafkaProducerService.sendMessage(kafkaTopicConstant,
					new Gson().toJson(notificationDto));
		}
	}
	
}
