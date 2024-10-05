package com.grtship.core.dto;

import com.grtship.core.annotation.EnableCustomAudit;
import com.grtship.core.annotation.IgnoreAuditField;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * A DTO for the {@link com.grt.efreight.domain.EntityGroups} entity.
 */
@Data
@EqualsAndHashCode(callSuper = true)	
@NoArgsConstructor
@AllArgsConstructor
@EnableCustomAudit
public class EntityGroupDTO extends ClientAuditableEntityDTO {
    
    /**
	 * 
	 */
	@IgnoreAuditField
	private static final long serialVersionUID = 1L;

	private Long id;

    private String name;

    private String code;

}
