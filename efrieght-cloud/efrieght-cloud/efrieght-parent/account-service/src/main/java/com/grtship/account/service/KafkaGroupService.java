package com.grtship.account.service;

import org.springframework.kafka.support.Acknowledgment;

public interface KafkaGroupService {
	void consumeGroupApprovalMessage(String message, Acknowledgment acknowledgment);
}
