package com.grtship.account.controller;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.grtship.account.service.KafkaBankService;
import com.grtship.account.service.KafkaGroupService;
import com.grtship.account.service.KafkaLedgerService;
import com.grtship.common.exception.InvalidDataException;
import com.grtship.core.constant.KafkaTopicConstant;

@RestController
public class KafkaConsumerController {

	
	private static final Logger log = LoggerFactory.getLogger(KafkaConsumerController.class);
	
	@Autowired private KafkaGroupService kafkaGroupService;
	@Autowired private KafkaLedgerService kafkaLedgerService;
	@Autowired private KafkaBankService kafkaBankService;
	private ExecutorService sseExecutorService = Executors.newCachedThreadPool();
	
	@GetMapping(value = "/consume",produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	@KafkaListener(containerFactory = "kafkaManualAckListenerContainerFactory", groupId = "account-service", topics = KafkaTopicConstant.KAFKA_GROUP_APPROVAL)
	public void listenerGroupApproval(@Payload String message, @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition,
			@Header(KafkaHeaders.OFFSET) int offset, Acknowledgment acknowledgment) throws InvalidDataException {
		log.info("Reached kafka group approval listener with meassage in notification: {} ", message);
		log.info("Reached kafka group approval listener with partition : {} ", partition);
		log.info("Reached kafka group approval listener with offset : {} ", offset);
		kafkaGroupService.consumeGroupApprovalMessage(message, acknowledgment);
	}
	
	@KafkaListener(containerFactory = "kafkaManualAckListenerContainerFactory", groupId = "account-service", topics = KafkaTopicConstant.KAFKA_LEDGER_APPROVAL)
	public void listenerLedgerApproval(@Payload String message, @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition,
			@Header(KafkaHeaders.OFFSET) int offset, Acknowledgment acknowledgment) throws InvalidDataException {
		log.info("Reached kafka ledger approval listener with meassage in notification: {} ", message);
		log.info("Reached kafka ledger approval listener with partition : {} ", partition);
		log.info("Reached kafka ledger approval listener with offset : {} ", offset);
        kafkaLedgerService.consumeLedgerApprovalMessage(message, acknowledgment);
	}
	
	
	@KafkaListener(containerFactory = "kafkaManualAckListenerContainerFactory", groupId = "account-service", topics = KafkaTopicConstant.KAFKA_BANK_APPROVAL)
	public void listenerBankApproval(@Payload String message, @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition,
			@Header(KafkaHeaders.OFFSET) int offset, Acknowledgment acknowledgment) throws InvalidDataException {
		log.info("Reached kafka bank approval listener with meassage in notification: {} ", message);
		log.info("Reached kafka bank approval listener with partition : {} ", partition);
		log.info("Reached kafka bank approval listener with offset : {} ", offset);
        kafkaBankService.consumeBankApprovalMessage(message, acknowledgment);
	}
	
	
}
