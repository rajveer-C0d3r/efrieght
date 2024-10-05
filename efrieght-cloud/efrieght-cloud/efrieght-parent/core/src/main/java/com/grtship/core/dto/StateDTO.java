package com.grtship.core.dto;

import com.grtship.core.annotation.EnableCustomAudit;
import com.grtship.core.annotation.IgnoreAuditField;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * A DTO for the {@link com.grt.efreight.domain.State} entity.
 */

@Data
@EqualsAndHashCode(callSuper = true)
@EnableCustomAudit
public class StateDTO extends AbstractAuditingDTO {

	/**
	 * 
	 */
	@IgnoreAuditField
	private static final long serialVersionUID = 1L;

	private Long id;

    private String code;

    private String name;

    private Long countryId;
}
