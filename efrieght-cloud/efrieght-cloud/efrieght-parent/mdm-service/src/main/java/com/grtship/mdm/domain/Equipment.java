package com.grtship.mdm.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.grtship.core.enumeration.EquipmentType;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * A Equipment.
 */
@Entity
@Table(name = "equipment")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Data
@EqualsAndHashCode(callSuper = true)
public class Equipment extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false, unique = true)
    private String name;
    
    @NotNull
    @Column(name = "code", nullable = false, unique = true)
    private String code;


    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private EquipmentType type;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    
	public Equipment code(String code) {
        this.code = code;
        return this;
    }

    public Equipment name(String name) {
        this.name = name;
        return this;
    }

    public Equipment type(EquipmentType type) {
        this.type = type;
        return this;
    }

}
