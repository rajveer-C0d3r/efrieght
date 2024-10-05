package com.grtship.mdm.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.grtship.core.annotation.EnableCustomAudit;

import lombok.Data;

/**
 * A Address.
 */
@Entity
@Table(name = "mdm_address")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Data
@EnableCustomAudit
public class Address implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "full_address")
	private String fullAddress;

	@ManyToOne
	private Country country;

	@ManyToOne
	private State state;

	@ManyToOne
	@NotNull(message = "City is Mandatory. Please Select City.")
	private Destination city;

	private String location;

	@Column(name = "pincode")
	private String pincode;

	@Column(name = "land_marks")
	private String landMarks;

	public Address address(String address) {
		this.fullAddress = address;
		return this;
	}
	public Address landMarks(String landMarks) {
		this.landMarks = landMarks;
		return this;
	}

	public Address pincode(String pincode) {
		this.pincode = pincode;
		return this;
	}

	public Address location(String location) {
		this.location = location;
		return this;
	}

	public Address city(Destination city) {
		this.city = city;
		return this;
	}

	public Address state(State state) {
		this.state = state;
		return this;
	}

	public Address country(Country country) {
		this.country = country;
		return this;
	}

	public void setCountry(Country country) {
		if(country!=null && country.getId()!=null)
			this.country = country;
	}
	public void setState(State state) {
		if(state!=null && state.getId()!=null)
			this.state = state;
	}
	public void setCity(Destination city) {
		if(city!=null && city.getId()!=null)
			this.city = city;
	}
	
}
