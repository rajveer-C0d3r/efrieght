package com.grtship.client.domain;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.ParamDef;

import com.grtship.core.annotation.EnableCustomAudit;
import com.grtship.core.enumeration.DomainStatus;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * A CompanyBranch.
 */
@Entity
@Table(name = "company_branch")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Data
@EqualsAndHashCode(callSuper = true)
@FilterDef(name = "clientAccessFilter", parameters = { @ParamDef(name = "clientId", type = "long")})
@FilterDef(name = "companyAccessFilter", parameters = { @ParamDef(name = "companyId", type = "long")})
@Filters({
	@Filter(name = "clientAccessFilter", condition = "client_id = :clientId"),
	@Filter(name = "companyAccessFilter", condition = "company_id = :companyId"),
})
@EnableCustomAudit
public class CompanyBranch extends AbstractAuditingEntity  {

    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	protected Long id;

    @NotBlank(message = "Branch comde can't be empty!")
    @Size(max = 6,message = "Maximum size Of Code must be 6!")
    @Column(name = "code", nullable = false)
    private String code;
    
    @Column(name="client_id")
    @NotNull(message = "Client Id can't be empty.")
    private Long clientId;
    
    @NotBlank(message = "Branch Name can't be empty.")
    @Size(max = 20, message = "Maximum size of Name must be 20.")
    @Column(name = "name", nullable = false)
    private String name;

    @Embedded
    private Address address;

    @OneToMany
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @Cascade(CascadeType.ALL)
    private Set<BranchGstDetails> branchGstDetails;

    @ManyToOne
    @NotNull(message = "company must not be empty.")
    private Company company;
    
    @Column(name = "status")
    @NotNull(message = "Status can't be null.")
    @Enumerated(EnumType.STRING)
    private DomainStatus status;
    
    @Column(name = "submit_for_approval")
    private Boolean submittedForApproval; // after approval set to true.
    
    @Column(name = "active_flag")
    private Boolean activeFlag = Boolean.FALSE;
    
    // activate reactivate fields
    @Embedded
    private DomainDeactivate deactivateDtls;
    @Embedded 
    private DomainReactivate reactivateDtls;
    
    // jhipster-needle-entity-add-field - JHipster will add fields here

    public void setCompany(Company company) {
    	if(company!=null && company.getId()!=null) 
    		this.company = company;
    }
	public CompanyBranch code(String code) {
        this.code = code;
        return this;
    }

    public CompanyBranch name(String name) {
        this.name = name;
        return this;
    }

    public CompanyBranch address(Address branchAddress) {
        this.address = branchAddress;
        return this;
    }

    public CompanyBranch branchGstDetails(Set<BranchGstDetails> branchGstDetails) {
        this.branchGstDetails = branchGstDetails;
        return this;
    }

    public CompanyBranch addBranchGstDetails(BranchGstDetails branchGstDetails) {
        this.branchGstDetails.add(branchGstDetails);
        return this;
    }
    
    public CompanyBranch removeBranchGstDetails(BranchGstDetails branchGstDetails) {
        this.branchGstDetails.remove(branchGstDetails);
        return this;
    }

    public CompanyBranch company(Company company) {
        this.company = company;
        return this;
    }

	public Long getCompanyId() {
		return (this.company!=null) ? this.company.getId() : null;
	}
}
