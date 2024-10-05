package com.grt.elogfrieght.services.user.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.security.NoSuchAlgorithmException;
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
public class EncryptionUtilTest {

	@Autowired private UserRepository userRepository;
	@Autowired private UserServiceImpl userService;
	@Autowired private PasswordEncoder passwordEncoder;
	@Autowired private AuthService authService;
	@Autowired private EncryptionUtil encryptionUtil;
    
    private UserCreationRequestDTO creationRequestDTO;
	private Map<String, String> params;
	
	@BeforeEach
	public void setUp() {
		creationRequestDTO=UserServiceImplTest.createUserDto();
        params=AuthServiceImplTest.prepareLoginDetails();
	}

	@Test
	void testEncrptionUsingAes() throws InvalidCredentialsException, NoSuchAlgorithmException, ObjectNotFoundException {
		AuthResponseDto authenticate=prepareAndAuthenticateUser();
		String encryptedToken=encryptionUtil.encrptionUsingAes(authenticate.getAccesstoken());
		assertThat(encryptedToken).isNotNull();
		assertThat(encryptedToken).isNotEqualTo(authenticate.getAccesstoken());
	}
	
	private AuthResponseDto prepareAndAuthenticateUser() throws ObjectNotFoundException, NoSuchAlgorithmException, InvalidCredentialsException {
		saveUserAsPerRequirement();
		HttpServletResponse httpServletResponse = new MockHttpServletResponse();
		HttpServletRequest httpServletRequest = new MockHttpServletRequest();
		AuthResponseDto authenticate = authService.authenticate(httpServletRequest, httpServletResponse, params);
		return authenticate;
	}
	
	private void saveUserAsPerRequirement() throws ObjectNotFoundException {
		User savedUser = setCreationRequestAsRequired();
		savedUser.setPassword(passwordEncoder.encode("user1"));
		savedUser.setActivated(true);
		userRepository.save(savedUser);
	}
	
	private User setCreationRequestAsRequired() throws ObjectNotFoundException {
		creationRequestDTO.setActiveFlag(Boolean.TRUE);
		creationRequestDTO.setAllowLogin(Boolean.TRUE);
		creationRequestDTO.setIsDeactivated(Boolean.FALSE);
		User savedUser = userService.createUser(creationRequestDTO);
		return savedUser;
	}

}
