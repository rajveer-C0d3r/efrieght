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

@Data
@ToString
@EqualsAndHashCode
@EnableCustomAudit
public class ReactivationDTO implements Serializable {
	@IgnoreAuditField
	private static final long serialVersionUID = 1L;

	@NotNull(message = "Reference Id can't be null")
	private Long referenceId;
	
	@NotNull(message = "Reactivation WEF Date can't be null.")
	private LocalDate reactivationWefDate;
	
	private LocalDate reactivatedDate;
	
	@NotNull(message = "Deactivation Reason can't be null.")
	private String reactivationReason;
	
	@NotNull(message = "Deactivation/Reactivation type can't be null.")
	@NotBlank(message = "Deactivation/Reactivation type can't be null.")
	private String type;
}
