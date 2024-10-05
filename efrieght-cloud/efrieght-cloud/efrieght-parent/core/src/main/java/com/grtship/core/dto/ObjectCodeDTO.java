package com.grtship.core.dto;

import javax.validation.constraints.NotNull;

import com.grtship.core.annotation.EnableCustomAudit;
import com.grtship.core.annotation.IgnoreAuditField;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * A DTO for the {@link com.grt.efreight.domain.ObjectCode} entity.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@EnableCustomAudit
public class ObjectCodeDTO extends ClientAuditableEntityDTO {
    
    /**
	 * 
	 */
	@IgnoreAuditField
	private static final long serialVersionUID = 1L;

	private Long id;

    @NotNull(message = "Object name is mandatory.")
    private String objectName;

    private String prefix;

    private Long padding = 1L;

    @NotNull(message = "Counter is mandatory.")
    private Long counter;
    
    private String parentCode;
    
    private Integer blockSize;
}
