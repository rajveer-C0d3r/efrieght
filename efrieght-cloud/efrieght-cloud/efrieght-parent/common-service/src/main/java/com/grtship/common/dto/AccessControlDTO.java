package com.grtship.common.dto;

import java.io.Serializable;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import lombok.Data;
@Data
@Component
@RequestScope
public class AccessControlDTO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Long clientId;
	private Long branchId;
	private Long companyId;

}
