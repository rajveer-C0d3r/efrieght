package com.grtship.core.dto;

import com.grtship.core.annotation.EnableCustomAudit;

import lombok.Data;

@Data
@EnableCustomAudit
public class UserDeactivationRequest {
	
	private Boolean isPermanent;
	private Long userId;

}
