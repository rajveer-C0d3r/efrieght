package com.grtship.core.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import javax.validation.constraints.Email;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.grtship.core.annotation.EnableAuditLevel;
import com.grtship.core.annotation.EnableCustomAudit;
import com.grtship.core.annotation.IgnoreAuditField;
import com.grtship.core.enumeration.CompanyType;
import com.grtship.core.enumeration.DomainStatus;
import com.grtship.core.enumeration.Domicile;
import com.grtship.core.enumeration.EntityCriteria;
import com.grtship.core.enumeration.EntityGstType;
import com.grtship.core.enumeration.EntityLevel;
import com.grtship.core.enumeration.TdsExemption;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
@EnableCustomAudit
public class ExternalEntityRequestDTO extends ClientAuditableEntityDTO{
    /**
	 * 
	 */
	@IgnoreAuditField
	private static final long serialVersionUID = 1L;

	private Long id;

    private String code;

    private String name;

    private String legalName;

    private String keyPersonName;

    private String cellNo;
    
    @Email
    private String email;

    private String landlineNo;

    private CompanyType companyType;
    
    private Long currencyId;
    
//    tax details fields
    private Domicile domicile;
    
    private EntityGstType gstType;
    private String gstNo;
    
    private String panNoTaxId;
    
//	credit terms fields
	private Boolean customerFlag = Boolean.FALSE;
	private EntityLevel customerEntityLevel;
	private Integer customerCreditDays;
	private Double customerCreditAmount;
	
	private Boolean vendorFlag = Boolean.FALSE;
    private EntityLevel vendorEntityLevel;
	private Integer vendorCreditDays;
	private Double vendorCreditAmount;
	
	@EnableAuditLevel(idOnly = true)
    private Set<ObjectAliasDTO> externalEntityAlias;
    
    private Set<String> entityDetails;
    
    @EnableAuditLevel(level = 1)
	private AddressDTO address;
	
    @EnableAuditLevel(level = 1)
	private EntityGroupDTO groups;
	
	//tdsExemption fields
	private EntityCriteria entityCriteria;
	private TdsExemption tdsExemption;
	private LocalDate tdsExemptionWefDate;
	private LocalDate tdsExemptionValidUptoDate;
	private Integer tdsExemptionPercentage;
	
	private DomainStatus status;
	
	private Boolean isLegalNameSameAsName;
	
	@EnableAuditLevel(idOnly = true)
	@JsonIgnore
	private List<CreditTermsDTO> creditTermsList;
	
}
