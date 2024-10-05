/**
 * 
 */
package com.grtship.audit.service;

/**
 * @author Ajay
 *
 */
public interface KafkaService {
//	void sendMessage(String message);

	void consume(String message);

}
