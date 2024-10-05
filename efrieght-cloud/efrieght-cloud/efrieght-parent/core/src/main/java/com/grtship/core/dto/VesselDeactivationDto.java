package com.grtship.core.dto;

import java.io.Serializable;
import java.time.LocalDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.grtship.core.annotation.EnableCustomAudit;
import com.grtship.core.annotation.IgnoreAuditField;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

// USE COMMON DEACTIVATION DTO ONCE INSTANT IS REPLACED. 
@Builder
@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@EnableCustomAudit
public class VesselDeactivationDto implements Serializable{

	/**
	 * 
	 */
	@IgnoreAuditField
	private static final long serialVersionUID = 1L;
	
	@NotNull(message = "Reference Id can't be null")
	private Long referenceId; // ex. vessel, 
	private LocalDate deactivationWefDate;
	private LocalDate deactivatedDate;
	private String deactivationReason;
	@NotBlank(message = "Deactivation type can't be null.")
	private String type; //deactivate or reactivate
}
