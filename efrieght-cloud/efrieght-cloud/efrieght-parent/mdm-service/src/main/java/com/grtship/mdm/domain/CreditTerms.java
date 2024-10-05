package com.grtship.mdm.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.grtship.core.annotation.EnableCustomAudit;
import com.grtship.core.enumeration.DomainStatus;
import com.grtship.core.enumeration.EntityType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * A CreditTerms.
 */
@Entity
@Table(name = "mdm_credit_terms")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@ToString
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EnableCustomAudit
public class CreditTerms extends ClientAuditableEntity {

    private static final long serialVersionUID = 1L;

    
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private DomainStatus status;

    @Column(name = "credit_days")
    private Integer creditDays;

    @Column(name = "credit_amount")
    private Double creditAmount;

    @Column(name = "reference_id")
    private Long referenceId;

    @Column(name = "reference_name")
    private String referenceName;

    @Enumerated(EnumType.STRING)
    @Column(name = "entity_type")
    private EntityType entityType;
    
    

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public CreditTerms status(DomainStatus status) {
        this.status = status;
        return this;
    }

    public CreditTerms creditDays(Integer creditDays) {
        this.creditDays = creditDays;
        return this;
    }

    public CreditTerms creditAmount(Double creditAmount) {
        this.creditAmount = creditAmount;
        return this;
    }

    public CreditTerms referenceId(Long referenceId) {
        this.referenceId = referenceId;
        return this;
    }

    public CreditTerms referenceName(String referenceName) {
        this.referenceName = referenceName;
        return this;
    }

    public CreditTerms entityType(EntityType entityType) {
        this.entityType = entityType;
        return this;
    }
}
