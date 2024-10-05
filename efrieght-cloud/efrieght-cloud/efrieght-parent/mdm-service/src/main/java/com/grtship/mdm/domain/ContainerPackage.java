package com.grtship.mdm.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.grtship.core.enumeration.PackageType;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * A ContainerPackage.
 */
@Entity
@Table(name = "container_package")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Data
@EqualsAndHashCode(callSuper = true)
public class ContainerPackage extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull
    @Column(name = "wooden", nullable = false)
    private Boolean wooden;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private PackageType type;

    @Column(name = "remarks")
    private String remarks;

    @Column(name = "palletizable")
    private Boolean palletizable;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public ContainerPackage name(String name) {
        this.name = name;
        return this;
    }


    public ContainerPackage wooden(Boolean wooden) {
        this.wooden = wooden;
        return this;
    }

    public ContainerPackage type(PackageType type) {
        this.type = type;
        return this;
    }


    public ContainerPackage remarks(String remarks) {
        this.remarks = remarks;
        return this;
    }

    public ContainerPackage palletizable(Boolean palletizable) {
        this.palletizable = palletizable;
        return this;
    }
}
