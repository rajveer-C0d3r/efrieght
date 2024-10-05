package com.grtship.core.dto;

import com.grtship.core.annotation.EnableCustomAudit;

import lombok.Data;

@Data
@EnableCustomAudit
public class MemoryDTO {

	private Long counter;
	private Integer max;
}
