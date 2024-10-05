package com.grtship.account.criteria;

import java.io.Serializable;

import com.grtship.account.interfaces.Criteria;
import com.grtship.core.filter.BooleanFilter;
import com.grtship.core.filter.Filter;
import com.grtship.core.filter.LongFilter;
import com.grtship.core.filter.StringFilter;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Criteria class for the {@link com.grt.efreight.account.domain.Tds} entity.
 * This class is used in {@link com.TdsController.efreight.account.web.rest.TdsResource}
 * to receive all the possible filtering options from the Http GET request
 * parameters. For example the following could be a valid request:
 * {@code /tds?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific
 * {@link Filter} class are used, we need to use fix type specific filters.
 */
@Data
@EqualsAndHashCode
public class TdsCriteria implements Serializable, Criteria {

	private static final long serialVersionUID = 1L;

	private LongFilter id;

	private StringFilter code;

	private StringFilter description;

	private BooleanFilter activeFlag;

	public TdsCriteria() {
	}

	public TdsCriteria(TdsCriteria other) {
		this.id = other.id == null ? null : other.id.copy();
		this.code = other.code == null ? null : other.code.copy();
		this.description = other.description == null ? null : other.description.copy();
		this.activeFlag = other.activeFlag == null ? null : other.activeFlag.copy();
	}

	@Override
	public TdsCriteria copy() {
		return new TdsCriteria(this);
	}

}
