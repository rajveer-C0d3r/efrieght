package com.grtship.core.dto;

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
public class UserApprovalRequestDTO {
	private Long id;
	private String email;
	private Long userId;
	private String moduleName;
	private ActionType action;
	private Long referenceId;
	private DomainStatus status;
	private String permissionCode;
}
