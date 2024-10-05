package com.grtship.core.dto;

import java.util.List;

import com.grtship.core.annotation.EnableAuditLevel;
import com.grtship.core.annotation.EnableCustomAudit;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EnableCustomAudit
public class CompanyWithBranchDTO {
	private Long companyId;
	private String companyName;
	
	@EnableAuditLevel(idOnly = true)
	private List<BranchDTO> branches;

}
