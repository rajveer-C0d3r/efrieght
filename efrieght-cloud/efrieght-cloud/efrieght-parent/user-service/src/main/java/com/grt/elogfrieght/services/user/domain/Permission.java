package com.grt.elogfrieght.services.user.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.grtship.core.enumeration.PermissionType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A Permission.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "auth_permissions")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Permission implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    @Column(name = "permission_label", nullable = false)
    private String permissionLabel;

    @Id
    @NotNull
    @Column(name = "permission_code", nullable = false, unique = true)
    private String permissionCode;

    @NotNull
    @Column(name = "module_name", nullable = false)
    private String moduleName;
    
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name="type", length = 10)
    private PermissionType permissionType;


    public String getPermissionLabel() {
        return permissionLabel;
    }
    
    public Permission permissionType(PermissionType permissionType) {
        this.permissionType = permissionType;
        return this;
    }

    public Permission permissionLabel(String permissionLabel) {
        this.permissionLabel = permissionLabel;
        return this;
    }

    public void setPermissionLabel(String permissionLabel) {
        this.permissionLabel = permissionLabel;
    }

    public String getPermissionCode() {
        return permissionCode;
    }

    public Permission permissionCode(String permissionCode) {
        this.permissionCode = permissionCode;
        return this;
    }

    public void setPermissionCode(String permissionCode) {
        this.permissionCode = permissionCode;
    }

    public String getModuleName() {
        return moduleName;
    }

    public Permission moduleName(String moduleName) {
        this.moduleName = moduleName;
        return this;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Permission)) {
            return false;
        }
        return permissionCode != null && permissionCode.equals(((Permission) o).permissionCode);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Permission{" +
            ", permissionLabel='" + getPermissionLabel() + "'" +
            ", permissionCode='" + getPermissionCode() + "'" +
            ", moduleName='" + getModuleName() + "'" +
            "}";
    }
}
