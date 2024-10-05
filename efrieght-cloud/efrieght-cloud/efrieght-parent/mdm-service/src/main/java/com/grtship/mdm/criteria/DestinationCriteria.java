package com.grtship.mdm.criteria;

import java.io.Serializable;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode
@NoArgsConstructor
public class DestinationCriteria implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Long id;
	private String name;
    private String code;
    private String sector;
    private String country;
    private Long countryId;
    private Long stateId;
    private Long cityId;
    private Boolean isReworkingPort;
    private String type;
    private Long portId;
    private String alias;
    private List<Long> ids;

}
