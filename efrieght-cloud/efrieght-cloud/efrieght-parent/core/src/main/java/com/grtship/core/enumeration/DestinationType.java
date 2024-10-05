package com.grtship.core.enumeration;

import lombok.Getter;

/**
 * The DestinationType enumeration.
 */
public enum DestinationType {
    DESTINATION("Destination"),
    ISD_INLAND_CONTAINER_DEPOT("ICD / Inland Container Depot"), 
    TERMINAL("Terminal"), 
    PORT("Port"), 
    AIRPORT("Airport"), 
    CITY("City");
	
	DestinationType(String label){
		this.label = label;
	}
	
	@Getter 
	private String label;
	
	
	@Override
	public String toString() {
		return this.label;
	}
}
