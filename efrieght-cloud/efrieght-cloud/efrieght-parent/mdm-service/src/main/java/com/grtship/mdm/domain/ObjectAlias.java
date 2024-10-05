
package com.grtship.mdm.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.grtship.core.annotation.EnableCustomAudit;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * A ObjectAlias.
 */
@Entity
@Table(name = "mdm_object_alias")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@ToString
@Data
@EqualsAndHashCode(callSuper = true)
@EnableCustomAudit
public class ObjectAlias extends ClientAuditableEntity {

    private static final long serialVersionUID = 1L;

    @Column(name = "name")
    private String name;

    @Column(name = "reference_name")
    private String referenceName;

    @Column(name = "reference_id")
    private Long referenceId;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public ObjectAlias name(String name) {
        this.name = name;
        return this;
    }

    public ObjectAlias referenceName(String referenceName) {
        this.referenceName = referenceName;
        return this;
    }

    public ObjectAlias referenceId(Long referenceId) {
        this.referenceId = referenceId;
        return this;
    }
}
