package com.grtship.core.dto;

import java.io.Serializable;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.grtship.core.annotation.EnableCustomAudit;
import com.grtship.core.annotation.IgnoreAuditField;

import lombok.Data;

/**
 * A DTO for the {@link com.grt.efreight.domain.Address} entity.
 */
@Data
@EnableCustomAudit
public class AddressDTO implements Serializable {
    
    /**
	 * 
	 */
	@IgnoreAuditField
	private static final long serialVersionUID = 1L;

	private Long id;

    private String address;
    @IgnoreAuditField
	private Long countryId;
    private String countryName;
    @IgnoreAuditField
	private Long stateId;
	private String stateName;
	@IgnoreAuditField
	private Long cityId;
    private String cityName;

    private String location;

	private String pincode;
    
	@JsonIgnore
	private String landMarks;
	
    private Set<String> landMarkSet;// use this in ui to set landmarks.
    
    public void setLandMarkSet(Set<String> landMarksSet) {
    	this.landMarkSet = landMarksSet;
    	if(!CollectionUtils.isEmpty(landMarksSet)) {
    		landMarks = landMarksSet.stream().map(Object::toString).collect(Collectors.joining(","));
		}
    }
    
}
