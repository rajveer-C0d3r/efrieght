/**
 * 
 */
package com.grtship.notification.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.RestController;

import com.grtship.common.exception.InvalidDataException;
import com.grtship.core.constant.KafkaTopicConstant;
import com.grtship.notification.service.KafkaService;

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
	 * @throws InvalidDataException 
	 */
	@KafkaListener(containerFactory = "kafkaManualAckListenerContainerFactory", groupId = "notification-service", topics = KafkaTopicConstant.KAFKA_GSA_NOTIFICATION_TOPIC)
	public void listenerGsaUser(@Payload String message, @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition,
			@Header(KafkaHeaders.OFFSET) int offset, Acknowledgment acknowledgment) throws InvalidDataException {
		log.info("Reached kafka gsa notification listener with meassage in notification: {} ", message);
		log.info("Reached kafka gsa notification listener with partition : {} ", partition);
		log.info("Reached kafka gsa notification listener with offset : {} ", offset);
		kafkaService.sendNotificationToGsaUserMessage(message, acknowledgment);;
	}
	/**
	 * @param message
	 * @param partition
	 * @param offset
	 */
	@KafkaListener(containerFactory = "kafkaManualAckListenerContainerFactory", groupId = "notification-service", topics = KafkaTopicConstant.KAFKA_CSA_NOTIFICATION_TOPIC)
	public void listenerCsaUser(@Payload String message, @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition,
			@Header(KafkaHeaders.OFFSET) int offset, Acknowledgment acknowledgment) {
		log.info("Reached kafka csa notification listener with message in notification: {} ", message);
		log.info("Reached kafka csa notification listener with partition : {} ", partition);
		log.info("Reached kafka csa notification listener with offset : {} ", offset);
		kafkaService.sendNotificationToCsaUser(message, acknowledgment);
	}
	
	@KafkaListener(containerFactory = "kafkaManualAckListenerContainerFactory", groupId = "notification-service", topics = KafkaTopicConstant.KAFKA_BSA_NOTIFICATION_TOPIC)
	public void listenerBsaUser(@Payload String message, @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition,
			@Header(KafkaHeaders.OFFSET) int offset, Acknowledgment acknowledgment) throws InvalidDataException {
		log.info("Reached kafka bsa notification listener with meassage in notification: {} ", message);
		log.info("Reached kafka bsa notification listener with partition : {} ", partition);
		log.info("Reached kafka bsa notification listener with offset : {} ", offset);
		kafkaService.sendNotificationToBsaUser(message, acknowledgment);
	}
	
	@KafkaListener(containerFactory = "kafkaManualAckListenerContainerFactory", groupId = "notification-service", topics = KafkaTopicConstant.KAFKA_USER_NOTIFICATION_TOPIC)
	public void listenerUser(@Payload String message, @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition,
			@Header(KafkaHeaders.OFFSET) int offset, Acknowledgment acknowledgment) throws InvalidDataException {
		log.info("Reached kafka user notification listener with meassage in notification: {} ", message);
		log.info("Reached kafka user notification listener with partition : {} ", partition);
		log.info("Reached kafka user notification listener with offset : {} ", offset);
		kafkaService.sendNotificationToUser(message, acknowledgment);
	}
	
	@KafkaListener(containerFactory = "kafkaManualAckListenerContainerFactory", groupId = "notification-service", topics = KafkaTopicConstant.KAFKA_RESET_PASSWORD_TOPIC)
	public void listenerResetPassword(@Payload String message, @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition,
			@Header(KafkaHeaders.OFFSET) int offset, Acknowledgment acknowledgment) throws InvalidDataException {
		log.info("Reached kafka reset password listener with meassage in notification: {} ", message);
		log.info("Reached kafka reset password listener with partition : {} ", partition);
		log.info("Reached kafka reset password listener with offset : {} ", offset);
		kafkaService.resetPassword(message, acknowledgment);
	}
	
	@KafkaListener(containerFactory = "kafkaManualAckListenerContainerFactory", groupId = "notification-service", topics = KafkaTopicConstant.KAFKA_SEND_APPROVAL_REQUEST)
	public void listenerApprovalRequest(@Payload String message, @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition,
			@Header(KafkaHeaders.OFFSET) int offset, Acknowledgment acknowledgment) throws InvalidDataException {
		log.info("Reached kafka Approval request listener with meassage in notification: {} ", message);
		log.info("Reached kafka Approval request listener with partition : {} ", partition);
		log.info("Reached kafka Approval request listener with offset : {} ", offset);
		kafkaService.approvalRequest(message, acknowledgment);
	}
}
