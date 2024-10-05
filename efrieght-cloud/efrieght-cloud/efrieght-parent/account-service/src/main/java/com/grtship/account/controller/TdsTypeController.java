package com.grtship.account.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.grtship.account.service.TdsTypeService;
import com.grtship.core.dto.TdsTypeDTO;

/**
 * REST controller for managing {@link com.grt.efreight.account.domain.TdsType}.
 */
@RestController
@RequestMapping("/api/tds-types")
public class TdsTypeController {

	private final Logger log = LoggerFactory.getLogger(TdsTypeController.class);

	@Autowired
	private TdsTypeService tdsTypeService;

	/**
	 * {@code GET } : get all the tdsRates.
	 *
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list
	 *         of TdsTypes in body.
	 */
	@GetMapping
	public List<TdsTypeDTO> getAllTdsTypes() {
		log.debug("REST request to get all TdsTypes");
		return tdsTypeService.findAll();
	}
}
