package com.grtship.client.controller;

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

import com.grtship.client.criteria.CompanyBranchCriteria;
import com.grtship.client.service.CompanyBranchQueryService;
import com.grtship.client.service.CompanyBranchService;
import com.grtship.client.util.HeaderUtil;
import com.grtship.client.util.PaginationUtil;
import com.grtship.client.util.ResponseUtil;
import com.grtship.common.exception.BadRequestAlertException;
import com.grtship.core.constant.AuthoritiesConstants;
import com.grtship.core.dto.CompanyBranchCreationDTO;
import com.grtship.core.dto.CompanyBranchDTO;
import com.grtship.core.dto.DeactivationDTO;
import com.grtship.core.dto.PageableEntityDTO;
import com.grtship.core.dto.ReactivationDTO;
import com.grtship.core.dto.UserAccessCompanyBranchDTO;
import com.grtship.core.dto.UserSpecificBranchDTO;

/**
 * REST controller for managing
 * {@link com.grtship.efreight.client.domain.CompanyBranch}.
 */
@RestController
@RequestMapping("/api/company-branch")
public class CompanyBranchController {

	private static final String IF_YOU_WANT_TO_REACTIVATE_BRANCH_THEN_TYPE_REACTIVATE_IN_TYPE_TEXT_BOX_AND_THEN_PROCEED = "If You want to reactivate Branch then type 'REACTIVATE' in type textBox and then proceed..!";

	private static final String TYPE = "type";

	private static final String IF_YOU_WANT_TO_DEACTIVATE_BRANCH_THEN_TYPE_DEACTIVATE_IN_TYPE_TEXT_BOX_AND_THEN_PROCEED = "If You want to deactivate Branch then type 'DEACTIVATE' in type textBox and then proceed..!";

	private final Logger log = LoggerFactory.getLogger(CompanyBranchController.class);

	private static final String ENTITY_NAME = "clientServiceCompanyBranch";

	
	@Value("${spring.application.name}")
	private String applicationName;

	private final CompanyBranchService companyBranchService;

	@Autowired
	private CompanyBranchQueryService branchFilterService;

	public CompanyBranchController(CompanyBranchService companyBranchService) {
		this.companyBranchService = companyBranchService;
	}

	/**
	 * {@code POST  /company-branch} : Create a new companyBranch.
	 *
	 * @param companyBranchDTO the companyBranchDTO to create.
	 * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with
	 *         body the new companyBranchDTO, or with status
	 *         {@code 400 (Bad Request)} if the companyBranch has already an ID.
	 * @throws URISyntaxException if the Location URI syntax is incorrect.
	 */
	@PostMapping
	@PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.COMPANY_BRANCH_ADD + "\")")
	public ResponseEntity<CompanyBranchDTO> createCompanyBranch(
			@Valid @RequestBody CompanyBranchCreationDTO branchCreationDto) throws URISyntaxException {
		log.debug("REST request to save CompanyBranch : {}", branchCreationDto);
		if (branchCreationDto.getId() != null) {
			throw new BadRequestAlertException("A new companyBranch cannot already have an ID", ENTITY_NAME,
					"idexists");
		}
		CompanyBranchDTO result = companyBranchService.save(branchCreationDto);
		return ResponseEntity
				.created(new URI("/api/company-branch/" + result.getId())).headers(HeaderUtil
						.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
				.body(result);
	}

	/**
	 * {@code PUT  /company-branches} : Updates an existing companyBranch.
	 *
	 * @param companyBranchDTO the companyBranchDTO to update.
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
	 *         the updated companyBranchDTO, or with status
	 *         {@code 400 (Bad Request)} if the companyBranchDTO is not valid, or
	 *         with status {@code 500 (Internal Server Error)} if the
	 *         companyBranchDTO couldn't be updated.
	 * @throws URISyntaxException if the Location URI syntax is incorrect.
	 */
	@PutMapping
	@PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.COMPANY_BRANCH_EDIT + "\")")
	public ResponseEntity<CompanyBranchDTO> updateCompanyBranch(@Valid @RequestBody CompanyBranchDTO companyBranchDto)
			throws URISyntaxException {
		log.debug("REST request to update CompanyBranch : {}", companyBranchDto);
		if (companyBranchDto.getId() == null) {
			throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
		}
		CompanyBranchDTO result = companyBranchService.update(companyBranchDto);
		return ResponseEntity.ok().headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME,
				companyBranchDto.getId().toString())).body(result);
	}

	/**
	 * {@code GET  /company-branches} : get all the companyBranches.
	 *
	 * @param pageable the pagination information.
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list
	 *         of companyBranches in body.
	 */
	@GetMapping
	public ResponseEntity<PageableEntityDTO<CompanyBranchDTO>> getAllCompanyBranches(
			CompanyBranchCriteria branchCriteria, Pageable pageable) {
		log.debug("REST request to get a page of CompanyBranches");
		Page<CompanyBranchDTO> page = branchFilterService.findByCriteria(branchCriteria, pageable);
		HttpHeaders headers = PaginationUtil
				.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
		return ResponseEntity.ok().headers(headers)
				.body(new PageableEntityDTO<CompanyBranchDTO>(page.getContent(), page.getTotalElements()));
	}

	/**
	 * {@code GET  /company-branches/:id} : get the "id" companyBranch.
	 *
	 * @param id the id of the companyBranchDTO to retrieve.
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
	 *         the companyBranchDTO, or with status {@code 404 (Not Found)}.
	 */
	@GetMapping("{id}")
	public ResponseEntity<CompanyBranchDTO> getCompanyBranch(@PathVariable Long id) {
		log.debug("REST request to get CompanyBranch : {}", id);
		Optional<CompanyBranchDTO> companyBranchDTO = branchFilterService.findOne(id);
		return ResponseUtil.wrapOrNotFound(companyBranchDTO);
	}

	@PostMapping("/deactivate")
	@PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.COMPANY_BRANCH_DEACTIVATE + "\")")
	public ResponseEntity<CompanyBranchDTO> deactivateBranch(@Valid @RequestBody DeactivationDTO deactivateDto) {
		if (StringUtils.isEmpty(deactivateDto.getType()) || !deactivateDto.getType().equals("DEACTIVATE"))
			throw new BadRequestAlertException(
					IF_YOU_WANT_TO_DEACTIVATE_BRANCH_THEN_TYPE_DEACTIVATE_IN_TYPE_TEXT_BOX_AND_THEN_PROCEED,
					ENTITY_NAME, TYPE);
		CompanyBranchDTO result = companyBranchService.deactivate(deactivateDto);
		return ResponseEntity.ok().body(result);
	}

	@PostMapping("/reactivate")
	@PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.COMPANY_BRANCH_REACTIVATE + "\")")
	public ResponseEntity<CompanyBranchDTO> reactivateBranch(@Valid @RequestBody ReactivationDTO reactivateDto) {
		if (StringUtils.isEmpty(reactivateDto.getType()) || (!reactivateDto.getType().equals("REACTIVATE")))
			throw new BadRequestAlertException(
					IF_YOU_WANT_TO_REACTIVATE_BRANCH_THEN_TYPE_REACTIVATE_IN_TYPE_TEXT_BOX_AND_THEN_PROCEED,
					ENTITY_NAME, TYPE);
		CompanyBranchDTO result = companyBranchService.reactivate(reactivateDto);
		return ResponseEntity.ok().body(result);
	}
	

	@PostMapping(value = "userSpecificBranchDetails")
	public List<UserSpecificBranchDTO> userSpecificBranchDetails(@RequestBody UserAccessCompanyBranchDTO companyBranchDTO) {
		log.debug("user specific company details request : {} ", companyBranchDTO);
		return companyBranchService.getUserSpecificBranchDetails(companyBranchDTO);
	}

}
