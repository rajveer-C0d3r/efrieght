package com.grtship.core.dto;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import com.grtship.core.annotation.EnableCustomAudit;
import com.grtship.core.annotation.IgnoreAuditField;
import com.grtship.core.enumeration.PermissionType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A DTO for the {@link com.grt.oath2.domain.Permission} entity.
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@EnableCustomAudit
public class PermissionDTO implements Serializable {
    
    /**
	 * 
	 */
	@IgnoreAuditField
	private static final long serialVersionUID = 1170813224355610244L;

	@NotNull
    private String permissionLabel;

    @NotNull
    private String permissionCode;

    @NotNull
    private String moduleName;
    
    private Boolean allPermissions;
    private PermissionType permissionType;

}
