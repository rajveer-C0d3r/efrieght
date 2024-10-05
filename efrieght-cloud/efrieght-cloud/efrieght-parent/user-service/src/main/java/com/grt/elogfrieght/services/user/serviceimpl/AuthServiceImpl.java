/**
 * 
 */
package com.grt.elogfrieght.services.user.serviceimpl;

import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.auth.InvalidCredentialsException;
import org.apache.kafka.common.errors.AuthorizationException;
import org.hibernate.validator.internal.constraintvalidators.hv.EmailValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.grt.elogfrieght.services.user.domain.Authority;
import com.grt.elogfrieght.services.user.domain.User;
import com.grt.elogfrieght.services.user.domain.UserLoginDetails;
import com.grt.elogfrieght.services.user.domain.UserRoles;
import com.grt.elogfrieght.services.user.dto.AuthResponseDto;
import com.grt.elogfrieght.services.user.repository.UserLoginRepository;
import com.grt.elogfrieght.services.user.secuirty.DomainUserDetailsService;
import com.grt.elogfrieght.services.user.service.AuthService;
import com.grt.elogfrieght.services.user.service.UserService;
import com.grt.elogfrieght.services.user.util.EncryptionUtil;
import com.grt.elogfrieght.services.user.util.JwtGenerator;
import com.grt.elogfrieght.services.user.util.SecurityUtils;
import com.grtship.common.exception.BadCredentialException;
import com.grtship.common.exception.InvalidDataException;
import com.grtship.common.exception.UserDisabledException;
import com.grtship.common.exception.UserNotFoundException;
import com.grtship.core.constant.ErrorCode;
import com.grtship.core.dto.LoggedInUserDto;
import com.grtship.core.dto.UserAuthorityDto;
import com.grtship.core.dto.UserDetailDto;
import com.grtship.core.exception.RequestException;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {

	private final DomainUserDetailsService domainUserDetailsService;
	private final AuthenticationManager authenticationManager;
	private final JwtGenerator tokenUtil;
	private final EncryptionUtil encryptionUtil;
	private final UserService userRepository;
	private final PasswordEncoder passwordEncoder;
	private final UserLoginRepository loginRepository;
	private final RoleQueryServiceImpl roleService;
	private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);
	private static final String INVALID_CREDENTIALS="Invalid Credentials";

	@Override
	public AuthResponseDto authenticate(HttpServletRequest request, HttpServletResponse response,
			Map<String, String> params) throws NoSuchAlgorithmException, InvalidCredentialsException {
		log.info("Inside AuthServiceImpl authenticate method called with login parameter : {} ", params);
		Map<String, Object> claims = new HashMap<>();
		String accessToken = "";
		String username = params.get("email");
		String password = params.get("password");
		String userType = params.get("userType");
		userCredentialValidation(username, password);
		UserDetails userDetails = domainUserDetailsService.loadUserByUsername(username);
		if (userDetails == null) {
			throw new UserNotFoundException("user not found with the given login Id : " + username);
		}
		Optional<User> user = userRepository.findOneWithAuthoritiesByEmailIgnoreCase(username);
		validateUser(password, userType, user);
		authenticate(username, password, userDetails.getAuthorities());
		accessToken = getAccessToken(user.get(), claims);
		String refreshToken = getRefreshToken(claims, user.get());
		response.addCookie(new Cookie("refreshToken", refreshToken));
		saveloginDetails(request, user.get(), refreshToken);
		return new AuthResponseDto(accessToken, "Bearer");
	}

	private void userCredentialValidation(String username, String password) {
		if (!new EmailValidator().isValid(username, null)) {
			throw new InvalidDataException(ErrorCode.INVALID_DATA_ERROR, INVALID_CREDENTIALS);
		}
		if (StringUtils.isEmpty(username)) {
			throw new InvalidDataException(ErrorCode.INVALID_DATA_ERROR, "Login Id cannot be empty");
		}
		if (StringUtils.isEmpty(password)) {
			throw new InvalidDataException(ErrorCode.INVALID_DATA_ERROR, "Password cannot be empty");
		}
	}

	private void validateUser(String password, String userType, Optional<User> user)
			throws InvalidCredentialsException {
		if (!user.isPresent()) {
			throw new InvalidDataException(ErrorCode.INVALID_DATA_ERROR, INVALID_CREDENTIALS);
		}
		if (!passwordEncoder.matches(password, user.get().getPassword())) {
			throw new InvalidDataException(ErrorCode.INVALID_DATA_ERROR,INVALID_CREDENTIALS);
		}
		if (!StringUtils.equalsIgnoreCase(userType, user.get().getUserType().toString())) {
			throw new AuthorizationException("You are not authorized to login into this system");
		}
	}

	private String getRefreshToken(Map<String, Object> claims, User user) throws NoSuchAlgorithmException {
		return tokenUtil.buildRefreshToken(user.getEmail(), String.valueOf(user.getId()), claims);
	}

	private String getAccessToken(User user, Map<String, Object> claims) throws NoSuchAlgorithmException {
		return tokenUtil.buildToken(user.getEmail(), String.valueOf(user.getId()), claims);
	}

	private void saveloginDetails(HttpServletRequest request, User user, String refreshToken) {
		UserLoginDetails loginDetails = new UserLoginDetails();
		loginDetails.setUserId(user.getId());
		loginDetails.setIpAddress(request.getLocalAddr());
		loginDetails.setToken(encryptionUtil.encrptionUsingAes(refreshToken));
		loginDetails.setLoggedOut(Boolean.FALSE);
		loginDetails.setUrl(String.valueOf(request.getRequestURL()));
		loginDetails.setLoggedInTimestamp(LocalDateTime.now());
		loginRepository.save(loginDetails);
	}

	private void authenticate(String username, String password, Collection<? extends GrantedAuthority> authority) {
		try {
			Authentication authenticate = authenticationManager
					.authenticate(new UsernamePasswordAuthenticationToken(username, password, authority));
			SecurityContextHolder.getContext().setAuthentication(authenticate);
		} catch (DisabledException e) {
			throw new UserDisabledException("USER_DISABLED", e);
		} catch (BadCredentialsException e) {
			throw new BadCredentialException("INVALID_CREDENTIALS", e);
		}
	}

	@Override
	public AuthResponseDto renewToken(String refreshToken, HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse) throws NoSuchAlgorithmException {
		String accessToken = "";
		Map<String, Object> map = new HashMap<>();
		Optional<UserLoginDetails> optionalLoginDetails = loginRepository
				.findByTokenAndLoggedOut(encryptionUtil.encrptionUsingAes(refreshToken), Boolean.FALSE);
		if (!optionalLoginDetails.isPresent()) {
			throw new RequestException("user is already logged out");
		}
		UserLoginDetails loginDetails = optionalLoginDetails.get();
		Optional<User> optionalUserMaster = userRepository.findById(loginDetails.getUserId());
		if (optionalUserMaster.isPresent()) {
			User userMaster = optionalUserMaster.get();
			Map<String, Object> claims = new HashMap<>();
			map.put("userId", userMaster.getId());
			accessToken = tokenUtil.buildToken(userMaster.getEmail(), String.valueOf(userMaster.getId()), claims);
		}
		return new AuthResponseDto(accessToken, "Bearer");
	}

	@Override
	public LoggedInUserDto getLoggedInUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		log.info("authenticate object : {} ", authentication);
		String email =  ((UserDetails)authentication.getPrincipal()).getUsername();
		log.info("principle name : {}", email);
		User user = userRepository.findByEmail(email);
		LoggedInUserDto loggedInUserDto = new LoggedInUserDto();
		BeanUtils.copyProperties(user, loggedInUserDto);
		log.info("loggedIn user details : {}", loggedInUserDto);
		return loggedInUserDto;
	}

	@Override
	public UserDetailDto getUserDetails(String username) {
		Optional<User> user = userRepository.findOneWithAuthoritiesByEmailIgnoreCase(username);
		UserDetailDto userDetailDto = new UserDetailDto();
		if (user.isPresent()) {
			User userDetails = user.get();
			BeanUtils.copyProperties(userDetails, userDetailDto);
			List<String> grantedAuthorities = new ArrayList<>();
			grantedAuthorities = userDetails.getAuthorities().stream().map(Authority::getName)
					.collect(Collectors.toList());
			grantedAuthorities.addAll(roleService
					.getRolePermissionsByIds(
							userDetails.getUserRoles().stream().filter(obj -> obj != null && obj.getRoleId() != null)
									.map(UserRoles::getRoleId).collect(Collectors.toList()))
					.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()));
			if (CollectionUtils.isNotEmpty(grantedAuthorities)) {
				userDetailDto.setCommmaSeperatedAuthority(grantedAuthorities.stream().collect(Collectors.joining(",")));
				log.debug("all granted authority of user : {} ", grantedAuthorities);
			}
		}
		return userDetailDto;
	}

	/**
	 * @apiNote this service will only provide authority_role
	 */
	@Override
	public UserAuthorityDto getUserAuthority() {
		Optional<User> optionalUser = SecurityUtils.getCurrentUserLogin()
				.flatMap(userRepository::findOneWithAuthoritiesByLogin);
		UserAuthorityDto authorityDto = new UserAuthorityDto();
		if (optionalUser.isPresent()) {
			User user = optionalUser.get();
			if (CollectionUtils.isNotEmpty(user.getAuthorities()))
				authorityDto.setAuthorities(
						user.getAuthorities().stream().map(Authority::getName).collect(Collectors.toList()));
			if (CollectionUtils.isNotEmpty(user.getUserRoles())) {
				List<Long> roleIds = user.getUserRoles().stream()
						.filter(role -> ObjectUtils.isNotEmpty(role.getRoleId())).map(UserRoles::getRoleId)
						.collect(Collectors.toList());
				authorityDto.setPermission(roleService.getPermissionMapByUserRoles(roleIds));
			}
		}
		log.info("User authority details {} ", authorityDto);
		return authorityDto;
	}

}
