package com.grtship.core.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.grtship.core.annotation.EnableCustomAudit;
import com.grtship.core.annotation.IgnoreAuditField;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * A DTO for the {@link com.grt.efreight.domain.Sector} entity.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@EnableCustomAudit
public class SectorDTO extends ClientAuditableEntityDTO {
	@IgnoreAuditField
	private static final long serialVersionUID = 1L;

	private Long id;
	
    private String code;

	@NotNull(message = "Sector Name Is Mandatory, Please Enter Sector Name.")
    @NotEmpty(message = "Sector Name Is Mandatory, Please Enter Sector Name.")
    private String name;

}
