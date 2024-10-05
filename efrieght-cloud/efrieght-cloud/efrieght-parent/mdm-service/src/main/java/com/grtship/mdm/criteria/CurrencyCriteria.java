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
public class CurrencyCriteria implements Serializable, Criteria{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private LongFilter id;
	private StringFilter name;
    private StringFilter code;
    
    public CurrencyCriteria(CurrencyCriteria other) {
    	this.id = (other.id == null) ? null : other.id.copy();
    	this.name = (other.name == null) ? null : other.name.copy();
    	this.code = (other.code == null) ? null : other.code.copy();
    }
    
	@Override
	public Criteria copy() {
		return new CurrencyCriteria(this);
	}
}
