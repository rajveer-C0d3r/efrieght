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
public class Deactivate implements Serializable {
	/**
	 * 
	 */
	
	private static final long serialVersionUID = -1795128837295607034L;

	@Column(name = "deactivation_wef_date")
	private LocalDate wefDate;

	@Column(name = "deactivated_date")
	private LocalDate deactivatedDate;

	@Column(name = "deactivation_reason", length = 225)
	private String reason;
	
	@Column(name = "deactivate_id")
	private String deactivateAutoGenerateId;
	
	public Deactivate reason(String reason) {
		this.reason = reason;
		return this;
	}

	public Deactivate wefDate(LocalDate wefDate) {
		this.wefDate = wefDate;
		return this;
	}

}