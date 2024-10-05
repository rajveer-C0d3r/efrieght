package com.grtship.mdm.criteria;

import java.io.Serializable;
import java.util.Objects;

import com.grtship.core.filter.Filter;
import com.grtship.core.filter.IntegerFilter;
import com.grtship.core.filter.LocalDateFilter;
import com.grtship.core.filter.LongFilter;
import com.grtship.core.filter.StringFilter;
import com.grtship.mdm.interfaces.Criteria;

/**
 * Criteria class for the {@link com.grt.efreight.domain.ObjectApprovalAction} entity. This class is used
 * in {@link com.grt.efreight.web.rest.ObjectApprovalActionResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /object-approval-actions?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class ObjectApprovalActionCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private IntegerFilter approvalSequence;

    private StringFilter permissionCode;

    private LongFilter actionBy;

    private StringFilter actionerName;

    private LocalDateFilter actionDateTime;

    private StringFilter action;

    private StringFilter actionComment;

    private LongFilter objectApprovalId;

    public ObjectApprovalActionCriteria() {
    }

    public ObjectApprovalActionCriteria(ObjectApprovalActionCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.approvalSequence = other.approvalSequence == null ? null : other.approvalSequence.copy();
        this.permissionCode = other.permissionCode == null ? null : other.permissionCode.copy();
        this.actionBy = other.actionBy == null ? null : other.actionBy.copy();
        this.actionerName = other.actionerName == null ? null : other.actionerName.copy();
        this.actionDateTime = other.actionDateTime == null ? null : other.actionDateTime.copy();
        this.action = other.action == null ? null : other.action.copy();
        this.actionComment = other.actionComment == null ? null : other.actionComment.copy();
        this.objectApprovalId = other.objectApprovalId == null ? null : other.objectApprovalId.copy();
    }

    @Override
    public ObjectApprovalActionCriteria copy() {
        return new ObjectApprovalActionCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public IntegerFilter getApprovalSequence() {
        return approvalSequence;
    }

    public void setApprovalSequence(IntegerFilter approvalSequence) {
        this.approvalSequence = approvalSequence;
    }

    public StringFilter getPermissionCode() {
        return permissionCode;
    }

    public void setPermissionCode(StringFilter permissionCode) {
        this.permissionCode = permissionCode;
    }

    public LongFilter getActionBy() {
        return actionBy;
    }

    public void setActionBy(LongFilter actionBy) {
        this.actionBy = actionBy;
    }

    public StringFilter getActionerName() {
        return actionerName;
    }

    public void setActionerName(StringFilter actionerName) {
        this.actionerName = actionerName;
    }

    public LocalDateFilter getActionDateTime() {
        return actionDateTime;
    }

    public void setActionDateTime(LocalDateFilter actionDateTime) {
        this.actionDateTime = actionDateTime;
    }

    public StringFilter getAction() {
        return action;
    }

    public void setAction(StringFilter action) {
        this.action = action;
    }

    public StringFilter getActionComment() {
        return actionComment;
    }

    public void setActionComment(StringFilter actionComment) {
        this.actionComment = actionComment;
    }

    public LongFilter getObjectApprovalId() {
        return objectApprovalId;
    }

    public void setObjectApprovalId(LongFilter objectApprovalId) {
        this.objectApprovalId = objectApprovalId;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final ObjectApprovalActionCriteria that = (ObjectApprovalActionCriteria) o;
        return
            Objects.equals(id, that.id) &&
            Objects.equals(approvalSequence, that.approvalSequence) &&
            Objects.equals(permissionCode, that.permissionCode) &&
            Objects.equals(actionBy, that.actionBy) &&
            Objects.equals(actionerName, that.actionerName) &&
            Objects.equals(actionDateTime, that.actionDateTime) &&
            Objects.equals(action, that.action) &&
            Objects.equals(actionComment, that.actionComment) &&
            Objects.equals(objectApprovalId, that.objectApprovalId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
        id,
        approvalSequence,
        permissionCode,
        actionBy,
        actionerName,
        actionDateTime,
        action,
        actionComment,
        objectApprovalId
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ObjectApprovalActionCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (approvalSequence != null ? "approvalSequence=" + approvalSequence + ", " : "") +
                (permissionCode != null ? "permissionCode=" + permissionCode + ", " : "") +
                (actionBy != null ? "actionBy=" + actionBy + ", " : "") +
                (actionerName != null ? "actionerName=" + actionerName + ", " : "") +
                (actionDateTime != null ? "actionDateTime=" + actionDateTime + ", " : "") +
                (action != null ? "action=" + action + ", " : "") +
                (actionComment != null ? "actionComment=" + actionComment + ", " : "") +
                (objectApprovalId != null ? "objectApprovalId=" + objectApprovalId + ", " : "") +
            "}";
    }

}
