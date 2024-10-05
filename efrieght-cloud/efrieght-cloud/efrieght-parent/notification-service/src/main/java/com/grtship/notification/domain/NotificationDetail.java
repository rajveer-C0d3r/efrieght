package com.grtship.notification.domain;

import java.time.Instant;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.CreatedDate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.grtship.core.enumeration.NotificationStatus;
import com.grtship.core.enumeration.NotificationType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "notification_detail")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationDetail {
	
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   protected Long id;
	
   @Enumerated(EnumType.STRING)
   @Column(name = "notification_type", nullable = false, length = 25)
   @NotNull(message = "Notification Type is Required")
   private NotificationType notificationType;
   
   @Column(name = "message",nullable = false)
   private String message;
   
   @CreatedDate
   @Column(name = "created_date", updatable = false)
   @JsonIgnore
   @Builder.Default
   private Instant createdDate = Instant.now();
   
   
   @Column(name = "user")
   @NotNull(message = "user is Required")
   private String user;
   
   @Enumerated(EnumType.STRING)
   @Column(name = "status", nullable = false, length = 25)
   @NotNull(message = "Status is Required")
   private NotificationStatus notificationStatus;
}
