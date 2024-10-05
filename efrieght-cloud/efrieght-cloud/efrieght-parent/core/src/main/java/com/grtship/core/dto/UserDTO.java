package com.grtship.core.dto;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.grtship.core.annotation.EnableAuditLevel;
import com.grtship.core.annotation.EnableCustomAudit;
import com.grtship.core.enumeration.DomainStatus;
import com.grtship.core.enumeration.UserDeactivationType;
import com.grtship.core.enumeration.UserType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * A DTO representing a user, with his authorities.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
@EnableCustomAudit
public class UserDTO extends BaseDTO{

	@JsonIgnore
    @Size(min = 1, max = 50)
    private String login;
	
    @Size(max = 50)
    private String contactNo;

    @NotNull
    @Email
    @Size(min = 5, max = 254)
    private String email;

    @Size(max = 256)
    private String imageUrl;
    @Builder.Default
    private boolean activeFlag = false;

    @Size(min = 2, max = 10)
    private String langKey;
    private String activationKey;
    private Long departmentId;
    private String departmentName;
    private Long designationId;
    private String designationName;
    private Long clientId;
    private Long companyId;
    private Long branchId;
    private UserType userType;
    
    private Boolean allCompanies;
    private Boolean allowLogin;
    private Boolean isDeactivated;
    private Set<String> authorities;
    @EnableAuditLevel(idOnly = true)
    private List<UserCompanyDTO> userCompanies;
    @EnableAuditLevel(idOnly = true)
    private List<UserRoleDTO> userRoles;
    private String createdBy;

    private Instant createdDate;

    private String lastModifiedBy;

    private Instant lastModifiedDate;
    
    @EnableAuditLevel(level = 1)
    private DeactivationDTO deactivate;
    @EnableAuditLevel(level = 1)
    private ReactivationDTO reactivate;
    private DomainStatus status;
    private Boolean submittedForApproval;
    private UserDeactivationType deactivationType;
    private String resetKey;
    
    private String verificationCode;
    
    public void addAuthorities(String authority) {
    	if(this.authorities==null) {
    		this.authorities = new HashSet<>();
    	}
    	this.authorities.add(authority);
    }
    public void addAuthorities(Set<String> authorities) {
    	if(this.authorities==null) {
    		this.authorities = new HashSet<>();
    	}
    	this.authorities.addAll(authorities);
    }
    
    public void addUserCompany(UserCompanyDTO userCompany) {
    	if(this.userCompanies == null && userCompany!=null) {
    		this.userCompanies = new ArrayList<>();
    		this.userCompanies.add(userCompany);
    	}
    }
    
}
