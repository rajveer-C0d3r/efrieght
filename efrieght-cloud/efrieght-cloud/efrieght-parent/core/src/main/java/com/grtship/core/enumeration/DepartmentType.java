package com.grtship.core.enumeration;

import lombok.Getter;

/**
 * The DepartmentType enumeration.
 */
public enum DepartmentType {
    SALES("Sales"),
    CUSTOMER_SERVICE("Customer Service"),
    OPERATIONS("Operations"),
    ACCOUNTS("Accounts"),
    IT("IT"),
    NOMINATION("Nomination"),
    BL_COUNTER("BL Counter"),
    DOCUMENTATION("Documentation"),
    MIS("MIS"),
    HR("HR"),
    PRICING("Pricing"),
    ADMIN("Admin"),
    OTHER("Other"),
    MANAGEMENT("Management");
	
	@Getter
	private String key;
	
	@Getter
	private String label;
	
	DepartmentType(String label) {
		this.label = label;
	}
	
	@Override
	public String toString() {
		return this.label;
	}
}
