/**
 * 
 */
package com.grtship.audit.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.grtship.audit.criteria.AuditCriteria;
import com.grtship.audit.service.AuditQueryService;
import com.grtship.audit.util.PaginationUtil;
import com.grtship.core.dto.PageableEntityDTO;
import com.grtship.core.dto.SystemAuditDTO;

/**
 * @author Ajay
 *
 */
@RestController
@RequestMapping("/api/system/audit")
public class AuditController {
	
	private static final Logger log = LoggerFactory.getLogger(AuditController.class);
	
	@Autowired
	private AuditQueryService auditFilterService;

	@GetMapping
	public ResponseEntity<PageableEntityDTO<SystemAuditDTO>> getAllSystemAudit(AuditCriteria criteria,
			Pageable pageable) {
		log.debug("REST request to get a page of system audit");
		Page<SystemAuditDTO> page = auditFilterService.findByCriteria(criteria, pageable);
		HttpHeaders headers = PaginationUtil
				.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
		return ResponseEntity.ok().headers(headers)
				.body(new PageableEntityDTO<SystemAuditDTO>(page.getContent(), page.getTotalElements()));
	}
}
