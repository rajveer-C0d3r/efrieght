package com.grtship.account.criteria;

import java.io.Serializable;

import lombok.Data;

/**
 * Criteria class for the {@link com.grt.efreight.account.domain.Bank} entity.
 * 
 */

@Data
public class BankCriteria implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;

	private String code;

	private String name;

	private String branchName;

	private String accountNo;

}
