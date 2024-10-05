package com.grtship.core.dto;

import java.io.Serializable;

import com.grtship.core.annotation.EnableCustomAudit;
import com.grtship.core.annotation.IgnoreAuditField;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A DTO for the {@link com.grt.efreight.domain.ObjectApprovalSequence} entity.
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@EnableCustomAudit
public class ObjectApprovalSequenceDTO implements Serializable {
    
    /**
	 * 
	 */
	@IgnoreAuditField
	private static final long serialVersionUID = -8281729452994651279L;

	private Long id;

//    private Long roleId;
	
	private String permissionCode;

    private Integer approvalSequence;

    private Integer approvalCount;

    private Long designationId;

    private Long departmentId;


    private Long objectModuleId;
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

//    public Long getRoleId() {
//        return roleId;
//    }
//
//    public void setRoleId(Long roleId) {
//        this.roleId = roleId;
//    }
    
    public String getPermissionCode() {
        return permissionCode;
    }

    public void setPermissionCode(String permissionCode) {
        this.permissionCode = permissionCode;
    }

    public Integer getApprovalSequence() {
        return approvalSequence;
    }

    public void setApprovalSequence(Integer approvalSequence) {
        this.approvalSequence = approvalSequence;
    }

    public Integer getApprovalCount() {
        return approvalCount;
    }

    public void setApprovalCount(Integer approvalCount) {
        this.approvalCount = approvalCount;
    }

    public Long getDesignationId() {
        return designationId;
    }

    public void setDesignationId(Long designationId) {
        this.designationId = designationId;
    }

    public Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    public Long getObjectModuleId() {
        return objectModuleId;
    }

    public void setObjectModuleId(Long objectModuleId) {
        this.objectModuleId = objectModuleId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ObjectApprovalSequenceDTO)) {
            return false;
        }

        return id != null && id.equals(((ObjectApprovalSequenceDTO) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ObjectApprovalSequenceDTO{" +
            "id=" + getId() +
            ", permissionCode=" + getPermissionCode() +
            ", approvalSequence=" + getApprovalSequence() +
            ", approvalCount=" + getApprovalCount() +
            ", designationId=" + getDesignationId() +
            ", departmentId=" + getDepartmentId() +
            ", objectModuleId=" + getObjectModuleId() +
            "}";
    }
}
