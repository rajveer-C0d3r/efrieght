package com.grtship.authorisation.criteria;

import java.io.Serializable;
import java.util.Objects;

import com.grtship.authorisation.interfaces.Criteria;
import com.grtship.core.filter.BooleanFilter;
import com.grtship.core.filter.Filter;
import com.grtship.core.filter.IntegerFilter;
import com.grtship.core.filter.LongFilter;
import com.grtship.core.filter.StringFilter;

/**
 * Criteria class for the {@link com.grt.efreight.domain.ObjectModule} entity. This class is used
 * in {@link com.ObjectModuleController.efreight.web.rest.ObjectModuleResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /object-modules?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class ObjectModuleCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter moduleName;

    private BooleanFilter approvalRequired;

    private IntegerFilter approvalLevel;

    private BooleanFilter makerAsApprover;

    private BooleanFilter duplicateApprover;

    private BooleanFilter parallelApprover;

    private LongFilter objectApprovalSequenceId;

    private LongFilter objectApprovalId;
    
    private String action;

    public ObjectModuleCriteria() {
    }

    public ObjectModuleCriteria(ObjectModuleCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.moduleName = other.moduleName == null ? null : other.moduleName.copy();
        this.approvalRequired = other.approvalRequired == null ? null : other.approvalRequired.copy();
        this.approvalLevel = other.approvalLevel == null ? null : other.approvalLevel.copy();
        this.makerAsApprover = other.makerAsApprover == null ? null : other.makerAsApprover.copy();
        this.duplicateApprover = other.duplicateApprover == null ? null : other.duplicateApprover.copy();
        this.parallelApprover = other.parallelApprover == null ? null : other.parallelApprover.copy();
        this.objectApprovalSequenceId = other.objectApprovalSequenceId == null ? null : other.objectApprovalSequenceId.copy();
        this.objectApprovalId = other.objectApprovalId == null ? null : other.objectApprovalId.copy();
        this.action = other.action == null ? null : other.action;
    }

    @Override
    public ObjectModuleCriteria copy() {
        return new ObjectModuleCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getModuleName() {
        return moduleName;
    }

    public void setModuleName(StringFilter moduleName) {
        this.moduleName = moduleName;
    }

    public BooleanFilter getApprovalRequired() {
        return approvalRequired;
    }

    public void setApprovalRequired(BooleanFilter approvalRequired) {
        this.approvalRequired = approvalRequired;
    }

    public IntegerFilter getApprovalLevel() {
        return approvalLevel;
    }

    public void setApprovalLevel(IntegerFilter approvalLevel) {
        this.approvalLevel = approvalLevel;
    }

    public BooleanFilter getMakerAsApprover() {
        return makerAsApprover;
    }

    public void setMakerAsApprover(BooleanFilter makerAsApprover) {
        this.makerAsApprover = makerAsApprover;
    }

    public BooleanFilter getDuplicateApprover() {
        return duplicateApprover;
    }

    public void setDuplicateApprover(BooleanFilter duplicateApprover) {
        this.duplicateApprover = duplicateApprover;
    }

    public BooleanFilter getParallelApprover() {
        return parallelApprover;
    }

    public void setParallelApprover(BooleanFilter parallelApprover) {
        this.parallelApprover = parallelApprover;
    }

    public LongFilter getObjectApprovalSequenceId() {
        return objectApprovalSequenceId;
    }

    public void setObjectApprovalSequenceId(LongFilter objectApprovalSequenceId) {
        this.objectApprovalSequenceId = objectApprovalSequenceId;
    }

    public LongFilter getObjectApprovalId() {
        return objectApprovalId;
    }

    public void setObjectApprovalId(LongFilter objectApprovalId) {
        this.objectApprovalId = objectApprovalId;
    }
    
    public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final ObjectModuleCriteria that = (ObjectModuleCriteria) o;
        return
            Objects.equals(id, that.id) &&
            Objects.equals(moduleName, that.moduleName) &&
            Objects.equals(approvalRequired, that.approvalRequired) &&
            Objects.equals(approvalLevel, that.approvalLevel) &&
            Objects.equals(makerAsApprover, that.makerAsApprover) &&
            Objects.equals(duplicateApprover, that.duplicateApprover) &&
            Objects.equals(parallelApprover, that.parallelApprover) &&
            Objects.equals(objectApprovalSequenceId, that.objectApprovalSequenceId) &&
            Objects.equals(objectApprovalId, that.objectApprovalId) && 
            Objects.equals(action, that.action);
    }

	@Override
    public int hashCode() {
        return Objects.hash(
        id,
        moduleName,
        approvalRequired,
        approvalLevel,
        makerAsApprover,
        duplicateApprover,
        parallelApprover,
        objectApprovalSequenceId,
        objectApprovalId,
        action
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ObjectModuleCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (moduleName != null ? "moduleName=" + moduleName + ", " : "") +
                (approvalRequired != null ? "apprivalRequired=" + approvalRequired + ", " : "") +
                (approvalLevel != null ? "approvalLevel=" + approvalLevel + ", " : "") +
                (makerAsApprover != null ? "makerAsApprover=" + makerAsApprover + ", " : "") +
                (duplicateApprover != null ? "duplicateApprover=" + duplicateApprover + ", " : "") +
                (parallelApprover != null ? "parallelApprover=" + parallelApprover + ", " : "") +
                (objectApprovalSequenceId != null ? "objectApprovalSequenceId=" + objectApprovalSequenceId + ", " : "") +
                (objectApprovalId != null ? "objectApprovalId=" + objectApprovalId + ", " : "") +
                (action != null ? "action=" + action + ", " : "") +
            "}";
    }

}
