package com.grtship.mdm.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.grtship.core.annotation.EnableCustomAudit;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * A EntityGroup.
 */
@Entity
@Table(name = "mdm_entity_group")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Data
@EqualsAndHashCode(callSuper = true)
@EnableCustomAudit
public class EntityGroup extends ClientAuditableEntity {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "Group Name is required.")
    @Size(max = 50,message = "Max size of Group Name must be 50.")
    @Column(name = "name")
    private String name;

    @NotBlank(message = "Group Code is required.")
    @Size(max = 6,message = "Max size of Code must be 6.")
    @Column(name = "code")
    private String code;


    public EntityGroup name(String name) {
        this.name = name;
        return this;
    }

    public EntityGroup code(String code) {
        this.code = code;
        return this;
    }
}
