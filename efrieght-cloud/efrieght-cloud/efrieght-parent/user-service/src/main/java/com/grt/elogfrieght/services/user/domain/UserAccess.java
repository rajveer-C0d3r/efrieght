package com.grt.elogfrieght.services.user.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.grtship.core.annotation.EnableCustomAudit;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A UserAccess.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@EnableCustomAudit
public class UserAccess implements Serializable {

	private static final long serialVersionUID = 1L;

	@Column(name = "branch_id")
	private Long branchId;

	@Column(name = "company_id")
	private Long companyId;

	@Column(name = "all_branches", columnDefinition = "tinyint(1) default 0")
	private Boolean allBranches;

	public Long getBranchId() {
		return branchId;
	}

	public UserAccess allBranches(Boolean allBranches) {
		this.allBranches = allBranches;
		return this;
	}

	public UserAccess branchId(Long branchId) {
		this.branchId = branchId;
		return this;
	}

	public void setBranchId(Long branchId) {
		this.branchId = branchId;
	}

	public Long getCompanyId() {
		return companyId;
	}

	public UserAccess companyId(Long companyId) {
		this.companyId = companyId;
		return this;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	public UserAccess(Long branchId, Long companyId) {
		super();
		this.branchId = branchId;
		this.companyId = companyId;
	}

	// jhipster-needle-entity-add-getters-setters - JHipster will add getters and
	// setters here

}
