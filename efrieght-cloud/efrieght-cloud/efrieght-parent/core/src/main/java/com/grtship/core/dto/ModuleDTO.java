package com.grtship.core.dto;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.grtship.core.annotation.EnableCustomAudit;
import com.grtship.core.annotation.IgnoreAuditField;

import lombok.Data;

/**
 * A DTO for the {@link com.grt.efreight.domain.Module} entity.
 */
@Data
@EnableCustomAudit
public class ModuleDTO implements Serializable {
    
    /**
	 * 
	 */
	@IgnoreAuditField
	private static final long serialVersionUID = -1824680818181105437L;
	@NotNull(message = "Module Name is Required")
	@NotBlank(message = "Module Name is Required")
    @Size(max = 100)
    private String moduleName;
	

}
