/**
 * 
 */
package com.grtship.audit.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author hp
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "obj_audit_data")
public class SystemAuditData {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	private String propertyName;
	private String oldValue;
	private String newValue;
}
