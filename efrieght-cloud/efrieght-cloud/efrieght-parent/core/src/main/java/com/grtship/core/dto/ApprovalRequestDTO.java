package com.grtship.core.dto;

import java.util.List;
import java.util.Set;

import com.grtship.core.enumeration.ActionType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ApprovalRequestDTO {
   private String moduleName;
   private ActionType action;
   private Set<Long> roleIds;
   private List<NotificationDTO> notificationDTOs;
   @Builder.Default
   private Boolean makerAsApprover=Boolean.FALSE;
   private Long referenceId;
}
