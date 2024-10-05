package com.grt.elogfrieght.services.user.validator.impl;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.util.CollectionUtils;

import com.grt.elogfrieght.services.user.annotation.ValidateUserAccess;
import com.grt.elogfrieght.services.user.domain.Authority;
import com.grt.elogfrieght.services.user.domain.User;
import com.grtship.core.enumeration.UserType;

//@Component
public class ValidateUserAccessImpl implements ConstraintValidator<ValidateUserAccess, User> {

	private static final Logger log = LoggerFactory.getLogger(ValidateUserAccessImpl.class);

	private static final String ROLE_GSA = "ROLE_GSA";

	private static final String ROLE_ADMIN = "ROLE_ADMIN";

	@Override
	public void initialize(ValidateUserAccess constraintAnnotation) {

		ConstraintValidator.super.initialize(constraintAnnotation);
		// Will use this code after autowire null error get resolved.
		// this.userService = applicationContext.getBean(UserService.class);
	}

	@Override
	public boolean isValid(User user, ConstraintValidatorContext context) {
		log.debug("Validating UserAccess {}", user);
		log.debug("User Access for validation {}", user.getUserAccess());
		Boolean isValidEntity = Boolean.TRUE;
		isValidEntity = validateUserAccess(user, context, isValidEntity);
		return isValidEntity;
	}

	private Boolean validateUserAccess(User user, ConstraintValidatorContext context, Boolean isValidEntity) {
		if (checkingAllCompanyAccess(user) && user.getUserType() != null && checkingUserIsClient(user)
				&& checkingUserAccessIsEmpty(user) && checkingUserAuthorityIsNotEmptyWithSpecificRoles(user)) {
			log.info("condition failed for user {} ", user);
			context.buildConstraintViolationWithTemplate("Enter User Company Details").addPropertyNode("User Company")
					.addConstraintViolation().disableDefaultConstraintViolation();
			isValidEntity = Boolean.FALSE;
		}
		return isValidEntity;
	}

	private boolean checkingUserAccessIsEmpty(User user) {
		boolean userAccess = CollectionUtils.isEmpty(user.getUserAccess());
		log.info("user access is empty : {} ", userAccess);
		return userAccess;
	}

	private boolean checkingUserIsClient(User user) {
		boolean userTypeClient = user.getUserType().equals(UserType.CLIENT);
		log.info("user is of type client : {} ", userTypeClient);
		return userTypeClient;
	}

	private boolean checkingAllCompanyAccess(User user) {
		log.info("checking user all company access");
		log.info("checking user all company access is null or not  {}", user.getAllCompanies() != null);
		return user.getAllCompanies() != null && user.getAllCompanies().equals(Boolean.FALSE);
	}

	private boolean checkingUserAuthorityIsNotEmptyWithSpecificRoles(User user) {
		log.info("checking if user authority empty not not {} ", CollectionUtils.isEmpty(user.getAuthorities()));
		return !CollectionUtils.isEmpty(user.getAuthorities()) && checkingGsaAndAdminRole(user);
	}

	private boolean checkingGsaAndAdminRole(User user) {
		log.info("checking authority contain gsa role : {} ", user.getAuthorities().contains(new Authority(ROLE_GSA)));
		log.info("checking authority contain admin role : {} ", user.getAuthorities().contains(new Authority(ROLE_ADMIN)));
		return !(user.getAuthorities().contains(new Authority(ROLE_GSA)) || user.getAuthorities().contains(new Authority(ROLE_ADMIN)));
	}

}
