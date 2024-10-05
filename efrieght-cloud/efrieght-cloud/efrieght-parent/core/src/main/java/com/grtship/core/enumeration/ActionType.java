package com.grtship.core.enumeration;

import lombok.Getter;

public enum ActionType {
   SAVE("Save"),
   UPDATE("Update"),
   DELETE("Delete"),
   VIEW("View"),
   DEACTIVATE("Deactivate"),
   REACTIVATE("Reactivate");
	
	@Getter
	private String key;
	
	@Getter
	private String label;
	
	ActionType(String label) {
		this.label = label;
	}
}
