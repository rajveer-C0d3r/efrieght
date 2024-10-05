package com.grt.elogfrieght.services.user.domain;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.ParamDef;

import com.grtship.core.annotation.EnableCustomAudit;

import lombok.Data;
import lombok.EqualsAndHashCode;

@MappedSuperclass
@FilterDef(name = "clientAccessFilter", parameters = { @ParamDef(name = "clientId", type = "long"), @ParamDef(name="includeAdmin", type="int") })
@FilterDef(name = "companyAccessFilter", parameters = { @ParamDef(name = "companyId", type = "long"), @ParamDef(name="includeAdmin", type="int") })
@FilterDef(name = "branchAccessFilter", parameters = { @ParamDef(name = "branchId", type = "long"), @ParamDef(name="includeAdmin", type="int") })

@Filters({ @Filter(name = "clientAccessFilter", condition = "(client_id = :clientId or (client_id = 0 and is_public=1 and 1=:includeAdmin))"),
		@Filter(name = "companyAccessFilter", condition = "(company_id = :companyId or (client_id = 0 and is_public=1 and 1=:includeAdmin))"),
		@Filter(name = "branchAccessFilter", condition = "(branch_id = :branchId or (client_id = 0 and is_public=1 and 1=:includeAdmin))" ) })
@Data
@EqualsAndHashCode(callSuper = true)
@EnableCustomAudit
public abstract class Domain extends AbstractAuditingEntity {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5453740763848516138L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	protected Long id;

	@Column(name = "company_id")
	protected Long companyId; // for admin portal this value should be 0 always

	@Column(name = "client_id")
	protected Long clientId;

	@Column(name = "branch_id")
	protected Long branchId;

}
