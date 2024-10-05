package com.grtship.account.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.grtship.core.dto.AddressDTO;
import com.grtship.core.enumeration.AccountType;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * A Bank.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "acc_bank")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Bank extends ClientAuditableEntity {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Size(max = 20)
	@NotNull(message = "Bank Code is Mandatory.")
	@Column(name = "bank_code", length = 20, nullable = false)
	private String code;

	@Size(max = 100)
	@NotNull(message = "Bank Name is Mandatory.")
	@Column(name = "bank_name", length = 100, nullable = false)
	private String name;

	@Size(max = 100)
	@NotNull(message = "Branch Name is Mandatory.")
	@Column(name = "branch_name", length = 100, nullable = false)
	private String branchName;

	@Size(max = 20)
	@Column(name = "ifsc_code", length = 20)
	private String ifscCode;

	@Size(max = 20)
	@NotNull(message = "Account No is Mandatory.")
	@Column(name = "account_no", length = 20, nullable = false)
	private String accountNo;

	@NotNull(message = "Account Type is Mandatory.")
	@Enumerated(EnumType.STRING)
	@Column(name = "account_type", length = 100, nullable = false)
	private AccountType accountType;

	@Size(max = 20)
	@Column(name = "swift_code", length = 20)
	private String swiftCode;

	@NotNull(message = "Address is Mandatory.")
	@Column(name = "address_id")
	private Long addressId;

	@Size(max = 100)
	@Column(name = "relationship_manager", length = 100)
	private String relationshipManager;

	@Size(max = 50)
	@Email(message = "Enter valid email id")
	@Column(name = "email_id", length = 50)
	private String emailId;

	@Size(max = 20)
	@Column(name = "contact_no", length = 20)
	private String contactNo;

	public Bank bankCode(String bankCode) {
		this.code = bankCode;
		return this;
	}

	public Bank bankName(String bankName) {
		this.name = bankName;
		return this;
	}

	public Bank branchName(String branchName) {
		this.branchName = branchName;
		return this;
	}

	public Bank ifscCode(String ifscCode) {
		this.ifscCode = ifscCode;
		return this;
	}

	public Bank accountNo(String accountNo) {
		this.accountNo = accountNo;
		return this;
	}

	public Bank accountType(AccountType accountType) {
		this.accountType = accountType;
		return this;
	}

	public Bank swiftCode(String swiftCode) {
		this.swiftCode = swiftCode;
		return this;
	}

	public Bank addressId(Long addressId) {
		this.addressId = addressId;
		return this;
	}

	public Bank relationshipManager(String relationshipManager) {
		this.relationshipManager = relationshipManager;
		return this;
	}

	public Bank emailId(String emailId) {
		this.emailId = emailId;
		return this;
	}

	public Bank contactNo(String contactNo) {
		this.contactNo = contactNo;
		return this;
	}

	public void setAddressId(AddressDTO address) {
		if (address != null && address.getId() != null)
			this.addressId = address.getId();
	}

}
