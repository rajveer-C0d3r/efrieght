/**
 * 
 */
package com.grtship.client.serviceimpl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.grtship.client.domain.UserLog;
import com.grtship.client.repository.UserLogRepository;
import com.grtship.client.service.KafkaService;
import com.grtship.core.constant.KafkaTopicConstant;
import com.grtship.core.dto.UserLogDTO;

/**
 * @author Ajay
 *
 */
@Service
public class KafkaServiceImpl implements KafkaService {

	private static final Logger log = LoggerFactory.getLogger(KafkaServiceImpl.class);

	@Autowired
	private UserLogRepository userLogRepository;

	@Override
	public void consume(String message) {
		log.info("consuming meassge from : " + KafkaTopicConstant.KAFKA_USER_CREATION_LOG_TOPIC + " : {} ", message);
		if (StringUtils.isNotEmpty(message)) {
			try {
				List<UserLogDTO> userLogs = new ObjectMapper().readValue(message,
						new TypeReference<List<UserLogDTO>>() {
						});
				if (!CollectionUtils.isEmpty(userLogs)) {
					UserLog userLog = null;
					List<UserLog> logs = new ArrayList<>();
					for (UserLogDTO userLogDTO : userLogs) {
						if (userLogDTO != null) {
							userLog = new UserLog();
							BeanUtils.copyProperties(userLogDTO, userLog);
							logs.add(userLog);
						}
					}
					if (CollectionUtils.isNotEmpty(logs)) {
						userLogRepository.saveAll(logs);
					}
				}

			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
		}
	}

}
