package com.grtship.mdm.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.grtship.core.enumeration.DepartmentType;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * A Department.
 */
@Entity
@Table(name = "mdm_department")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Data
@EqualsAndHashCode(callSuper = true)
public class Department extends ClientAuditableEntity {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "Department Code can't be null.")
    @Column(name = "code", nullable = false)
    private String code;  

    @NotNull(message = "Department Name can't be null.")
    @Column(name = "name", nullable = false)
    @Size(max = 50)
    private String name;

    @NotNull(message = "Department Type can't be null.")
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private DepartmentType type;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Department code(String code) {
        this.code = code;
        return this;
    }

    public Department name(String name) {
        this.name = name;
        return this;
    }

    public Department type(DepartmentType type) {
        this.type = type;
        return this;
    }

}
