package com.grt.elogfrieght.services.user.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.auth.InvalidCredentialsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.grt.elogfrieght.services.user.domain.User;
import com.grt.elogfrieght.services.user.dto.AuthResponseDto;
import com.grt.elogfrieght.services.user.repository.UserRepository;
import com.grt.elogfrieght.services.user.service.AuthService;
import com.grt.elogfrieght.services.user.serviceimpl.AuthServiceImplTest;
import com.grt.elogfrieght.services.user.serviceimpl.UserServiceImpl;
import com.grt.elogfrieght.services.user.serviceimpl.UserServiceImplTest;
import com.grtship.core.dto.UserCreationRequestDTO;

import javassist.tools.rmi.ObjectNotFoundException;

@SpringBootTest(properties = "config.caching.enabled=false")
@Transactional
@ActiveProfiles(profiles = { "test" })
public class JwtTokenUtilTest {
	
	private static final String INVALID_TOKEN = "eyJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJ0ZXN0LnVzZXJAZ21haWwuY29tI"
			+ "iwiZXhwIjoxNjM1NDEwOTUyLCJpc3MiOiIyNDE0OCJ9.kOx9146rZ4C0SyjhyWG4R4Zv1bSdA4Z1flOTo0lEIDx"
			+ "E8WSKEIw9SPXmOciICJXJJdsT9PTnK73oDs2T6CJnhnDNwGztcDoOnCuumKvFzzlXLiNWOv100gUGYMAFw7kG3wQ8RD"
			+ "lB1SFplvl4vhJfs5z94PUZtA9AJ2HT-KeOydZKfPyuALyy_9jCKL-e6fdFZS9BqrfOCtKujUPHFOp4arhj75FTYdLyDko"
			+ "H4Xgp8Ac_jKnlvSxqsjZZILmvMwDN-IaeMRzIVUYsDSelmmvuoaItR3tt8N1O0OeNECKcWj36KE-iiPJkqa_JOZs0p"
			+ "zgQTojTPzzke5QNiOunZt6zNw";
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private UserServiceImpl userService;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private AuthService authService;
	@Autowired
	private JwtTokenUtil jwtTokenUtil;
	@Autowired
	private JwtGenerator tokenUtil;

	private UserCreationRequestDTO creationRequestDTO;
	private Map<String, String> params;
	private AuthResponseDto authResponseDto;
	private User savedUser;

	@BeforeEach
	public void setUp() throws ObjectNotFoundException, InvalidCredentialsException, NoSuchAlgorithmException {
		creationRequestDTO = UserServiceImplTest.createUserDto();
		params = AuthServiceImplTest.prepareLoginDetails();
		saveUserAsPerRequirement();
		HttpServletResponse httpServletResponse = new MockHttpServletResponse();
		HttpServletRequest httpServletRequest = new MockHttpServletRequest();
		authResponseDto = authService.authenticate(httpServletRequest, httpServletResponse, params);
	}

	private void saveUserAsPerRequirement() throws ObjectNotFoundException {
		savedUser = setCreationRequestAsRequired();
		savedUser.setPassword(passwordEncoder.encode("user1"));
		savedUser.setActivated(true);
		userRepository.save(savedUser);
	}

	private User setCreationRequestAsRequired() throws ObjectNotFoundException {
		creationRequestDTO.setActiveFlag(Boolean.TRUE);
		creationRequestDTO.setAllowLogin(Boolean.TRUE);
		creationRequestDTO.setIsDeactivated(Boolean.FALSE);
		savedUser = userService.createUser(creationRequestDTO);
		return savedUser;
	}

	@Test
	void testGetUsernameFromAccessToken() throws NoSuchAlgorithmException, InvalidKeySpecException {
		String username = jwtTokenUtil.getUsernameFromAccessToken(authResponseDto.getAccesstoken());
		System.out.println("Access Token response" + authResponseDto.getAccesstoken());
		assertThat(username).isNotNull();
		assertThat(username).isEqualTo(savedUser.getEmail());
	}

	@Test
	void testGetUsernameFromAccessTokenForInvalidToken() throws NoSuchAlgorithmException, InvalidKeySpecException {
		assertThrows(io.jsonwebtoken.SignatureException.class, () -> {
			jwtTokenUtil.getUsernameFromAccessToken(INVALID_TOKEN);
		});
	}

	@Test
	void testGetExpirationDateFromToken() throws NoSuchAlgorithmException, InvalidKeySpecException {
		Date date = jwtTokenUtil.getExpirationDateFromToken(authResponseDto.getAccesstoken());
		assertThat(date).isNotNull();
		System.out.println("Expiration Date " + date);
	}

	@Test
	void testIsTokenExpired() {
		Boolean tokenExpired = jwtTokenUtil.isTokenExpired(authResponseDto.getAccesstoken());
		assertThat(tokenExpired).isNotNull();
		assertFalse(tokenExpired);
	}

	@Test
	void testValidateToken() {
		Boolean validateToken = jwtTokenUtil.validateToken(authResponseDto.getAccesstoken());
		assertThat(validateToken).isNotNull();
		assertFalse(validateToken);
	}

	@Test
	void testValidateRefreshToken() throws NoSuchAlgorithmException {
		Map<String, Object> claims = new HashMap<>();
		String refreshToken = tokenUtil.buildRefreshToken(savedUser.getEmail(), String.valueOf(savedUser.getId()),
				claims);
		assertThat(refreshToken).isNotNull();
	}

	@Test
	void testIsRefreshTokenExpired() throws NoSuchAlgorithmException {
		Map<String, Object> claims = new HashMap<>();
		String refreshToken = tokenUtil.buildRefreshToken(savedUser.getEmail(), String.valueOf(savedUser.getId()),
				claims);
		Boolean refreshTokenExpired = jwtTokenUtil.isRefreshTokenExpired(refreshToken);
		assertFalse(refreshTokenExpired);
	}

	@Test
	void testGetExpirationDateFromRefreshToken() throws NoSuchAlgorithmException, InvalidKeySpecException {
		Map<String, Object> claims = new HashMap<>();
		String refreshToken = tokenUtil.buildRefreshToken(savedUser.getEmail(), String.valueOf(savedUser.getId()),
				claims);
		Date date = jwtTokenUtil.getExpirationDateFromRefreshToken(refreshToken);
		assertThat(date).isNotNull();
	}

}
