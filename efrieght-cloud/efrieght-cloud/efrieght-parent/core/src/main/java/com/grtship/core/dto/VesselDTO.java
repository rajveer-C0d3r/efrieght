package com.grtship.core.dto;

import com.grtship.core.annotation.EnableCustomAudit;
import com.grtship.core.annotation.IgnoreAuditField;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * A DTO for the {@link com.grt.efreight.domain.Vessel} entity.
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@EnableCustomAudit
public class VesselDTO extends VesselUpdateRequest{
	@IgnoreAuditField
	private static final long serialVersionUID = -2003918928559383790L;
    
    private String operatorName;
    private Boolean submittedForApproval;
   
    @Override
    public VesselUpdateRequest active(Boolean active) {
    	this.active = active;
    	return this;
    }
    @Override
    public VesselDTO deactivationDetails(VesselDeactivationDto deactivationDetails) {
    	this.deactivationDetails = deactivationDetails;
    	return this;
    }
    @Override
    public VesselDTO reactivationDetails(VesselReactivationDto reactivationDetails) {
    	this.reactivationDetails = reactivationDetails;
    	return this;
    }
    @Override
    public VesselDTO operatorId(Long operatorId) {
    	this.operatorId = operatorId;
    	return this;
    }
    @Override
    public VesselDTO name(String name) {
    	this.name = name;
    	return this;
    }

}
