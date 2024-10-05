package com.grtship.mdm.domain;

import java.time.LocalDate;

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
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.ParamDef;

import com.grtship.core.annotation.EnableCustomAudit;
import com.grtship.core.enumeration.CompanyType;
import com.grtship.core.enumeration.DomainStatus;
import com.grtship.core.enumeration.Domicile;
import com.grtship.core.enumeration.EntityCriteria;
import com.grtship.core.enumeration.EntityGstType;
import com.grtship.core.enumeration.EntityLevel;
import com.grtship.core.enumeration.TdsExemption;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * A ExternalEntity.
 */
@Entity
@Table(name = "mdm_external_entity")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Data
@ToString
@EqualsAndHashCode(callSuper = true)
@FilterDef(name = "clientAccessFilter", parameters = { @ParamDef(name = "clientId", type = "long")})
@FilterDef(name = "companyAccessFilter", parameters = { @ParamDef(name = "companyId", type = "long")})
@FilterDef(name = "branchAccessFilter", parameters = { @ParamDef(name = "branchId", type = "long")})
		
@Filters({
	@Filter(name = "clientAccessFilter", condition = "client_id = :clientId"),
	@Filter(name = "companyAccessFilter", condition = "company_id = :companyId"),
	@Filter(name = "branchAccessFilter", condition = "branch_id = :branchId")
})
@EnableCustomAudit
public class ExternalEntity extends AbstractAuditingEntity {

	private static final long serialVersionUID = 1L;

	@NotBlank(message = "Code Is Mandatory")
	@Size(max=6,message = "Maximum size of Code must be 6.")
	@Column(name = "code", nullable = false, length = 6)
	private String code;

	@NotBlank(message = "Entity name is required.")
	@Size(max=60, message = "Maximum size of Name must be 60.")
	@Column(name = "name",length = 60)
	private String name;

	@Size(max=60, message = "Maximum size of Legal Name must be 60.")
	@Column(name = "legal_name",length = 60)
	private String legalName;

	@Size(max=40, message = "Maximum size of Key Person Name must be 40.")
	@Column(name = "key_person_name", length = 40)
	private String keyPersonName;

	@Size(max=10, message = "Size of Cell Number must be 10.")
	@Column(name = "cell_no", length = 10)
	private String cellNo;

	@Column(name = "landline_no", length = 10)
	@Size(max=10, message = "Maximum size of Landline Number must be 10.")
	private String landlineNo;

	@NotBlank(message = "Email Can't Be Null.")
	@Email(message = "Email format not supported.")
	@Size(max=40, message = "Maximum size of Email must be 40.")
	@Column(name = "email", length = 40)
	private String email;

	@Enumerated(EnumType.STRING)
	@Column(name = "company_type")
	private CompanyType companyType;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "entity_criteria")
	private EntityCriteria entityCriteria;

	@ManyToOne
	private Currency currency;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "address_id")
	private Address address;
	
	@ManyToOne
	@JoinColumn(name = "group_id")
	private EntityGroup groups;

	// tdsExemption, tdsExemptionWefDate, tdsExemptionValidUptoDate, tdsExemptionPercentage will removed into another table @Arun sir discussion
	@Enumerated(EnumType.STRING)
	@Column(name = "tds_exemption")
	private TdsExemption tdsExemption;
	
	@Column(name = "tds_exemption_wef_date")
	private LocalDate tdsExemptionWefDate;
	
	@Column(name = "tds_exemption_valid_upto_date")
	private LocalDate tdsExemptionValidUptoDate;
	
	@Column(name = "tds_exemption_percentage")
	private Integer tdsExemptionPercentage;

	@Column(name = "active_flag")
	private Boolean activeFlag = Boolean.FALSE;

	@Enumerated(EnumType.STRING)
	@Column(name = "status")
	@NotNull(message = "Status can't be null.")
	private DomainStatus status;
	
	// deactivation-reactivation fields
	@Embedded
	private DomainDeactivate deactivateDtls;
	
	@Embedded
	private DomainReactivate reactivateDtls;
	
	//tax details fields
    @Enumerated(EnumType.STRING)
    @Column(name = "domicile")
    private Domicile domicile;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "gst_type")
    private EntityGstType gstType;
    
    @Column(name = "gst_no")
    private String gstNo;

    @Column(name = "pan_no_tax_id")
    @Size(max=16, message = "Maximum size of Pan Number must be 16.")
    private String panNoTaxId;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "customer_entity_level")
    private EntityLevel customerEntityLevel;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "vendor_entity_level")
    private EntityLevel vendorEntityLevel;
    
    @Column(name = "customer_flag")
    private Boolean customerFlag = Boolean.FALSE;
    
    @Column(name = "vendor_flag")
    private Boolean vendorFlag = Boolean.FALSE;
    
    //approval submition flag
    @Column(name = "submited_for_approval")
    private Boolean submittedForApproval;
    
    @Column(name = "is_legal_name_same_as_name")
    private Boolean isLegalNameSameAsName;
    
    @Column(name = "client_id")
   	private Long clientId;
       
    @Column(name = "company_id")
   	private Long companyId;

	// jhipster-needle-entity-add-field - JHipster will add fields here
    public ExternalEntity customerEntityLevel(EntityLevel customerEntityLevel) {
        this.customerEntityLevel = customerEntityLevel;
        return this;
    }
    
    public ExternalEntity vendorEntityLevel(EntityLevel vendorEntityLevel) {
        this.vendorEntityLevel = vendorEntityLevel;
        return this;
    }
    
    public ExternalEntity tdsExemptionPercentage(Integer tdsExemptionPercentage) {
    	this.tdsExemptionPercentage = tdsExemptionPercentage;
    	return this;
    }
    
    public ExternalEntity customerFlag(Boolean customerFlag) {
        this.customerFlag = customerFlag;
        return this;
    }
    
    public ExternalEntity vendorFlag(Boolean vendorFlag) {
        this.vendorFlag = vendorFlag;
        return this;
    }
    
    public ExternalEntity gstNo(String gstNo) {
        this.gstNo = gstNo;
        return this;
    }

    public ExternalEntity panNoTaxId(String panNoTaxId) {
        this.panNoTaxId = panNoTaxId;
        return this;
    }
	
    public ExternalEntity gstType(EntityGstType gstType) {
        this.gstType = gstType;
        return this;
    }
    
    public ExternalEntity domicile(Domicile domicile) {
        this.domicile = domicile;
        return this;
    }
	
	public ExternalEntity tdsExemptionWefDate(LocalDate tdsExemptionWefDate) {
		this.tdsExemptionWefDate = tdsExemptionWefDate;
		return this;
	}
	
	public ExternalEntity tdsExemptionValidUptoDate(LocalDate tdsExemptionValidUptoDate) {
		this.tdsExemptionValidUptoDate = tdsExemptionValidUptoDate;
		return this;
	}
	
	public ExternalEntity tdsExemption(TdsExemption tdsExemption) {
		this.tdsExemption = tdsExemption;
		return this;
	}
	public ExternalEntity status(DomainStatus status) {
		this.status = status;
		return this;
	}
	public ExternalEntity activeFlag(Boolean activateSatus) {
		this.activeFlag = activateSatus;
		return this;
	}

	public ExternalEntity code(String code) {
		this.code = code;
		return this;
	}

	public ExternalEntity address(Address address) {
		this.address = address;
		return this;
	}
	
	public ExternalEntity groups(EntityGroup groups) {
		this.groups = groups;
		return this;
				
	}

	public ExternalEntity name(String name) {
		this.name = name;
		return this;
	}


	public ExternalEntity legalName(String legalName) {
		this.legalName = legalName;
		return this;
	}


	public ExternalEntity keyPersonName(String keyPersonName) {
		this.keyPersonName = keyPersonName;
		return this;
	}

	public ExternalEntity cellNo(String cellNo) {
		this.cellNo = cellNo;
		return this;
	}

	public ExternalEntity landlineNo(String landlineNo) {
		this.landlineNo = landlineNo;
		return this;
	}

	public ExternalEntity companyType(CompanyType companyType) {
		this.companyType = companyType;
		return this;
	}

	public ExternalEntity email(String email) {
		this.email = email;
		return this;
	}

	public ExternalEntity currency(Currency currency) {
		this.currency = currency;
		return this;
	}
	
	public void setCurrency(Currency currency) {
		if(currency!=null && currency.getId()!=null)
			this.currency = currency;
	}
	public void setGroups(EntityGroup group) {
		if(group!=null && group.getId()!=null)
			this.groups = group;
	}
	public void setGroupsId(Long groupId) {
		if(this.groups==null) {
			this.groups = new EntityGroup();
		}
			this.groups.setId(groupId);
	}
}
