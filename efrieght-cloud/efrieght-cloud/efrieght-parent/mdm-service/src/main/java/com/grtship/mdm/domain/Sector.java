package com.grtship.mdm.domain;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * A Sector.
 */
@Entity
@Table(name = "mdm_sector")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"countries"})
public class Sector extends ClientAuditableEntity {

    private static final long serialVersionUID = 1L;

    @Size(max = 6)
    @NotNull(message = "Sector Code Is Mandatory, Please Enter Sector Code.")
    @NotEmpty(message = "Sector Code Is Mandatory, Please Enter Sector Code.")
    @Column(name = "code", nullable = false, length = 6)
    private String code;

    @Size(max = 100)
    @NotNull(message = "Sector Name Is Mandatory, Please Enter Sector Name.")
    @NotEmpty(message = "Sector Name Is Mandatory, Please Enter Sector Name.")
    @Column(name = "name", nullable = false, length = 100)
    private String name;
    
    @OneToMany(mappedBy = "sector")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<Country> countries = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here
   

    public Sector code(String code) {
        this.code = code;
        return this;
    }

    public Sector name(String name) {
        this.name = name;
        return this;
    }

    public Sector countries(Set<Country> countries) {
        this.countries = countries;
        return this;
    }

    public Sector addCountry(Country country) {
        this.countries.add(country);
        country.setSector(this);
        return this;
    }

    public Sector removeCountry(Country country) {
        this.countries.remove(country);
        country.setSector(null);
        return this;
    }
}
