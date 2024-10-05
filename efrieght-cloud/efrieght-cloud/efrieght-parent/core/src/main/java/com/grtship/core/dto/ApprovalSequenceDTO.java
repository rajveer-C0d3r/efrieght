package com.grtship.core.dto;

import java.io.Serializable;

import com.grtship.core.annotation.EnableCustomAudit;
import com.grtship.core.annotation.IgnoreAuditField;

/**
 * A DTO for the {@link com.grt.efreight.domain.ApprovalSequence} entity.
 */
@EnableCustomAudit
public class ApprovalSequenceDTO implements Serializable {
    
    /**
	 * 
	 */
	@IgnoreAuditField
	private static final long serialVersionUID = -400860019781470103L;

	private Long id;

    private Long roleId;

    private Integer approvalSequence;

    private Integer approvalCount;

    private Long designationId;

    private Long departmentId;


    private Long moduleId;
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
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

    public Long getModuleId() {
        return moduleId;
    }

    public void setModuleId(Long moduleId) {
        this.moduleId = moduleId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ApprovalSequenceDTO)) {
            return false;
        }

        return id != null && id.equals(((ApprovalSequenceDTO) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ApprovalSequenceDTO{" +
            "id=" + getId() +
            ", roleId=" + getRoleId() +
            ", approvalSequence=" + getApprovalSequence() +
            ", approvalCount=" + getApprovalCount() +
            ", designationId=" + getDesignationId() +
            ", departmentId=" + getDepartmentId() +
            ", moduleId=" + getModuleId() +
            "}";
    }
}
