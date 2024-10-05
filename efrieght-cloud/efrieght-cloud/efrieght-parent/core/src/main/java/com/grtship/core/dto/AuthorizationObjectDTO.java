package com.grtship.core.dto;

import com.grtship.core.enumeration.ActionType;
import com.grtship.core.enumeration.DomainStatus;
import com.grtship.core.enumeration.ModuleName;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthorizationObjectDTO {
	private ActionType action;
	private ModuleName moduleName;
	private Long clientId;
	private Long companyId;
	private Long referenceId;
	private DomainStatus status;
}
