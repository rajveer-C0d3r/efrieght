package com.grtship.core.dto;

import java.io.Serializable;
import java.util.Set;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.grtship.core.annotation.EnableAuditLevel;
import com.grtship.core.annotation.EnableCustomAudit;
import com.grtship.core.annotation.IgnoreAuditField;
import com.grtship.core.enumeration.DomainStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A DTO for the {@link com.grt.efreight.domain.Role} entity.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EnableCustomAudit
public class RoleDTO implements Serializable {
    
    /**
	 * 
	 */
	@IgnoreAuditField
	private static final long serialVersionUID = 3398450225377766033L;

	private Long id;   

    @NotNull(message = "Role name is mandatory")
    private String name;

    private Boolean isPublic;
    private DomainStatus status;
    private Boolean activeFlag;
    private Boolean submittedForApproval;
    private Long companyId;
    private Long clientId;
    
    @EnableAuditLevel(level = 1)
    private DeactivationDTO deactivate;
    
    @EnableAuditLevel(level = 1)
    private ReactivationDTO reactivate;
    private Boolean isSystemCreated;
    private String createdBy;
    
    @EnableAuditLevel(level = 1)
    @NotEmpty(message = "Permissions are mandatory.")
    private Set<PermissionDTO> permissions;

}
