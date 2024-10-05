/**
 * 
 */
package com.grtship.common.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import com.grtship.common.service.KafkaProducerService;
import com.grtship.core.exception.TechnicalException;

/**
 * @author Ajay
 *
 */
@Service
public class KafkaProducerServiceImpl implements KafkaProducerService {

	private static final Logger log = LoggerFactory.getLogger(KafkaProducerServiceImpl.class);

	@Autowired
	private KafkaTemplate<String, String> kafkaTemplate;

	@Override
	public void sendMessage(String kafkaTopic, String message) {
		if (StringUtils.isNotEmpty(kafkaTopic) && StringUtils.isNotEmpty(message)) {
			log.info("kafka producer topic found : {} ", kafkaTopic);
			log.info("kafka message found : {} ", message);
			ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send(kafkaTopic, message);
			future.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {
				@Override
				public void onSuccess(SendResult<String, String> result) {
					log.info("Message [{}] delivered with offset {}", message, result.getRecordMetadata().offset());
				}

				@Override
				public void onFailure(Throwable ex) {
					log.error("Unable to deliver message [{}]. {}", message, ex.getMessage());
					throw new TechnicalException("Error while send message on kafka server");
				}
			});

		}
	}

}
