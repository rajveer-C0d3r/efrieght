package com.grtship.core.dto;

import com.grtship.core.annotation.EnableCustomAudit;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EnableCustomAudit
public class BranchDTO {
	private Long branchId;
	private String branchName;

}
