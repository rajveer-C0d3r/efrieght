package com.grtship.notification.serviceImpl;

import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.grtship.core.enumeration.NotificationStatus;
import com.grtship.core.enumeration.NotificationType;
import com.grtship.notification.domain.NotificationDetail;
import com.grtship.notification.repository.NotificationRepository;
import com.grtship.notification.service.NotificationLogService;

@Service
public class NotificationServiceImpl implements NotificationLogService {
	
	private final Logger log=LoggerFactory.getLogger(NotificationServiceImpl.class);
	
	@Autowired
	private NotificationRepository repository;

	@Override
	public void saveNotificationLog(String user, String message, NotificationType notificationType,
			NotificationStatus notificationStatus) {
		NotificationDetail notificationDetail=NotificationDetail.builder().message(ObjectUtils.isNotEmpty(message)?message:null)
				                              .notificationStatus(ObjectUtils.isNotEmpty(notificationStatus)?notificationStatus:null)
				                              .user(user).notificationType(ObjectUtils.isNotEmpty(notificationType)?notificationType:null).build();
		log.debug("Notification Detail to Save : {}", notificationDetail);
		repository.save(notificationDetail);
		log.debug("Notification Detail Saved Successfully");
	}
		
}
