package com.grtship.core.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.grtship.core.enumeration.ActionType;
import com.grtship.core.annotation.EnableCustomAudit;
import com.grtship.core.enumeration.NotificationType;
import com.grtship.core.enumeration.UserType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
@EnableCustomAudit
public class NotificationDTO extends BaseDTO {
	
   private String email;
   
   private String login;
	
   private String contactNo;
   
   @Builder.Default
   private NotificationType notificationType = NotificationType.EMAIL;
   
   private String activationKey;
   
   private boolean activated;
   
   private UserType userType;
   
   private String langKey;
   
   private String verificationCode;
   
   private String resetKey;
   
   private String moduleName;
   
   private ActionType actionType;
   
   private Long referenceId;
   
   private Long roleId;
}
