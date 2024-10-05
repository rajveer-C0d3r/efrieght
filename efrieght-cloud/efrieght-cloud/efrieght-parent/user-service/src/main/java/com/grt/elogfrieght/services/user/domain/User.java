package com.grt.elogfrieght.services.user.domain;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.grt.elogfrieght.services.user.annotation.ValidateUserAccess;
import com.grtship.core.annotation.EnableAuditLevel;
import com.grtship.core.annotation.EnableCustomAudit;
import com.grtship.core.annotation.IgnoreAuditField;
import com.grtship.core.constant.Constants;
import com.grtship.core.enumeration.DomainStatus;
import com.grtship.core.enumeration.UserDeactivationType;
import com.grtship.core.enumeration.UserType;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * A user.
 */
@ValidateUserAccess
@Entity
@Table(name = "auth_user")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Data
@EqualsAndHashCode(callSuper = true)
@EnableCustomAudit
public class User extends Domain {

	@IgnoreAuditField
	private static final long serialVersionUID = 1L;

	@JsonIgnore
	@NotBlank(message = "password is required")
	@Size(min = 60, max = 60)
	@Column(name = "password_hash", length = 60, nullable = false)
	private String password;

	@NotBlank(message = "Login is required")
	@Pattern(regexp = Constants.LOGIN_REGEX)
	@Size(min = 1, max = 254)
	@Column(length = 254, unique = true)
	private String login;

	@Size(max = 150)
	@Column(name = "employee_name", length = 150)
	private String name;

	@NotBlank(message = "email is required")
	@Email(message = "Enter valid email")
	@Size(min = 5, max = 254)
	@Column(length = 254, unique = true)
	private String email;

	// this value will store !deactivate flag data for dto.
	@NotNull(message = "activated is required")
	@Column(name = "is_active", nullable = false, columnDefinition = "boolean default false")
	private boolean activated;
	
	@NotNull(message = "allow login is required")
	@Column(name = "allow_login", nullable = false)
	private Boolean allowLogin;
	
	@Column(name = "is_deactivated")
	private Boolean isDeactivated;

	@Column(name = "activation_date")
	private Date activationDate;

	@Size(min = 2, max = 10)
	@Column(name = "lang_key", length = 10)
	private String langKey;

	@Size(max = 256)
	@Column(name = "image_url", length = 256)
	private String imageUrl;

	@Size(max = 20)
	@Column(name = "activation_key", length = 20)
	@JsonIgnore
	private String activationKey;

	@Size(max = 20)
	@Column(name = "reset_key", length = 20)
	@JsonIgnore
	private String resetKey;

	@Column(name = "reset_date")
	private Instant resetDate = null;

	@Column(name = "contact_no", length = 20)
	private String contactNo;

	@NotNull(message = "UserType is required")
	@Column(name = "user_type", length = 10)
	@Enumerated(EnumType.STRING)
	private UserType userType;

	@NotNull(message = "all companies is required")
	@Column(name = "all_companies_access", nullable = false)
	private Boolean allCompanies;

	@Column(name = "designation_id")
	private Long designationId;

	@Column(name = "department_id")
	private Long departmentId;

	@Column(name = "force_password_change")
	private Boolean forcePasswordChange;

	@Column(name = "registration_ref_code", length = 10)
	private String registrationRefCode;

	@Column(name = "account_locked")
	private Boolean accountLocked;
	
	@Column(name = "deactivation_type", length = 20, nullable = true)
	@Enumerated(EnumType.STRING)
	private UserDeactivationType deactivationType;
	
	@Embedded
	private Deactivate deactivate;
	
	@Embedded
	private Reactivate reactivate;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false, length = 25)
    @NotNull(message = "Status is Required")
	private DomainStatus status;
	
	@Column(name="submitted_for_approval")
    private Boolean submittedForApproval;

	@NotEmpty(message = "Role is mandatory")
	@ToString.Exclude
	@ElementCollection
	@CollectionTable(name = "auth_user_roles", joinColumns = @JoinColumn(name = "user_id"))
	@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
	@EnableAuditLevel(level = 1)
	private Set<UserRoles> userRoles = new HashSet<>();

	@ToString.Exclude
	@ElementCollection
	@CollectionTable(name = "auth_user_access", joinColumns = @JoinColumn(name = "user_id", unique = false))
	@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
	@IgnoreAuditField
	private Set<UserAccess> userAccess = new HashSet<>();

	@JsonIgnore
	@ManyToMany
	@JoinTable(name = "jhi_user_authority", joinColumns = {
			@JoinColumn(name = "user_id", referencedColumnName = "id") }, inverseJoinColumns = {
					@JoinColumn(name = "authority_name", referencedColumnName = "name") })
	@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
	@BatchSize(size = 20)
	@IgnoreAuditField
	private Set<Authority> authorities = new HashSet<>();
	
	@Column(name="verification_code")
	@IgnoreAuditField
	private String verificationCode;
	
	public void setDeactivationWefDate(LocalDate deactivationWefDate) {
		if(this.deactivate==null) {
			this.deactivate = new Deactivate();
		}
		this.deactivate.setWefDate(deactivationWefDate);
	}
	
	public LocalDate getDeactivationWefDate() {
		return (this.deactivate !=null) ? this.deactivate.getWefDate() : null;
	}
	
	public void setReactivationWefDate(LocalDate reactivationWefDate) {
		if(this.reactivate==null) {
			this.reactivate = new Reactivate();
		}
		this.reactivate.setWefDate(reactivationWefDate);
	}
	
	public void setLogin(String login) {
		this.login = StringUtils.lowerCase(login);
	}

	public User login(String login) {
		if (StringUtils.isNotBlank(login)) {
			this.login = login.toLowerCase();
		}
		return this;
	}

	public User userAccess(Set<UserAccess> userAccess) {
		if (!CollectionUtils.isEmpty(userAccess)) {
			this.userAccess = userAccess;
		}
		return this;
	}

	// Lowercase the login before saving it in database

	public Boolean getAccountLocked() {
		return (this.accountLocked == null || this.accountLocked.equals(Boolean.FALSE)) ? Boolean.FALSE : Boolean.TRUE;
	}

	public User allowLogin(Boolean allowLogin) {
		this.allowLogin = allowLogin;
		return this;
	}

	public User companyId(Long companyId) {
		this.companyId = companyId;
		return this;
	}

	public void addUserAccess(UserAccess userAccess) {
		if(this.userAccess==null) {
			this.userAccess = new HashSet<>();
		}
		this.userAccess.add(userAccess);
	}

	public void setEmail(String email) {
		if (StringUtils.isNotBlank(email)) {
			this.email = email.toLowerCase();
		}
	}

	public void setUserRoles(Set<UserRoles> userRoles) {
		if (!CollectionUtils.isEmpty(userRoles)) {
			this.userRoles = userRoles;
		}
	}

	public Set<UserRoles> getUserRoles() {
		return this.userRoles;
	}

	public User id(Long id) {
		this.id = id;
		return this;
	}

	public Instant getResetDate() {
		return resetDate;
	}

	public User userRoles(Set<UserRoles> userRoles) {
		if (!CollectionUtils.isEmpty(userRoles)) {
			this.userRoles = userRoles;
		} else {
			this.userRoles = null;
		}
		return this;
	}

	public User langKey(String langKey) {
		if (StringUtils.isBlank(langKey)) {
			this.setLangKey(Constants.DEFAULT_LANGUAGE); // default language
		} else {
			this.setLangKey(langKey);
		}
		return this;
	}

	public User name(String name) {
		this.name = name;
		return this;
	}

	public User email(String email) {
		this.email = email.toLowerCase();
		return this;
	}

	public User contactNo(String contactNo) {
		this.contactNo = contactNo;
		return this;
	}

	public User clientId(Long clientId) {
		this.clientId = clientId;
		return this;
	}

	public User userType(UserType userType) {
		this.userType = userType;
		return this;
	}

	public User departmentId(Long departmentId) {
		this.departmentId = departmentId;
		return this;
	}

	public User designationId(Long designationId) {
		this.designationId = designationId;
		return this;
	}

	public void setLangKey(String langKey) {
		if (StringUtils.isBlank(langKey)) {
			this.langKey = Constants.DEFAULT_LANGUAGE; // default language
		} else {
			this.langKey = langKey;
		}
	}

	public User imageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
		return this;
	}

	public User activated(boolean activated) {
		this.activated = activated;
		return this;
	}

	public User allCompanies(Boolean allCompanies) {
		this.allCompanies = (allCompanies == null) ? Boolean.FALSE : allCompanies;
		return this;
	}

	public User authorities(Set<Authority> authorities) {
		this.authorities = authorities;
		return this;
	}

	public User password(String randomAlphabetic) {
		this.password = randomAlphabetic;
		return this;
	}

}
