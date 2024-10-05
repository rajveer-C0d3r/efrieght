package com.grtship.notification.serviceImpl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import javax.validation.ConstraintViolationException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.audit.AuditAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.metrics.KafkaMetricsAutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.grtship.core.enumeration.NotificationStatus;
import com.grtship.core.enumeration.NotificationType;
import com.grtship.notification.domain.NotificationDetail;
import com.grtship.notification.repository.NotificationRepository;

@SpringBootTest(properties = "config.caching.enabled=false")
@Transactional
@EnableAutoConfiguration(exclude = { KafkaAutoConfiguration.class, KafkaMetricsAutoConfiguration.class,
		AuditAutoConfiguration.class })
@ActiveProfiles(profiles = { "test" })
public class NotificationServiceImplTest {
	
	private static final String MESSAGE = "Sent Successfully";
	private static final String USER = "jayesh.testNotification@gmail.com";
	@Autowired 
	private NotificationServiceImpl notificationServiceImpl;
	@Autowired
	private NotificationRepository notificationRepository;

	@Test
	void testSaveNotificationLog() {
		notificationServiceImpl.saveNotificationLog(USER, MESSAGE,
				NotificationType.EMAIL, NotificationStatus.SUCCESS);
		List<NotificationDetail> notificationDetails = notificationRepository.findByUser(USER);
		assertThat(notificationDetails).isNotEmpty();
		assertThat(notificationDetails).hasSizeGreaterThanOrEqualTo(1);
		assertThat(notificationDetails).allMatch(notification -> notification.getId()!=null);
		assertThat(notificationDetails).allMatch(notification -> notification.getUser().equals(USER));
		assertThat(notificationDetails).allMatch(notification -> notification.getNotificationType()!=null);
		assertThat(notificationDetails).allMatch(notification -> notification.getNotificationStatus()!=null);
	}
	
	@Test
	void testUserIsRequired() {
		assertThrows(ConstraintViolationException.class, () -> {
			notificationServiceImpl.saveNotificationLog(null, MESSAGE, NotificationType.EMAIL,
					NotificationStatus.SUCCESS);
		});
	}
	
	@Test
	void testNotificationStatusIsRequired() {
		assertThrows(ConstraintViolationException.class, () -> {
			notificationServiceImpl.saveNotificationLog(USER, MESSAGE, NotificationType.EMAIL, null);
		});
	}
	
	@Test
	void testNotificationTypeIsRequired() {
		assertThrows(ConstraintViolationException.class, () -> {
			notificationServiceImpl.saveNotificationLog(USER, MESSAGE, null, NotificationStatus.SUCCESS);
		});
	}

}
