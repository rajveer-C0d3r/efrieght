
package com.grtship.mdm.domain;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.ParamDef;

import com.grtship.core.enumeration.DomainStatus;
import com.grtship.core.enumeration.EntityLevel;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * A BranchDetails.
 */
@Entity
@Table(name = "mdm_entity_branch")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Data
@EqualsAndHashCode(callSuper = true)
@FilterDef(name = "clientAccessFilter", parameters = { @ParamDef(name = "clientId", type = "long")})
@FilterDef(name = "companyAccessFilter", parameters = { @ParamDef(name = "companyId", type = "long")})
@FilterDef(name = "branchAccessFilter", parameters = { @ParamDef(name = "branchId", type = "long")})
		
@Filters({
	@Filter(name = "clientAccessFilter", condition = "client_id = :clientId"),
	@Filter(name = "companyAccessFilter", condition = "company_id = :companyId"),
	@Filter(name = "branchAccessFilter", condition = "branch_id = :branchId")
})
public class EntityBranch extends AbstractAuditingEntity{

	private static final long serialVersionUID = 1L;

	@NotBlank(message = "Entity Branch Code Can't Be Null.")
	@Size(max=6,message = "Max size of Code must be 6.")
	@Column(name = "code", nullable = false, length = 6)
	private String code;

	@NotBlank(message = "Entity Branch Name Can't Be Null.")
	@Size(max=60, message = "Max size of Name must be 60.")
	@Column(name = "name", nullable = false, length = 60)
	private String name;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "address_id")
	@NotNull(message = "Address cant be null.")
	private Address address;

	@ManyToOne(optional = false)
	@NotNull(message = "Entity Id is mandatory")
	@JoinColumn(name = "mdm_external_entity_id")
	private ExternalEntity externalEntity;

	@Column(name = "cell_numbers")
	private String cellNumbers;

	@Column(name = "sez")
	private Boolean sez;

	@Column(name = "sez_wef_date")
	private Date sezWEFDate;

	@Column(name = "sez_valid_upto_date")
	private Date sezValidUptoDate;

	@Column(name = "active_flag")
	private Boolean activeFlag ;

	@Enumerated(EnumType.STRING)
	@Column(name = "status")
	@NotNull(message = "Status Must Not be Null.")
	private DomainStatus status;

	// deactivation-reactivation fields
	@Embedded
	private DomainDeactivate deactivateDtls;
	@Embedded
	private DomainReactivate reactivateDtls;

	@Enumerated(EnumType.STRING)
	@Column(name = "customer_branch_level")
	private EntityLevel customerEntityLevel;

	@Enumerated(EnumType.STRING)
	@Column(name = "vendor_branch_level")
	private EntityLevel vendorEntityLevel;

	@Column(name = "customer_flag")
	private Boolean customerFlag = Boolean.FALSE;

	@Column(name = "vendor_flag")
	private Boolean vendorFlag = Boolean.FALSE;

	@Column(name = "default_branch_flag")
	private Boolean defaultBranchFlag = Boolean.FALSE;
	
	//approval submition flag
    @Column(name = "submited_for_approval")
    private Boolean submittedForApproval;
    
    @Column(name = "client_id")
	private Long clientId;
    
    @Column(name = "company_id")
	private Long companyId;
    

	// jhipster-needle-entity-add-field - JHipster will add fields here

	public EntityBranch defaultBranchFlag(Boolean defaultBranchFlag) {
		this.defaultBranchFlag = defaultBranchFlag;
		return this;
	}


	public EntityBranch customerBranchLevel(EntityLevel branchLevel) {
		this.customerEntityLevel = branchLevel;
		return this;
	}

	public EntityBranch vendorBranchLevel(EntityLevel vendorBranchLevel) {
		this.vendorEntityLevel = vendorBranchLevel;
		return this;
	}

	public EntityBranch customerFlag(Boolean customerFlag) {
		this.customerFlag = customerFlag;
		return this;
	}

	public EntityBranch vendorFlag(Boolean vendorFlag) {
		this.vendorFlag = vendorFlag;
		return this;
	}

	public EntityBranch sez(Boolean sez) {
		this.sez = sez;
		return this;
	}
	public EntityBranch sezWEFDate(Date sezWEFDate) {
		this.sezWEFDate = sezWEFDate;
		return this;
	}
	public EntityBranch sezValidUptoDate(Date sezValidUptoDate) {
		this.sezValidUptoDate = sezValidUptoDate;
		return this;
	}

	public EntityBranch status(DomainStatus status) {
		this.status = status;
		return this;
	}
	public EntityBranch activeFlag(Boolean activateSatus) {
		this.activeFlag = activateSatus;
		return this;
	}

	public EntityBranch cellNumbers(String cellNumbers) {
		this.cellNumbers = cellNumbers;
		return this;
	}

	public EntityBranch address(Address address) {
		this.address = address;
		return this;
	}

	public EntityBranch externalEntity(ExternalEntity externalEntity) {
		this.externalEntity = externalEntity;
		return this;
	}
	public EntityBranch code(String code) {
		this.code = code;
		return this;
	}

	public EntityBranch name(String name) {
		this.name = name;
		return this;
	}
	public void setExternalEntity(ExternalEntity externalEntity) {
		if(externalEntity!=null && externalEntity.getId()!=null)
			this.externalEntity = externalEntity;
	}
}
