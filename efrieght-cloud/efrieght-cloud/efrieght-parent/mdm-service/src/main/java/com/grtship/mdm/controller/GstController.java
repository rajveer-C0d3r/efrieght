package com.grtship.mdm.controller;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

import com.grtship.common.exception.BadRequestAlertException;
import com.grtship.core.annotation.Auditable;
import com.grtship.core.dto.GstDTO;
import com.grtship.core.dto.PageableEntityDTO;
import com.grtship.mdm.criteria.GstCriteria;
import com.grtship.mdm.service.GstFilterService;
import com.grtship.mdm.service.GstService;
import com.grtship.mdm.util.HeaderUtil;
import com.grtship.mdm.util.PaginationUtil;
import com.grtship.mdm.util.ResponseUtil;

/**
 * REST controller for managing {@link com.grt.efreight.domain.Gst}.
 */
@RestController
@RequestMapping("/api/gsts")
public class GstController {

	private final Logger log = LoggerFactory.getLogger(GstController.class);

	private static final String ENTITY_NAME = "masterDataManagementServiceGst";

	@Value("${spring.application.name}")
	private String applicationName;

	@Autowired
	private GstService gstService;

	@Autowired
	private GstFilterService gstFilterService;

	/**
	 * {@code POST  /gsts} : Create a new gst.
	 *
	 * @param gstDTO the gstDTO to create.
	 * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with
	 *         body the new gstDTO, or with status {@code 400 (Bad Request)} if the
	 *         gst has already an ID.
	 * @throws URISyntaxException if the Location URI syntax is incorrect.
	 */
	@PostMapping
	public ResponseEntity<GstDTO> createGst(@Valid @RequestBody GstDTO gstDTO) throws URISyntaxException {
		log.debug("REST request to save Gst : {}", gstDTO);
		if (gstDTO.getId() != null) {
			throw new BadRequestAlertException("A new gst cannot already have an ID", ENTITY_NAME, "idexists");
		}
		GstDTO result = gstService.save(gstDTO);
		return ResponseEntity
				.created(new URI("/api/gsts/" + result.getId())).headers(HeaderUtil
						.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
				.body(result);
	}

	/**
	 * {@code PUT  /gsts} : Updates an existing gst.
	 *
	 * @param gstDTO the gstDTO to update.
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
	 *         the updated gstDTO, or with status {@code 400 (Bad Request)} if the
	 *         gstDTO is not valid, or with status
	 *         {@code 500 (Internal Server Error)} if the gstDTO couldn't be
	 *         updated.
	 * @throws URISyntaxException if the Location URI syntax is incorrect.
	 */
	@PutMapping
	public ResponseEntity<GstDTO> updateGst(@Valid @RequestBody GstDTO gstDTO) throws URISyntaxException {
		log.debug("REST request to update Gst : {}", gstDTO);
		if (gstDTO.getId() == null) {
			throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
		}
		GstDTO result = gstService.save(gstDTO);
		return ResponseEntity.ok().headers(
				HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, gstDTO.getId().toString()))
				.body(result);
	}

	/**
	 * {@code GET  /gsts} : get all the gsts.
	 *
	 * @param pageable the pagination information.
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list
	 *         of gsts in body.
	 */
	@GetMapping
	public ResponseEntity<PageableEntityDTO<GstDTO>> getAllGsts(GstCriteria criteria, Pageable pageable) {
		log.debug("REST request to get Gsts by criteria: {}", criteria);
		Page<GstDTO> page = gstFilterService.findByCriteria(criteria, pageable);
		HttpHeaders headers = PaginationUtil
				.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
		return ResponseEntity.ok().headers(headers)
				.body(new PageableEntityDTO<GstDTO>(page.getContent(), page.getTotalElements()));
	}

	/**
	 * {@code GET  /gsts/:id} : get the "id" gst.
	 *
	 * @param id the id of the gstDTO to retrieve.
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
	 *         the gstDTO, or with status {@code 404 (Not Found)}.
	 */
	@GetMapping("{id}")
	public ResponseEntity<GstDTO> getGst(@PathVariable Long id) {
		log.debug("REST request to get Gst : {}", id);
		Optional<GstDTO> gstDTO = gstService.findOne(id);
		return ResponseUtil.wrapOrNotFound(gstDTO);
	}

	/**
	 * {@code DELETE  /gsts/:id} : delete the "id" gst.
	 *
	 * @param id the id of the gstDTO to delete.
	 * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
	 */
	@Auditable
	@DeleteMapping("{id}")
	public ResponseEntity<Void> deleteGst(@PathVariable Long id) {
		log.debug("REST request to delete Gst : {}", id);
		gstService.delete(id);
		return ResponseEntity.noContent()
				.headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
				.build();
	}
}
