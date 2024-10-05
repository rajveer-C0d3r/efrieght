package com.grtship.mdm.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import lombok.EqualsAndHashCode;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;

import java.io.Serializable;

/**
 * A Container.
 */
@Entity
@Table(name = "container")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Data
@EqualsAndHashCode(callSuper = true)
public class Container extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = "deactivate")
    private Boolean deactivate;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = "containers", allowSetters = true)
    private Equipment equipmentType;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = "containers", allowSetters = true)
    private EquipmentSize size;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Container deactivate(Boolean deactivate) {
        this.deactivate = deactivate;
        return this;
    }

    public Container equipmentType(Equipment equipment) {
        this.equipmentType = equipment;
        return this;
    }

    public Container size(EquipmentSize equipmentSize) {
        this.size = equipmentSize;
        return this;
    }

}
