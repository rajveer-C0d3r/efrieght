package com.grtship.mdm.criteria;

import java.io.Serializable;
import java.util.Objects;

import com.grtship.core.filter.Filter;
import com.grtship.core.filter.IntegerFilter;
import com.grtship.core.filter.LongFilter;
import com.grtship.mdm.interfaces.Criteria;

/**
 * Criteria class for the {@link com.grt.efreight.domain.ApprovalSequence}
 * entity. This class is used in
 * {@link com.grt.efreight.web.rest.ApprovalSequenceResource} to receive all the
 * possible filtering options from the Http GET request parameters. For example
 * the following could be a valid request:
 * {@code /approval-sequences?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific
 * {@link Filter} class are used, we need to use fix type specific filters.
 */
public class ApprovalSequenceCriteria implements Serializable, Criteria {

	private static final long serialVersionUID = 1L;

	private LongFilter id;

	private LongFilter roleId;

	private IntegerFilter approvalSequence;

	private IntegerFilter approvalCount;

	private LongFilter designationId;

	private LongFilter departmentId;

	private LongFilter moduleId;

	public ApprovalSequenceCriteria() {
	}

	public ApprovalSequenceCriteria(ApprovalSequenceCriteria other) {
		this.id = other.id == null ? null : other.id.copy();
		this.roleId = other.roleId == null ? null : other.roleId.copy();
		this.approvalSequence = other.approvalSequence == null ? null : other.approvalSequence.copy();
		this.approvalCount = other.approvalCount == null ? null : other.approvalCount.copy();
		this.designationId = other.designationId == null ? null : other.designationId.copy();
		this.departmentId = other.departmentId == null ? null : other.departmentId.copy();
		this.moduleId = other.moduleId == null ? null : other.moduleId.copy();
	}

	@Override
	public ApprovalSequenceCriteria copy() {
		return new ApprovalSequenceCriteria(this);
	}

	public LongFilter getId() {
		return id;
	}

	public void setId(LongFilter id) {
		this.id = id;
	}

	public LongFilter getRoleId() {
		return roleId;
	}

	public void setRoleId(LongFilter roleId) {
		this.roleId = roleId;
	}

	public IntegerFilter getApprovalSequence() {
		return approvalSequence;
	}

	public void setApprovalSequence(IntegerFilter approvalSequence) {
		this.approvalSequence = approvalSequence;
	}

	public IntegerFilter getApprovalCount() {
		return approvalCount;
	}

	public void setApprovalCount(IntegerFilter approvalCount) {
		this.approvalCount = approvalCount;
	}

	public LongFilter getDesignationId() {
		return designationId;
	}

	public void setDesignationId(LongFilter designationId) {
		this.designationId = designationId;
	}

	public LongFilter getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentId(LongFilter departmentId) {
		this.departmentId = departmentId;
	}

	public LongFilter getModuleId() {
		return moduleId;
	}

	public void setModuleId(LongFilter moduleId) {
		this.moduleId = moduleId;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		final ApprovalSequenceCriteria that = (ApprovalSequenceCriteria) o;
		return Objects.equals(id, that.id) && Objects.equals(roleId, that.roleId)
				&& Objects.equals(approvalSequence, that.approvalSequence)
				&& Objects.equals(approvalCount, that.approvalCount)
				&& Objects.equals(designationId, that.designationId) && Objects.equals(departmentId, that.departmentId)
				&& Objects.equals(moduleId, that.moduleId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, roleId, approvalSequence, approvalCount, designationId, departmentId, moduleId);
	}

	// prettier-ignore
	@Override
	public String toString() {
		return "ApprovalSequenceCriteria{" + (id != null ? "id=" + id + ", " : "")
				+ (roleId != null ? "roleId=" + roleId + ", " : "")
				+ (approvalSequence != null ? "approvalSequence=" + approvalSequence + ", " : "")
				+ (approvalCount != null ? "approvalCount=" + approvalCount + ", " : "")
				+ (designationId != null ? "designationId=" + designationId + ", " : "")
				+ (departmentId != null ? "departmentId=" + departmentId + ", " : "")
				+ (moduleId != null ? "moduleId=" + moduleId + ", " : "") + "}";
	}

}
