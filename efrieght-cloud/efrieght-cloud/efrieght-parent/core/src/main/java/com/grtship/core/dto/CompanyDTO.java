package com.grtship.core.dto;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.grtship.core.annotation.EnableAuditLevel;
import com.grtship.core.annotation.EnableCustomAudit;
import com.grtship.core.annotation.IgnoreAuditField;
import com.grtship.core.enumeration.DomainStatus;
import com.grtship.core.enumeration.GstVatType;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * A DTO for the {@link com.grtship.efreight.client.domain.Company} entity.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@EnableCustomAudit
public class CompanyDTO extends AbstractAuditingDTO {

	@IgnoreAuditField
	private static final long serialVersionUID = 1L;

	private Long id;

	@IgnoreAuditField
    private String clientCode;
	@IgnoreAuditField
    private String clientName;

    private String code;

    @NotBlank(message = "Company name is mandatory.")
    @NotNull(message = "Company name is mandatory.")
    private String name;

    private String mobileNo;

    private String emailId;

    private String panNo;
    
    private GstVatType gstVatType;

    private String gstNo;

    private String withholdingTaxId;

    private LocalDate incorporationDate;

    private Boolean fixedFinancialYearFlag;

    private Month financialYearStartMonth;

    private Month financialYearEndMonth;
    
    @IgnoreAuditField
    private DomainStatus status;
    @IgnoreAuditField
    private Boolean activeFlag;
    @IgnoreAuditField
    private Boolean submittedForApproval;

    @EnableAuditLevel(level = 1)
	private DeactivationDTO deactivateDtls;
    
    @EnableAuditLevel(level = 1)
	private ReactivationDTO reactivateDtls;
    
    @EnableAuditLevel(level = 1)
    private AddressDTO address;
    
    private String countryName;
    
    @EnableAuditLevel(idOnly = true)
    private List<CsaDetailsDTO> csaDetails;

    @NotNull(message = "Client Id is mandatory.")
    private Long clientId;

}
