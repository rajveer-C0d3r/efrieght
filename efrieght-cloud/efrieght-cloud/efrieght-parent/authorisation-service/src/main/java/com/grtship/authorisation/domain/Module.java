package com.grtship.authorisation.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import lombok.Data;

/**
 * A Module.
 */
@Data
@Entity
@Table(name = "module")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Module implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @NotNull(message = "Module Name is mandatory")
    @Size(max = 100)
    @Column(name = "module_name", length = 100, nullable = false, unique = true)
    private String moduleName;
    
    
    public Module moduleName(String moduleName) {
        this.moduleName = moduleName;
        return this;
    }

}
