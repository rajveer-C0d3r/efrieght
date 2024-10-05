package com.grtship.mdm.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.grtship.core.enumeration.GstType;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * A Gst.
 */
@Entity
@Table(name = "mdm_gst")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Data
@EqualsAndHashCode(callSuper = true)
public class Gst extends ClientAuditableEntity {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "HSN/SAC code is mandatory.")
    @Column(name = "hsn_sac_code", nullable = false)
    private String hsnSacCode;

    @Column(name = "description")
    private String description;

    @NotNull(message = "GST Type is mandatory.")
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private GstType type;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Gst code(String hsnSacCode) {
        this.hsnSacCode = hsnSacCode;
        return this;
    }

    public Gst description(String description) {
        this.description = description;
        return this;
    }

    public Gst type(GstType type) {
        this.type = type;
        return this;
    }
}
