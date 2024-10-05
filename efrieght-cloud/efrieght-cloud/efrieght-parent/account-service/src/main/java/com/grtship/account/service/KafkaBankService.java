package com.grtship.account.service;

import org.springframework.kafka.support.Acknowledgment;

public interface KafkaBankService {
	void consumeBankApprovalMessage(String message, Acknowledgment acknowledgment);
}
