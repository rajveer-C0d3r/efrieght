/**
 * 
 */
package com.grtship.core.enumeration;

import lombok.Getter;

/**
 * @author ER Ajay Sharma
 *
 */
public enum TimeStampFormatType {
	DATE("dd-MM-yyyy hh:mm:ss SSS"), INSTANT("dd-MM-yyyy hh:mm:ss SSS z"), LOCALDATE("dd-MMM-yyyy"), LOCALDATETIME("dd-MM-yyyy HH:mm:ss");

	@Getter
	private String key;
	@Getter
	private String label;

	private TimeStampFormatType(String label) {
		this.label = label;
	}

}
