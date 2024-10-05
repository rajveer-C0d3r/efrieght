/**
 * 
 */
package com.grtship.authorisation.aop;

import static com.grtship.authorisation.util.SecurityUtils.getCurrentUserLogin;

import java.util.Date;
import java.util.Optional;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.grtship.common.interfaces.JsonConverter;
import com.grtship.common.service.KafkaProducerService;
import com.grtship.core.constant.AuthoritiesConstants;
import com.grtship.core.constant.KafkaTopicConstant;

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

	@Pointcut("execution(public !void org.springframework.data.repository.Repository+.saveAll(..))")
	public void repositorySaveAllMethod() {
	}

	@Pointcut("execution(public !void org.springframework.data.repository.Repository+.save(..))")
	public void repositorySaveMethod() {
	}

	@Pointcut("execution(public void org.springframework.data.repository.Repository+.delete(..))")
	public void repositoryDeleteMethod() {
	}

	/**
	 * @param jp
	 * @return
	 * @throws Throwable
	 * this will always intercept save method only
	 */
	@Around("repositorySaveMethod() || repositorySaveAllMethod()")
	private Object aroundAspect(ProceedingJoinPoint jp) throws Throwable {
		Object reponseObject = null;
		log.info("around aspect for repository level annotation : {} ", jp);
		reponseObject = jp.proceed();
		if (reponseObject != null) {
			Optional<String> currentUserLogin = getCurrentUserLogin();
			JSONObject json = jsonConverter.convert(reponseObject);
			if (!json.isEmpty()) {
				JsonElement jsonElement = new Gson().fromJson(json.toString(), JsonElement.class);
				jsonElement.getAsJsonObject().addProperty("referenceType", reponseObject.getClass().getSimpleName());
				jsonElement.getAsJsonObject().addProperty("action", jp.getSignature().getName());
				jsonElement.getAsJsonObject().addProperty("owner", currentUserLogin.isPresent() ? currentUserLogin.get() : AuthoritiesConstants.ANONYMOUS);
				jsonElement.getAsJsonObject().addProperty("sequence", new Date().getTime());
				String jsonData = new Gson().toJson(jsonElement);
				log.info("converted json {} ", jsonData);
				kafkaService.sendMessage(KafkaTopicConstant.KAFKA_AUDIT_TOPIC, jsonData);
			}
		}
		if (reponseObject != null) {
			log.info("around aspect for repository level work data is : {} ", reponseObject);
		}

		return reponseObject;
	}

	/**
	 * @param jp
	 * @return
	 * @throws Throwable
	 */
	@Around("repositoryDeleteMethod()")
	private Object deleteAspect(ProceedingJoinPoint jp) throws Throwable {
		Object reponseObject = null;
		log.info("around aspect for repository level annotation : {} ", jp);
		reponseObject = jp.proceed();
		if (reponseObject != null) {
			Optional<String> currentUserLogin = getCurrentUserLogin();
			JSONObject json = new JSONObject();
			json.put("referenceType", reponseObject.getClass().getSimpleName());
			json.put("action", jp.getSignature().getName());
			json.put("id", jp.getArgs());
			json.put("owner", currentUserLogin.isPresent() ? currentUserLogin.get() : AuthoritiesConstants.ANONYMOUS);
			String jsonData = new Gson().toJson(json);
			log.info("converted json {} ", jsonData);
			kafkaService.sendMessage(KafkaTopicConstant.KAFKA_AUDIT_TOPIC, jsonData);
		}
		if (reponseObject != null) {
			log.info("around aspect for repository level work data is : {} ", reponseObject);
		}

		return jp;
	}

}
