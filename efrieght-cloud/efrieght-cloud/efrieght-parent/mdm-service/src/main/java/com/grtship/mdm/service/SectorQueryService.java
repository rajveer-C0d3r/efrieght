/**
 * 
 */
package com.grtship.mdm.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.grtship.core.dto.SectorDTO;
import com.grtship.mdm.criteria.SectorCriteria;

/**
 * @author Ajay
 *
 */
public interface SectorQueryService {

	Page<SectorDTO> findByCriteria(SectorCriteria sectorCriteria, Pageable pageable);

}
