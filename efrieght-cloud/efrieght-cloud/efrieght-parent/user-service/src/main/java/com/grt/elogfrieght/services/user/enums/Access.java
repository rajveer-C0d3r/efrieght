/**
 * 
 */
package com.grt.elogfrieght.services.user.enums;

import lombok.Getter;

/**
 * @author ER Ajay Sharma
 *
 */
public enum Access {
	UPDATE("Update"), ADD("Add"), VIEW("View"), DELETE("Delete"), ALL("All");

	@Getter
	private String key;

	@Getter
	private String label;

	Access(String label) {
		this.label = label;
	}

	@Override
	public String toString() {
		return this.label;
	}

}
