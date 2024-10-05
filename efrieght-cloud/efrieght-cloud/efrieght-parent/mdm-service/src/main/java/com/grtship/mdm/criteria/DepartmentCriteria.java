
package com.grtship.mdm.criteria;

import java.io.Serializable;
import java.util.List;

import com.grtship.core.filter.StringFilter;
import com.grtship.mdm.interfaces.Criteria;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode
@NoArgsConstructor
public class DepartmentCriteria implements Serializable, Criteria{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	    private StringFilter code;  
	    private StringFilter name;
	    private String type;
	    private List<Long> ids;
	    
	    public DepartmentCriteria(DepartmentCriteria other) {
			this.name = other.name == null ? null : other.name.copy();
	        this.code = other.code == null ? null : other.code.copy();
		}


	@Override
	public Criteria copy() {
		return new DepartmentCriteria(this);
	}

}
