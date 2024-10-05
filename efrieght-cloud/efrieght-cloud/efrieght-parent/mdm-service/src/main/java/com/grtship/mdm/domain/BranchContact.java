package com.grtship.mdm.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

/**
 * A ContactDetails.
 */
@Entity
@Table(name = "mdm_contact_details")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Data
public class BranchContact implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name can't be null.")
    @Size(max = 50, message = "Max size of Name must be 50.")
    @Column(name = "name", nullable = false)
    private String name;

    @NotBlank(message = "Email can't be null.")
    @Email(message = "Email format is not supported.")
    @Column(name = "email", nullable = false, length = 45)
    private String email;

    @NotBlank(message = "Cell Number can't be null")
    @Size(min = 10,max = 10,message = "Cell number size must be 10.")
    @Column(name = "cell_number", nullable = false)
    private String cellNumber;
    
    @Column(name = "department_name")
	private String departmentName;
    
    @Column(name = "designaiton_name")
  	private String designationName;
    
    @ManyToOne(optional = false)
    @NotNull(message = "Entity BranchId can't be null.")
    @JsonIgnoreProperties(value = "branchContacts", allowSetters = true)
    private EntityBranch entityBranch;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    
    public BranchContact entityBranch(EntityBranch entityBranch) {
        this.entityBranch = entityBranch;
        return this;
    }
    
    public BranchContact departmentName(String departmentName) {
        this.departmentName = departmentName;
        return this;
    }
    
    public BranchContact designationName(String designationName) {
        this.designationName = designationName;
        return this;
    }
    
    public BranchContact name(String name) {
        this.name = name;
        return this;
    }

    public BranchContact email(String email) {
        this.email = email;
        return this;
    }

    public BranchContact cellNumber(String cellNumber) {
        this.cellNumber = cellNumber;
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
