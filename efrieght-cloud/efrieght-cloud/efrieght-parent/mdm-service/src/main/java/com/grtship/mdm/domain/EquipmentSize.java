package com.grtship.mdm.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * A EquipmentSize.
 */
@Entity
@Table(name = "equipment_size")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Data
@EqualsAndHashCode(callSuper = true)
public class EquipmentSize extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    @Column(name = "size", nullable = false, unique = true)
    private Integer size;

    @NotNull
    @Column(name = "no_of_teu", nullable = false)
    private String noOfTeu;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public EquipmentSize size(Integer size) {
        this.size = size;
        return this;
    }

    public EquipmentSize noOfTeu(String noOfTeu) {
        this.noOfTeu = noOfTeu;
        return this;
    }

}
