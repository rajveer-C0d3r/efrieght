package com.grtship.core.enumeration;

import lombok.Getter;

public enum DestinationFclMargin {
	
	BASE_PORT("Base Port"),
	ODD_PORT("ODD Port"),
	INLAND_DESTINATION("Inland Destination"),
	DOOR("Door");
	
	@Getter private String key;
	@Getter private String label;
	
	DestinationFclMargin(String label){
		this.label = label;
	}

}
