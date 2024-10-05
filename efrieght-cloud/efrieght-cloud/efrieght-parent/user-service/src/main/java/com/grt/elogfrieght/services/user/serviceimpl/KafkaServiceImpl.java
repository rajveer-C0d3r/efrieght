/**
 * 
 */
package com.grt.elogfrieght.services.user.serviceimpl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.grt.elogfrieght.services.user.service.KafkaService;
import com.grt.elogfrieght.services.user.service.UserService;
import com.grtship.common.exception.InvalidDataException;
import com.grtship.core.constant.KafkaTopicConstant;
import com.grtship.core.dto.ApprovalRequestDTO;
import com.grtship.core.dto.CsaDetailsDTO;
import com.grtship.core.dto.GsaDetailsDTO;

/**
 * @author Ajay
 *
 */
@Service
public class KafkaServiceImpl implements KafkaService {

	private static final Logger log = LoggerFactory.getLogger(KafkaServiceImpl.class);

	@Autowired
	private UserService userService;

	@Override
	public void consumeCsaUserMessage(String message, Acknowledgment acknowledgment) {
		log.info("consuming message from : " + KafkaTopicConstant.KAFKA_CSA_USER_CREATION_TOPIC + " : {} ", message);
		if (StringUtils.isNotEmpty(message)) {
			try {
				Boolean generateCsaUsers = userService.generateCsaUsers(
						new ObjectMapper().readValue(message, new TypeReference<List<CsaDetailsDTO>>() {
						}));
				if (generateCsaUsers) {
					acknowledgment.acknowledge();
				}
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void consumeGsaUserMessage(String message, Acknowledgment acknowledgment) throws InvalidDataException {
		log.info("consuming message from : " + KafkaTopicConstant.KAFKA_GSA_USER_CREATION_TOPIC + " : {} ", message);
		if (StringUtils.isNotEmpty(message)) {
			try {
				Boolean generateGsaUsers = userService.generateGsaUsers(
						new ObjectMapper().readValue(message, new TypeReference<List<GsaDetailsDTO>>() {
						}));
				if (generateGsaUsers) {
					acknowledgment.acknowledge();
				}
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
		}
	}

}
