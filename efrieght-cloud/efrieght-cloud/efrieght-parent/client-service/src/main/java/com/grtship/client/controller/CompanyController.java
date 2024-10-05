package com.grtship.client.controller;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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

import com.grtship.client.criteria.CompanyCriteria;
import com.grtship.client.service.CompanyQueryService;
import com.grtship.client.service.CompanyService;
import com.grtship.client.util.HeaderUtil;
import com.grtship.client.util.PaginationUtil;
import com.grtship.client.util.ResponseUtil;
import com.grtship.common.exception.BadRequestAlertException;
import com.grtship.common.exception.InvalidDataException;
import com.grtship.core.constant.AuthoritiesConstants;
import com.grtship.core.constant.ErrorCode;
import com.grtship.core.dto.CompanyBranchBaseDTO;
import com.grtship.core.dto.CompanyDTO;
import com.grtship.core.dto.DeactivationDTO;
import com.grtship.core.dto.PageableEntityDTO;
import com.grtship.core.dto.ReactivationDTO;
import com.grtship.core.dto.UserAccessCompanyBranchDTO;
import com.grtship.core.dto.UserCompanyDTO;
import com.grtship.core.dto.UserSpecificCBRequest;
import com.grtship.core.dto.UserSpecificCBResponse;
import com.grtship.core.dto.UserSpecificCompanyDTO;

/**
 * REST controller for managing
 * {@link com.grtship.efreight.client.domain.Company}.
 */
@RestController
@RequestMapping("/api/company")
public class CompanyController {

	private final Logger log = LoggerFactory.getLogger(CompanyController.class);

	private static final String IF_YOU_WANT_TO_DEACTIVATE_COMPANY_THEN_WRITE_DEACTIVATE_IN_DEACTIVATION_TEXT_BOX = "If you want to deactivate company then, write 'DEACTIVATE' in deactivation Text Box.";

	private static final String IF_YOU_WANT_TO_REACTIVATE_COMPANY_THEN_WRITE_REACTIVATE_IN_DEACTIVATION_TEXT_BOX = "If you want to reactivate company then, write 'REACTIVATE' in deactivation Text Box.";

	private static final String ENTITY_NAME = "clientServiceCompany";

	private static final String DEACTIVATE = "DEACTIVATE";

	private static final String REACTIVATE = "REACTIVATE";

	@Value("${spring.application.name}")
	private String applicationName;

	@Autowired
	private CompanyService companyService;

	@Autowired
	private CompanyQueryService companyQueryService;

	/**
	 * {@code POST  /companies} : Create a new company.
	 *
	 * @param companyDTO the companyDTO to create.
	 * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with
	 *         body the new companyDTO, or with status {@code 400 (Bad Request)} if
	 *         the company has already an ID.
	 * @throws Exception
	 */
	@PostMapping
	@PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.COMPANY_ADD + "\")")
	public ResponseEntity<CompanyDTO> createCompany(@Valid @RequestBody CompanyDTO companyDTO) throws Exception {
		log.debug("REST request to save Company : {}", companyDTO);
		if (companyDTO.getId() != null) {
			throw new BadRequestAlertException("A new company cannot already have an ID", ENTITY_NAME, "idexists");
		}
		CompanyDTO result = companyService.save(companyDTO);
		return ResponseEntity
				.created(new URI("/api/company/" + result.getId())).headers(HeaderUtil
						.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
				.body(result);
	}

	/**
	 * {@code PUT  /companies} : Updates an existing company.
	 *
	 * @param companyDTO the companyDTO to update.
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
	 *         the updated companyDTO, or with status {@code 400 (Bad Request)} if
	 *         the companyDTO is not valid, or with status
	 *         {@code 500 (Internal Server Error)} if the companyDTO couldn't be
	 *         updated.
	 * @throws Exception
	 */
	@PutMapping
	@PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.COMPANY_EDIT + "\")")
	public ResponseEntity<CompanyDTO> updateCompany(@Valid @RequestBody CompanyDTO companyDTO) throws Exception {
		log.debug("REST request to update Company : {}", companyDTO);
		if (companyDTO.getId() == null) {
			throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
		}
		CompanyDTO result = companyService.update(companyDTO);
		return ResponseEntity.ok().headers(
				HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, companyDTO.getId().toString()))
				.body(result);
	}

	/**
	 * {@code GET  /companies} : get all the companies.
	 *
	 * @param pageable the pagination information.
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list
	 *         of companies in body.
	 */
	@GetMapping
	@PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.COMPANY_VIEW + "\")")
	public ResponseEntity<PageableEntityDTO<CompanyDTO>> getAllCompanies(CompanyCriteria criteria, Pageable pageable) {
		log.debug("REST request to get a page of Companies");
		Page<CompanyDTO> page = companyQueryService.findByCriteria(criteria, pageable);
		HttpHeaders headers = PaginationUtil
				.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
		return ResponseEntity.ok().headers(headers)
				.body(new PageableEntityDTO<CompanyDTO>(page.getContent(), page.getTotalElements()));
	}

	/**
	 * {@code GET  /companies/:id} : get the "id" company.
	 *
	 * @param id the id of the companyDTO to retrieve.
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
	 *         the companyDTO, or with status {@code 404 (Not Found)}.
	 */
	@GetMapping("{id}")
	@PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.COMPANY_VIEW + "\")")
	public ResponseEntity<CompanyDTO> getCompany(@PathVariable Long id) {
		log.debug("REST request to get Company : {}", id);
		Optional<CompanyDTO> companyDTO = companyQueryService.findOne(id);
		return ResponseUtil.wrapOrNotFound(companyDTO);
	}

	@PostMapping("getByUserAccess")
	public ResponseEntity<Set<UserCompanyDTO>> getByUserAccess(@RequestBody List<UserCompanyDTO> userCompany) {
		log.debug("Request to get User companies {}", userCompany);
		return ResponseEntity.ok().body(companyQueryService.getByUserAccess(userCompany));
	}

	@GetMapping("getByUserId/{userId}")
	public ResponseEntity<Set<CompanyBranchBaseDTO>> getCompanyAndBranches(@PathVariable Long userId) {
		log.debug("Request to get User companies {}", userId);
		return ResponseEntity.ok().body(companyQueryService.getCompanyAndBanchesByUserId(userId));
	}

	@PostMapping("deactivate")
	@PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.COMPANY_DEACTIVATE + "\")")
	public ResponseEntity<CompanyDTO> deactivate(@Valid @RequestBody DeactivationDTO deactivateDto) {
		log.debug("REST request to deactivate Company : {}", deactivateDto);
		if (StringUtils.isEmpty(deactivateDto.getType()) || !deactivateDto.getType().equals(DEACTIVATE))
			throw new InvalidDataException(ErrorCode.INVALID_DATA_ERROR,
					IF_YOU_WANT_TO_DEACTIVATE_COMPANY_THEN_WRITE_DEACTIVATE_IN_DEACTIVATION_TEXT_BOX);
		CompanyDTO result = companyService.deactivate(deactivateDto);
		return ResponseEntity.ok().body(result);
	}

	@PostMapping("reactivate")
	@PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.COMPANY_REACTIVATE + "\")")
	public ResponseEntity<CompanyDTO> reactivate(@Valid @RequestBody ReactivationDTO activateDto) {
		log.debug("REST request to deactivate Company : {}", activateDto);
		if (StringUtils.isEmpty(activateDto.getType()) || (!activateDto.getType().equals(REACTIVATE)))
			throw new InvalidDataException(ErrorCode.INVALID_DATA_ERROR,
					IF_YOU_WANT_TO_REACTIVATE_COMPANY_THEN_WRITE_REACTIVATE_IN_DEACTIVATION_TEXT_BOX);
		CompanyDTO result = companyService.reactivate(activateDto);
		return ResponseEntity.ok().body(result);
	}

	@PostMapping(value = "userSpecificCBDetails")
	public UserSpecificCBResponse userSpecificCBDetails(@RequestBody UserSpecificCBRequest cbRequest) {
		log.debug("user specific company branch details request : {} ", cbRequest);
		return companyService.userSpecificCBDetails(cbRequest);
	}
	
	@PostMapping(value = "userSpecificCompanyDetails")
	public List<UserSpecificCompanyDTO> userSpecificCompanyDetails(@RequestBody UserAccessCompanyBranchDTO companyBranchDTO) {
		log.debug("user specific company details request : {} ", companyBranchDTO);
		return companyService.userSpecificCompanyDetails(companyBranchDTO);
	}
}
