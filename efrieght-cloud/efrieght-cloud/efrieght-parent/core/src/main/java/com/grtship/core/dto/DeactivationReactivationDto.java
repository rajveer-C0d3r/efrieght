package com.grtship.core.dto;

import java.io.Serializable;
import java.time.Instant;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.grtship.core.annotation.EnableCustomAudit;
import com.grtship.core.annotation.IgnoreAuditField;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString
@EqualsAndHashCode
@EnableCustomAudit
public class DeactivationReactivationDto implements Serializable{

	/**
	 * 
	 */
	@IgnoreAuditField
	private static final long serialVersionUID = 1L;
	
	@NotNull(message = "Reference Id can't be null")
	private Long referenceId; // ex. entity, branch
	
	private Instant deactivationWefDate;
	
	private Instant deactivatedDate;
	
	private Instant reactivationWefDate;
	
	private Instant reactivatedDate;
	
	private String deactivationReason;
	
	private String reactivationReason;
	
	@NotBlank(message = "Deactivation/Reactivation type can't be null.")
	private String type; //deactivate or reactivate
}
