package com.grtship.mdm.domain;

import java.io.Serializable;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.Data;

@Data
@Embeddable
public class Deactivate implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1795128837295607034L;

	@Column(name = "deactivation_wef_date")
	private LocalDate wefDate;
	
	@Column(name="deactivated_date")
	private LocalDate deactivatedDate;
	
	@Column(name="deactivation_reason")
	private String reason;

}
