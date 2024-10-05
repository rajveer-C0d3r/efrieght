package com.grtship.core.dto;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import com.grtship.core.annotation.EnableCustomAudit;
import com.grtship.core.annotation.IgnoreAuditField;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A DTO for the {@link com.grt.oath2.domain.UserAccess} entity.
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@EnableCustomAudit
public class UserAccessDTO implements Serializable {
    

    /**
	 * 
	 */
	@IgnoreAuditField
	private static final long serialVersionUID = -6520695866038018003L;

	private Long branchId;

    private Long companyId;

    @NotNull
    private Long userId;
    
    private boolean allBranches;

}
