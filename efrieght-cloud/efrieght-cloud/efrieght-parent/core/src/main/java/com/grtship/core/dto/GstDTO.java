package com.grtship.core.dto;

import javax.validation.constraints.NotNull;

import com.grtship.core.annotation.EnableCustomAudit;
import com.grtship.core.annotation.IgnoreAuditField;
import com.grtship.core.enumeration.GstType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * A DTO for the {@link com.grt.efreight.domain.Gst} entity.
 */
@Data
@EqualsAndHashCode(callSuper=true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EnableCustomAudit
public class GstDTO extends ClientAuditableEntityDTO{

	/**
	 * 
	 */
	@IgnoreAuditField
	private static final long serialVersionUID = 1L;

	private Long id;

    @NotNull(message = "HSN/SAC code is mandatory.")
    private String hsnSacCode;

    private String description;

    @NotNull(message = "GST Type is mandatory.")
    private GstType type;
}
