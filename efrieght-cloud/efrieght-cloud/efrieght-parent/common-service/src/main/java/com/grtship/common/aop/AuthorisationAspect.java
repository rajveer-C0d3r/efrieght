package com.grtship.common.aop;

import java.lang.reflect.Field;

import org.apache.commons.lang3.ObjectUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.grtship.common.interfaces.AuthorizationManager;
import com.grtship.core.annotation.Authorize;
import com.grtship.core.dto.AuthorizationContainerDTO;
import com.grtship.core.dto.AuthorizationObjectDTO;

@Aspect
@Component
public class AuthorisationAspect {

	private static final Logger log = LoggerFactory.getLogger(AuthorisationAspect.class);

	@Autowired
	private AuthorizationManager authorizationManager;

	@Pointcut("@annotation(com.grtship.core.annotation.Authorize)")
	public void authorizableMethods() {
	}

	@AfterReturning(pointcut = "authorizableMethods()", returning = "retValue")
	public void authoriseObject(JoinPoint joinPoint, Object retValue) throws Throwable {
		if (ObjectUtils.isNotEmpty(retValue)) {
			log.debug("Return object of auth aspect : {}", retValue);
			callAuthManager(joinPoint, retValue);
		}
	}

	private void callAuthManager(JoinPoint joinPoint, Object retValue) throws IllegalAccessException, NoSuchFieldException {
		authorizationManager.manage(createAuthorizationContainer(retValue, getAuthorizationObject(
				(Long) makeFieldAccessible(retValue).get(retValue), getAuthorizationAnnotationDetails(joinPoint))));
	}

	private Field makeFieldAccessible(Object object) throws NoSuchFieldException {
		return setAccessibleTrue(object);
	}

	private Field setAccessibleTrue(Object object) throws NoSuchFieldException {
		Field field = extractedIdField(object);
		field.setAccessible(true);
		return field;
	}

	private Field extractedIdField(Object object) throws NoSuchFieldException {
		return object.getClass().getSuperclass().getDeclaredField("id");
	}

	private Authorize getAuthorizationAnnotationDetails(JoinPoint joinPoint) {
		return getMethodSignature(joinPoint).getMethod().getAnnotation(Authorize.class);
	}

	private MethodSignature getMethodSignature(JoinPoint joinPoint) {
		return extractMethodSignature(joinPoint);
	}

	private MethodSignature extractMethodSignature(JoinPoint joinPoint) {
		return (MethodSignature) joinPoint.getSignature();
	}

	private AuthorizationContainerDTO createAuthorizationContainer(Object object,
			AuthorizationObjectDTO authorizationObjectDTO) {
		return buildAuthorizationContainer(object, authorizationObjectDTO);
	}

	private AuthorizationContainerDTO buildAuthorizationContainer(Object object,
			AuthorizationObjectDTO authorizationObjectDTO) {
		return AuthorizationContainerDTO.builder().authorizationObjectDTO(authorizationObjectDTO).object(object)
				.build();
	}

	private AuthorizationObjectDTO getAuthorizationObject(Long value, Authorize authorize) {
		return buildAuthorizationObject(value, authorize);
	}

	private AuthorizationObjectDTO buildAuthorizationObject(Long value, Authorize authorize) {
		return AuthorizationObjectDTO.builder().action(authorize.action()).moduleName(authorize.moduleName())
				.referenceId(value).build();
	}
}
