package com.grtship.core.dto;

import javax.validation.constraints.NotNull;

import com.grtship.core.annotation.EnableCustomAudit;
import com.grtship.core.annotation.IgnoreAuditField;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@EnableCustomAudit
public class CompanyBranchBaseDTO extends AbstractAuditingDTO{
	/**
	 * 
	 */
	@IgnoreAuditField
	private static final long serialVersionUID = -8184352502455387228L;

	private Long id;

    @NotNull
    private String code;

    @NotNull
    private String name;
    
    private Long companyId;
    private String companyName;
    private Long clientId;
    
	public CompanyBranchBaseDTO(Long id, @NotNull String code, Long companyId, String companyName, Long clientId) {
		super();
		this.id = id;
		this.code = code;
		this.companyId = companyId;
		this.companyName = companyName;
		this.clientId = clientId;
	}

	public CompanyBranchBaseDTO(Long id, @NotNull String code, @NotNull String name, Long companyId, String companyName,
			Long clientId) {
		super();
		this.id = id;
		this.code = code;
		this.name = name;
		this.companyId = companyId;
		this.companyName = companyName;
		this.clientId = clientId;
	}
    
    
    
    
}
