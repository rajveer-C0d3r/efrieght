package com.grtship.core.dto;

import com.grtship.core.annotation.EnableCustomAudit;

import lombok.Data;

@Data
@EnableCustomAudit
public class EntityGroupCriteriaDTO {
private Long id;
private String code;
private String name;

public EntityGroupCriteriaDTO(Long id,String code, String name) {
	this.id = id;
	this.code = code;
	this.name = name;
}

}
