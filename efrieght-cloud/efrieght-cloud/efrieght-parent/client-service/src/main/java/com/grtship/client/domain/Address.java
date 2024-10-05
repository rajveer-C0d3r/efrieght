package com.grtship.client.domain;

import java.io.Serializable;

//FIXME this is duplicated entity  of Address needs to move in common module.

import javax.persistence.Column;
import javax.persistence.Embeddable;

import com.grtship.core.annotation.EnableCustomAudit;

import lombok.Data;

/*this call will replace with Address after ELF-246 chnges..*/
@Data
//@EqualsAndHashCode(callSuper = true)
@EnableCustomAudit
@Embeddable
public class Address implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Column(name = "countryId")
	private Long countryId;
	
	@Column(name = "state")
	private Long state;

	@Column(name = "city")
	private Long city;

	@Column(name = "location")
	private String location;

	@Column(name = "pincode")
	private String pincode;

	@Column(name = "address")
	private String address;
}
