/**
 * 
 */
package com.grtship.account.aop;

import static com.grtship.account.util.SecurityUtils.getCurrentUserLogin;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.Optional;

import org.apache.commons.lang3.ObjectUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.grtship.common.interfaces.JsonConverter;
import com.grtship.common.service.KafkaProducerService;
import com.grtship.core.annotation.Auditable;
import com.grtship.core.constant.AuthoritiesConstants;
import com.grtship.core.constant.KafkaTopicConstant;
import com.grtship.core.dto.AuditDataHeader;
import com.grtship.core.dto.AuditDataWrapper;

/**
 * @author Ajay
 *
 */
@Aspect
@Component
public class RepositoryAspect {

	private static final Logger log = LoggerFactory.getLogger(RepositoryAspect.class);

	@Autowired
	private KafkaProducerService kafkaService;

	@Autowired
	private JsonConverter jsonConverter;

	@Pointcut("@annotation(com.grtship.core.annotation.Auditable)")
	public void auditableMethods() {
	}

	@Pointcut("@annotation(org.springframework.transaction.annotation.Transactional)")
	public void transactionMethods() {
	}

	@Pointcut("(auditableMethods() && transactionMethods()) || auditableMethods()")
	public void entityCreationMethods() {
	}

	@AfterReturning(pointcut = "entityCreationMethods()", returning = "retVal")
	public void afterReturningAdvice(JoinPoint joinPoint, Object retVal)
			throws IllegalArgumentException, IllegalAccessException {
		log.error("Method Signature : {}  " + joinPoint.getSignature());
		log.error("after return aspect of joint Point: {} ", joinPoint);
		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
		Method method = signature.getMethod();
		Auditable auditable = method.getAnnotation(Auditable.class);
		log.error("after return aspect of auditable annotation : {} ", auditable);
		Object reponseObject = retVal;
		Optional<String> currentUserLogin = getCurrentUserLogin();
		AuditDataWrapper dataWrapper = null;
		AuditDataHeader header = null;
		dataWrapper = new AuditDataWrapper();
		header = new AuditDataHeader();
		dataWrapper.setHeader(header);
		if (ObjectUtils.isNotEmpty(reponseObject)) {
			log.error("Returning object data of aspect : {} ", retVal.toString());
			JSONObject json = jsonConverter.convert(reponseObject);
			if (!json.isEmpty()) {
				header = addDefaultDataInHeader(auditable, currentUserLogin, header);
				dataWrapper.setPayload(json.toMap());
				log.error("converted json with response object {} ", dataWrapper);
			}
		} else {
			header = addDefaultDataInHeader(auditable, currentUserLogin, header);
			log.error("converted json with void response object {} ", dataWrapper);
		}
		log.error("audit wrapper data value as json {} ", dataWrapper);
		kafkaService.sendMessage(KafkaTopicConstant.KAFKA_AUDIT_TOPIC, new Gson().toJson(dataWrapper));
	}

	private AuditDataHeader addDefaultDataInHeader(Auditable auditable, Optional<String> currentUserLogin,
			AuditDataHeader header) {
		addAnnotationParameterData(auditable, header);
		addCurrentLoggedInUserData(currentUserLogin, header);
		addSequenceInData(header);
		return header;
	}

	private void addSequenceInData(AuditDataHeader header) {
		header.setSequence(new Date().getTime());
	}

	private void addCurrentLoggedInUserData(Optional<String> currentUserLogin, AuditDataHeader header) {
		header.setOwner(addCurrentUser(currentUserLogin));
	}

	private void addAnnotationParameterData(Auditable auditable, AuditDataHeader header) {
		header.setAction(auditable.action().getLabel());
		header.setReferenceType(auditable.module().getLabel());
	}

	private String addCurrentUser(Optional<String> currentUserLogin) {
		return currentUserLogin.isPresent() ? currentUserLogin.get() : AuthoritiesConstants.ANONYMOUS;
	}
}
