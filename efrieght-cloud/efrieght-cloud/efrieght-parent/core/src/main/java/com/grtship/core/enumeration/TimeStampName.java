/**
 * 
 */
package com.grtship.core.enumeration;

import java.util.Arrays;
import java.util.List;

/**
 * @author Ajay
 *
 */
public enum TimeStampName {
	DATE(1, "class java.util.Date"), INSTANT(2, "class java.time.Instant"), LOCALDATE(3, "class java.time.LocalDate"),
	LOCALDATETIME(4, "class java.time.LocalDateTime");

	TimeStampName(int id, String name) {
		this.id = id;
		this.name = name;

	}

	public static List<String> getTimeStampNames() {
		return Arrays.asList(DATE.getName(), INSTANT.getName(), LOCALDATE.getName(), LOCALDATETIME.getName());
	}

	private int id;
	private String name;

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

}
