package com.grtship.mdm.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * A TaxDetails.
 */
@Entity
@Table(name = "mdm_entity_branch_tax")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Data
@EqualsAndHashCode(callSuper = false)
public class EntityBranchTax implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Gst No is mandatory.")
    @Column(name = "gst_no")
    private String gstNo;
    
    @Column(name = "description")
    private String description;
    
    @Column(name = "client_id")
    private Long clientId;
    
    @Column(name = "company_id")
    private Long companyId;
    
    @ManyToOne(optional = false)
    @NotNull(message = "Branch Id is Mandatory.")
    @JsonIgnoreProperties(value = "entityBranchTaxes", allowSetters = true)
    private EntityBranch entityBranch;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public EntityBranchTax gstNo(String gstNo) {
        this.gstNo = gstNo;
        return this;
    }

    public EntityBranchTax entityBranch(EntityBranch entityBranch) {
        this.entityBranch = entityBranch;
        return this;
    }
    
    public EntityBranchTax companyId(Long companyId) {
        this.companyId = companyId;
        return this;
    }

    public EntityBranchTax clientId(Long clientId) {
        this.clientId = clientId;
        return this;
    }

    public EntityBranchTax description(String description) {
        this.description = description;
        return this;
    }
    
    public void setEntityBranchId(Long branchId) {
		if(this.entityBranch==null) {
			this.entityBranch = new EntityBranch();
		}
		  this.entityBranch.setId(branchId);
	}
    
    public void setEntityBranch(EntityBranch entityBranch) {
    	this.entityBranch = entityBranch;
    }

}
