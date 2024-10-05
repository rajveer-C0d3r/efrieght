package com.grtship.common.aop;

import java.lang.reflect.Method;
import java.util.List;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.grtship.common.exception.ValidationException;
import com.grtship.common.interfaces.ValidationError;
import com.grtship.common.interfaces.Validator;
import com.grtship.core.annotation.Validate;
import com.grtship.core.enumeration.ValidationErrorType;

@Aspect
@Component
public class ValidatorAspect {
   
	private final Logger log=LoggerFactory.getLogger(ValidatorAspect.class);
	
	@Autowired
	private ApplicationContext context;
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Around(value = "@annotation(validate)")
	public Object callValidator(ProceedingJoinPoint joinPoint,Validate validate) throws Throwable {
		log.info("Action Value {}",validate.action());
		log.info("Validator Value {}",validate.validator());
		String validatorName=validate.validator();
		Validator validator=context.getBean(validatorName, Validator.class);
		Object[] args = joinPoint.getArgs();
		List<ValidationError> errors = null;
		if (args != null && args.length > validate.objectIndex()) {
			try{
				Method declaredMethod = validator.getClass().getDeclaredMethod(validate.action()+"Validate", args[validate.objectIndex()].getClass());
				Object invokedResult = declaredMethod.invoke(validator, args[validate.objectIndex()]);
				if(invokedResult instanceof List) {
					errors = (List)invokedResult;
				}
				
			}catch(NoSuchMethodException ex) {
				//no such method. call the common method
				errors = validator.validate(args[validate.objectIndex()],validate.action());
			}
			
			if (null != errors && errors.size() > 0 ) {
				if (errors.stream().filter(err -> ValidationErrorType.ERROR.equals(err.getType())).findAny().isPresent()) {
					throw new ValidationException(errors);
				}
			}
		}
		
		Object obj = joinPoint.proceed();
		return obj;
	}
}
