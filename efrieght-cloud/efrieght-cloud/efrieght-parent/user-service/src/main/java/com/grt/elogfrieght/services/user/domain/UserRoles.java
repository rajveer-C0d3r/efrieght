package com.grt.elogfrieght.services.user.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.grtship.core.annotation.EnableCustomAudit;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A UserAccess.
 */
@Embeddable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Data 
@NoArgsConstructor
@AllArgsConstructor
@EnableCustomAudit
public class UserRoles implements Serializable {

	private static final long serialVersionUID = 1L;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@Fetch(FetchMode.JOIN)
	@JsonIgnoreProperties(value = "userRoles", allowSetters = true)
	private Role roles;

	@Column(name = "company_id")
	private Long companyId;
	
	@Column(name="branch_id")
    private Long branchId;
	
	// jhipster-needle-entity-add-field - JHipster will add fields here
	
	public void setRoleId(Long roleId) {
		if	(this.roles==null) {
			this.roles = new Role(roleId);
		}else {
			this.roles.setId(roleId); 
		}
	}
	
	public Long getRoleId() {
		return (this.roles!=null) ? this.roles.getId() : null;
	}
	
	public UserRoles roles(Long roleId) {
		if(this.roles==null) {
			this.roles = new Role(roleId);
		}else {
			this.roles.setId(roleId); 
		}
		return this;
	}
	
	public UserRoles roles(Role role) {
		if(this.roles==null) {
			this.roles = new Role();
		}
		this.roles = role; 
		return this;
	}
	
	public UserRoles companyId(Long companyId) {
		this.companyId = companyId;
		return this;
	}
	
	public UserRoles(Role roles) {
		super();
		this.roles = roles;
	}

	public UserRoles branchId(Long branchId) {
		this.branchId = branchId;
		return this;
	}
	
	

}
