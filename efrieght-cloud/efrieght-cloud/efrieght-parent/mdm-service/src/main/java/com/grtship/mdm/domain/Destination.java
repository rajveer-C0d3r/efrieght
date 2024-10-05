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
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.grtship.core.constant.Constants;
import com.grtship.core.enumeration.DestinationType;
import com.grtship.core.enumeration.PortType;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * A Destination.
 */
@Entity
@Table(name = "mdm_destination")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Data
@EqualsAndHashCode(callSuper = true)
public class Destination extends ClientAuditableEntity {

	private static final long serialVersionUID = 1L;

	@NotNull(message = "Destination Code Is Mandatory, Please Enter Destination Code.")
	@NotEmpty(message = "Destination Code Is Mandatory, Please Enter Destination Code.")
	@Pattern(regexp = Constants.ALPHABETS_REGEX, message = "Destination Code Should Contain Only alphabets.")
	@Size(min = 3, max = 3, message = "Destination Code must be of 3 Characters.")
	@Column(name = "code", nullable = false, length = 3)
	private String code;

	@NotNull(message = "Destination Name Is Mandatory, Please Enter Destination Name.")
	@Pattern(regexp = Constants.ALPHA_NUMERIC_REGEX, message = "Destination Name Should Contain Only alphabets and numeric values.")
	@Size(max = 100, message = "Destination name must be of 100 Characters.")
	@Column(name = "name", nullable = false, length = 100)
	private String name;

	@NotNull(message = "Destination Type Is Mandatory, Please Select Destination Type.")
	@Enumerated(EnumType.STRING)
	@Column(name = "type", nullable = false, length = 30)
	private DestinationType type;

	@Enumerated(EnumType.STRING)
	@Column(name = "port_type", length = 20)
	private PortType portType;

	@Size(max = 10, message = "IATA Airport Code must be of 10 Characters.")
	@Column(name = "iata_airport_code")
	private String iataAirportCode;

	@Column(name = "is_reworking_port")
	private Boolean isReworkingPort;

	@EqualsAndHashCode.Exclude
	@OneToMany(mappedBy = "port")
	private Set<Destination> terminals = new HashSet<>();

	@EqualsAndHashCode.Exclude
	@ManyToOne
	@JoinColumn
	private Destination port;

	@EqualsAndHashCode.Exclude
	@ManyToOne
	@JsonIgnoreProperties(value = "destinations", allowSetters = true)
	private Destination city;

	@ManyToOne
	@JsonIgnoreProperties(value = "destinations", allowSetters = true)
	private State state;

	@NotNull(message = "Country Is Mandatory, Please Select Country.")
	@ManyToOne
	@JsonIgnoreProperties(value = "destinations", allowSetters = true)
	private Country country;
	
	private Boolean isAdminCreated;

	// jhipster-needle-entity-add-field - JHipster will add fields here

	public Destination code(String code) {
		this.code = code;
		return this;
	}

	public Destination name(String name) {
		this.name = name;
		return this;
	}

	public Destination type(DestinationType type) {
		this.type = type;
		return this;
	}

	public Destination iataAirportCode(String iataAirportCode) {
		this.iataAirportCode = iataAirportCode;
		return this;
	}

	public Destination isReworkingPort(Boolean isReworkingPort) {
		this.isReworkingPort = isReworkingPort;
		return this;
	}

	public void setPort(Destination port) {
		if (port != null && port.id != null)
			this.port = port;
	}

	public void setCity(Destination city) {
		if (city != null && city.getId() != null)
			this.city = city;
	}

	public void setState(State state) {
		if (state != null && state.getId() != null)
			this.state = state;
	}

	public Destination port(Long destination) {
		this.port.id = destination;
		return this;
	}

	public Destination terminals(Set<Destination> terminals) {
		this.terminals = terminals;
		return this;
	}

	public Destination city(Long destination) {
		this.city.id = destination;
		return this;
	}

	public Destination state(Long state) {
		this.state.id = state;
		return this;
	}

	public Destination country(Long country) {
		this.country.id = country;
		return this;
	}

}
