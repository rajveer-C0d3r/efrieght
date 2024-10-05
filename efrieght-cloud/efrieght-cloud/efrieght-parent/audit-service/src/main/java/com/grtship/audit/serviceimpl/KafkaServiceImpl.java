/**
 * 
 */
package com.grtship.audit.serviceimpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.grtship.audit.service.AuditService;
import com.grtship.audit.service.KafkaService;
import com.grtship.core.constant.KafkaTopicConstant;

/**
 * @author Ajay
 *
 */
@Service
public class KafkaServiceImpl implements KafkaService{

	private static final Logger log = LoggerFactory.getLogger(KafkaServiceImpl.class);
	
	@Autowired
	private AuditService auditService;

	@Override
	public void consume(String message) {
		log.info("consuming meassge from : " + KafkaTopicConstant.KAFKA_AUDIT_TOPIC + " : {} ", message);
		auditService.saveAudit(message);
	}

}
