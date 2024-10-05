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

import com.grtship.core.dto.SubModuleDTO;
import com.grtship.mdm.service.SubModuleService;
import com.grtship.mdm.util.ResponseUtil;

/**
 * REST controller for managing {@link com.grt.efreight.domain.SubModule}.
 */
@RestController
@RequestMapping("api/sub-modules")
public class SubModuleController {

	private final Logger log = LoggerFactory.getLogger(SubModuleController.class);

	@Autowired
	private SubModuleService subModuleService;

	/**
	 * get all the subModules.
	 *
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list
	 *         of subModules in body.
	 */
	@GetMapping
	public List<SubModuleDTO> getAllSubModules() {
		log.debug("REST request to get all SubModules");
		return subModuleService.findAll();
	}

	/**
	 * @param id the id of the SubModuleDTO to retrieve.
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
	 *         the SubModuleDTO, or with status {@code 404 (Not Found)}.
	 */
	@GetMapping("{id}")
	public ResponseEntity<SubModuleDTO> getSubModule(@PathVariable Long id) {
		log.debug("REST request to get Address : {}", id);
		Optional<SubModuleDTO> subModuleDto = subModuleService.findOne(id);
		return ResponseUtil.wrapOrNotFound(subModuleDto);
	}

	/**
	 * @param id the mainModuleId of the SubModuleDTO to retrieve.
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list
	 *         of subModules in body.
	 */
	@GetMapping("/getByMainModuleId/{id}")
	public List<SubModuleDTO> getSubModulesByMainModuleId(@PathVariable Long id) {
		log.debug("REST request to get Address : {}", id);
		return subModuleService.findByMainModuleId(id);
	}
}
