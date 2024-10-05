package com.grt.elogfrieght.services.user.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import com.grt.elogfrieght.services.user.validator.impl.ValidateUserAccessImpl;

@Target({ ElementType.FIELD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = { ValidateUserAccessImpl.class })
@Documented
public @interface ValidateUserAccess {
	String message() default "Enter User Company Details";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

}
