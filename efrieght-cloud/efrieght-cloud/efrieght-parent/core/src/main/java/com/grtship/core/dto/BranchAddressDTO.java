package com.grtship.core.dto;

import java.io.Serializable;

import com.grtship.core.annotation.EnableCustomAudit;
import com.grtship.core.annotation.IgnoreAuditField;

import lombok.Data;

@Data
@EnableCustomAudit
public class BranchAddressDTO implements Serializable{
	
	/**
	 * 
	 */
	@IgnoreAuditField
	private static final long serialVersionUID = 1L;

	private Long countryId;
	
	private String countryName;
	
	private Long stateId;

	private Long cityId;

	private String location;

	private String pincode;

	private String address;
}
