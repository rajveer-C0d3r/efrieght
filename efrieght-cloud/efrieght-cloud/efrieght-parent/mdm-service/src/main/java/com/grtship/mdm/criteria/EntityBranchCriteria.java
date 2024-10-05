package com.grtship.mdm.criteria;

import java.io.Serializable;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode
@ToString
public class EntityBranchCriteria implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Long id;
	private String name;
	private String code;
	private String country;
	private String gstNo;
	private Boolean customerFlag;
	private Boolean vendorFlag;
	private Boolean activeFlag;
	private Long entiyId;
	private String status;
	private List<Long> entityIds;
}
