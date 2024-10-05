package com.grtship.core.dto;

import javax.validation.constraints.NotNull;

import com.grtship.core.annotation.EnableCustomAudit;
import com.grtship.core.annotation.IgnoreAuditField;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * A DTO for the {@link com.grt.efreight.domain.Designation} entity.
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@EnableCustomAudit
public class DesignationDTO extends ClientAuditableEntityDTO {
    
    /**
	 * 
	 */
	@IgnoreAuditField
	private static final long serialVersionUID = 5844692876709345988L;

	private Long id;

    private String code;

    @NotNull(message = "Designation name is mandatory.")
    private String name;

}
