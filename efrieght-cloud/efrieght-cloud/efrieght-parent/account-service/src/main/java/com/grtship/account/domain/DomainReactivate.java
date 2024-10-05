package com.grtship.account.domain;

import java.io.Serializable;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.Data;

@Data
@Embeddable
public class DomainReactivate implements Serializable {

	private static final long serialVersionUID = 5256624371503396348L;

	@Column(name = "reactivation_wef_date")
	private LocalDate wefDate;

	@Column(name = "reactivated_date")
	private LocalDate reactivatedDate;

	@Column(name = "reactivattion_reason")
	private String reason;

}
