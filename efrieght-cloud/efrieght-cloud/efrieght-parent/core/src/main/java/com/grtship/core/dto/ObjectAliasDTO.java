package com.grtship.core.dto;

import com.grtship.core.annotation.EnableCustomAudit;
import com.grtship.core.annotation.IgnoreAuditField;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * A DTO for the {@link com.grt.efreight.domain.ObjectAlias} entity.
 */
@Data
@ToString
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EnableCustomAudit
public class ObjectAliasDTO extends ClientAuditableEntityDTO {
    
    /**
	 * 
	 */
	@IgnoreAuditField
	private static final long serialVersionUID = 1L;
	
	private Long id;

    private String name;

    private String referenceName;

    private Long referenceId;
    
    private Long clientId;
    private Long companyId;

    
   }
