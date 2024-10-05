package com.grtship.notification.service;

import com.grtship.core.enumeration.NotificationStatus;
import com.grtship.core.enumeration.NotificationType;

public interface NotificationLogService {
  public void saveNotificationLog(String user,String Message,NotificationType notificationType,NotificationStatus notificationStatus);;
}
