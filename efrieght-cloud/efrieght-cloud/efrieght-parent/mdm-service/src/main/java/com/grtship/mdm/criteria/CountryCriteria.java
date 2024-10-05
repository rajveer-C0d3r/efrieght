package com.grtship.mdm.criteria;

import java.io.Serializable;

import com.grtship.core.filter.LongFilter;
import com.grtship.core.filter.StringFilter;
import com.grtship.mdm.interfaces.Criteria;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode
@NoArgsConstructor
public class CountryCriteria implements Serializable, Criteria {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1089851990255354265L;
	private LongFilter id;
	private StringFilter name;
	private StringFilter code;
	private LongFilter sectorId;
	private StringFilter sectorName;
	private String alias;
	private String status;
	private Boolean activeFlag;

	public CountryCriteria(CountryCriteria other) {
		this.id = other.id == null ? null : other.id.copy();
		this.name = other.name == null ? null : other.name.copy();
		this.code = other.code == null ? null : other.code.copy();
		this.sectorId = other.sectorId == null ? null : other.sectorId.copy();
		this.sectorName = other.sectorName == null ? null : other.sectorName.copy();
		this.alias = other.alias == null ? null : other.getAlias();
	}

	@Override
	public Criteria copy() {
		return new CountryCriteria(this);
	}

}
