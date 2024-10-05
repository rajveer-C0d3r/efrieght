package com.grtship.account.service.impl;

import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import com.grtship.account.service.KafkaBankService;

@Service
public class KafkaBankServiceImpl implements KafkaBankService {

	@Override
	public void consumeBankApprovalMessage(String message, Acknowledgment acknowledgment) {
		// TODO Auto-generated method stub

	}

}
