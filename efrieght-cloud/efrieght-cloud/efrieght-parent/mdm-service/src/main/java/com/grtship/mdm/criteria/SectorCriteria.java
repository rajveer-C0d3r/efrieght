/**
 * 
 */
package com.grtship.mdm.criteria;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author Ajay
 *
 */
@Data
@EqualsAndHashCode
@NoArgsConstructor
public class SectorCriteria implements Serializable{
	private static final long serialVersionUID = 1L;
	private Long id;
	private String code;
	private String name;
}
