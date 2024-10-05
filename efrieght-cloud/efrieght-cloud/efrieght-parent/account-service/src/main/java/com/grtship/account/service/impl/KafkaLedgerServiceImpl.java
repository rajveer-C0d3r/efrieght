package com.grtship.account.service.impl;

import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import com.grtship.account.service.KafkaLedgerService;

@Service
public class KafkaLedgerServiceImpl implements KafkaLedgerService {

	@Override
	public void consumeLedgerApprovalMessage(String message, Acknowledgment acknowledgment) {
		// TODO Auto-generated method stub

	}

}
