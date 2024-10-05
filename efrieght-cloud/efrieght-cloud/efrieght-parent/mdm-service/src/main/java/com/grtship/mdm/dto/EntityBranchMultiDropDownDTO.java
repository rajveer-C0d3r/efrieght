package com.grtship.mdm.dto;

import java.io.Serializable;

import com.grtship.core.annotation.EnableCustomAudit;

import lombok.Data;

@Data
@EnableCustomAudit
public class EntityBranchMultiDropDownDTO implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long id;
	private String code;
	private String name;
	private String gstNo;
	private Long gstId;

	public EntityBranchMultiDropDownDTO(Long id,String code, String name) {
		this.id = id;
		this.code = code;
		this.name = name;
	}

	// Copy factory
    public static EntityBranchMultiDropDownDTO newInstance(EntityBranchMultiDropDownDTO branch) {
        return new EntityBranchMultiDropDownDTO(branch.getId(),branch.getCode(),branch.getName());
    }
}
