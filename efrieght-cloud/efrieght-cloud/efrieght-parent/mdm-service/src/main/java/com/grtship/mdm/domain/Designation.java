package com.grtship.mdm.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * A Designation.
 */
@Entity
@Table(name = "mdm_designation")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Data
@EqualsAndHashCode(callSuper = true)
public class Designation extends ClientAuditableEntity{

    private static final long serialVersionUID = 1L;

    @NotNull(message = "Designation code is mandatory.")
    @Column(name = "code", nullable = false)
    private String code;

    @NotNull(message = "Designation name is mandatory.")
    @Column(name = "name", nullable = false)
    private String name;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Designation code(String code) {
        this.code = code;
        return this;
    }

    public Designation name(String name) {
        this.name = name;
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

}
