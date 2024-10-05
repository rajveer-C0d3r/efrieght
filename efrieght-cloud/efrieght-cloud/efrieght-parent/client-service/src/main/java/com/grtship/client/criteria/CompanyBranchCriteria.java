package com.grtship.client.criteria;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
public class CompanyBranchCriteria implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Long id;
	private String code;
	private String name;
	private String location;
	private Long cityId;
	private String status;
	private Boolean activeFlag;
	private Boolean submitForApproval;
	private List<Long> ids;
	private Long companyId;
	private List<Long> companyIds;
	private List<Long> clientIds;
	
}
