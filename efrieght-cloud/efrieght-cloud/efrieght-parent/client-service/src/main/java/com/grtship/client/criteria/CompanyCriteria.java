package com.grtship.client.criteria;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
public class CompanyCriteria implements Serializable{

	private static final long serialVersionUID = 1L;

	private Long id;
	
	private String code;
	
	private String name;
	
	private String status;
	
	private List<Long> countryIds;
	
	private Boolean activeFlag;
}
