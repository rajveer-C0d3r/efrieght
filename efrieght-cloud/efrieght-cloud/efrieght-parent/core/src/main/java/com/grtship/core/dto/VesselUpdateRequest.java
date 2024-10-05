package com.grtship.core.dto;

import com.grtship.core.annotation.EnableAuditLevel;
import com.grtship.core.annotation.EnableCustomAudit;
import com.grtship.core.annotation.IgnoreAuditField;
import com.grtship.core.enumeration.DomainStatus;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@EnableCustomAudit
public class VesselUpdateRequest extends VesselCreationRequest{
	/**
	 * 
	 */
	@IgnoreAuditField
	private static final long serialVersionUID = -8891281199741542984L;
	protected Long id;
	protected Boolean active;
	@EnableAuditLevel(level = 1)
	protected VesselDeactivationDto deactivationDetails;
	@EnableAuditLevel(level = 1)
	protected VesselReactivationDto reactivationDetails;
	private DomainStatus status;
    
    public VesselUpdateRequest active(Boolean active) {
    	this.active = active;
    	return this;
    }
    public VesselUpdateRequest deactivationDetails(VesselDeactivationDto deactivationDetails) {
    	this.deactivationDetails = deactivationDetails;
    	return this;
    }
    public VesselUpdateRequest reactivationDetails(VesselReactivationDto reactivationDetails) {
    	this.reactivationDetails = reactivationDetails;
    	return this;
    }
	
	

}
