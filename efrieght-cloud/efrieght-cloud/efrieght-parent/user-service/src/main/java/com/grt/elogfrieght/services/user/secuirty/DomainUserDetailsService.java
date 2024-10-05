package com.grt.elogfrieght.services.user.secuirty;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.grt.elogfrieght.services.user.domain.User;
import com.grt.elogfrieght.services.user.domain.UserRoles;
import com.grt.elogfrieght.services.user.repository.UserRepository;
import com.grt.elogfrieght.services.user.serviceimpl.RoleQueryServiceImpl;
import com.grtship.common.exception.AccountLockedException;
import com.grtship.common.exception.InvalidDataException;
import com.grtship.common.exception.UserNotActivatedException;
import com.grtship.core.constant.ErrorCode;

/**
 * Authenticate a user from the database.
 */
@Component("userDetailsService")
public class DomainUserDetailsService implements UserDetailsService {

	private static final String USER = "User ";

	private final Logger log = LoggerFactory.getLogger(DomainUserDetailsService.class);

	private final UserRepository userRepository;
	private final RoleQueryServiceImpl roleService;

	public DomainUserDetailsService(UserRepository userRepository, RoleQueryServiceImpl roleService) {
		this.roleService = roleService;
		this.userRepository = userRepository;
	}
	
	@Override
	@Transactional
	public UserDetails loadUserByUsername(final String email) {
		log.debug("Authenticating {}", email);

		log.debug("Valid Email. Fetching user by email");
		Optional<User> userOptional = userRepository.findOneWithAuthoritiesByEmailIgnoreCase(email);
		if (!userOptional.isPresent()) {
			throw new UsernameNotFoundException("User with this email " + email + " is not register with our system");
		}
		return createSpringSecurityUser(email, userOptional.get());
	}

	private org.springframework.security.core.userdetails.User createSpringSecurityUser(String lowercaseLogin,
			User user) {
		if (!user.isActivated()) {
			throw new UserNotActivatedException(USER + lowercaseLogin + " was not activated");
		}
		if (user.getAllowLogin().equals(Boolean.FALSE)) {
			throw new UserNotActivatedException(USER + lowercaseLogin + " was not allowed to login");
		}
		if (user.getAccountLocked().equals(Boolean.TRUE)) {
			throw new AccountLockedException(USER + lowercaseLogin + " is locked. Contact to admin");
		}
		if (user.getIsDeactivated().equals(Boolean.TRUE)) {
			throw new UserNotActivatedException(USER + lowercaseLogin + " has been Temporary Deactivated");
		}
		List<GrantedAuthority> grantedAuthorities = user.getAuthorities().stream()
				.map(authority -> new SimpleGrantedAuthority(authority.getName())).collect(Collectors.toList());
		List<Long> roleIds = user.getUserRoles().stream().filter(obj -> obj != null && obj.getRoleId() != null)
				.map(UserRoles::getRoleId).collect(Collectors.toList());
		List<GrantedAuthority> permissions = roleService.getRolePermissionsByIds(roleIds);
		log.debug("Permissions for user {}", permissions);
		grantedAuthorities.addAll(permissions);
		log.debug("Account locked ? {}", user.getAccountLocked());
		return new org.springframework.security.core.userdetails.User(user.getLogin(), user.getPassword(),
				grantedAuthorities);
	}

}
