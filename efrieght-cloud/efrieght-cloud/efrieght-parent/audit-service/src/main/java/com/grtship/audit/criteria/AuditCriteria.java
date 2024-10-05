/**
 * 
 */
package com.grtship.audit.criteria;

import java.time.LocalDate;

import lombok.Data;

/**
 * @author ER Ajay Sharma
 *
 */
@Data
public class AuditCriteria {
	private Long referenceId;
	private String referenceType;
	private LocalDate startDate;
	private LocalDate endDate;
}
