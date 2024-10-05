package com.grtship.core.dto;

import java.io.Serializable;

import com.grtship.core.annotation.EnableCustomAudit;
import com.grtship.core.annotation.IgnoreAuditField;
import com.grtship.core.enumeration.AccessType;

import lombok.Data;

@Data
@EnableCustomAudit
public class SubModuleDTO implements Serializable {
	@IgnoreAuditField
	private static final long serialVersionUID = 1L;

	private Long id;
	
	private String code;
	
	private String name;
	
	private Boolean cargoLevelFlag;
	
	private Boolean masterLevelFlag;
	
	private AccessType accessType;
	
	private String accessCode;
	
    private Long mainModuleId;
}
