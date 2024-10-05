package com.grtship.core.dto;

import java.io.Serializable;
import java.util.Set;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.grtship.core.annotation.EnableAuditLevel;
import com.grtship.core.annotation.EnableCustomAudit;
import com.grtship.core.annotation.IgnoreAuditField;
import com.grtship.core.enumeration.ActionType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A DTO for the {@link com.grt.efreight.domain.ObjectModule} entity.
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@EnableCustomAudit
public class ObjectModuleDTO implements Serializable {
    
    /**
	 * 
	 */
	@IgnoreAuditField
	private static final long serialVersionUID = -3483430566043273006L;

	private Long id;
    @Size(max = 40)
    @NotNull(message = "Module Name is required") 
    private String moduleName;
    @NotNull(message = "Approval Required is mandatory") 
    private Boolean approvalRequired;
    @NotNull(message = "Approval Level is required") 
    private Integer approvalLevel;
    @NotNull(message = "Maker As Approver is required") 
    private Boolean makerAsApprover;
    @NotNull(message = "Duplicate approver is required") 
    private Boolean duplicateApprover;
    @NotNull(message = "Parallet Approver is required") 
    private Boolean parallelApprover;
    @NotNull private Long companyId;
    @NotNull private Long clientId;
    
    @EnableAuditLevel(idOnly = true)
    @NotEmpty(message = "Approval Sequence is required") 
    private Set<ObjectApprovalSequenceDTO> objectApprovalSequence;
    @NotNull(message = "Action is required") 
    private ActionType action;
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public Integer getApprovalLevel() {
        return approvalLevel;
    }

    public void setApprovalLevel(Integer approvalLevel) {
        this.approvalLevel = approvalLevel;
    }

    public Boolean isMakerAsApprover() {
        return makerAsApprover;
    }

    public void setMakerAsApprover(Boolean makerAsApprover) {
        this.makerAsApprover = makerAsApprover;
    }

    public Boolean isDuplicateApprover() {
        return duplicateApprover;
    }

    public void setDuplicateApprover(Boolean duplicateApprover) {
        this.duplicateApprover = duplicateApprover;
    }

    public Boolean isParallelApprover() {
        return parallelApprover;
    }

    public void setParallelApprover(Boolean parallelApprover) {
        this.parallelApprover = parallelApprover;
    }
}
