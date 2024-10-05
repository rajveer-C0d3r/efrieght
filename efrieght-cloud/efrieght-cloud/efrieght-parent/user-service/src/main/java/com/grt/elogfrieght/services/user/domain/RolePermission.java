package com.grt.elogfrieght.services.user.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A RolePermission.
 */
@Data
@NoArgsConstructor
@Embeddable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class RolePermission implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    @Column(name = "permission_code", nullable = false)
    private String permissionCode;
    
    @Column(name="all_permissions")
    private Boolean allPermissions;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public String getPermissionCode() {
        return permissionCode;
    }

    public RolePermission permissionCode(String permissionCode) {
        this.permissionCode = permissionCode;
        return this;
    }

    public void setPermissionCode(String permissionCode) {
        this.permissionCode = permissionCode;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

}
