package com.grtship.account.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.grtship.account.service.GroupService;
import com.grtship.account.service.KafkaGroupService;
import com.grtship.core.constant.KafkaTopicConstant;
import com.grtship.core.dto.AuthorizationObjectDTO;

@Service
public class KafkaGroupServiceImpl implements KafkaGroupService {
	
	
	private static final Logger log = LoggerFactory.getLogger(KafkaGroupServiceImpl.class);

	@Autowired private GroupService groupService;

	@Override
	public void consumeGroupApprovalMessage(String message, Acknowledgment acknowledgment) {
		log.info("consuming message from : " + KafkaTopicConstant.KAFKA_GROUP_APPROVAL + " : {} ", message);
		if (StringUtils.isNotEmpty(message)) {
			try {
				AuthorizationObjectDTO authorizationObjectDTO=new ObjectMapper().readValue(message, new TypeReference<AuthorizationObjectDTO>() {});
				Boolean generateCsaUsers = groupService.approveGroup(authorizationObjectDTO);
				if (generateCsaUsers) {
					acknowledgment.acknowledge();
				}
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
		}

	}

}
