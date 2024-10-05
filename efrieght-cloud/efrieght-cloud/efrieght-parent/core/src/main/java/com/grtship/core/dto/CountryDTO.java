package com.grtship.core.dto;

import java.util.Set;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.grtship.core.annotation.EnableAuditLevel;
import com.grtship.core.annotation.EnableCustomAudit;
import com.grtship.core.annotation.IgnoreAuditField;
import com.grtship.core.enumeration.DomainStatus;
import com.grtship.core.enumeration.GstVatType;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * A DTO for the {@link com.grt.efreight.domain.Country} entity.
 */

@Data
@EqualsAndHashCode(callSuper=true)
@EnableCustomAudit
public class CountryDTO extends ClientAuditableEntityDTO {


	/**
	 * 
	 */
	@IgnoreAuditField
	private static final long serialVersionUID = 1L;

	private Long id;

	@Size(min = 3, max = 3, message = "Country Code Should Have 3 Characters.")
	@NotBlank(message = "Country Code Is Mandatory, Please Enter Country Code.")
    private String code;

	@Size(max = 50, message = "Country Name Exceeds Character Limits, Maximum 50 Characters Allowed.")
	@NotBlank(message = "Country Name Is Mandatory, Please Enter Country Name.")
    private String name;

	@NotNull(message = "GST/VAT Type Is Mandatory, Please Select GST/VAT Type.")
    private GstVatType gstOrVatType;

    private Boolean isStateMandatory;

    @NotNull(message = "Currency Is Mandatory, Please Select Currency.")
    private Long currencyId;

    @NotNull(message = "Sector Is Mandatory, Please Select Sector.")
    private Long sectorId;
    
    private Boolean activeFlag = false;
    
    private DomainStatus status;
    
    private Boolean submittedForApproval = false;
    
    private String currencyName;

    private String sectorName;

    @EnableAuditLevel(idOnly = true)
    private Set<ObjectAliasDTO> aliases;

    @EnableAuditLevel(idOnly = true)
    private Set<StateDTO> states;

    @EnableAuditLevel(idOnly = true)
    private Set<DocumentDTO> documents;
}
