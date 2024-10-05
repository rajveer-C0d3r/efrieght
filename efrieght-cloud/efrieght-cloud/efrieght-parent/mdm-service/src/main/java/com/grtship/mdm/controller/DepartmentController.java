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
import com.grtship.core.dto.DepartmentDTO;
import com.grtship.core.dto.PageableEntityDTO;
import com.grtship.mdm.criteria.DepartmentCriteria;
import com.grtship.mdm.service.DepartmentService;
import com.grtship.mdm.util.HeaderUtil;
import com.grtship.mdm.util.PaginationUtil;
import com.grtship.mdm.util.ResponseUtil;

/**
 * REST controller for managing {@link com.grt.efreight.domain.Department}.
 */
@RestController
@RequestMapping("/api/departments")
public class DepartmentController {

	private static final String IDNULL = "idnull";

	private static final String IDEXISTS = "idexists";

	private static final String A_NEW_DEPARTMENT_CANNOT_ALREADY_HAVE_AN_ID = "A new department cannot already have an ID";

	private static final String INVALID_ID = "Invalid id";

	private final Logger log = LoggerFactory.getLogger(DepartmentController.class);

	private static final String ENTITY_NAME = "masterDataManagementServiceDepartment";

	@Value("${spring.application.name}")
	private String applicationName;

	private final DepartmentService departmentService;

	public DepartmentController(DepartmentService departmentService) {
		this.departmentService = departmentService;
	}

	/**
	 * {@code POST  /departments} : Create a new department.
	 *
	 * @param departmentDTO the departmentDTO to create.
	 * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with
	 *         body the new departmentDTO, or with status {@code 400 (Bad Request)}
	 *         if the department has already an ID.
	 * @throws URISyntaxException if the Location URI syntax is incorrect.
	 */
	@PostMapping
	@PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.DEPARTMENT_ADD + "\") or hasAuthority('ROLE_ADMIN')")
	public ResponseEntity<DepartmentDTO> createDepartment(@Valid @RequestBody DepartmentDTO departmentDTO)
			throws URISyntaxException {
		log.debug("REST request to save Department : {}", departmentDTO);
		if (departmentDTO.getId() != null) {
			throw new BadRequestAlertException(A_NEW_DEPARTMENT_CANNOT_ALREADY_HAVE_AN_ID, ENTITY_NAME, IDEXISTS);
		}
		DepartmentDTO result = departmentService.save(departmentDTO);
		return ResponseEntity
				.created(new URI("/api/departments/" + result.getId())).headers(HeaderUtil
						.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
				.body(result);
	}

	/**
	 * {@code PUT  /departments} : Updates an existing department.
	 *
	 * @param departmentDTO the departmentDTO to update.
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
	 *         the updated departmentDTO, or with status {@code 400 (Bad Request)}
	 *         if the departmentDTO is not valid, or with status
	 *         {@code 500 (Internal Server Error)} if the departmentDTO couldn't be
	 *         updated.
	 * @throws URISyntaxException if the Location URI syntax is incorrect.
	 */
	@PutMapping
	@PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.DEPARTMENT_ADD + "\") and hasAuthority(\""
			+ AuthoritiesConstants.DEPARTMENT_EDIT + "\")")
	public ResponseEntity<DepartmentDTO> updateDepartment(@Valid @RequestBody DepartmentDTO departmentDTO)
			throws URISyntaxException {
		log.debug("REST request to update Department : {}", departmentDTO);
		if (departmentDTO.getId() == null) {
			throw new BadRequestAlertException(INVALID_ID, ENTITY_NAME, IDNULL);
		}
		DepartmentDTO result = departmentService.save(departmentDTO);
		return ResponseEntity.ok().headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME,
				departmentDTO.getId().toString())).body(result);
	}

	/**
	 * {@code GET  /departments} : get all the departments.
	 *
	 * @param pageable the pagination information.
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list
	 *         of departments in body.
	 */
	@GetMapping
	@PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.DEPARTMENT_VIEW + "\") or hasAuthority(\""
			+ AuthoritiesConstants.DEPARTMENT_EDIT + "\") or hasAuthority('ROLE_ADMIN')")
	public ResponseEntity<PageableEntityDTO<DepartmentDTO>> getAllDepartments(DepartmentCriteria departmentCriteria,
			Pageable pageable) {
		log.debug("REST request to get a page of Departments");
		Page<DepartmentDTO> page = departmentService.findAll(departmentCriteria, pageable);
		HttpHeaders headers = PaginationUtil
				.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
		return ResponseEntity.ok().headers(headers)
				.body(new PageableEntityDTO<DepartmentDTO>(page.getContent(), page.getTotalElements()));
	}

	/**
	 * {@code GET  /departments/:id} : get the "id" department.
	 *
	 * @param id the id of the departmentDTO to retrieve.
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
	 *         the departmentDTO, or with status {@code 404 (Not Found)}.
	 */
	@GetMapping("{id}")
	@PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.DEPARTMENT_VIEW + "\") or hasAuthority(\""
			+ AuthoritiesConstants.DEPARTMENT_EDIT + "\") or hasAuthority('ROLE_ADMIN')")
	public ResponseEntity<DepartmentDTO> getDepartment(@PathVariable Long id) {
		log.debug("REST request to get Department : {}", id);
		Optional<DepartmentDTO> departmentDTO = departmentService.findOne(id);
		return ResponseUtil.wrapOrNotFound(departmentDTO);
	}

	/**
	 * {@code DELETE  /departments/:id} : delete the "id" department.
	 *
	 * @param id the id of the departmentDTO to delete.
	 * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
	 */

	@Auditable
	@DeleteMapping("{id}")
	@PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.DEPARTMENT_DELETE + "\")")
	public ResponseEntity<Void> deleteDepartment(@PathVariable Long id) {
		log.debug("REST request to delete Department : {}", id);
		departmentService.delete(id);
		return ResponseEntity.noContent()
				.headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
				.build();
	}

	@PostMapping("getByIds")
	public ResponseEntity<List<DepartmentDTO>> getDepartmentsByIds(@RequestBody List<Long> departmentIds) {
		log.debug("Request to fetch departmenst by ids");
		List<DepartmentDTO> departments = departmentService.getDepartmentsByIds(departmentIds);
		return ResponseEntity.ok().body(departments);
	}
}
