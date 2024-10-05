package com.grtship.core.dto;

import java.io.Serializable;
import java.time.LocalDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.grtship.core.annotation.EnableCustomAudit;
import com.grtship.core.annotation.IgnoreAuditField;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

// USE COMMON DEACTIVATION DTO ONCE INSTANT IS REPLACED. 
@Data
@ToString
@EqualsAndHashCode
@EnableCustomAudit
public class VesselReactivationDto implements Serializable{

	/**
	 * 
	 */
	@IgnoreAuditField
	private static final long serialVersionUID = 1L;
	
	@NotNull(message = "Reference Id can't be null")
	private Long referenceId; 
	private LocalDate reactivationWefDate;
	private LocalDate reactivatedDate;
	@NotBlank(message = "Reactivation type can't be null.")
	private String type; 
}
