/**
 * 
 */
package com.grtship.client.service;

/**
 * @author Ajay
 *
 */
public interface KafkaService {
//	void sendMessage(String message);

	void consume(String message);

}
