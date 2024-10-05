package com.grtship.mdm.domain;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * A Alias.
 */
@MappedSuperclass
@Data
@EqualsAndHashCode(callSuper = true)
public class Alias extends AbstractAuditingEntity {

    private static final long serialVersionUID = 1L;

    @Column(name = "alias")
    private String alias;
    
    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Alias label(String alias) {
        this.alias = alias;
        return this;
    }
}
