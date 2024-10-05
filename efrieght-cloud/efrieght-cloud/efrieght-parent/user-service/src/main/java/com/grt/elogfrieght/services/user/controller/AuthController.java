/**
 * 
 */
package com.grt.elogfrieght.services.user.controller;

import java.security.NoSuchAlgorithmException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.auth.InvalidCredentialsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.grt.elogfrieght.services.user.dto.AuthResponseDto;
import com.grt.elogfrieght.services.user.service.AuthService;
import com.grtship.core.dto.LoggedInUserDto;
import com.grtship.core.dto.UserAuthorityDto;
import com.grtship.core.dto.UserDetailDto;

/**
 * @author Ajay
 *
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

	private static final Logger log = LoggerFactory.getLogger(AuthController.class);

	@Autowired
	private AuthService authService;

	@PostMapping(value = "/login",consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<AuthResponseDto> requestMethodName(HttpServletRequest request, HttpServletResponse response,
			@RequestBody Map<String, String> params) throws NoSuchAlgorithmException, InvalidCredentialsException {
		log.info("request for login : {} ", params);
		AuthResponseDto authenticate = authService.authenticate(request, response, params);
		return ResponseEntity.ok(authenticate);
	}

	@PostMapping(value = "/renewToken")
	public ResponseEntity<AuthResponseDto> renewToken(@RequestParam String refreshToken,
			HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse)
			throws NoSuchAlgorithmException {
		AuthResponseDto authenticate = authService.renewToken(refreshToken, httpServletRequest, httpServletResponse);
		return ResponseEntity.ok(authenticate);
	}

	@GetMapping(value = "/getLoggedInUser")
	public ResponseEntity<LoggedInUserDto> getLoggedInUser() {
		LoggedInUserDto loggedInUser = authService.getLoggedInUser();
		return ResponseEntity.ok(loggedInUser);
	}

	@GetMapping(value = "/getUserDetails")
	public ResponseEntity<UserDetailDto> getUserDetails(@RequestParam String username) {
		log.info("request for getUserDetails : {} ", username);
		UserDetailDto userDetails = authService.getUserDetails(username);
		return ResponseEntity.ok(userDetails);
	}
	
	@GetMapping(value = "/getUserAuthority")
	public ResponseEntity<UserAuthorityDto> getUserAuthority() {
		UserAuthorityDto authorityDto = authService.getUserAuthority();
		return ResponseEntity.ok(authorityDto);
	}


}
