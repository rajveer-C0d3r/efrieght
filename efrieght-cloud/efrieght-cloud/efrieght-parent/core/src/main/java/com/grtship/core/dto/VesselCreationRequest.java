package com.grtship.core.dto;

import javax.validation.constraints.NotNull;

import com.grtship.core.annotation.EnableCustomAudit;
import com.grtship.core.annotation.IgnoreAuditField;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@EnableCustomAudit
public class VesselCreationRequest extends ClientAuditableEntityDTO{
	@IgnoreAuditField
	private static final long serialVersionUID = -7235292577444369999L;
	protected Long id;
    @NotNull(message = "Name is required")
    protected String name;
    protected Long operatorId;
    
    public VesselCreationRequest operatorId(Long operatorId) {
    	this.operatorId = operatorId;
    	return this;
    }
    public VesselCreationRequest name(String name) {
    	this.name = name;
    	return this;
    }

}
