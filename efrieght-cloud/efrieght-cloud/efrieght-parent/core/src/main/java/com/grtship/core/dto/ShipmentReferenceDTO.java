package com.grtship.core.dto;

import java.util.Set;

import com.grtship.core.annotation.EnableAuditLevel;
import com.grtship.core.annotation.EnableCustomAudit;
import com.grtship.core.annotation.IgnoreAuditField;
import com.grtship.core.enumeration.ReferenceType;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * A DTO for the {@link com.grt.efreight.account.domain.ShipmentReference} entity.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@EnableCustomAudit
public class ShipmentReferenceDTO extends AbstractAuditingDTO {
	@IgnoreAuditField
	private static final long serialVersionUID = 1L;

	private Long id;

    private ReferenceType referenceType;
    
    private String referenceNo;

    private Set<String> referenceNos;
    
    @EnableAuditLevel(idOnly = true)
    private Set<BaseDTO> responseReferenceNos;
    
}
