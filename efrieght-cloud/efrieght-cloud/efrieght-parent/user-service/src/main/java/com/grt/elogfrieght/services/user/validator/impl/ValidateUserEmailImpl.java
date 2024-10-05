package com.grt.elogfrieght.services.user.validator.impl;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.grt.elogfrieght.services.user.annotation.ValidateUserEmail;

public class ValidateUserEmailImpl implements ConstraintValidator<ValidateUserEmail, String> {

	@Override
	public boolean isValid(String email, ConstraintValidatorContext context) {

		/*
		 * context.buildConstraintViolationWithTemplate(message)
		 * .addPropertyNode(firstFieldName) .addConstraintViolation()
		 * .disableDefaultConstraintViolation();
		 */
		return false;
	}

}
