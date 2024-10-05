package com.grtship.notification.serviceImpl;

import java.util.List;

import javax.mail.MessagingException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.grtship.common.exception.InvalidDataException;
import com.grtship.core.constant.KafkaTopicConstant;
import com.grtship.core.dto.ApprovalRequestDTO;
import com.grtship.core.dto.NotificationDTO;
import com.grtship.notification.service.KafkaService;
import com.grtship.notification.service.MailService;

@Service
public class KafkaServiceImpl implements KafkaService {

	private static final Logger log = LoggerFactory.getLogger(KafkaServiceImpl.class);

	@Autowired
	private MailService mailService;

	@Override
	public void sendNotificationToCsaUser(String message, Acknowledgment acknowledgment) {
		log.info("consuming message from : " + KafkaTopicConstant.KAFKA_CSA_NOTIFICATION_TOPIC + " : {} ", message);
		if (StringUtils.isNotEmpty(message)) {
			try {
				List<NotificationDTO> notificationDTOs = new ObjectMapper().readValue(message,
						new TypeReference<List<NotificationDTO>>() {
						});
				notificationDTOs.stream().forEach(user -> {
					try {

						Boolean generateCsaUsers = mailService.sendCreationEmail(user);
						if (generateCsaUsers) {
							acknowledgment.acknowledge();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				});
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
		}

	}

	@Override
	public void sendNotificationToGsaUserMessage(String message, Acknowledgment acknowledgment)
			throws InvalidDataException {
		log.info("consuming meassge from : " + KafkaTopicConstant.KAFKA_GSA_NOTIFICATION_TOPIC + " : {} ", message);
		if (StringUtils.isNotEmpty(message)) {
			try {
				List<NotificationDTO> notificationDTOs = new ObjectMapper().readValue(message,
						new TypeReference<List<NotificationDTO>>() {
						});
				notificationDTOs.stream().forEach(user -> {
					try {

						Boolean generateCsaUsers = mailService.sendCreationEmail(user);
						if (generateCsaUsers) {
							acknowledgment.acknowledge();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				});
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
		}

	}

	@Override
	public void sendNotificationToBsaUser(String message, Acknowledgment acknowledgment) throws InvalidDataException {
		log.info("consuming meassge from : " + KafkaTopicConstant.KAFKA_GSA_NOTIFICATION_TOPIC + " : {} ", message);
		if (StringUtils.isNotEmpty(message)) {
			try {
				List<NotificationDTO> notificationDTOs = new ObjectMapper().readValue(message,
						new TypeReference<List<NotificationDTO>>() {
						});
				notificationDTOs.stream().forEach(user -> {
					try {

						Boolean generateCsaUsers = mailService.sendCreationEmail(user);
						if (generateCsaUsers) {
							acknowledgment.acknowledge();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				});
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void sendNotificationToUser(String message, Acknowledgment acknowledgment) throws InvalidDataException {

		log.info("consuming meassge from : " + KafkaTopicConstant.KAFKA_USER_NOTIFICATION_TOPIC + " : {} ", message);
		if (StringUtils.isNotEmpty(message)) {
			try {
				NotificationDTO notificationDTO = new ObjectMapper().readValue(message,
						new TypeReference<NotificationDTO>() {
						});
				Boolean generateCsaUsers = mailService.sendCreationEmail(notificationDTO);
				if (generateCsaUsers) {
					acknowledgment.acknowledge();
				}
			} catch (JsonProcessingException | MessagingException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void resetPassword(String message, Acknowledgment acknowledgment) throws InvalidDataException {
		log.info("consuming meassge from : " + KafkaTopicConstant.KAFKA_RESET_PASSWORD_TOPIC + " : {} ", message);
		if (StringUtils.isNotEmpty(message)) {
			try {
				NotificationDTO notificationDTO = new ObjectMapper().readValue(message,
						new TypeReference<NotificationDTO>() {
						});
				Boolean generateCsaUsers = mailService.sendPasswordResetMail(notificationDTO);
				if (generateCsaUsers) {
					acknowledgment.acknowledge();
				}
			} catch (JsonProcessingException | MessagingException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void approvalRequest(String message, Acknowledgment acknowledgment) throws InvalidDataException {
		log.info("consuming message from : " + KafkaTopicConstant.KAFKA_SEND_APPROVAL_REQUEST + " : {} ", message);
		if (StringUtils.isNotEmpty(message)) {
			try {
				ApprovalRequestDTO approvalRequestDTO = new ObjectMapper().readValue(message,
						new TypeReference<ApprovalRequestDTO>() {
						});
				approvalRequestDTO.getNotificationDTOs().stream().forEach(notificationObj -> {
					try {
						notificationObj.setActionType(approvalRequestDTO.getAction());
						notificationObj.setModuleName(approvalRequestDTO.getModuleName());
						notificationObj.setReferenceId(approvalRequestDTO.getReferenceId());
						Boolean generateCsaUsers = mailService.sendApprovalRequestEmail(notificationObj);
						if (generateCsaUsers) {
							acknowledgment.acknowledge();
						}
					} catch (Exception e) {
						log.trace("log tracing : {} ", e);
						e.printStackTrace();
					}
				});
			} catch (JsonProcessingException e) {
				log.trace("log tracing : {} ", e);
				e.printStackTrace();
			}
		}

	}

}
