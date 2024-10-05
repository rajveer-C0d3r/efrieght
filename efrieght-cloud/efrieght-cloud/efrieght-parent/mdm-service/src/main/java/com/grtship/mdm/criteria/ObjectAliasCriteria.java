package com.grtship.mdm.criteria;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
public class ObjectAliasCriteria implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Long id;
	private String referenceName;
	private Long referenceId;
	private List<Long> referenceIds;

}

