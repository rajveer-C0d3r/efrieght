package com.grt.elogfrieght.services.user.controller;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.grt.elogfrieght.services.user.criteria.RoleCriteria;
import com.grt.elogfrieght.services.user.dto.PageableEntityDTO;
import com.grt.elogfrieght.services.user.service.RoleService;
import com.grt.elogfrieght.services.user.serviceimpl.RoleQueryServiceImpl;
import com.grt.elogfrieght.services.user.util.HeaderUtil;
import com.grt.elogfrieght.services.user.util.PaginationUtil;
import com.grt.elogfrieght.services.user.util.ResponseUtil;
import com.grtship.common.exception.BadRequestAlertException;
import com.grtship.common.exception.InvalidDataException;
import com.grtship.core.annotation.Auditable;
import com.grtship.core.constant.AuthoritiesConstants;
import com.grtship.core.dto.DeactivationDTO;
import com.grtship.core.dto.ReactivationDTO;
import com.grtship.core.dto.RoleDTO;

import javassist.tools.rmi.ObjectNotFoundException;

/**
 * REST controller for managing {@link com.grt.efreight.domain.Role}.
 */
@RestController
@RequestMapping("/api/role")
public class RoleController {

	private final Logger log = LoggerFactory.getLogger(RoleController.class);

	private static final String ENTITY_NAME = "masterDataManagementServiceRole";
	
	private static final String IF_YOU_WANT_TO_DEACTIVATE_ROLE_THEN_TYPE_DEACTIVATE_IN_THE_TEXT_BOX = "If you want to deactivate Role then type 'DEACTIVATE' in the text Box";
	
	private static final String IF_YOU_WANT_TO_REACTIVE_ROLE_THEN_YOU_NEED_TO_TYPE_REACTIVATE_IN_TEXT_BOX = "If you want to reactive Role then you need to type 'REACTIVATE' in text box.";

	private static final String DEACTIVATE = "DEACTIVATE";
	
	private static final String REACTIVATE = "REACTIVATE";
	
	private static final String TYPE = "type";
	
	@Value("${spring.application.name}")
	private String applicationName;

	private final RoleService roleService;
	private final RoleQueryServiceImpl roleFilterService;

	public RoleController(RoleService roleService, RoleQueryServiceImpl roleFilterService) {
		this.roleService = roleService;
		this.roleFilterService = roleFilterService;
	}

	/**
	 * {@code POST  /roles} : Create a new role.
	 *
	 * @param roleDTO the roleDTO to create.
	 * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with
	 *         body the new roleDTO, or with status {@code 400 (Bad Request)} if the
	 *         role has already an ID.
	 * @throws URISyntaxException if the Location URI syntax is incorrect.
	 * @throws InvalidDataException 
	 */
	@PostMapping
	@PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ROLE_ADD + "\") ")
	public ResponseEntity<RoleDTO> createRole(@Valid @RequestBody RoleDTO roleDTO) throws URISyntaxException, InvalidDataException {
		log.debug("REST request to save Role : {}", roleDTO);
		if (roleDTO.getId() != null) {
			throw new BadRequestAlertException("A new role cannot already have an ID", ENTITY_NAME, "idexists");
		}
		RoleDTO result = roleService.save(roleDTO);
		return ResponseEntity
				.created(new URI("/api/roles/" + result.getId())).headers(HeaderUtil
						.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
				.body(result);
	}

	/**
	 * {@code PUT  /roles} : Updates an existing role.
	 *
	 * @param roleDTO the roleDTO to update.
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
	 *         the updated roleDTO, or with status {@code 400 (Bad Request)} if the
	 *         roleDTO is not valid, or with status
	 *         {@code 500 (Internal Server Error)} if the roleDTO couldn't be
	 *         updated.
	 * @throws URISyntaxException      if the Location URI syntax is incorrect.
	 * @throws ObjectNotFoundException
	 */
	@PutMapping
	@PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ROLE_EDIT + "\") ")
	public ResponseEntity<RoleDTO> updateRole(@Valid @RequestBody RoleDTO roleDTO)
			throws URISyntaxException, ObjectNotFoundException {
		log.debug("REST request to update Role : {}", roleDTO);
		if (roleDTO.getId() == null) {
			throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
		}
		RoleDTO result = roleService.update(roleDTO);
		return ResponseEntity.ok().headers(
				HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, roleDTO.getId().toString()))
				.body(result);
	}

	/**
	 * {@code GET  /roles} : get all the roles.
	 *
	 * @param pageable the pagination information.
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list
	 *         of roles in body.
	 */
	@GetMapping
	@PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ROLE_VIEW + "\") ")
	public ResponseEntity<PageableEntityDTO<RoleDTO>> getAllRoles(RoleCriteria roleCriteria, Pageable pageable) {
		log.debug("REST request to get a page of Roles");
		Page<RoleDTO> page = roleFilterService.findByCriteria(roleCriteria, pageable);
		HttpHeaders headers = PaginationUtil
				.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
		return ResponseEntity.ok().headers(headers)
				.body(new PageableEntityDTO<RoleDTO>(page.getContent(), page.getTotalElements()));
	}

	/**
	 * {@code GET  /roles/:id} : get the "id" role.
	 *
	 * @param id the id of the roleDTO to retrieve.
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
	 *         the roleDTO, or with status {@code 404 (Not Found)}.
	 */
	@GetMapping("{id}")
	@PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ROLE_VIEW + "\") ")
	public ResponseEntity<RoleDTO> getRole(@PathVariable Long id) {
		log.debug("REST request to get Role : {}", id);
		Optional<RoleDTO> roleDTO = roleFilterService.findOne(id);
		return ResponseUtil.wrapOrNotFound(roleDTO);
	}

	/**
	 * {@code DELETE  /roles/:id} : delete the "id" role.
	 *
	 * @param id the id of the roleDTO to delete.
	 * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
	 */
	@Auditable
	@DeleteMapping("{id}")
	@PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ROLE_DELETE + "\") ")
	public ResponseEntity<Void> deleteRole(@PathVariable Long id) {
		log.debug("REST request to delete Role : {}", id);
		roleService.delete(id);
		return ResponseEntity.noContent()
				.headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
				.build();
	}
	
	@PostMapping("/deactivate")
	@PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ROLE_DEACTIVATE + "\")")
	public ResponseEntity<RoleDTO> deactivate(@RequestBody DeactivationDTO deactivateDto) {
		log.debug("REST request to deactivate Role : {}", deactivateDto);
		if (StringUtils.isEmpty(deactivateDto.getType()) || !deactivateDto.getType().equals(DEACTIVATE))
			throw new BadRequestAlertException(
					IF_YOU_WANT_TO_DEACTIVATE_ROLE_THEN_TYPE_DEACTIVATE_IN_THE_TEXT_BOX,
					ENTITY_NAME, TYPE);
		RoleDTO result = roleService.deactivate(deactivateDto);
		return ResponseEntity.ok().body(result);
	}

	@PostMapping("/reactivate")
	@PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ROLE_REACTIVATE + "\")")
	public ResponseEntity<RoleDTO> reactivate(@Valid @RequestBody ReactivationDTO reactivateDto) {
		log.debug("REST request to reactivate Role : {}", reactivateDto);
		if (StringUtils.isEmpty(reactivateDto.getType()) || (!reactivateDto.getType().equals(REACTIVATE)))
			throw new BadRequestAlertException(
					IF_YOU_WANT_TO_REACTIVE_ROLE_THEN_YOU_NEED_TO_TYPE_REACTIVATE_IN_TEXT_BOX, ENTITY_NAME, TYPE);
		RoleDTO result = roleService.reactivate(reactivateDto);
		return ResponseEntity.ok().body(result);
	}

}
