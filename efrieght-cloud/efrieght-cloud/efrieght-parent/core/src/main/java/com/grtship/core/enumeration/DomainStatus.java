package com.grtship.core.enumeration;

import com.grtship.core.annotation.EnableCustomAudit;

import lombok.Getter;

/**
 * The ApprovalStatus enumeration.
 */
@EnableCustomAudit
public enum DomainStatus {
	PENDING("Pending Approval"), 
	DEACTIVATED("Deactivated"),
	APPROVED("Approved"),
	REJECTED("Rejected"),
	DRAFT("Draft"),
	EXPIRED("Expired");

	@Getter
	private String key;

	@Getter
	private String label;

	DomainStatus(String label) {
		this.label = label;
	}

	@Override
	public String toString() {
		return this.label;
	}
	
	public static String getName(String status) {
		for (DomainStatus domainStatus : values()) {
			if (domainStatus.label.equalsIgnoreCase(status)) {
				return domainStatus.name();
			}
		}
		return null;
	}
}
