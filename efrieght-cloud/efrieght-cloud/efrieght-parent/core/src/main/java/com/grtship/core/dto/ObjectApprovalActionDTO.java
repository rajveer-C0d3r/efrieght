package com.grtship.core.dto;

import java.io.Serializable;
import java.time.LocalDate;

import javax.validation.constraints.Size;

import com.grtship.core.annotation.EnableCustomAudit;
import com.grtship.core.annotation.IgnoreAuditField;

/**
 * A DTO for the {@link com.grt.efreight.domain.ObjectApprovalAction} entity.
 */
@EnableCustomAudit
public class ObjectApprovalActionDTO implements Serializable {
    
    /**
	 * 
	 */
	@IgnoreAuditField
	private static final long serialVersionUID = -102440659539762223L;

	private Long id;

    private Integer approvalSequence;

    @Size(max = 45)
    private String permissionCode;

    private Long actionBy;

    @Size(max = 100)
    private String actionerName;

    private LocalDate actionDateTime;

    @Size(max = 30)
    private String action;

    @Size(max = 150)
    private String actionComment;


    private Long objectApprovalId;
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getApprovalSequence() {
        return approvalSequence;
    }

    public void setApprovalSequence(Integer approvalSequence) {
        this.approvalSequence = approvalSequence;
    }

    public String getPermissionCode() {
        return permissionCode;
    }

    public void setPermissionCode(String permissionCode) {
        this.permissionCode = permissionCode;
    }

    public Long getActionBy() {
        return actionBy;
    }

    public void setActionBy(Long actionBy) {
        this.actionBy = actionBy;
    }

    public String getActionerName() {
        return actionerName;
    }

    public void setActionerName(String actionerName) {
        this.actionerName = actionerName;
    }

    public LocalDate getActionDateTime() {
        return actionDateTime;
    }

    public void setActionDateTime(LocalDate actionDateTime) {
        this.actionDateTime = actionDateTime;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getActionComment() {
        return actionComment;
    }

    public void setActionComment(String actionComment) {
        this.actionComment = actionComment;
    }

    public Long getObjectApprovalId() {
        return objectApprovalId;
    }

    public void setObjectApprovalId(Long objectApprovalId) {
        this.objectApprovalId = objectApprovalId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ObjectApprovalActionDTO)) {
            return false;
        }

        return id != null && id.equals(((ObjectApprovalActionDTO) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ObjectApprovalActionDTO{" +
            "id=" + getId() +
            ", approvalSequence=" + getApprovalSequence() +
            ", permissionCode='" + getPermissionCode() + "'" +
            ", actionBy=" + getActionBy() +
            ", actionerName='" + getActionerName() + "'" +
            ", actionDateTime='" + getActionDateTime() + "'" +
            ", action='" + getAction() + "'" +
            ", actionComment='" + getActionComment() + "'" +
            ", objectApprovalId=" + getObjectApprovalId() +
            "}";
    }
}
