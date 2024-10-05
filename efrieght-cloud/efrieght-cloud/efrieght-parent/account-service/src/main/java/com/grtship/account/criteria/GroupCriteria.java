package com.grtship.account.criteria;

import java.io.Serializable;
import java.util.Objects;

import com.grtship.account.interfaces.Criteria;
import com.grtship.core.filter.BooleanFilter;
import com.grtship.core.filter.Filter;
import com.grtship.core.filter.LongFilter;
import com.grtship.core.filter.StringFilter;

import lombok.Data;

/**
 * Criteria class for the {@link com.grt.efreight.account.domain.Group} entity.
 * This class is used in {@link com.GroupController.efreight.account.web.rest.GroupResource}
 * to receive all the possible filtering options from the Http GET request
 * parameters. For example the following could be a valid request:
 * {@code /groups?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific
 * {@link Filter} class are used, we need to use fix type specific filters.
 */
@Data
public class GroupCriteria implements Serializable, Criteria {

	private static final long serialVersionUID = 1L;

	private LongFilter id;

	private StringFilter code;

	private StringFilter name;

	private String natureOfGroup;

	private String alias;

	private BooleanFilter activeFlag;

	private String status;

	private String parentGroupName;

	public GroupCriteria() {
	}

	public GroupCriteria(GroupCriteria other) {
		this.id = other.id == null ? null : other.id.copy();
		this.code = other.code == null ? null : other.code.copy();
		this.name = other.name == null ? null : other.name.copy();
		this.activeFlag = other.activeFlag == null ? null : other.activeFlag.copy();
	}

	@Override
	public GroupCriteria copy() {
		return new GroupCriteria(this);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		final GroupCriteria that = (GroupCriteria) o;
		return Objects.equals(id, that.id) && Objects.equals(code, that.code) && Objects.equals(name, that.name)
				&& Objects.equals(natureOfGroup, that.natureOfGroup) && Objects.equals(activeFlag, that.activeFlag);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, code, name, natureOfGroup, activeFlag);
	}

	// prettier-ignore
	@Override
	public String toString() {
		return "GroupCriteria{" + (id != null ? "id=" + id + ", " : "") + (code != null ? "code=" + code + ", " : "")
				+ (name != null ? "name=" + name + ", " : "")
				+ (natureOfGroup != null ? "natureOfGroup=" + natureOfGroup + ", " : "")
				+ (activeFlag != null ? "activeFlag=" + activeFlag + ", " : "") + "}";
	}

}
