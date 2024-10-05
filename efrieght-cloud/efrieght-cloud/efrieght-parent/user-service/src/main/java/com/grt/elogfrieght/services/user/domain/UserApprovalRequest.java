package com.grt.elogfrieght.services.user.domain;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.grtship.core.enumeration.ActionType;
import com.grtship.core.enumeration.DomainStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class UserApprovalRequest{
   @Id
   @GeneratedValue(strategy = GenerationType.AUTO)
   private Long id;
   private String email;
   private Long userId;
   private String moduleName;
   @Enumerated(EnumType.STRING)
   private ActionType action;
   private Long referenceId;
   @Enumerated(EnumType.STRING)
   private DomainStatus status;
   private String permissionCode;
}
