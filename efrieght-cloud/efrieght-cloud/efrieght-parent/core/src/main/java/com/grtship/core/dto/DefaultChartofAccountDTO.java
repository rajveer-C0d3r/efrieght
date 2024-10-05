package com.grtship.core.dto;

import java.io.Serializable;

import com.grtship.core.annotation.EnableCustomAudit;
import com.grtship.core.annotation.IgnoreAuditField;
import com.grtship.core.enumeration.NatureOfGroup;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EnableCustomAudit
public class DefaultChartofAccountDTO implements Serializable{
	
	/**
	 * 
	 */
	@IgnoreAuditField
	private static final long serialVersionUID = 1120342700953984322L;
	private String name;
	private String code;
	private NatureOfGroup natureOfGroup;
	public DefaultChartofAccountDTO(String name, String code, String natureOfGroup) {
		super();
		this.name = name;
		this.code = code;
		this.natureOfGroup = NatureOfGroup.valueOf(natureOfGroup);
	}
	

}
