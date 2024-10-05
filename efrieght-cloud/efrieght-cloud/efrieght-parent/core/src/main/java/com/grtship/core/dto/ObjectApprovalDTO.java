package com.grtship.core.dto;

import java.io.Serializable;
import java.time.LocalDate;

import javax.validation.constraints.Size;

import com.grtship.core.annotation.EnableCustomAudit;
import com.grtship.core.annotation.IgnoreAuditField;

/**
 * A DTO for the {@link com.grt.efreight.domain.ObjectApproval} entity.
 */
@EnableCustomAudit
public class ObjectApprovalDTO implements Serializable {
    
    /**
	 * 
	 */
	@IgnoreAuditField
	private static final long serialVersionUID = 3459588299654333682L;

	private Long id;

    private Long objReferenceId;

    private Boolean isParallel;

    private Long initiaterId;

    @Size(max = 100)
    private String initiaterName;

    private LocalDate approvalRequestDateTime;

    private String status;

    private String objectName;

    private Integer objectVersion;

    @Size(max = 30)
    private String objectStatus;

    @Size(max = 30)
    private String approvalStatus;


    private Long objectModuleId;
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getObjReferenceId() {
        return objReferenceId;
    }

    public void setObjReferenceId(Long objReferenceId) {
        this.objReferenceId = objReferenceId;
    }

    public Boolean isIsParallel() {
        return isParallel;
    }

    public void setIsParallel(Boolean isParallel) {
        this.isParallel = isParallel;
    }

    public Long getInitiaterId() {
        return initiaterId;
    }

    public void setInitiaterId(Long initiaterId) {
        this.initiaterId = initiaterId;
    }

    public String getInitiaterName() {
        return initiaterName;
    }

    public void setInitiaterName(String initiaterName) {
        this.initiaterName = initiaterName;
    }

    public LocalDate getApprovalRequestDateTime() {
        return approvalRequestDateTime;
    }

    public void setApprovalRequestDateTime(LocalDate approvalRequestDateTime) {
        this.approvalRequestDateTime = approvalRequestDateTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    public Integer getObjectVersion() {
        return objectVersion;
    }

    public void setObjectVersion(Integer objectVersion) {
        this.objectVersion = objectVersion;
    }

    public String getObjectStatus() {
        return objectStatus;
    }

    public void setObjectStatus(String objectStatus) {
        this.objectStatus = objectStatus;
    }

    public String getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(String approvalStatus) {
        this.approvalStatus = approvalStatus;
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
        if (!(o instanceof ObjectApprovalDTO)) {
            return false;
        }

        return id != null && id.equals(((ObjectApprovalDTO) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ObjectApprovalDTO{" +
            "id=" + getId() +
            ", objReferenceId=" + getObjReferenceId() +
            ", isParallel='" + isIsParallel() + "'" +
            ", initiaterId=" + getInitiaterId() +
            ", initiaterName='" + getInitiaterName() + "'" +
            ", approvalRequestDateTime='" + getApprovalRequestDateTime() + "'" +
            ", status='" + getStatus() + "'" +
            ", objectName='" + getObjectName() + "'" +
            ", objectVersion=" + getObjectVersion() +
            ", objectStatus='" + getObjectStatus() + "'" +
            ", approvalStatus='" + getApprovalStatus() + "'" +
            ", objectModuleId=" + getObjectModuleId() +
            "}";
    }
}
