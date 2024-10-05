/**
 * 
 */
package com.grtship.audit.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.grtship.audit.criteria.AuditCriteria;
import com.grtship.core.dto.SystemAuditDTO;

/**
 * @author Ajay
 *
 */
public interface AuditQueryService {

	Page<SystemAuditDTO> findByCriteria(AuditCriteria criteria, Pageable pageable);

}
