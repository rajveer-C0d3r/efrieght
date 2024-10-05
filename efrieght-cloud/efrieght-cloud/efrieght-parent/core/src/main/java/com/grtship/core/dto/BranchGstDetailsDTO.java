package com.grtship.core.dto;

import java.io.Serializable;

import com.grtship.core.annotation.EnableCustomAudit;
import com.grtship.core.annotation.IgnoreAuditField;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * A DTO for the {@link com.grtship.efreight.client.domain.BranchGstDetails} entity.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@EnableCustomAudit
public class BranchGstDetailsDTO extends AbstractAuditingDTO implements Serializable {
    
    /**
	 * 
	 */
	@IgnoreAuditField
	private static final long serialVersionUID = 1683591038343538821L;

	private Long id;

    private String gstNo;

    private String natureOfBusinessActivity;

    private Long companyId;
}
