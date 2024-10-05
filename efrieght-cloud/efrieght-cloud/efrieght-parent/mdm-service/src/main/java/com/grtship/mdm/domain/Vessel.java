package com.grtship.mdm.domain;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.grtship.core.enumeration.DomainStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * A Vessel.
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "vessel")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@EqualsAndHashCode(callSuper = true)
public class Vessel extends ClientAuditableEntity {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "Name is Required")
    @Column(name = "name", nullable = false)
    private String name;
    
    @Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false)
    @NotNull(message = "Status is Required")
	private DomainStatus status;

    @Embedded
    private Deactivate deactivate;
    
    @Embedded
    private Reactivate reactivate;
    
    @Column(name = "active")
	private Boolean active;

    @EqualsAndHashCode.Exclude
    @ManyToOne(optional = false)
    @NotNull(message = "Operator is required")
    @JsonIgnoreProperties(value = "vessels", allowSetters = true)
    private ExternalEntity operator;
    
    @NotNull(message = "submitted_for_approval")
    @Column(name = "submitted_for_approval", nullable = false)
    private Boolean submittedForApproval;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Vessel submittedForApproval(Boolean submittedForApproval) {
    	this.submittedForApproval = submittedForApproval;
    	return this;
    }
    public Vessel status(DomainStatus status) {
    	this.status = status;
    	return this;
    }
    public Vessel name(String name) {
        this.name = name;
        return this;
    }

    public Vessel deactivate(Deactivate deactivate) {
        this.deactivate = deactivate;
        return this;
    }

    public Vessel operator(ExternalEntity externalEntity) {
        this.operator = externalEntity;
        return this;
    }

	public Long getOperatorId() {
		return (this.operator!=null && this.operator.getId()!=null) ? this.operator.getId() : null;
	}

}
