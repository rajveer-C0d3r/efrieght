package com.grt.elogfrieght.services.user.criteria;

import java.io.Serializable;

import com.grt.elogfrieght.services.user.interfaces.Criteria;
import com.grtship.core.filter.LongFilter;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode
@NoArgsConstructor
public class RoleCriteria implements Serializable, Criteria {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4256462915093924033L;
	private LongFilter id;
	private String name;
	private String status;
	private Boolean active;
	private Boolean isPublic;
	private Boolean submittedForApproval;
	private Boolean isSystemCreated;

	public RoleCriteria(RoleCriteria other) {
		this.name = other.name == null ? null : other.name;
		this.id = other.id == null ? null : other.id.copy();
	}

	@Override
	public Criteria copy() {
		return new RoleCriteria(this);
	}

}
