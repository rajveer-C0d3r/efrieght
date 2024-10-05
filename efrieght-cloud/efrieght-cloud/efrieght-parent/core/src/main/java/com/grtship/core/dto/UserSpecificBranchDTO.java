package com.grtship.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserSpecificBranchDTO {
	private Long clientId;
	private Long companyId;
	private Long branchId;
	private String code;
	private String name;
	private Boolean isBranchDeactivated;
}
