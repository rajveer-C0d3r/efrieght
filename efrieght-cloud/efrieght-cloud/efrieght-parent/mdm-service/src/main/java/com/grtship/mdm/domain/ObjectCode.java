package com.grtship.mdm.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * A ObjectCode.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "mdm_object_code")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class ObjectCode extends ClientAuditableEntity {

    private static final long serialVersionUID = 1L;

    @Size(max = 20)
    @NotNull(message = "Object name is mandatory.")
    @Column(name = "object_name", length = 20, nullable = false)
    private String objectName;

    @Size(max = 10)
    @Column(name = "prefix", length = 10)
    private String prefix;

    @Column(name = "padding", columnDefinition = "bigint default 1")
    private Long padding;

    @NotNull(message = "Counter is mandatory.")
    @Column(name = "counter", nullable = false)
    private Long counter;
    
    @Size(max = 20)
    @Column(name = "parent_code", length = 20)
    private String parentCode;
    
    @Column(name = "block_size")
    private Integer blockSize;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public ObjectCode objectName(String objectName) {
        this.objectName = objectName;
        return this;
    }

    public ObjectCode prefix(String prefix) {
        this.prefix = prefix;
        return this;
    }

    public ObjectCode padding(Long padding) {
        this.padding = padding;
        return this;
    }

    public ObjectCode counter(Long counter) {
        this.counter = counter;
        return this;
    }
}
