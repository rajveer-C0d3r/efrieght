package com.grtship.core.dto;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import com.grtship.core.annotation.EnableCustomAudit;
import com.grtship.core.annotation.IgnoreAuditField;

import lombok.Data;

/**
 * A DTO for the {@link com.grt.efreight.domain.EntityBranchTax} entity.
 */
@Data
@EnableCustomAudit
public class EntityBranchTaxDTO implements Serializable {
    
    /**
	 * 
	 */
	@IgnoreAuditField
	private static final long serialVersionUID = 1L;

	private Long id;

	@NotNull(message = "Gst No is mandatory.")
    private String gstNo;
    
    private String description;
    
    private Long clientId;
    private Long companyId;
    
    @NotNull(message = "Branch Id is mandatory.")
    private Long entityBranchId;

}
