/**
 * 
 */
package com.grtship.common.service;

/**
 * @author Ajay
 *
 */
public interface KafkaProducerService {
	void sendMessage(String kafkaTopic, String message);
}
