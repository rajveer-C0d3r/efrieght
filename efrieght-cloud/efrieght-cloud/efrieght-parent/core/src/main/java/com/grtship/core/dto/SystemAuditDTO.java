/**
 * 
 */
package com.grtship.core.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import com.grtship.core.annotation.EnableCustomAudit;

import lombok.Data;

/**
 * @author Ajay
 *
 */
@Data
@EnableCustomAudit
public class SystemAuditDTO {
	private Long id;
	private Long referenceId;
	private String referenceType;
	private String action;
	private Long version;
	private String createdBy;
	private LocalDate createdOnDate;
	private LocalTime createdOnTime;
	private Long sequence;
	private List<SystemAuditDataDto> auditData;
}
