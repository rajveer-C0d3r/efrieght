package com.grtship.mdm.controller;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.grtship.core.dto.MainModuleDTO;
import com.grtship.mdm.service.MainModuleService;
import com.grtship.mdm.util.ResponseUtil;

/**
 * REST controller for managing {@link com.grt.efreight.domain.MainModule}.
 */
@RestController
@RequestMapping("api/main-modules")
public class MainModuleController {

	private final Logger log = LoggerFactory.getLogger(MainModuleController.class);

	@Autowired
	private MainModuleService mainModuleService;

	/**
	 * get all the mainModules.
	 *
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list
	 *         of mainModules in body.
	 */
	@GetMapping
	public List<MainModuleDTO> getAllMainModules() {
		log.debug("REST request to get all MainModules");
		return mainModuleService.findAll();
	}

	/**
	 * @param id the id of the MainModuleDTO to retrieve.
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
	 *         the MainModuleDTO, or with status {@code 404 (Not Found)}.
	 */
	@GetMapping("{id}")
	public ResponseEntity<MainModuleDTO> getMainModule(@PathVariable Long id) {
		log.debug("REST request to get Address : {}", id);
		Optional<MainModuleDTO> mainModuleDto = mainModuleService.findOne(id);
		return ResponseUtil.wrapOrNotFound(mainModuleDto);
	}
}
