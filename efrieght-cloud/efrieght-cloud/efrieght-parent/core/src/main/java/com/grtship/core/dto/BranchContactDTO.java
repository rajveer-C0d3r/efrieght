package com.grtship.core.dto;

import java.io.Serializable;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

import com.grtship.core.annotation.EnableCustomAudit;
import com.grtship.core.annotation.IgnoreAuditField;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * A DTO for the {@link com.grt.efreight.domain.BranchContacts} entity.
 */
@Data
@ToString
@EqualsAndHashCode
@EnableCustomAudit
public class BranchContactDTO implements Serializable {
    
    /**
	 * 
	 */
	@IgnoreAuditField
	private static final long serialVersionUID = 1L;

	private Long id;
	
    @NotNull(message = "Name is Mandatory.")
    private String name;

    @NotNull(message = "Email is Mandatory.")
    @Email(message = "Email format not supported")
    private String email;

    @NotNull(message = "Cell Number is Mandatory.")
    private String cellNumber;
    
    private String departmentName;

    private String designationName;
    
    @NotNull(message = "Branch Id is required.")
    private Long entityBranchId;
    private String entityBranchName;
    
}
