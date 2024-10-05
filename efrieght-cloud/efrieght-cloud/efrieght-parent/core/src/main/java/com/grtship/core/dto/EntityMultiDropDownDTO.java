package com.grtship.core.dto;

import java.io.Serializable;
import java.util.Set;

import com.grtship.core.annotation.EnableAuditLevel;
import com.grtship.core.annotation.EnableCustomAudit;
import com.grtship.core.annotation.IgnoreAuditField;

import lombok.Data;

@Data
@EnableCustomAudit
public class EntityMultiDropDownDTO implements Serializable{
	/**
	 * 
	 */
	@IgnoreAuditField
	private static final long serialVersionUID = 1L;
	private Long id;
	private String code;
	private String name;
	@EnableAuditLevel
	private Set<String> alias;
	

	public EntityMultiDropDownDTO(Long id,String code, String name) {
		this.id = id;
		this.code = code;
		this.name = name;
	}
}
