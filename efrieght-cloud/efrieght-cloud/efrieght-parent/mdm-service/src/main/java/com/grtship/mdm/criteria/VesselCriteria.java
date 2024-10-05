package com.grtship.mdm.criteria;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import com.grtship.core.filter.Filter;
import com.grtship.core.filter.LongFilter;
import com.grtship.core.filter.StringFilter;
import com.grtship.mdm.interfaces.Criteria;

import lombok.Data;

/**
 * Criteria class for the {@link com.grt.efreight.domain.Vessel} entity. This
 * class is used in {@link com.VesselController.efreight.web.rest.VesselResource} to receive
 * all the possible filtering options from the Http GET request parameters. For
 * example the following could be a valid request:
 * {@code /vessels?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific
 * {@link Filter} class are used, we need to use fix type specific filters.
 */
@Data
public class VesselCriteria implements Serializable, Criteria {

	private static final long serialVersionUID = 1L;

	private StringFilter name;

	private Boolean deactivate;

	private LongFilter operatorId;
	private String containsOperatorName;

	private Long id;
	private Long notEqualToId;
	private List<Long> idList;
	private List<Long> notInIdList;
	private String equalsToName;

	public VesselCriteria() {
	}

	public VesselCriteria(VesselCriteria other) {
		this.name = other.name == null ? null : other.name.copy();
		this.deactivate = other.deactivate == null ? null : other.deactivate;
		this.operatorId = other.operatorId == null ? null : other.operatorId.copy();
	}

	@Override
	public VesselCriteria copy() {
		return new VesselCriteria(this);
	}

	public StringFilter getName() {
		return name;
	}

	public void setName(StringFilter name) {
		this.name = name;
	}

	public LongFilter getOperatorId() {
		return operatorId;
	}

	public void setOperatorId(LongFilter operatorId) {
		this.operatorId = operatorId;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		final VesselCriteria that = (VesselCriteria) o;
		return Objects.equals(id, that.id) && Objects.equals(name, that.name)
				&& Objects.equals(deactivate, that.deactivate) && Objects.equals(operatorId, that.operatorId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, name, deactivate, operatorId);
	}

	// prettier-ignore
	@Override
	public String toString() {
		return "VesselCriteria{" + (id != null ? "id=" + id + ", " : "") + (name != null ? "name=" + name + ", " : "")
				+ (deactivate != null ? "deactivate=" + deactivate + ", " : "")
				+ (operatorId != null ? "operatorId=" + operatorId + ", " : "") + "}";
	}

}
