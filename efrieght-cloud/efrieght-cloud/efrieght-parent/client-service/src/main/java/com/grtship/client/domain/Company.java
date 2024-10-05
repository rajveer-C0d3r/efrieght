package com.grtship.client.domain;

import java.time.LocalDate;
import java.time.Month;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.ParamDef;

import com.grtship.core.annotation.EnableAuditLevel;
import com.grtship.core.annotation.EnableCustomAudit;
import com.grtship.core.enumeration.DomainStatus;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * A Company.
 */
@Entity
@Table(name = "company")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Data
@EqualsAndHashCode(callSuper = true)
@FilterDef(name = "clientAccessFilter", parameters = { @ParamDef(name = "clientId", type = "long")})
		
@Filters({
	@Filter(name = "clientAccessFilter", condition = "client_id = :clientId")
})
@EnableCustomAudit
public class Company extends AbstractAuditingEntity  {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Size(max = 10, message = "Company Code must be of 10 Character.")
	@NotBlank(message = "Company code is mandatory")
	@Column(name = "code", length = 10, nullable = false)
	private String code;

	@Size(max = 60, message = "Company name must be of 60 characters.")
	@NotBlank(message = "Company name is mandatory.")
	@Column(name = "name", length = 60, nullable = false)
	private String name;

	@Size(max = 10, message = "Max allowed digits for mobile no is 10.")
	@Column(name = "mobile_no", length = 10)
	private String mobileNo;

	@Size(max = 50, message = "Max allowed characters for Email id is 50.")
	@Column(name = "email_id", length = 50)
	private String emailId;

	@Size(max = 16, message = "Max allowed characters for Company Pan No. / Income Tax ID is 16.")
	@Column(name = "pan_no", length = 16)
	private String panNo;

	@Size(max = 16, message = "Max allowed characters for Company GST No./VAT ID/Sales Tax ID is 16.")
	@Column(name = "gst_no", length = 16)
	private String gstNo;

	@Size(max = 15, message = "Max allowed characters for Withholding Tax ID / TAN No / TDS ID is 15.")
	@Column(name = "withholding_tax_id", length = 15)
	private String withholdingTaxId;

	@Column(name = "incorporation_date")
	private LocalDate incorporationDate;

	@Column(name = "fixed_financial_year_flag")
	private Boolean fixedFinancialYearFlag;

	@Column(name = "financial_year_start_month")
	@Enumerated(EnumType.STRING)
	private Month financialYearStartMonth;

	@Column(name = "financial_year_end_month")
	@Enumerated(EnumType.STRING)
	private Month financialYearEndMonth;
	
	@Embedded
	@EnableAuditLevel(idOnly = true)
    private Address address;

	@NotNull(message = "Status is mandatory.")
	@Column(name = "status", length = 20)
	@Enumerated(EnumType.STRING)
	private DomainStatus status;

	@Column(name = "submitted_for_approval")
	private Boolean submittedForApproval = Boolean.FALSE;

	@Column(name = "active_flag")
	private Boolean activeFlag = Boolean.FALSE;
	
	 // activate reactivate fields
    @Embedded
    private DomainDeactivate deactivateDtls;
    
    @Embedded 
    private DomainReactivate reactivateDtls;

	@NotNull(message = "Client Id is mandatory.")
	@ManyToOne
	@JoinColumn(name = "client_id")
	@EnableAuditLevel(idOnly = true)
	private Client client;

	public void setClient(Client client) {
		if(client != null && client.getId() != null)
			this.client = client;
	}
}
