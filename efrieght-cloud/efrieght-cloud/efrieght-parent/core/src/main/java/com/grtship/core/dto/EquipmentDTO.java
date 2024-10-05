package com.grtship.core.dto;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import com.grtship.core.annotation.EnableCustomAudit;
import com.grtship.core.annotation.IgnoreAuditField;
import com.grtship.core.enumeration.EquipmentType;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * A DTO for the {@link com.grt.efreight.domain.Equipment} entity.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@EnableCustomAudit
public class EquipmentDTO extends AbstractAuditingDTO implements Serializable {

	/**
	 * 
	 */
	@IgnoreAuditField
	private static final long serialVersionUID = -7327120139329649132L;

	private Long id;

	@NotNull
	private String name;

	private String code;

	private EquipmentType type;
}
