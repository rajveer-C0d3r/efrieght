/**
 * 
 */
package com.grtship.audit.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.RestController;

import com.grtship.audit.service.KafkaService;
import com.grtship.core.constant.KafkaTopicConstant;

/**
 * @author Ajay
 *
 */
@RestController
public class KafkaConsumerController {

	@Autowired
	private KafkaService kafkaService;

	private static final Logger log = LoggerFactory.getLogger(KafkaConsumerController.class);

	/**
	 * @param message
	 * @param partition
	 * @param offset
	 */
	@KafkaListener(containerFactory = "kafkaManualAckListenerContainerFactory", groupId = "audit-service", topics = KafkaTopicConstant.KAFKA_AUDIT_TOPIC)
	public void listenerAudit(@Payload String message, @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition,
			@Header(KafkaHeaders.OFFSET) int offset, Acknowledgment acknowledgment) {
		log.info("Reached kafka audit listener with meassage : {} ", message);
		log.info("Reached kafka audit listener with partition : {} ", partition);
		log.info("Reached kafka audit listener with offset : {} ", offset);
		kafkaService.consume(message);
		acknowledgment.acknowledge();
	}
}
