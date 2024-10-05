/**
 * 
 */
package com.grtship.core.dto;

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
public class BranchResponse {
	private Long branchId;
	private Long companyId;
	private String code;
	private String name;
	private Boolean isBranchDeactivated;
}
