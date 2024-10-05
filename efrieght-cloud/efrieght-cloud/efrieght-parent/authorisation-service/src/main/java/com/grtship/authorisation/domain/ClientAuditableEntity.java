package com.grtship.authorisation.domain;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.ParamDef;

import lombok.Data;

@MappedSuperclass
@Data
@FilterDef(name = "clientAccessFilter", parameters = { @ParamDef(name = "clientId", type = "long") })
@FilterDef(name = "companyAccessFilter", parameters = { @ParamDef(name = "companyId", type = "long") })
@FilterDef(name = "branchAccessFilter", parameters = { @ParamDef(name = "branchId", type = "long") })

@Filters({ @Filter(name = "clientAccessFilter", condition = "client_id = :clientId"),
		@Filter(name = "companyAccessFilter", condition = "company_id = :companyId"),
		@Filter(name = "branchAccessFilter", condition = "branch_id = :branchId") })
public abstract class ClientAuditableEntity extends AbstractAuditingEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Column(name = "client_id")
	private Long clientId;

	@Column(name = "company_id")
	private Long companyId;

}
