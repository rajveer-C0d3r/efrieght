package com.grtship.client.criteria;

import java.io.Serializable;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode
@NoArgsConstructor
public class ClientCriteria implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2134969987360945739L;
	private Long id;
	private String name;
	private String code;
	private String status;
	private List<Long> ids;
	private Boolean activeFlag;
}
