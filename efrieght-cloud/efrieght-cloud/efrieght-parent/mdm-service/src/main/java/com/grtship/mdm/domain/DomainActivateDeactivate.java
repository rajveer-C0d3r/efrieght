package com.grtship.mdm.domain;


import java.io.Serializable;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.Data;

@Data
@Embeddable
public class DomainActivateDeactivate implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Column(name = "deactivation_wef_date")
	private LocalDate deactivationWefDate;
	
	@Column(name = "deactivate_date")
	private LocalDate deactivatedDate;
	
	@Column(name = "reactivation_wef_date")
	private LocalDate reactivationWefDate;
	
	@Column(name = "reactivated_date")
	private LocalDate reactivatedDate;
	
	@Column(name = "deactivate_reason")
	private String deactivationReason;
	
	@Column(name = "reactivattion_reason")
	private String reactivationReason;
	
	@Column(name = "deactivate_id")
	private String deactivateAutoGenerateId;
}
