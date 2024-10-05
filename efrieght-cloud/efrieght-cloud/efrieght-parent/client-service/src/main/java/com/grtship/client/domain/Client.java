package com.grtship.client.domain;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.grtship.core.annotation.EnableCustomAudit;
import com.grtship.core.enumeration.DomainStatus;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * A Client.
 */
@Entity
@Table(name = "client")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Data
@EqualsAndHashCode(callSuper = true)
@EnableCustomAudit
public class Client extends AbstractAuditingEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	protected Long id;
    
    @Size(max = 10, message = "Maximum size of client code is 10 characters.")
    @Column(name = "code", nullable = false)
    private String code;

    @NotNull(message = "Client Name Is Mandatory, Please Enter Client Name.")
    @NotBlank(message = "Client Name Is Mandatory, Please Enter Client Name.")
    @Size(max = 60, message = "Client Name Exceeds Character Limits, Maximum 60 Characters Allowed.")
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull(message = "Mobile Number Is Mandatory, Please Enter Mobile Number.")
    @NotBlank(message = "Mobile Number Is Mandatory, Please Enter Mobile Number.")
    @Size(max = 10, message = "Maximum size of client mobile no is 10 characters.")
    @Column(name = "mobile_no", nullable = false)
    private String mobileNo;

    @Email(message = "Enter Valid Email Address")
    @NotNull(message = "Email Id Is Mandatory, Please Enter Email Id.")
    @NotBlank(message = "Email Id Is Mandatory, Please Enter Email Id.")
    @Column(name = "email_id", nullable = false)
    private String emailId;

    @NotNull(message = "PAN No Or Income Tax Id Is Mandatory, Please Enter PAN No Or Income Tax Id.")
    @NotBlank(message = "PAN No Or Income Tax Id Is Mandatory, Please Enter PAN No Or Income Tax Id.")
    @Size(max = 16, message = "PAN No Or Income Tax Id Exceeds Character Limits, Maximum 16 Characters Allowed.")
    @Column(name = "pan_no", nullable = false)
    private String panNo;

    @Size(max = 16, message = "GST or vat or Sales Tax Id Exceeds Character Limits, Maximum 16 Characters Allowed.")
    @Column(name = "sales_tax_id")
    private String salesTaxId;

    @Column(name = "active_flag")
	private Boolean activeFlag = Boolean.FALSE;
    
    @Column(name = "submitted_for_approval")
	private Boolean submittedForApproval = Boolean.FALSE;

	@Enumerated(EnumType.STRING)
	@Column(name = "status")
	private DomainStatus status;
	
	// deactivation-reactivation fields
	@Embedded
	private DomainDeactivate deactivateDtls;
		
	@Embedded
	private DomainReactivate reactivateDtls;
    
    @Embedded
    private Address address;

}
