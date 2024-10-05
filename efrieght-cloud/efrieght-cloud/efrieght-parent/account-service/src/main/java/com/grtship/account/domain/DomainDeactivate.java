package com.grtship.account.domain;

import java.io.Serializable;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.Data;

@Data
@Embeddable
public class DomainDeactivate implements Serializable {

	private static final long serialVersionUID = -5107544614881266243L;

	@Column(name = "deactivation_wef_date")
	private LocalDate wefDate;

	@Column(name = "deactivated_date")
	private LocalDate deactivatedDate;

	@Column(name = "deactivate_reason")
	private String reason;

	@Column(name = "deactivate_id")
	private String deactivateAutoGenerateId;

	public DomainDeactivate reason(String reason) {
		this.reason = reason;
		return this;
	}

	public DomainDeactivate wefDate(LocalDate wefDate) {
		this.wefDate = wefDate;
		return this;
	}

}
