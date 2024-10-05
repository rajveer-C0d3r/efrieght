package com.grtship.mdm.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * A State.
 */
@Entity
@Table(name = "mdm_state")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Data
@EqualsAndHashCode(callSuper = true)
public class State extends AbstractAuditingEntity {

    private static final long serialVersionUID = 1L;

    @Size(min = 2, max = 2, message = "State Code Should Have 2 Characters.")
    @NotNull(message = "State Code is mandatory.")
    @NotBlank(message = "State Code is mandatory.")
    @Column(name = "code", nullable = false)
    private String code;

    @Size(max = 50, message = "State Name Exceeds Character Limits, Maximum 50 Characters Allowed.")
    @NotNull(message = "State Name is mandatory.")
    @NotBlank(message = "State Name is mandatory.")
    @Column(name = "name", nullable = false)
    private String name;

    @ManyToOne
    @JsonIgnoreProperties(value = "states", allowSetters = true)
    private Country country;
    
    // jhipster-needle-entity-add-field - JHipster will add fields here

    public State code(String code) {
        this.code = code;
        return this;
    }

    public State name(String name) {
        this.name = name;
        return this;
    }

    public State country(Country country) {
        this.country = country;
        return this;
    }

}
