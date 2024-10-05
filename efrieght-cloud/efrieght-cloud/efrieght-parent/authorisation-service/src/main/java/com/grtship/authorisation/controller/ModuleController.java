package com.grtship.authorisation.controller;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.grtship.authorisation.criteria.ModuleCriteria;
import com.grtship.authorisation.service.ModuleFilterService;
import com.grtship.authorisation.service.ModuleService;
import com.grtship.authorisation.util.HeaderUtil;
import com.grtship.authorisation.util.PaginationUtil;
import com.grtship.authorisation.util.ResponseUtil;
import com.grtship.common.exception.BadRequestAlertException;
import com.grtship.core.annotation.Auditable;
import com.grtship.core.dto.ModuleDTO;

/**
 * REST controller for managing {@link com.grt.efreight.domain.Module}.
 */
@RestController
@RequestMapping("/api/module")
public class ModuleController {

	private final Logger log = LoggerFactory.getLogger(ModuleController.class);

	private static final String ENTITY_NAME = "masterDataManagementServiceModule";

	@Value("${spring.application.name}")
	private String applicationName;

	private final ModuleService moduleService;

	private final ModuleFilterService moduleFilterService;

	public ModuleController(ModuleService moduleService, ModuleFilterService moduleQueryService) {
		this.moduleService = moduleService;
		this.moduleFilterService = moduleQueryService;
	}

	/**
	 * {@code POST  /modules} : Create a new module.
	 *
	 * @param moduleDTO the moduleDTO to create.
	 * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with
	 *         body the new moduleDTO, or with status {@code 400 (Bad Request)} if
	 *         the module has already an ID.
	 * @throws URISyntaxException if the Location URI syntax is incorrect.
	 */
	@PostMapping
	public ResponseEntity<ModuleDTO> createModule(@Valid @RequestBody ModuleDTO moduleDTO) throws URISyntaxException {
		log.debug("REST request to save Module : {}", moduleDTO);
		ModuleDTO result = moduleService.save(moduleDTO);
		return ResponseEntity
				.created(new URI("/api/modules/" + result.getModuleName())).headers(HeaderUtil
						.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getModuleName()))
				.body(result);
	}

	/**
	 * {@code PUT  /modules} : Updates an existing module.
	 *
	 * @param moduleDTO the moduleDTO to update.
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
	 *         the updated moduleDTO, or with status {@code 400 (Bad Request)} if
	 *         the moduleDTO is not valid, or with status
	 *         {@code 500 (Internal Server Error)} if the moduleDTO couldn't be
	 *         updated.
	 * @throws URISyntaxException if the Location URI syntax is incorrect.
	 */
	@PutMapping
	public ResponseEntity<ModuleDTO> updateModule(@Valid @RequestBody ModuleDTO moduleDTO) throws URISyntaxException {
		log.debug("REST request to update Module : {}", moduleDTO);
		if (moduleDTO.getModuleName() == null) {
			throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
		}
		ModuleDTO result = moduleService.save(moduleDTO);
		return ResponseEntity.ok().headers(
				HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, moduleDTO.getModuleName()))
				.body(result);
	}

	/**
	 * {@code GET  /modules} : get all the modules.
	 *
	 * @param pageable the pagination information.
	 * @param criteria the criteria which the requested entities should match.
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list
	 *         of modules in body.
	 */
	@GetMapping
	public ResponseEntity<List<ModuleDTO>> getAllModules(ModuleCriteria criteria, Pageable pageable) {
		log.debug("REST request to get Modules by criteria: {}", criteria);
		Page<ModuleDTO> page = moduleFilterService.findByCriteria(criteria, pageable);
		HttpHeaders headers = PaginationUtil
				.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
		return ResponseEntity.ok().headers(headers).body(page.getContent());
	}

	/**
	 * {@code GET  /modules/count} : count all the modules.
	 *
	 * @param criteria the criteria which the requested entities should match.
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count
	 *         in body.
	 */
	@GetMapping("count")
	public ResponseEntity<Long> countModules(ModuleCriteria criteria) {
		log.debug("REST request to count Modules by criteria: {}", criteria);
		return ResponseEntity.ok().body(moduleFilterService.countByCriteria(criteria));
	}

	/**
	 * {@code GET  /modules/:id} : get the "id" module.
	 *
	 * @param id the id of the moduleDTO to retrieve.
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
	 *         the moduleDTO, or with status {@code 404 (Not Found)}.
	 */
	@GetMapping("{moduleName}")
	public ResponseEntity<ModuleDTO> getModule(@PathVariable String moduleName) {
		log.debug("REST request to get Module : {}", moduleName);
		Optional<ModuleDTO> moduleDTO = moduleService.findOne(moduleName);
		return ResponseUtil.wrapOrNotFound(moduleDTO);
	}

	/**
	 * {@code DELETE  /modules/:id} : delete the "id" module.
	 *
	 * @param id the id of the moduleDTO to delete.
	 * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
	 */
	@Auditable
	@DeleteMapping("{moduleName}")
	public ResponseEntity<Void> deleteModule(@PathVariable String moduleName) {
		log.debug("REST request to delete Module : {}", moduleName);
		moduleService.delete(moduleName);
		return ResponseEntity.noContent()
				.headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, moduleName)).build();
	}
}
