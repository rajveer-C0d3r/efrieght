package com.grtship.core.dto;

import java.io.Serializable;
import java.time.LocalDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.grtship.core.annotation.EnableCustomAudit;
import com.grtship.core.annotation.IgnoreAuditField;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString
@EqualsAndHashCode
@EnableCustomAudit
public class DeactivationDTO implements Serializable {
	/**
	 * 
	 */
	@IgnoreAuditField
	private static final long serialVersionUID = 1L;

	@NotNull(message = "Reference Id can't be null")
	private Long referenceId; // ex. entity, branch
	
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
//	@JsonFormat(pattern = "yyyy-MM-dd@HH:mm:ss.SSSZ")
	@NotNull(message = "Deactivation WEF Date can't be null.")
	private LocalDate deactivationWefDate;
	
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
	private LocalDate deactivatedDate;
	
	@NotNull(message = "Deactivation Reason can't be null.")
	private String deactivationReason;
	
	@NotBlank(message = "Deactivation/Reactivation type can't be null.")
	private String type; //deactivate or reactivate
	
	
	
	@JsonIgnore
	private String deactivateAutoGenerateId;
	
}
