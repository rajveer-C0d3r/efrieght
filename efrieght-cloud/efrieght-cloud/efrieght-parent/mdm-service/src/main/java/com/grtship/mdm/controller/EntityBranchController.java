package com.grtship.mdm.controller;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.grtship.common.exception.BadRequestAlertException;
import com.grtship.core.constant.AuthoritiesConstants;
import com.grtship.core.dto.BranchContactDTO;
import com.grtship.core.dto.DeactivationDTO;
import com.grtship.core.dto.EntityBranchDTO;
import com.grtship.core.dto.EntityBranchRequestDTO;
import com.grtship.core.dto.EntityBranchTaxDTO;
import com.grtship.core.dto.PageableEntityDTO;
import com.grtship.core.dto.ReactivationDTO;
import com.grtship.mdm.criteria.EntityBranchCriteria;
import com.grtship.mdm.dto.EntityBranchMultiDropDownDTO;
import com.grtship.mdm.service.EntityBranchQueryService;
import com.grtship.mdm.service.EntityBranchService;
import com.grtship.mdm.util.HeaderUtil;
import com.grtship.mdm.util.PaginationUtil;
import com.grtship.mdm.util.ResponseUtil;

/**
 * REST controller for managing {@link com.grt.efreight.domain.EntityBranch}.
 */
@RestController
@RequestMapping("/api/entity-branch")
public class EntityBranchController {

	private final Logger log = LoggerFactory.getLogger(EntityBranchController.class);

	private static final String DEACTIVATE = "DEACTIVATE";

	private static final String REACTIVATE = "REACTIVATE";

	private static final String DEACTIVATE_TYPE_CAN_T_BE_NULL_IF_YOU_WANT_TO_DEACTIVATE_BRANCH_THEN_TYPE_DEACTIVATE_IN_TEXT_BOX = "Deactivate Type can't be null. If you want to deactivate branch then type 'DEACTIVATE' in text box.";

	private static final String REACTIVATE_TYPE_CAN_T_BE_NULL_IF_YOU_WANT_TO_REACTIVATE_BRANCH_THEN_TYPE_REACTIVATE_IN_TEXT_BOX = "Reactivate Type can't be null. If you want to reactivate branch then type 'REACTIVATE' in text box.";

	private static final String ENTITY_NAME = "masterDataManagementServiceBranchDetails";

	@Value("${spring.application.name}")
	private String applicationName;

	private final EntityBranchService branchDetailsService;

	@Autowired
	private EntityBranchQueryService branchFilterService;

	public EntityBranchController(EntityBranchService branchDetailsService) {
		this.branchDetailsService = branchDetailsService;
	}

	/**
	 * {@code POST  /branch-details} : Create a new branchDetails.
	 *
	 * @param branchDetailsDTO the branchDetailsDTO to create.
	 * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with
	 *         body the new branchDetailsDTO, or with status
	 *         {@code 400 (Bad Request)} if the branchDetails has already an ID.
	 * @throws URISyntaxException if the Location URI syntax is incorrect.
	 */
	@PostMapping
	@PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ENTITY_BRANCH_ADD + "\")")
	public ResponseEntity<EntityBranchDTO> createBranchDetails(
			@Valid @RequestBody EntityBranchRequestDTO branchDetailsDTO) throws URISyntaxException {
		log.debug("REST request to save BranchDetails : {}", branchDetailsDTO);
		if (branchDetailsDTO.getId() != null) {
			throw new BadRequestAlertException("A new branchDetails cannot already have an ID", ENTITY_NAME,
					"idexists");
		}
		EntityBranchDTO result = branchDetailsService.save(branchDetailsDTO);
		return ResponseEntity
				.created(new URI("/api/entity-branch/" + result.getId())).headers(HeaderUtil
						.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
				.body(result);
	}

	/**
	 * {@code PUT  /branch-details} : Updates an existing branchDetails.
	 *
	 * @param branchDetailsDTO the branchDetailsDTO to update.
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
	 *         the updated branchDetailsDTO, or with status
	 *         {@code 400 (Bad Request)} if the branchDetailsDTO is not valid, or
	 *         with status {@code 500 (Internal Server Error)} if the
	 *         branchDetailsDTO couldn't be updated.
	 * @throws URISyntaxException if the Location URI syntax is incorrect.
	 */
	@PutMapping
	@PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ENTITY_BRANCH_UPDATE + "\")")
	public ResponseEntity<EntityBranchDTO> updateBranchDetails(@Valid @RequestBody EntityBranchDTO branchDetailsDTO)
			throws URISyntaxException {
		log.debug("REST request to update BranchDetails : {}", branchDetailsDTO);
		if (branchDetailsDTO.getId() == null) {
			throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
		}
		EntityBranchDTO result = branchDetailsService.update(branchDetailsDTO);
		return ResponseEntity.ok().headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME,
				branchDetailsDTO.getId().toString())).body(result);
	}

	/**
	 * {@code GET  /branch-details} : get all the branchDetails.
	 *
	 * @param pageable the pagination information.
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list
	 *         of branchDetails in body.
	 */
	@GetMapping("/get-all")
	@PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ENTITY_BRANCH_VIEW + "\")")
	public ResponseEntity<List<EntityBranchDTO>> getAllBranchDetails(EntityBranchCriteria entityBranchCriteria,
			Pageable pageable) {
		log.debug("REST request to get a page of BranchDetails");
		Page<EntityBranchDTO> page = branchFilterService.findAll(entityBranchCriteria, pageable);
		HttpHeaders headers = PaginationUtil
				.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
		return ResponseEntity.ok().headers(headers).body(page.getContent());
	}

	/**
	 * {@code GET  /branch-details/:id} : get the "id" branchDetails.
	 *
	 * @param id the id of the branchDetailsDTO to retrieve.
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
	 *         the branchDetailsDTO, or with status {@code 404 (Not Found)}.
	 */
	@GetMapping("/{id}")
	@PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ENTITY_BRANCH_VIEW + "\")")
	public ResponseEntity<EntityBranchDTO> getBranchDetails(@PathVariable Long id) {
		Optional<EntityBranchDTO> branchDetailsDTO = branchFilterService.findOne(id);
		return ResponseUtil.wrapOrNotFound(branchDetailsDTO);
	}

	@PostMapping("/deactivate")
	@PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ENTITY_BRANCH_DEACTIVATE + "\")")
	public ResponseEntity<EntityBranchDTO> deactivateBranch(@Valid @RequestBody DeactivationDTO deactivateDto) {
		if (StringUtils.isEmpty(deactivateDto.getType()) || !deactivateDto.getType().equalsIgnoreCase(DEACTIVATE))
			throw new BadRequestAlertException(
					DEACTIVATE_TYPE_CAN_T_BE_NULL_IF_YOU_WANT_TO_DEACTIVATE_BRANCH_THEN_TYPE_DEACTIVATE_IN_TEXT_BOX,
					ENTITY_NAME, "type");

		EntityBranchDTO result = branchDetailsService.deactivate(deactivateDto);
		return ResponseEntity.ok().body(result);
	}

	@PostMapping("/reactivate")
	@PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.EXTERNAL_ENTITY_REACTIVATE + "\")")
	public ResponseEntity<EntityBranchDTO> reactivateEntity(@RequestBody @Valid ReactivationDTO reactivateDto) {
		if (StringUtils.isEmpty(reactivateDto.getType()) || !reactivateDto.getType().equalsIgnoreCase(REACTIVATE))
			throw new BadRequestAlertException(
					REACTIVATE_TYPE_CAN_T_BE_NULL_IF_YOU_WANT_TO_REACTIVATE_BRANCH_THEN_TYPE_REACTIVATE_IN_TEXT_BOX,
					ENTITY_NAME, "type");
		;

		EntityBranchDTO branchDto = branchDetailsService.reactivate(reactivateDto);
		return ResponseEntity.ok().body(branchDto);
	}

	@PostMapping("/create-branch-tax")
	@PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ENTITY_BRANCH_ADD + "\")")
	public ResponseEntity<List<EntityBranchTaxDTO>> createBranchTaxDetails(
			@Valid @RequestBody List<EntityBranchTaxDTO> branchTaxDtos) {
		List<EntityBranchTaxDTO> savedBranchTaxDto = branchDetailsService.saveBranchTax(branchTaxDtos);
		return ResponseEntity.ok().body(savedBranchTaxDto);
	}

	@PostMapping("/create-branch-contacts")
	@PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ENTITY_BRANCH_ADD + "\")")
	public ResponseEntity<List<BranchContactDTO>> createBranchContacts(
			@Valid @RequestBody List<BranchContactDTO> branchContactDtos) {
		List<BranchContactDTO> savedContacts = branchDetailsService.saveBranchContacts(branchContactDtos);
		return ResponseEntity.ok().body(savedContacts);
	}

	@GetMapping("/getAllBranchesForMultiDD")
	public ResponseEntity<PageableEntityDTO<EntityBranchMultiDropDownDTO>> getAllBranchesForMultiDD(
			EntityBranchCriteria entityCriteria, Pageable pageable) {
		Page<EntityBranchMultiDropDownDTO> page = branchFilterService.getAllBranchesForMultiDD(entityCriteria,
				pageable);
		HttpHeaders headers = PaginationUtil
				.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
		return ResponseEntity.ok().headers(headers)
				.body(new PageableEntityDTO<EntityBranchMultiDropDownDTO>(page.getContent(), page.getTotalElements()));
	}
}
