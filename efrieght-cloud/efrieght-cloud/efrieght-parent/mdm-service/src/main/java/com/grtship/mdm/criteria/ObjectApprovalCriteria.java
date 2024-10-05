package com.grtship.mdm.criteria;

import java.io.Serializable;
import java.util.Objects;

import com.grtship.core.filter.BooleanFilter;
import com.grtship.core.filter.Filter;
import com.grtship.core.filter.IntegerFilter;
import com.grtship.core.filter.LocalDateFilter;
import com.grtship.core.filter.LongFilter;
import com.grtship.core.filter.StringFilter;
import com.grtship.mdm.interfaces.Criteria;

/**
 * Criteria class for the {@link com.grt.efreight.domain.ObjectApproval} entity.
 * This class is used in
 * {@link com.grt.efreight.web.rest.ObjectApprovalResource} to receive all the
 * possible filtering options from the Http GET request parameters. For example
 * the following could be a valid request:
 * {@code /object-approvals?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific
 * {@link Filter} class are used, we need to use fix type specific filters.
 */
public class ObjectApprovalCriteria implements Serializable, Criteria {

	private static final long serialVersionUID = 1L;

	private LongFilter id;

	private LongFilter objReferenceId;

	private BooleanFilter isParallel;

	private LongFilter initiaterId;

	private StringFilter initiaterName;

	private LocalDateFilter approvalRequestDateTime;

	private StringFilter status;

	private StringFilter objectName;

	private IntegerFilter objectVersion;

	private StringFilter objectStatus;

	private StringFilter approvalStatus;

	private LongFilter objectModuleId;

	private LongFilter objectApprovalActionId;

	private LongFilter objectApprovalDataId;

	public ObjectApprovalCriteria() {
	}

	public ObjectApprovalCriteria(ObjectApprovalCriteria other) {
		this.id = other.id == null ? null : other.id.copy();
		this.objReferenceId = other.objReferenceId == null ? null : other.objReferenceId.copy();
		this.isParallel = other.isParallel == null ? null : other.isParallel.copy();
		this.initiaterId = other.initiaterId == null ? null : other.initiaterId.copy();
		this.initiaterName = other.initiaterName == null ? null : other.initiaterName.copy();
		this.approvalRequestDateTime = other.approvalRequestDateTime == null ? null
				: other.approvalRequestDateTime.copy();
		this.status = other.status == null ? null : other.status.copy();
		this.objectName = other.objectName == null ? null : other.objectName.copy();
		this.objectVersion = other.objectVersion == null ? null : other.objectVersion.copy();
		this.objectStatus = other.objectStatus == null ? null : other.objectStatus.copy();
		this.approvalStatus = other.approvalStatus == null ? null : other.approvalStatus.copy();
		this.objectModuleId = other.objectModuleId == null ? null : other.objectModuleId.copy();
		this.objectApprovalActionId = other.objectApprovalActionId == null ? null : other.objectApprovalActionId.copy();
		this.objectApprovalDataId = other.objectApprovalDataId == null ? null : other.objectApprovalDataId.copy();
	}

	@Override
	public ObjectApprovalCriteria copy() {
		return new ObjectApprovalCriteria(this);
	}

	public LongFilter getId() {
		return id;
	}

	public void setId(LongFilter id) {
		this.id = id;
	}

	public LongFilter getObjReferenceId() {
		return objReferenceId;
	}

	public void setObjReferenceId(LongFilter objReferenceId) {
		this.objReferenceId = objReferenceId;
	}

	public BooleanFilter getIsParallel() {
		return isParallel;
	}

	public void setIsParallel(BooleanFilter isParallel) {
		this.isParallel = isParallel;
	}

	public LongFilter getInitiaterId() {
		return initiaterId;
	}

	public void setInitiaterId(LongFilter initiaterId) {
		this.initiaterId = initiaterId;
	}

	public StringFilter getInitiaterName() {
		return initiaterName;
	}

	public void setInitiaterName(StringFilter initiaterName) {
		this.initiaterName = initiaterName;
	}

	public LocalDateFilter getApprovalRequestDateTime() {
		return approvalRequestDateTime;
	}

	public void setApprovalRequestDateTime(LocalDateFilter approvalRequestDateTime) {
		this.approvalRequestDateTime = approvalRequestDateTime;
	}

	public StringFilter getStatus() {
		return status;
	}

	public void setStatus(StringFilter status) {
		this.status = status;
	}

	public StringFilter getObjectName() {
		return objectName;
	}

	public void setObjectName(StringFilter objectName) {
		this.objectName = objectName;
	}

	public IntegerFilter getObjectVersion() {
		return objectVersion;
	}

	public void setObjectVersion(IntegerFilter objectVersion) {
		this.objectVersion = objectVersion;
	}

	public StringFilter getObjectStatus() {
		return objectStatus;
	}

	public void setObjectStatus(StringFilter objectStatus) {
		this.objectStatus = objectStatus;
	}

	public StringFilter getApprovalStatus() {
		return approvalStatus;
	}

	public void setApprovalStatus(StringFilter approvalStatus) {
		this.approvalStatus = approvalStatus;
	}

	public LongFilter getObjectModuleId() {
		return objectModuleId;
	}

	public void setObjectModuleId(LongFilter objectModuleId) {
		this.objectModuleId = objectModuleId;
	}

	public LongFilter getObjectApprovalActionId() {
		return objectApprovalActionId;
	}

	public void setObjectApprovalActionId(LongFilter objectApprovalActionId) {
		this.objectApprovalActionId = objectApprovalActionId;
	}

	public LongFilter getObjectApprovalDataId() {
		return objectApprovalDataId;
	}

	public void setObjectApprovalDataId(LongFilter objectApprovalDataId) {
		this.objectApprovalDataId = objectApprovalDataId;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		final ObjectApprovalCriteria that = (ObjectApprovalCriteria) o;
		return Objects.equals(id, that.id) && Objects.equals(objReferenceId, that.objReferenceId)
				&& Objects.equals(isParallel, that.isParallel) && Objects.equals(initiaterId, that.initiaterId)
				&& Objects.equals(initiaterName, that.initiaterName)
				&& Objects.equals(approvalRequestDateTime, that.approvalRequestDateTime)
				&& Objects.equals(status, that.status) && Objects.equals(objectName, that.objectName)
				&& Objects.equals(objectVersion, that.objectVersion) && Objects.equals(objectStatus, that.objectStatus)
				&& Objects.equals(approvalStatus, that.approvalStatus)
				&& Objects.equals(objectModuleId, that.objectModuleId)
				&& Objects.equals(objectApprovalActionId, that.objectApprovalActionId)
				&& Objects.equals(objectApprovalDataId, that.objectApprovalDataId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, objReferenceId, isParallel, initiaterId, initiaterName, approvalRequestDateTime, status,
				objectName, objectVersion, objectStatus, approvalStatus, objectModuleId, objectApprovalActionId,
				objectApprovalDataId);
	}

	// prettier-ignore
	@Override
	public String toString() {
		return "ObjectApprovalCriteria{" + (id != null ? "id=" + id + ", " : "")
				+ (objReferenceId != null ? "objReferenceId=" + objReferenceId + ", " : "")
				+ (isParallel != null ? "isParallel=" + isParallel + ", " : "")
				+ (initiaterId != null ? "initiaterId=" + initiaterId + ", " : "")
				+ (initiaterName != null ? "initiaterName=" + initiaterName + ", " : "")
				+ (approvalRequestDateTime != null ? "approvalRequestDateTime=" + approvalRequestDateTime + ", " : "")
				+ (status != null ? "status=" + status + ", " : "")
				+ (objectName != null ? "objectName=" + objectName + ", " : "")
				+ (objectVersion != null ? "objectVersion=" + objectVersion + ", " : "")
				+ (objectStatus != null ? "objectStatus=" + objectStatus + ", " : "")
				+ (approvalStatus != null ? "approvalStatus=" + approvalStatus + ", " : "")
				+ (objectModuleId != null ? "objectModuleId=" + objectModuleId + ", " : "")
				+ (objectApprovalActionId != null ? "objectApprovalActionId=" + objectApprovalActionId + ", " : "")
				+ (objectApprovalDataId != null ? "objectApprovalDataId=" + objectApprovalDataId + ", " : "") + "}";
	}

}
