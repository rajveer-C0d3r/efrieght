package com.grt.elogfrieght.services.user.domain;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.ParamDef;
import org.springframework.util.CollectionUtils;

import com.grtship.core.enumeration.DomainStatus;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * A Role.
 */
@Entity
@Table(name = "auth_role")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@FilterDef(name = "standardFilter", parameters = {  @ParamDef(name="includeAdmin", type="int"), @ParamDef(name="includeCompany", type="int") })
@FilterDef(name = "clientAccessFilter", parameters = { @ParamDef(name = "clientId", type = "long"), @ParamDef(name="includeAdmin", type="int") })
@FilterDef(name = "companyAccessFilter", parameters = { @ParamDef(name = "companyId", type = "long"), @ParamDef(name="includeAdmin", type="int"), @ParamDef(name="includeCompany", type="int") })

@Filters({ 
	@Filter(name = "standardFilter", condition = "((company_id = 0 and 1=:includeCompany) or (client_id = 0 and is_public=1 and 1=:includeAdmin))"),
		@Filter(name = "clientAccessFilter", condition = "(client_id = :clientId or (client_id = 0 and is_public=1 and 1=:includeAdmin))"),
		@Filter(name = "companyAccessFilter", condition = "(company_id = :companyId or (company_id = 0 and 1=:includeCompany) or (client_id = 0 and is_public=1 and 1=:includeAdmin))")})
public class Role extends AbstractAuditingEntity {

    private static final long serialVersionUID = 1L;
    
    @Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	protected Long id;

    @NotNull(message = "Role Name is mandatory")
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "is_public")
    private Boolean isPublic;
    
    @Column(name = "is_system_created")
    private Boolean isSystemCreated;
    
    @Column(name="status")
    @Enumerated(EnumType.STRING)
    private DomainStatus status;
    
    @Column(name="active")
    private Boolean active;
    
    @Column(name = "company_id")
	protected Long companyId; // for admin portal this value should be 0 always

	@Column(name = "client_id")
	protected Long clientId;
    
    @Embedded
	private Deactivate deactivate;
    
    @Embedded
    private Reactivate reactivate;
    
    @Column(name="submitted_for_approval")
    private Boolean submittedForApproval;

    @ElementCollection
    @CollectionTable(name="auth_role_permission", joinColumns = @JoinColumn(name="role_id"))
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<RolePermission> permissions = new HashSet<>();

    public Role(Long id) {
    	this.id = id;
    }
    public Role submittedForApproval(Boolean submittedForApproval) {
    	this.submittedForApproval = submittedForApproval;
    	return this;
    }
    public Role active(Boolean active) {
    	this.active = active;
    	return this;
    }
    public Role deactivate(Deactivate deactivate) {
    	this.deactivate = deactivate;
    	return this;
    }
    public Role reactivate(Reactivate reactivate) {
    	this.reactivate = reactivate;
    	return this;
    }
    public Role name(String name) {
        this.name = name;
        return this;
    }
    public Role status(DomainStatus status) {
        this.status = status;
        return this;
    }

    public Role isPublic(Boolean isPublic) {
        this.isPublic = isPublic;
        return this;
    }

	public Role permissions(Set<RolePermission> permissions) {
		if(!CollectionUtils.isEmpty(permissions)) {
			this.permissions = permissions;
		}
		return this;
	}

}
