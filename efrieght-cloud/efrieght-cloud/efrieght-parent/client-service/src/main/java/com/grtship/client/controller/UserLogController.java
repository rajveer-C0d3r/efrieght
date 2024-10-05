/**
 * 
 */
package com.grtship.client.controller;

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

import com.grtship.client.criteria.UserLogCriteria;
import com.grtship.client.service.UserLogQueryService;
import com.grtship.client.util.PaginationUtil;
import com.grtship.core.dto.PageableEntityDTO;
import com.grtship.core.dto.UserLogDTO;

/**
 * @author Ajay
 *
 */
@RestController
@RequestMapping("/api/system/user/log")
public class UserLogController {
	
	private static final Logger log = LoggerFactory.getLogger(UserLogController.class);
	@Autowired
	private UserLogQueryService userLogQueryService;
	
	@GetMapping
	public ResponseEntity<PageableEntityDTO<UserLogDTO>> getAllClientUserLog(UserLogCriteria criteria,
			Pageable pageable) {
		log.debug("REST request to get a page of system audit");
		Page<UserLogDTO> page = userLogQueryService.findByCriteria(criteria, pageable);
		HttpHeaders headers = PaginationUtil
				.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
		return ResponseEntity.ok().headers(headers)
				.body(new PageableEntityDTO<UserLogDTO>(page.getContent(), page.getTotalElements()));
	}
}
