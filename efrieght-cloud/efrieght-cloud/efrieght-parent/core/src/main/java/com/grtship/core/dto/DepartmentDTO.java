package com.grtship.core.dto;

import javax.validation.constraints.NotNull;

import com.grtship.core.annotation.EnableCustomAudit;
import com.grtship.core.annotation.IgnoreAuditField;
import com.grtship.core.enumeration.DepartmentType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * A DTO for the {@link com.grt.efreight.domain.Department} entity.
 */
@Builder
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@EnableCustomAudit
public class DepartmentDTO extends AbstractAuditingDTO {
    
    /**
	 * 
	 */
	@IgnoreAuditField
	private static final long serialVersionUID = -5466732077969496237L;

	private Long id;

    private String code;

    @NotNull(message = "Department Name can't be null.")
    private String name;

    @NotNull(message = "Department Type can't be null.")
    private DepartmentType type;
    
    @NotNull(message = "clientId can't be null.")
    private Long clientId;
    private Long companyId;

}
