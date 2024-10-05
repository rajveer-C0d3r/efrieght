package com.grtship.mdm.criteria;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@EqualsAndHashCode
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExternalEntityCriteria implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Long id;
	private String name;
	private String code;
	private String country;
	private String alias;
	private String entityBusinessType; // used for all business types (Entity Details-ui)
	private Boolean activeFlag;
	private Boolean customerFlag; // used for customer type entity.
	private Boolean vendorFlag; // used for vendor type entity.
	private Long groupId;
	private String status;

}
