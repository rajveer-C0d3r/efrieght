package com.grtship.core.dto;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserAccessCompanyBranchDTO {
    private Long clientId;
    private Long companyId;
    private Boolean allCompanies=Boolean.FALSE;
    private Boolean allBranches;
    private Set<Long> companyIds;
    private Set<Long> branchIds;
}
