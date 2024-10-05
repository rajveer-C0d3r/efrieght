package com.grtship.core.dto;

import com.grtship.core.annotation.EnableCustomAudit;
import com.grtship.core.annotation.IgnoreAuditField;
import com.grtship.core.enumeration.DomainStatus;
import com.grtship.core.enumeration.EntityType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * A DTO for the {@link com.grt.efreight.domain.CreditTerms} entity.
 */
@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@EnableCustomAudit
public class CreditTermsDTO extends AbstractAuditingDTO  {
    
    /**
	 * 
	 */
	@IgnoreAuditField
	private static final long serialVersionUID = 1L;

	private Long id;

    private DomainStatus status;

    private Integer creditDays;

    private Double creditAmount;

    private Long referenceId;
    private String referenceName;

    private EntityType entityType; //customer or vendor

}
