package com.grt.elogfrieght.services.user.secuirty;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.grt.elogfrieght.services.user.domain.User;
import com.grt.elogfrieght.services.user.repository.UserRepository;
import com.grt.elogfrieght.services.user.serviceimpl.UserServiceImpl;
import com.grt.elogfrieght.services.user.serviceimpl.UserServiceImplTest;
import com.grtship.common.exception.AccountLockedException;
import com.grtship.common.exception.UserNotActivatedException;
import com.grtship.core.dto.UserCreationRequestDTO;

import javassist.tools.rmi.ObjectNotFoundException;

@SpringBootTest(properties = "config.caching.enabled=false")
@Transactional
@ActiveProfiles(profiles = { "test" })
public class DomainUserDetailsServiceTest {
	
	@Autowired private UserRepository userRepository;
	@Autowired private UserServiceImpl userService;
    @Mock Pageable pageable;
    @Autowired private DomainUserDetailsService domainUserDetailsService;    
    private UserCreationRequestDTO creationRequestDTO;

	@BeforeEach
	void setUp() {
		creationRequestDTO=UserServiceImplTest.createUserDto();
	}

	@Test
	void testLoadUserByUsername() throws ObjectNotFoundException {
		User savedUser = userService.createUser(creationRequestDTO);
		savedUser.setActivated(true);
		savedUser.setAllowLogin(Boolean.TRUE);
		userRepository.save(savedUser);
		UserDetails loadUserByUsername = domainUserDetailsService.loadUserByUsername(savedUser.getEmail());
		System.out.println("Load User By Name" + loadUserByUsername);
		assertThat(loadUserByUsername).isNotNull();
		assertThat(loadUserByUsername.getUsername()).isEqualTo(savedUser.getEmail());
		assertThat(loadUserByUsername.getAuthorities()).isNotEmpty();
		assertThat(loadUserByUsername.getAuthorities()).hasSizeGreaterThan(1);
	}
	
	@Test
	void checkUserEmailIsPresent() {
		assertThrows(UsernameNotFoundException.class,() -> {
	        domainUserDetailsService.loadUserByUsername("jayeshjain");		
		});
	}
	
	@Test
	void checkUserIsActivated() throws ObjectNotFoundException {
		User savedUser=userService.createUser(creationRequestDTO);
		assertThrows(UserNotActivatedException.class,() -> {
	        domainUserDetailsService.loadUserByUsername(savedUser.getEmail());		
		});
	}
	
	@Test
	void checkUserAllowedToLogin() throws ObjectNotFoundException {
		creationRequestDTO.setAllowLogin(Boolean.FALSE);
		User savedUser=userService.createUser(creationRequestDTO);
		savedUser.setActivated(true);
		userRepository.save(savedUser);
		assertThrows(UserNotActivatedException.class,() -> {
	        domainUserDetailsService.loadUserByUsername(savedUser.getEmail());		
		});
	}
	
	@Test
	void checkUserAccountLocked() throws ObjectNotFoundException {
		User savedUser=userService.createUser(creationRequestDTO);
		savedUser.setAccountLocked(Boolean.TRUE);
		savedUser.setActivated(true);
		userRepository.save(savedUser);
		assertThrows(AccountLockedException.class,() -> {
	        domainUserDetailsService.loadUserByUsername(savedUser.getEmail());		
		});
	}
	
	@Test
	void checkUserIsDeactivated() throws ObjectNotFoundException {
		User savedUser = userService.createUser(creationRequestDTO);
		savedUser.setIsDeactivated(Boolean.TRUE);
		savedUser.setActivated(true);
		userRepository.save(savedUser);
		assertThrows(UserNotActivatedException.class, () -> {
			domainUserDetailsService.loadUserByUsername(savedUser.getEmail());
		});
	}
}
