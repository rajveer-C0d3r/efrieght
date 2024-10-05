package com.grtship.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserSpecificCompanyDTO {
	private Long companyId;
	private Long clientId;
	private String code;
	private String name;
	private Boolean isCompanyDeactivated;
}
