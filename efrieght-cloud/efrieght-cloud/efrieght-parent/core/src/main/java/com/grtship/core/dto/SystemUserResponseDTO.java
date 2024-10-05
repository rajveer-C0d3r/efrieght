package com.grtship.core.dto;

import java.util.List;

import com.grtship.core.annotation.EnableAuditLevel;
import com.grtship.core.annotation.EnableCustomAudit;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EnableCustomAudit
public class SystemUserResponseDTO {
	private Long id;
	private String name;
	private String contactNo;
	private Boolean deactivate;
	private Long designationId;
	private String desingationName;
	private Long departmentId;
	private String departmentName;
	private Boolean allowLogin;
	private Boolean allCompanies;
	private Boolean allBranches;
	@EnableAuditLevel(idOnly = true)
	private List<BaseDTO> companyList;
	@EnableAuditLevel(idOnly = true)
	private List<BaseDTO> branchList;
	
	public SystemUserResponseDTO(Long id, String name, String contactNo, Boolean deactivate, Long designationId, Long departmentId, Boolean allowLogin, Boolean allCompanies) {
		this.id = id;
		this.name = name;
		this.contactNo = contactNo;
		this.deactivate = deactivate;
		this.designationId = designationId;
		this.departmentId = departmentId;
		this.allCompanies = allCompanies;
		this.allowLogin = allowLogin;
	}
	

}
