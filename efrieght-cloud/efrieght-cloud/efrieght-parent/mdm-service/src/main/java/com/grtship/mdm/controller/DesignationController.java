package com.grtship.mdm.controller;

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

import com.grtship.common.exception.BadRequestAlertException;
import com.grtship.core.annotation.Auditable;
import com.grtship.core.constant.AuthoritiesConstants;
import com.grtship.core.dto.DesignationDTO;
import com.grtship.core.dto.PageableEntityDTO;
import com.grtship.core.dto.ResponseCodeDTO;
import com.grtship.mdm.criteria.DesignationCriteria;
import com.grtship.mdm.service.DesignationService;
import com.grtship.mdm.util.HeaderUtil;
import com.grtship.mdm.util.PaginationUtil;
import com.grtship.mdm.util.ResponseUtil;

/**
 * REST controller for managing {@link com.grt.efreight.domain.Designation}.
 */
@RestController
@RequestMapping("/api/designations")
public class DesignationController {

	private static final String IDNULL = "idnull";

	private static final String INVALID_ID = "Invalid id";

	private static final String IDEXISTS = "idexists";

	private static final String A_NEW_DESIGNATION_CANNOT_ALREADY_HAVE_AN_ID = "A new designation cannot already have an ID";

	private final Logger log = LoggerFactory.getLogger(DesignationController.class);

	private static final String ENTITY_NAME = "masterDataManagementServiceDesignation";

	@Value("${spring.application.name}")
	private String applicationName;

	private final DesignationService designationService;

	public DesignationController(DesignationService designationService) {
		this.designationService = designationService;
	}

	/**
	 * {@code POST  /designations} : Create a new designation.
	 *
	 * @param designationDTO the designationDTO to create.
	 * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with
	 *         body the new designationDTO, or with status {@code 400 (Bad Request)}
	 *         if the designation has already an ID.
	 * @throws URISyntaxException if the Location URI syntax is incorrect.
	 */
	@PostMapping
	@PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.DESIGNATION_ADD + "\") or hasAuthority('ROLE_ADMIN')")
	public ResponseEntity<DesignationDTO> createDesignation(@Valid @RequestBody DesignationDTO designationDTO)
			throws URISyntaxException {
		log.debug("REST request to save Designation : {}", designationDTO);
		if (designationDTO.getId() != null) {
			throw new BadRequestAlertException(A_NEW_DESIGNATION_CANNOT_ALREADY_HAVE_AN_ID, ENTITY_NAME, IDEXISTS);
		}
		DesignationDTO result = designationService.save(designationDTO);
		return ResponseEntity
				.created(new URI("/api/designations/" + result.getId())).headers(HeaderUtil
						.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
				.body(result);
	}

	/**
	 * {@code PUT  /designations} : Updates an existing designation.
	 *
	 * @param designationDTO the designationDTO to update.
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
	 *         the updated designationDTO, or with status {@code 400 (Bad Request)}
	 *         if the designationDTO is not valid, or with status
	 *         {@code 500 (Internal Server Error)} if the designationDTO couldn't be
	 *         updated.
	 * @throws URISyntaxException if the Location URI syntax is incorrect.
	 */
	@PutMapping
	@PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.DESIGNATION_EDIT + "\") or hasAuthority('ROLE_ADMIN')")
	public ResponseEntity<DesignationDTO> updateDesignation(@Valid @RequestBody DesignationDTO designationDTO)
			throws URISyntaxException {
		log.debug("REST request to update Designation : {}", designationDTO);
		if (designationDTO.getId() == null) {
			throw new BadRequestAlertException(INVALID_ID, ENTITY_NAME, IDNULL);
		}
		DesignationDTO result = designationService.save(designationDTO);
		return ResponseEntity.ok().headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME,
				designationDTO.getId().toString())).body(result);
	}

	/**
	 * {@code GET  /designations} : get all the designations.
	 *
	 * @param pageable the pagination information.
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list
	 *         of designations in body.
	 */
	@GetMapping
	@PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.DESIGNATION_VIEW + "\") or hasAuthority('ROLE_ADMIN')")
	public ResponseEntity<PageableEntityDTO<DesignationDTO>> getAllDesignations(DesignationCriteria designationCriteria,
			Pageable pageable) {
		log.debug("REST request to get a page of Designations");
		Page<DesignationDTO> page = designationService.findAll(designationCriteria, pageable);
		HttpHeaders headers = PaginationUtil
				.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
		return ResponseEntity.ok().headers(headers)
				.body(new PageableEntityDTO<DesignationDTO>(page.getContent(), page.getTotalElements()));
	}

	/**
	 * {@code GET  /designations/:id} : get the "id" designation.
	 *
	 * @param id the id of the designationDTO to retrieve.
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
	 *         the designationDTO, or with status {@code 404 (Not Found)}.
	 */
	@GetMapping("{id}")
	@PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.DESIGNATION_VIEW + "\") or hasAuthority('ROLE_ADMIN')")
	public ResponseEntity<DesignationDTO> getDesignation(@PathVariable Long id) {
		log.debug("REST request to get Designation : {}", id);
		Optional<DesignationDTO> designationDTO = designationService.findOne(id);
		return ResponseUtil.wrapOrNotFound(designationDTO);
	}

	@GetMapping("getGeneratedDesignationCode")
	public ResponseEntity<ResponseCodeDTO> getGeneratedDesignationCode() {
		log.debug("REST request to get generated Designation Code");
		ResponseCodeDTO designationCode = designationService.getGeneratedDesignationCode();
		return ResponseEntity.ok().body(designationCode);
	}

	/**
	 * {@code DELETE  /designations/:id} : delete the "id" designation.
	 *
	 * @param id the id of the designationDTO to delete.
	 * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
	 */
	@Auditable
	@DeleteMapping("{id}")
	@PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.DESIGNATION_DELETE + "\") or hasAuthority('ROLE_ADMIN')")
	public ResponseEntity<Void> deleteDesignation(@PathVariable Long id) {
		log.debug("REST request to delete Designation : {}", id);
		designationService.delete(id);
		return ResponseEntity.noContent()
				.headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
				.build();
	}

	@PostMapping("getByIds")
	public ResponseEntity<List<DesignationDTO>> getByIds(@RequestBody List<Long> designationIds) {
		log.debug("Request to get Designations based on ids ");
		List<DesignationDTO> designations = designationService.getByIds(designationIds);
		return ResponseEntity.ok().body(designations);
	}
}
