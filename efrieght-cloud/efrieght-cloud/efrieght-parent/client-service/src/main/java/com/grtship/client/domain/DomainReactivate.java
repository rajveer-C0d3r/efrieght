package com.grtship.client.domain;
import java.io.Serializable;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.Data;


/**
 * FIXME this is duplicated entity needs to move in common module.
*/
@Data
@Embeddable
public class DomainReactivate implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Column(name = "reactivation_wef_date")
	private LocalDate reactivationWefDate;
	
	@Column(name = "reactivated_date")
	private LocalDate reactivatedDate;
	
	@Column(name = "reactivattion_reason")
	private String reactivationReason;
}
