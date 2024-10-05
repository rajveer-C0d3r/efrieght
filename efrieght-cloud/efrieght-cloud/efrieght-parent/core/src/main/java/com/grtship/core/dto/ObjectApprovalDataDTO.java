package com.grtship.core.dto;

import java.io.Serializable;

import javax.validation.constraints.Size;

import com.grtship.core.annotation.EnableCustomAudit;
import com.grtship.core.annotation.IgnoreAuditField;

/**
 * A DTO for the {@link com.grt.efreight.domain.ObjectApprovalData} entity.
 */
@EnableCustomAudit
public class ObjectApprovalDataDTO implements Serializable {
    
    /**
	 * 
	 */
	@IgnoreAuditField
	private static final long serialVersionUID = 4514885832099108554L;

	private Long id;

    @Size(max = 100)
    private String propertyName;

    @Size(max = 2000)
    private String oldValue;

    @Size(max = 2000)
    private String newValue;

    @Size(max = 30)
    private String dataType;

    @Size(max = 45)
    private String action;


    private Long objectApprovalId;
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public String getOldValue() {
        return oldValue;
    }

    public void setOldValue(String oldValue) {
        this.oldValue = oldValue;
    }

    public String getNewValue() {
        return newValue;
    }

    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
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
        if (!(o instanceof ObjectApprovalDataDTO)) {
            return false;
        }

        return id != null && id.equals(((ObjectApprovalDataDTO) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ObjectApprovalDataDTO{" +
            "id=" + getId() +
            ", propertyName='" + getPropertyName() + "'" +
            ", oldValue='" + getOldValue() + "'" +
            ", newValue='" + getNewValue() + "'" +
            ", dataType='" + getDataType() + "'" +
            ", action='" + getAction() + "'" +
            ", objectApprovalId=" + getObjectApprovalId() +
            "}";
    }
}
