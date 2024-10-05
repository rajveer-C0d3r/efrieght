package com.grtship.mdm.domain;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.grtship.core.enumeration.DomainStatus;
import com.grtship.core.enumeration.GstVatType;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * A Country.
 */
@Entity
@Table(name = "mdm_country")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Data
@ToString(exclude = {"sector", "states"})
@EqualsAndHashCode(callSuper = true, exclude = {"sector", "states"})
public class Country extends ClientAuditableEntity {

    private static final long serialVersionUID = 1L;

    @Size(min = 3, max = 3, message = "Country Code Should Have 3 Characters.")
    @NotBlank(message = "Country Code Is Mandatory, Please Enter Country Code.")
    @Column(name = "code", nullable = false)
    private String code;

    @Size(max = 50, message = "Country Name Exceeds Character Limits, Maximum 50 Characters Allowed.")
    @NotBlank(message = "Country Name Is Mandatory, Please Enter Country Name.")
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull(message = "GST/VAT Type Is Mandatory, Please Select GST/VAT Type.")
    @Enumerated(EnumType.STRING)
    @Column(name = "gst_or_vat_type", nullable = false)
    private GstVatType gstOrVatType;

    @Column(name = "is_state_mandatory")
    private Boolean isStateMandatory = Boolean.FALSE;

    @NotNull(message = "currency Is Mandatory, Please Select currency.")
    @ManyToOne
    @JoinColumn(name = "currency_id")
    private Currency currency;

    @NotNull(message = "Sector Is Mandatory, Please Select Sector.")
    @ManyToOne
    @JsonIgnoreProperties(value = "countries", allowSetters = true)
    private Sector sector;

    @OneToMany(mappedBy = "country")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<State> states = new HashSet<>();

    @Column(name = "phone_code")
    private String phoneCode;
    
    @Column(name = "active_flag")
    private Boolean activeFlag = false;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private DomainStatus status;
    
    @Column(name = "submitted_for_approval")
	private Boolean submittedForApproval = false;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Country code(String code) {
        this.code = code;
        return this;
    }

    public Country name(String name) {
        this.name = name;
        return this;
    }

    public Country gstOrVatType(GstVatType gstOrVatType) {
        this.gstOrVatType = gstOrVatType;
        return this;
    }

    public Country isStateMandatory(Boolean isStateMandatory) {
        this.isStateMandatory = isStateMandatory;
        return this;
    }

    public Country currency(Currency currency) {
        this.currency = currency;
        return this;
    }

    public Country states(Set<State> states) {
        this.states = states;
        return this;
    }
    
    public void setSector(Sector sector) {
		if(sector!=null && sector.getId()!=null)
			this.sector = sector;
	}
    
    public void setCurrency(Currency currency) {
    	if(currency!=null && currency.getId()!=null)
    		this.currency = currency;
    }
    
    public Country addState(State state) {
        this.states.add(state);
        state.setCountry(this);
        return this;
    }

    public Country removeState(State state) {
        this.states.remove(state);
        state.setCountry(null);
        return this;
    }


}
