package com.grtship.core.dto;

import java.io.Serializable;
import java.time.LocalDate;

import com.grtship.core.annotation.EnableCustomAudit;
import com.grtship.core.annotation.IgnoreAuditField;

import lombok.Data;

@Data
@EnableCustomAudit
public class DomainActivateDeactivateDTO implements Serializable{
	/**
	 * 
	 */
	@IgnoreAuditField
	private static final long serialVersionUID = 1L;
	
	private LocalDate deactivationWefDate;
	private LocalDate deactivatedDate;
	private LocalDate reactivationWefDate;
	private LocalDate reactivatedDate;
	private String deactivationReason;
	private String reactivationReason;
}
