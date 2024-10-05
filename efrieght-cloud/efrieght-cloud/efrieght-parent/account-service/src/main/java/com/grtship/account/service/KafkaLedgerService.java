package com.grtship.account.service;

import org.springframework.kafka.support.Acknowledgment;

public interface KafkaLedgerService {
	void consumeLedgerApprovalMessage(String message, Acknowledgment acknowledgment);
}
