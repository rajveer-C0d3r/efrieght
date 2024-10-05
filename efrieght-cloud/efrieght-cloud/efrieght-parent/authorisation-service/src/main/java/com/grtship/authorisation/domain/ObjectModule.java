package com.grtship.authorisation.domain;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Type;

import com.grtship.core.enumeration.ActionType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A ObjectModule.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "obj_module")
public class ObjectModule extends ClientAuditableEntity {

    private static final long serialVersionUID = 1L;
    
    @NotBlank(message = "Module Name is Required")
    @NotNull(message = "Module Name is Required")
    private String moduleName;
    
    @Enumerated(EnumType.STRING)
    @NotNull(message = "Action is Required")
    private ActionType action;

    @Type(type= "org.hibernate.type.NumericBooleanType")
    @NotNull(message = "Approval Required is mandatory")
    @Column(name = "approval_required")
    private Boolean approvalRequired;

    @NotNull(message = "Approval Level is mandatory")
    @Column(name = "approval_level")
    private Integer approvalLevel;

    @Type(type= "org.hibernate.type.NumericBooleanType")
    @NotNull(message = "Maker as Approver is mandatory")
    @Column(name = "maker_as_approver")
    private Boolean makerAsApprover;

    @Type(type= "org.hibernate.type.NumericBooleanType")
    @NotNull(message = "Duplicate Approver is mandatory")
    @Column(name = "duplicate_approver")
    private Boolean duplicateApprover;

    @Type(type= "org.hibernate.type.NumericBooleanType")
    @NotNull(message = "Parallel Approver is mandatory")
    @Column(name = "parallel_approver")
    private Boolean parallelApprover;

    @NotEmpty(message = "Approval Sequence is mandatory")
    @OneToMany(fetch = FetchType.EAGER,cascade = CascadeType.ALL)
    private Set<ObjectApprovalSequence> objectApprovalSequences = new HashSet<>();
    
//    @OneToMany(fetch = FetchType.EAGER)
//    private Set<ObjectApproval> objectApprovals = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public ObjectModule moduleName(String moduleName) {
        this.moduleName = moduleName;
        return this;
    }

    public ObjectModule approvalRequired(Boolean apprivalRequired) {
        this.approvalRequired = apprivalRequired;
        return this;
    }

    public ObjectModule approvalLevel(Integer approvalLevel) {
        this.approvalLevel = approvalLevel;
        return this;
    }

    public ObjectModule makerAsApprover(Boolean makerAsApprover) {
        this.makerAsApprover = makerAsApprover;
        return this;
    }

    public ObjectModule duplicateApprover(Boolean duplicateApprover) {
        this.duplicateApprover = duplicateApprover;
        return this;
    }

    public ObjectModule parallelApprover(Boolean parallelApprover) {
        this.parallelApprover = parallelApprover;
        return this;
    }

    public ObjectModule objectApprovalSequences(Set<ObjectApprovalSequence> objectApprovalSequences) {
        this.objectApprovalSequences = objectApprovalSequences;
        return this;
    }

//    public ObjectModule addObjectApprovalSequence(ObjectApprovalSequence objectApprovalSequence) {
//        this.objectApprovalSequences.add(objectApprovalSequence);
//        objectApprovalSequence.setObjectModule(this);
//        return this;
//    }
//
//    public ObjectModule removeObjectApprovalSequence(ObjectApprovalSequence objectApprovalSequence) {
//        this.objectApprovalSequences.remove(objectApprovalSequence);
//        objectApprovalSequence.setObjectModule(null);
//        return this;
//    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

}
