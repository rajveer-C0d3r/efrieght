package com.grtship.core.enumeration;

import lombok.Getter;

/**
 * The CompanyType enumeration.
 */
public enum CompanyType {
	PROPRIETOR("Properitorship"),
	PARTNERSHIP("Partnership"), 
    PRIVATE("Private Limited"), 
    PUBLIC("Public Limited"),
    LLP("LLP"), 
    HUF("HUF"),
    OPC("OPC"),
    AOP("AOP"),
    CENTRAL_GOVT("Central Government"),
    STATE_GOVERNMENT("State Government"),
	MUNICIPALITY("Municipality");
  
	
	@Getter
	private String key;
	
	@Getter
	private String label;
	
	CompanyType(String label) {
		this.label = label;
	}
	
	@Override
	public String toString() {
		return this.label;
	}
	
}
