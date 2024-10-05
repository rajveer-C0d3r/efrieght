package com.grt.elogfrieght.services.user.domain;

import java.io.Serializable;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import com.grtship.core.annotation.EnableCustomAudit;

import lombok.Data;

@Data
@Embeddable
@EnableCustomAudit
public class Reactivate implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5448206764493506011L;

	@Column(name="reactivation_wef_date")
	private LocalDate wefDate;
	
	@Column(name="reactivated_date")
	private LocalDate reactivatedDate;
	
	@Column(name="reactivation_reason", length = 225)
	private String reason;
}
