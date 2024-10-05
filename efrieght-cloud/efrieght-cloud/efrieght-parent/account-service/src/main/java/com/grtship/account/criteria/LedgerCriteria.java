package com.grtship.account.criteria;

import java.io.Serializable;
import java.util.List;

import com.grtship.account.interfaces.Criteria;
import com.grtship.core.filter.LongFilter;
import com.grtship.core.filter.StringFilter;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LedgerCriteria implements Serializable, Criteria {

	@Override
	public Criteria copy() {
		return new LedgerCriteria(this);
	}

	private static final long serialVersionUID = 1L;

	private LongFilter id;

	private StringFilter code;

	private StringFilter name;

	private String groupName;

	private String alias;

	private List<String> aliasList;

	private Boolean activeFlag;

	private String natureOfGroup;

	private List<Long> ids;

	private Long groupId;

	private String status;

	public LedgerCriteria(LedgerCriteria other) {
		this.id = other.id == null ? null : other.id.copy();
		this.code = other.code == null ? null : other.code.copy();
		this.name = other.name == null ? null : other.name.copy();
		this.alias = other.alias == null ? null : other.alias;
	}

}
