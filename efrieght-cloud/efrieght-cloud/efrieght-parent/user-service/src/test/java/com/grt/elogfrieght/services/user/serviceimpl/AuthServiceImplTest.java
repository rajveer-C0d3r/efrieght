package com.grt.elogfrieght.services.user.serviceimpl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.auth.InvalidCredentialsException;
import org.apache.kafka.common.errors.AuthorizationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.grt.elogfrieght.services.user.domain.User;
import com.grt.elogfrieght.services.user.dto.AuthResponseDto;
import com.grt.elogfrieght.services.user.repository.UserRepository;
import com.grt.elogfrieght.services.user.service.AuthService;
import com.grtship.common.exception.InvalidDataException;
import com.grtship.core.dto.LoggedInUserDto;
import com.grtship.core.dto.UserCreationRequestDTO;
import com.grtship.core.dto.UserDetailDto;
import com.grtship.core.enumeration.UserType;

import javassist.tools.rmi.ObjectNotFoundException;

@SpringBootTest(properties = "config.caching.enabled=false")
@Transactional
@ActiveProfiles(profiles = { "test" })
public class AuthServiceImplTest {
	
	private static final String BEARER = "Bearer";
	private static final String USER_TYPE = "userType";
	private static final String PASSWORD = "password";
	private static final String EMAIL = "email";
	@Autowired private UserRepository userRepository;
	@Autowired private UserServiceImpl userService;
	@Autowired private PasswordEncoder passwordEncoder;
	@Autowired private AuthService authService;
    
    private UserCreationRequestDTO creationRequestDTO;
	private Map<String, String> params;
	
	public static Map<String, String> prepareLoginDetails() {
		Map<String, String> params=new HashMap<>();
		params.put(EMAIL,UserServiceImplTest.createUserDto().getEmail());
		params.put(PASSWORD, "user1");
		params.put(USER_TYPE,UserType.CLIENT.toString());
		return params;
	}
	
	@BeforeEach
	public void setUp() {
		creationRequestDTO=UserServiceImplTest.createUserDto();
        params=prepareLoginDetails();
	}

	@Test
	void testAuthenticate() throws ObjectNotFoundException, InvalidCredentialsException, NoSuchAlgorithmException {
		saveUserAsPerRequirement();
		HttpServletResponse httpServletResponse = new MockHttpServletResponse();
		HttpServletRequest httpServletRequest = new MockHttpServletRequest();
		AuthResponseDto authResponseDto = authService.authenticate(httpServletRequest, httpServletResponse, params);
		assertThat(authResponseDto).isNotNull();
		assertThat(authResponseDto.getTokenType()).isEqualTo(BEARER);
		assertThat(authResponseDto.getAccesstoken()).isNotNull();
	}

	private void saveUserAsPerRequirement() throws ObjectNotFoundException {
		User savedUser = setCreationRequestAsRequired();
		savedUser.setPassword(passwordEncoder.encode("user1"));
		savedUser.setActivated(true);
		userRepository.save(savedUser);
	}
	
	@Test
	void checkIfUserEmailIsValid()
			throws ObjectNotFoundException, InvalidCredentialsException, NoSuchAlgorithmException {
		params.put(EMAIL, "jayeshjain");
		assertThrows(InvalidDataException.class, () -> {
			authService.authenticate(null, null, params);
		});
	}
	
	@Test
	void checkIfUserEmailIsNotEmpty()
			throws ObjectNotFoundException, InvalidCredentialsException, NoSuchAlgorithmException {
		params.put(EMAIL, null);
		assertThrows(InvalidDataException.class, () -> {
			authService.authenticate(null, null, params);
		});
	}
	
	@Test
	void checkIfUserPasswordIsNotEmpty()
			throws ObjectNotFoundException, InvalidCredentialsException, NoSuchAlgorithmException {
		params.put(PASSWORD, null);
		assertThrows(InvalidDataException.class, () -> {
			authService.authenticate(null, null, params);
		});
	}
	
	@Test
	void checkIfUserIsValid()
			throws ObjectNotFoundException, InvalidCredentialsException, NoSuchAlgorithmException {
		assertThrows(UsernameNotFoundException.class, () -> {
			authService.authenticate(null, null, params);
		});
	}
	
	@Test
	void checkIfUserPasswordIsCorrect()
			throws ObjectNotFoundException, InvalidCredentialsException, NoSuchAlgorithmException {
		User savedUser = setCreationRequestAsRequired();
		savedUser.setActivated(true);
		userRepository.save(savedUser);
		assertThrows(InvalidDataException.class, () -> {
			authService.authenticate(null, null, params);
		});
	}
	
	@Test
	void checkIfUserTypeIsCorrect()
			throws ObjectNotFoundException, InvalidCredentialsException, NoSuchAlgorithmException {
		User savedUser = setCreationRequestAsRequired();
		savedUser.setPassword(passwordEncoder.encode("user1"));
		savedUser.setUserType(UserType.ADMIN);
		savedUser.setActivated(true);
		userRepository.save(savedUser);
		assertThrows(AuthorizationException.class, () -> {
			authService.authenticate(null, null, params);
		});
	}
	
	private User setCreationRequestAsRequired() throws ObjectNotFoundException {
		creationRequestDTO.setActiveFlag(Boolean.TRUE);
		creationRequestDTO.setAllowLogin(Boolean.TRUE);
		creationRequestDTO.setIsDeactivated(Boolean.FALSE);
		User savedUser = userService.createUser(creationRequestDTO);
		return savedUser;
	}
	
	@Test
	void testGetLoggedInUser() throws ObjectNotFoundException, InvalidCredentialsException, NoSuchAlgorithmException {
		saveUserAsPerRequirement();
		HttpServletResponse httpServletResponse = new MockHttpServletResponse();
		HttpServletRequest httpServletRequest = new MockHttpServletRequest();
		authService.authenticate(httpServletRequest, httpServletResponse, params);
		LoggedInUserDto loggedInUserDto = authService.getLoggedInUser();
		assertThat(loggedInUserDto).isNotNull();
		assertThat(loggedInUserDto.getId()).isNotNull();
		assertThat(loggedInUserDto.getEmail()).isEqualTo(creationRequestDTO.getEmail());
		assertThat(loggedInUserDto.getClientId()).isEqualTo(creationRequestDTO.getClientId());
	}

	@Test
	void testGetUserDetails() throws ObjectNotFoundException {
		User savedUser = userService.createUser(creationRequestDTO);
		UserDetailDto userDetailDto=authService.getUserDetails(savedUser.getEmail());
		assertThat(userDetailDto.getCommmaSeperatedAuthority()).isNotNull();
		assertThat(userDetailDto.getEmail()).isEqualTo(savedUser.getEmail());
	}
	
	@Test
	public void testGetUserDetailsIfEmailNotExists() throws ObjectNotFoundException{
		userService.createUser(creationRequestDTO);
		UserDetailDto userDetailDto=authService.getUserDetails("yuv@loaclhost.com");
		assertThat(userDetailDto.getCommmaSeperatedAuthority()).isNull();;
		assertThat(userDetailDto.getEmail()).isNull();
	}

	@Test
	void testGetUserAuthority() throws ObjectNotFoundException {
		userService.createUser(creationRequestDTO);
		authService.getUserAuthority();
	}

}
