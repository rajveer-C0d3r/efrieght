package com.grtship.mdm.controller;

import java.net.URI;
import java.net.URISyntaxException;
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
import com.grtship.common.exception.InvalidDataException;
import com.grtship.core.constant.AuthoritiesConstants;
import com.grtship.core.constant.ErrorCode;
import com.grtship.core.dto.DeactivationDTO;
import com.grtship.core.dto.EntityMultiDropDownDTO;
import com.grtship.core.dto.ExternalEntityDTO;
import com.grtship.core.dto.ExternalEntityRequestDTO;
import com.grtship.core.dto.PageableEntityDTO;
import com.grtship.core.dto.ReactivationDTO;
import com.grtship.mdm.criteria.ExternalEntityCriteria;
import com.grtship.mdm.service.ExternalEntityQueryService;
import com.grtship.mdm.service.ExternalEntityService;
import com.grtship.mdm.util.HeaderUtil;
import com.grtship.mdm.util.PaginationUtil;
import com.grtship.mdm.util.ResponseUtil;

/**
 * REST controller for managing {@link com.grt.efreight.domain.ExternalEntity}.
 */
@RestController
@RequestMapping("/api/external-entity")
public class ExternalEntityController {

	private static final String A_NEW_EXTERNAL_ENTITY_CANNOT_ALREADY_HAVE_AN_ID = "A new externalEntity cannot already have an ID";

	private static final String DEACTIVATE = "DEACTIVATE";
	
	private static final String REACTIVATE = "REACTIVATE";

	private static final String DEACTIVATE_TYPE_CAN_T_BE_NULL_IF_YOU_WANT_TO_DEACTIVATE_ENTITY_THEN_PLEASE_ENTERED_TYPE_AS_DEACTIVATE = "Deactivate Type Can't be Null. If you want to deactivate entity then please entered type as DEACTIVATE..!";

	private static final String REACTIVATE_TYPE_CAN_T_BE_NULL_IF_YOU_WANT_TO_REACTIVATE_ENTITY_THEN_PLEASE_ENTERED_TYPE_AS_REACTIVATE = "Reactivate Type Can't be Null. If you want to reactivate entity then please entered type as REACTIVATE..!";
	
	private static final String IDNULL = "idnull";

	private static final String INVALID_ID = "Invalid id";

	private final Logger log = LoggerFactory.getLogger(ExternalEntityController.class);

	private static final String ENTITY_NAME = "masterDataManagementServiceExternalEntity";

	@Value("${spring.application.name}")
	private String applicationName;

	private final ExternalEntityService externalEntityService;

	@Autowired
	private ExternalEntityQueryService entityFilterService;

	public ExternalEntityController(ExternalEntityService externalEntityService) {
		this.externalEntityService = externalEntityService;
	}

	/**
	 * {@code POST  /external-entities} : Create a new externalEntity.
	 *
	 * @param externalEntityDTO the externalEntityDTO to create.
	 * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with
	 *         body the new externalEntityDTO, or with status
	 *         {@code 400 (Bad Request)} if the externalEntity has already an ID.
	 * @throws URISyntaxException if the Location URI syntax is incorrect.
	 */
	@PostMapping
	@PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.EXTERNAL_ENTITY_ADD + "\")")
	public ResponseEntity<ExternalEntityDTO> createExternalEntity(
			@Valid @RequestBody ExternalEntityRequestDTO externalEntityDTO) throws URISyntaxException {
		log.debug("REST request to save ExternalEntity : {}", externalEntityDTO);
		if (externalEntityDTO.getId() != null) {
			throw new BadRequestAlertException(A_NEW_EXTERNAL_ENTITY_CANNOT_ALREADY_HAVE_AN_ID, ENTITY_NAME,
					"idexists");
		}

		ExternalEntityDTO result = externalEntityService.save(externalEntityDTO);
		return ResponseEntity
				.created(new URI("/api/external-entity/" + result.getId())).headers(HeaderUtil
						.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
				.body(result);
	}

	/**
	 * {@code PUT  /external-entities} : Updates an existing externalEntity.
	 *
	 * @param externalEntityDto the externalEntityDTO to update.
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
	 *         the updated externalEntityDTO, or with status
	 *         {@code 400 (Bad Request)} if the externalEntityDTO is not valid, or
	 *         with status {@code 500 (Internal Server Error)} if the
	 *         externalEntityDTO couldn't be updated.
	 * @throws URISyntaxException if the Location URI syntax is incorrect.
	 */
	@PutMapping
	@PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.EXTERNAL_ENTITY_UPDATE + "\")")
	public ResponseEntity<ExternalEntityDTO> updateExternalEntity(
			@Valid @RequestBody ExternalEntityDTO externalEntityDto) throws URISyntaxException {
		if (externalEntityDto.getId() == null) {
			throw new BadRequestAlertException(INVALID_ID, ENTITY_NAME, IDNULL);
		}
		ExternalEntityDTO result = externalEntityService.update(externalEntityDto);
		return ResponseEntity.ok().headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME,
				externalEntityDto.getId().toString())).body(result);
	}

	/**
	 * {@code GET  /external-entities} : get all the externalEntities.
	 *
	 * @param pageable the pagination information.
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list
	 *         of externalEntities in body.
	 */
	@GetMapping
	@PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.EXTERNAL_ENTITY_VIEW + "\")")
	public ResponseEntity<PageableEntityDTO<ExternalEntityDTO>> getAllExternalEntities(
			ExternalEntityCriteria entityCriteria, Pageable pageable) {
		Page<ExternalEntityDTO> page = entityFilterService.findAll(entityCriteria, pageable);
		HttpHeaders headers = PaginationUtil
				.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
		return ResponseEntity.ok().headers(headers)
				.body(new PageableEntityDTO<ExternalEntityDTO>(page.getContent(), page.getTotalElements()));
	}

	/**
	 * {@code GET  /external-entities/:id} : get the "id" externalEntity.
	 *
	 * @param id the id of the externalEntityDTO to retrieve.
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
	 *         the externalEntityDTO, or with status {@code 404 (Not Found)}.
	 */
	@GetMapping("{id}")
	@PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.EXTERNAL_ENTITY_VIEW + "\")")
	public ResponseEntity<ExternalEntityDTO> getExternalEntity(@PathVariable Long id) {
		Optional<ExternalEntityDTO> externalEntityDTO = entityFilterService.findOne(id);
		return ResponseUtil.wrapOrNotFound(externalEntityDTO);
	}

	@PostMapping("/deactivate")
	@PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.EXTERNAL_ENTITY_DEACTIVATE + "\")")
	public ResponseEntity<ExternalEntityDTO> deactivateEntity(@RequestBody @Valid DeactivationDTO deactivateDto) {
		if (StringUtils.isEmpty(deactivateDto.getType()) || !deactivateDto.getType().equalsIgnoreCase(DEACTIVATE))
			throw new InvalidDataException(ErrorCode.INVALID_DATA_ERROR,
					DEACTIVATE_TYPE_CAN_T_BE_NULL_IF_YOU_WANT_TO_DEACTIVATE_ENTITY_THEN_PLEASE_ENTERED_TYPE_AS_DEACTIVATE);

		ExternalEntityDTO entityDto = externalEntityService.deactivate(deactivateDto);
		return ResponseEntity.ok().body(entityDto);
	}
	
	
	@PostMapping("/reactivate")
	@PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.EXTERNAL_ENTITY_REACTIVATE + "\")")
	public ResponseEntity<ExternalEntityDTO> reactivateEntity(@RequestBody @Valid ReactivationDTO reactivateDto) {
		if (StringUtils.isEmpty(reactivateDto.getType()) || !reactivateDto.getType().equalsIgnoreCase(REACTIVATE))
			throw new InvalidDataException(ErrorCode.INVALID_DATA_ERROR,
					REACTIVATE_TYPE_CAN_T_BE_NULL_IF_YOU_WANT_TO_REACTIVATE_ENTITY_THEN_PLEASE_ENTERED_TYPE_AS_REACTIVATE);

		ExternalEntityDTO entityDto = externalEntityService.reactivate(reactivateDto);
		return ResponseEntity.ok().body(entityDto);
	}

	@GetMapping("/getAllExternalEntitiesForMultiDD")
	public ResponseEntity<PageableEntityDTO<EntityMultiDropDownDTO>> getAllExternalEntitiesForMultiDD(
			ExternalEntityCriteria entityCriteria, Pageable pageable) {
		Page<EntityMultiDropDownDTO> page = entityFilterService.getAllExternalEntitiesForMultiDD(entityCriteria,
				pageable);
		HttpHeaders headers = PaginationUtil
				.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
		return ResponseEntity.ok().headers(headers)
				.body(new PageableEntityDTO<EntityMultiDropDownDTO>(page.getContent(), page.getTotalElements()));
	}

}
