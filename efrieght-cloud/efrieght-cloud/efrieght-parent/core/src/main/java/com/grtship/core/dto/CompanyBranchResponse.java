/**
 * 
 */
package com.grtship.core.dto;

import java.util.List;

import com.grtship.core.annotation.EnableAuditLevel;
import com.grtship.core.annotation.EnableCustomAudit;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Ajay
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EnableCustomAudit
public class CompanyBranchResponse {
	private Long clientId;
	private Long companyId;
	private String code;
	private String name;
	private Boolean isCompanyDeactivated;
	@EnableAuditLevel(idOnly = true)
	List<BranchResponse> branches;
}
