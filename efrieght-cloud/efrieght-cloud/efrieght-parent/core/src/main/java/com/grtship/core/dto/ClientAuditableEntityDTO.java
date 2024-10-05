package com.grtship.core.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ClientAuditableEntityDTO extends AbstractAuditingDTO {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Long clientId;

	private Long companyId;
	private Long branchId;

}
