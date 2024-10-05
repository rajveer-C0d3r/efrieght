package com.grtship.core.enumeration;

import lombok.Getter;

/**
 * The InstrumentType enumeration.
 */
public enum InstrumentType {
    CHEQUE("Cheque"),
    NEFT("NEFT"),
    RTGS("RTGS"),
    DEMAND_DRAFT("Demand Draft"),
    INWARD_REMITTANCE_FOREIGN_PARTY("Inward Remittance-Foreign Party"),
    INWARD_REMITTANCE_LOCAL_PARTY("Inward Remittance-Local Party");

	@Getter
	private String key;
	
	@Getter
	private String label;
	
	InstrumentType(String label) {
		this.label = label;
	}
	
	@Override
	public String toString() {
		return this.label;
	}
}
