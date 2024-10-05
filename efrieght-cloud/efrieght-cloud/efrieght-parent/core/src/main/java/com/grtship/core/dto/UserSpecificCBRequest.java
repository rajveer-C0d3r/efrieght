/**
 * 
 */
package com.grtship.core.dto;

import java.util.Set;

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
public class UserSpecificCBRequest {
	private Long clientId;
	private Long companyId;
	private boolean allCompanies;
	@EnableAuditLevel(idOnly = true)
	private Set<UserAccessDTO> userAccess;
    
	public boolean getAllCompanies() {
		return this.allCompanies;
	}
}
