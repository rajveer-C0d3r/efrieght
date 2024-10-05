package com.grt.elogfrieght.services.user.controller;

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

import com.grt.elogfrieght.services.user.criteria.PermissionCriteria;
import com.grt.elogfrieght.services.user.service.PermissionService;
import com.grt.elogfrieght.services.user.serviceimpl.PermissionQueryServiceImpl;
import com.grt.elogfrieght.services.user.util.HeaderUtil;
import com.grt.elogfrieght.services.user.util.PaginationUtil;
import com.grt.elogfrieght.services.user.util.ResponseUtil;
import com.grtship.common.exception.BadRequestAlertException;
import com.grtship.common.exception.InvalidDataException;
import com.grtship.core.constant.ErrorCode;
import com.grtship.core.dto.PermissionDTO;

/**
 * REST controller for managing {@link com.grt.oath2.domain.Permission}.
 */
@RestController
@RequestMapping("/api/permission")
public class PermissionController {

	private final Logger log = LoggerFactory.getLogger(PermissionController.class);

	private static final String ENTITY_NAME = "permission";

	@Value("${spring.application.name}")
	private String applicationName;

	private final PermissionService permissionService;

	private final PermissionQueryServiceImpl permissionQueryService;

	public PermissionController(PermissionService permissionService, PermissionQueryServiceImpl permissionQueryService) {
		this.permissionService = permissionService;
		this.permissionQueryService = permissionQueryService;
	}

	/**
	 * {@code POST  /permissions} : Create a new permission.
	 *
	 * @param permissionDTO the permissionDTO to create.
	 * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with
	 *         body the new permissionDTO, or with status {@code 400 (Bad Request)}
	 *         if the permission has already an ID.
	 * @throws URISyntaxException if the Location URI syntax is incorrect.
	 */
	@PostMapping
	public ResponseEntity<PermissionDTO> createPermission(@Valid @RequestBody PermissionDTO permissionDTO)
			throws URISyntaxException {
		log.debug("REST request to save Permission : {}", permissionDTO);
		PermissionDTO result = permissionService.save(permissionDTO);
		return ResponseEntity
				.created(new URI("/api/permissions/" + result.getPermissionCode())).headers(HeaderUtil
						.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getPermissionCode()))
				.body(result);
	}

	/**
	 * {@code PUT  /permissions} : Updates an existing permission.
	 *
	 * @param permissionDTO the permissionDTO to update.
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
	 *         the updated permissionDTO, or with status {@code 400 (Bad Request)}
	 *         if the permissionDTO is not valid, or with status
	 *         {@code 500 (Internal Server Error)} if the permissionDTO couldn't be
	 *         updated.
	 * @throws URISyntaxException if the Location URI syntax is incorrect.
	 * @throws InvalidDataException 
	 */
	@PutMapping
	public ResponseEntity<PermissionDTO> updatePermission(@Valid @RequestBody PermissionDTO permissionDTO)
			throws URISyntaxException, InvalidDataException {
		log.debug("REST request to update Permission : {}", permissionDTO);
		if (permissionDTO.getPermissionCode() == null) {
			throw new BadRequestAlertException(ErrorCode.BAD_REQUEST,"Invalid code");
		}
		PermissionDTO result = permissionService.update(permissionDTO);
		return ResponseEntity.ok().headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME,
				permissionDTO.getPermissionCode())).body(result);
	}

	/**
	 * {@code GET  /permissions} : get all the permissions.
	 *
	 * @param pageable the pagination information.
	 * @param criteria the criteria which the requested entities should match.
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list
	 *         of permissions in body.
	 */
	@GetMapping
	public ResponseEntity<List<PermissionDTO>> getAllPermissions(PermissionCriteria criteria, Pageable pageable) {
		log.debug("REST request to get Permissions by criteria: {}", criteria);
		Page<PermissionDTO> page = permissionQueryService.findByCriteria(criteria, pageable);
		HttpHeaders headers = PaginationUtil
				.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
		return ResponseEntity.ok().headers(headers).body(page.getContent());
	}

	/**
	 * {@code GET  /permissions/:id} : get the "id" permission.
	 *
	 * @param id the id of the permissionDTO to retrieve.
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
	 *         the permissionDTO, or with status {@code 404 (Not Found)}.
	 */
	@GetMapping("/{permissionCode}")
	public ResponseEntity<PermissionDTO> getPermission(@PathVariable String permissionCode) {
		log.debug("REST request to get Permission : {}", permissionCode);
		Optional<PermissionDTO> permissionDTO = permissionQueryService.findOne(permissionCode);
		return ResponseUtil.wrapOrNotFound(permissionDTO);
	}

	/**
	 * {@code DELETE  /permissions/:id} : delete the "id" permission.
	 *
	 * @param id the id of the permissionDTO to delete.
	 * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
	 */
	@DeleteMapping("/{permissionCode}")
	public ResponseEntity<Void> deletePermission(@PathVariable String permissionCode) {
		log.debug("REST request to delete Permission : {}", permissionCode);
		permissionService.delete(permissionCode);
		return ResponseEntity.noContent()
				.headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, permissionCode))
				.build();
	}	
	
	@GetMapping("/getPermissionCodesByModuleName")
	public ResponseEntity<List<String>> getAllPermissionCodes(PermissionCriteria criteria) {
		log.debug("REST request to get Permissions Codes by criteria: {}", criteria);
		List<String> permissionCodes=permissionQueryService.getPermissionCodes(criteria);
		return ResponseEntity.ok().body(permissionCodes);
	}
}
