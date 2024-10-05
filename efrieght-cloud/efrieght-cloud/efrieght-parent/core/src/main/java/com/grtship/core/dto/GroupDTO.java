package com.grtship.core.dto;

import com.grtship.core.annotation.EnableAuditLevel;
import com.grtship.core.annotation.EnableCustomAudit;
import com.grtship.core.annotation.IgnoreAuditField;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * A DTO for the {@link com.grt.efreight.account.domain.Group} entity.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@EnableCustomAudit
public class GroupDTO extends GroupCreationDTO {

    
    /**
	 * 
	 */
	@IgnoreAuditField
	private static final long serialVersionUID = 1L;

    private String parentGroupName;
   
    @EnableAuditLevel(level = 1)
    private DeactivationDTO deactivate;
    
    @EnableAuditLevel(level = 1)
	private ReactivationDTO reactivate;
	
	private Boolean activeFlag = false;

}
