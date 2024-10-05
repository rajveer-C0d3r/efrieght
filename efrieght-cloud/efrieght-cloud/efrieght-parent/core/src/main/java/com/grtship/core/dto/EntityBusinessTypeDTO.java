package com.grtship.core.dto;

import java.io.Serializable;

import com.grtship.core.annotation.EnableCustomAudit;
import com.grtship.core.annotation.IgnoreAuditField;
import com.grtship.core.enumeration.EntityType;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * A DTO for the {@link com.grt.efreight.domain.EntityBusinessType} entity.
 */
@Data
@ToString
@EqualsAndHashCode
@EnableCustomAudit
public class EntityBusinessTypeDTO implements Serializable {
    
    /**
	 * 
	 */
	@IgnoreAuditField
	private static final long serialVersionUID = 1L;
	
	private EntityType entityType;
	
	private Long externalEntityId;
	private String name;
    
}
