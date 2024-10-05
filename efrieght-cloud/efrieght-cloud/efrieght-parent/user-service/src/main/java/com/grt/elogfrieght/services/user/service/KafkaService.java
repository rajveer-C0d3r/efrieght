/**
 * 
 */
package com.grt.elogfrieght.services.user.service;

import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import com.grtship.common.exception.InvalidDataException;

/**
 * @author Ajay
 *
 */

@Component
public interface KafkaService {
	void consumeCsaUserMessage(String message, Acknowledgment acknowledgment);

	void consumeGsaUserMessage(String message, Acknowledgment acknowledgment) throws InvalidDataException;
}
