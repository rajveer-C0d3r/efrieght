package com.grtship.core.dto;

import java.io.Serializable;
import java.util.Set;

import com.grtship.core.annotation.EnableAuditLevel;
import com.grtship.core.annotation.EnableCustomAudit;
import com.grtship.core.annotation.IgnoreAuditField;

import lombok.Data;

@Data
@EnableCustomAudit
public class MainModuleDTO implements Serializable{
	@IgnoreAuditField
	private static final long serialVersionUID = 1L;
	
	private Long id;
	
	private String name;

	@EnableAuditLevel(idOnly = true)
	private Set<SubModuleDTO> subModules;
	
}
