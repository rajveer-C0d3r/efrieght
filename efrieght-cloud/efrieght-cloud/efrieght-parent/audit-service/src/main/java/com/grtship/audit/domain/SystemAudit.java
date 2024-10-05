/**
 * 
 */
package com.grtship.audit.domain;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Ajay
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "obj_audit")
public class SystemAudit {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	private Long referenceId;
	@Column(name = "obj_name")
	@Size(max = 45, message = "Maximum obj_name for audit must be 45.")
	private String referenceType;
	@Size(max = 30, message = "Maximum obj_name for audit must be 30.")
	private String action;
	private Long version;
	@Column(name = "action_by")
	private String createdBy;
//	private String owner;
	private Long sequence;
	@Column(name = "action_date")
	private LocalDate createdOnDate;
	@Column(name = "action_time")
	private LocalTime createdOnTime;
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private List<SystemAuditData> auditData;

}
