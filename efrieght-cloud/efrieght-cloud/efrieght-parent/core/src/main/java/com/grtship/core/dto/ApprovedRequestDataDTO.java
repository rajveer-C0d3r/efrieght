package com.grtship.core.dto;

import javax.validation.constraints.NotNull;

import com.grtship.core.enumeration.DomainStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApprovedRequestDataDTO {
	
	@NotNull(message = "Module Name is required")
    private String moduleName;
	@NotNull(message = "Reference Id is required")
    private Long referenceId;
	@NotNull(message = "Status is required")
    private DomainStatus status;
    private String email;
    private String comment;
    @NotNull(message = "Permission Code is required")
    private String permissionCode;
}
