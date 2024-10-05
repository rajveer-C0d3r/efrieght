package com.grtship.mdm.domain;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.ParamDef;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Base abstract class for entities which will hold definitions for client id, company id attributes.
 * 
 */
@MappedSuperclass
@Data
@EqualsAndHashCode(callSuper = true)
@FilterDef(name="mdmAdminAccessFilter")
@FilterDef(name = "mdmClientAccessFilter", parameters = { 
		@ParamDef(name="clientId", type = "long"), 
		@ParamDef(name="includeAdmin", type="int") },
		defaultCondition = "client_id = :clientId or (client_id=0 and 1 = :includeAdmin)")
@FilterDef(name = "mdmCompanyAccessFilter", parameters = { @ParamDef(name = "companyId", type = "long"), @ParamDef(name="includeAdmin", type="int") })
@FilterDef(name = "mdmBranchAccessFilter", parameters = { @ParamDef(name = "branchId", type = "long"), @ParamDef(name="includeAdmin", type="int") })
@Filters({ 
		@Filter(name = "mdmAdminAccessFilter", condition = "(client_id = 0  and is_public=1)"),
	    @Filter(name = "mdmClientAccessFilter", condition = "(client_id = :clientId or (client_id=0  and 1 = :includeAdmin))"),
		@Filter(name = "mdmCompanyAccessFilter", condition = "(company_id = :companyId or (client_id = 0  and is_public=1 and 1 = :includeAdmin))"),
		@Filter(name = "mdmBranchAccessFilter", condition = "(branch_id = :branchId or (client_id = 0  and is_public=1 and 1 = :includeAdmin))" ) })
public abstract class ClientAuditableEntity extends AbstractAuditingEntity {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
    @Column(name = "client_id", nullable = true)
	private Long clientId;
    
    @Column(name = "company_id", nullable = true)
	private Long companyId;
    
    @Column(name = "branch_id", nullable = true)
    private Long branchId;

}
