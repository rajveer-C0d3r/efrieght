/**
 * 
 */
package com.grtship.notification.service;

import org.springframework.kafka.support.Acknowledgment;

import com.grtship.common.exception.InvalidDataException;

/**
 * @author Ajay
 *
 */
public interface KafkaService {
	void sendNotificationToCsaUser(String message, Acknowledgment acknowledgment);

	void sendNotificationToGsaUserMessage(String message, Acknowledgment acknowledgment) throws InvalidDataException;

	void sendNotificationToBsaUser(String message, Acknowledgment acknowledgment) throws InvalidDataException;
	
	void sendNotificationToUser(String message, Acknowledgment acknowledgment) throws InvalidDataException;
	
	void resetPassword(String message, Acknowledgment acknowledgment) throws InvalidDataException;
	
	void approvalRequest(String message, Acknowledgment acknowledgment) throws InvalidDataException;
}
