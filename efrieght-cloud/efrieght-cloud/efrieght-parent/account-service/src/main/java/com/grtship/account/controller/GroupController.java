package com.grtship.account.controller;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
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

import com.grtship.account.criteria.GroupCriteria;
import com.grtship.account.service.GroupQueryService;
import com.grtship.account.service.GroupService;
import com.grtship.account.util.HeaderUtil;
import com.grtship.account.util.PaginationUtil;
import com.grtship.account.util.ResponseUtil;
import com.grtship.common.exception.BadRequestAlertException;
import com.grtship.core.annotation.Auditable;
import com.grtship.core.constant.AuthoritiesConstants;
import com.grtship.core.dto.BaseDTO;
import com.grtship.core.dto.DeactivationDTO;
import com.grtship.core.dto.GroupDTO;
import com.grtship.core.dto.PageableEntityDTO;
import com.grtship.core.dto.ReactivationDTO;

/**
 * REST controller for managing {@link com.grt.efreight.account.domain.Group}.
 */
@RestController
@RequestMapping("/api/group")
public class GroupController {

	private static final String BASE_GROUPS_CREATED_SUCCESSFULLY = "Base groups created successfully";

	private static final String IF_YOU_WANT_TO_REACTIVE_GROUP_THEN_YOU_NEED_TO_TYPE_REACTIVATE_IN_TEXT_BOX = "If you want to reactivate Group then you need to type 'REACTIVATE' in text box.";

	private static final String REACTIVATE = "REACTIVATE";

	private static final String IDNULL = "idnull";

	private static final String INVALID_ID = "Invalid id";

	private static final String IDEXISTS = "idexists";

	private static final String A_NEW_GROUP_CANNOT_ALREADY_HAVE_AN_ID = "A new group cannot already have an ID";

	private static final String TYPE = "type";

	private static final String IF_YOU_WANT_TO_DEACTIVATE_GROUP_THEN_WRITE_DEACTIVATE_IN_DEACTIVATION_TEXT_BOX = "If you want to deactivate Group then, write 'DEACTIVATE' in deactivation Text Box.";

	private static final String DEACTIVATE = "DEACTIVATE";

	private final Logger log = LoggerFactory.getLogger(GroupController.class);

	private static final String ENTITY_NAME = "accountServiceGroup";

	@Value("${spring.application.name}")
	private String applicationName;

	private final GroupService groupService;

	private final GroupQueryService groupQueryService;

	public GroupController(GroupService groupService, GroupQueryService groupQueryService) {
		this.groupService = groupService;
		this.groupQueryService = groupQueryService;
	}

	/**
	 * {@code POST  /groups} : Create a new group.
	 *
	 * @param groupDTO the groupDTO to create.
	 * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with
	 *         body the new groupDTO, or with status {@code 400 (Bad Request)} if
	 *         the group has already an ID.
	 * @throws URISyntaxException if the Location URI syntax is incorrect.
	 */
	@PostMapping
	@PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.GROUP_ADD + "\")")
	public ResponseEntity<GroupDTO> createGroup(@Valid @RequestBody GroupDTO groupDTO) throws URISyntaxException {
		log.debug("REST request to save Group : {}", groupDTO);
		if (groupDTO.getId() != null) {
			throw new BadRequestAlertException(A_NEW_GROUP_CANNOT_ALREADY_HAVE_AN_ID, ENTITY_NAME, IDEXISTS);
		}
		GroupDTO result = groupService.save(groupDTO);
		return ResponseEntity
				.created(new URI("/api/group/" + result.getId())).headers(HeaderUtil
						.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
				.body(result);
	}

	/**
	 * {@code PUT  /groups} : Updates an existing group.
	 *
	 * @param groupDTO the groupDTO to update.
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
	 *         the updated groupDTO, or with status {@code 400 (Bad Request)} if the
	 *         groupDTO is not valid, or with status
	 *         {@code 500 (Internal Server Error)} if the groupDTO couldn't be
	 *         updated.
	 * @throws URISyntaxException if the Location URI syntax is incorrect.
	 */
	@PutMapping
	@PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.GROUP_EDIT + "\")")
	public ResponseEntity<GroupDTO> updateGroup(@Valid @RequestBody GroupDTO groupDTO) throws URISyntaxException {
		log.debug("REST request to update Group : {}", groupDTO);
		if (groupDTO.getId() == null) {
			throw new BadRequestAlertException(INVALID_ID, ENTITY_NAME, IDNULL);
		}
		GroupDTO result = groupService.update(groupDTO);
		return ResponseEntity.ok().headers(
				HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, groupDTO.getId().toString()))
				.body(result);
	}

	/**
	 * {@code GET  /groups} : get all the groups.
	 *
	 * @param pageable the pagination information.
	 * @param criteria the criteria which the requested entities should match.
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list
	 *         of groups in body.
	 */
	@GetMapping
	@PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.GROUP_VIEW + "\")")
	public ResponseEntity<PageableEntityDTO<GroupDTO>> getAllGroups(GroupCriteria criteria, Pageable pageable) {
		log.debug("REST request to get Groups by criteria: {}", criteria);
		Page<GroupDTO> page = groupQueryService.findByCriteria(criteria, pageable);
		HttpHeaders headers = PaginationUtil
				.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
		return ResponseEntity.ok().headers(headers)
				.body(new PageableEntityDTO<GroupDTO>(page.getContent(), page.getTotalElements()));
	}

	/**
	 * {@code GET  /groups/count} : count all the groups.
	 *
	 * @param criteria the criteria which the requested entities should match.
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count
	 *         in body.
	 */
	@GetMapping("count")
	public ResponseEntity<Long> countGroups(GroupCriteria criteria) {
		log.debug("REST request to count Groups by criteria: {}", criteria);
		return ResponseEntity.ok().body(groupQueryService.countByCriteria(criteria));
	}

	/**
	 * {@code GET  /groups/:id} : get the "id" group.
	 *
	 * @param id the id of the groupDTO to retrieve.
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
	 *         the groupDTO, or with status {@code 404 (Not Found)}.
	 */
	@GetMapping("{id}")
	@PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.GROUP_VIEW + "\")")
	public ResponseEntity<GroupDTO> getGroup(@PathVariable Long id) {
		log.debug("REST request to get Group : {}", id);
		Optional<GroupDTO> groupDTO = groupQueryService.findOne(id);
		return ResponseUtil.wrapOrNotFound(groupDTO);
	}

	/**
	 * {@code DELETE  /groups/:id} : delete the "id" group.
	 *
	 * @param id the id of the groupDTO to delete.
	 * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
	 */
	@Auditable
	@DeleteMapping("{id}")
	@PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.GROUP_DELETE + "\")")
	public ResponseEntity<Void> deleteGroup(@PathVariable Long id) {
		log.debug("REST request to delete Group : {}", id);
		groupService.delete(id);
		return ResponseEntity.noContent()
				.headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
				.build();
	}

	@GetMapping("getParentGroups")
	public ResponseEntity<List<BaseDTO>> getParentGroups() {
		log.debug("REST request to get all parent Groups");
		List<BaseDTO> groupDTO = groupQueryService.getParentGroups();
		return ResponseEntity.ok().body(groupDTO);
	}

	@GetMapping("childs/{id}")
	public ResponseEntity<List<BaseDTO>> getChildGroups(@PathVariable Long id) {
		log.debug("REST request to get all parent Child Groups : {}", id);
		List<BaseDTO> groupDTO = groupQueryService.getChildGroups(id);
		return ResponseEntity.ok().body(groupDTO);
	}

	@PostMapping("deactivate")
	@PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.GROUP_DEACTIVATE + "\")")
	public ResponseEntity<GroupDTO> deactivate(@RequestBody DeactivationDTO deactivateDto) {
		if (StringUtils.isEmpty(deactivateDto.getType()) || !deactivateDto.getType().equals(DEACTIVATE)) {
			throw new BadRequestAlertException(
					IF_YOU_WANT_TO_DEACTIVATE_GROUP_THEN_WRITE_DEACTIVATE_IN_DEACTIVATION_TEXT_BOX, ENTITY_NAME, TYPE);
		}
		GroupDTO result = groupService.deactivate(deactivateDto);
		return ResponseEntity.ok().body(result);
	}

	@PostMapping("/reactivate")
	@PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.GROUP_REACTIVATE + "\")")
	public ResponseEntity<GroupDTO> reactivate(@RequestBody ReactivationDTO reactivateDto) {
		if (StringUtils.isEmpty(reactivateDto.getType()) || (!reactivateDto.getType().equals(REACTIVATE)))
			throw new BadRequestAlertException(
					IF_YOU_WANT_TO_REACTIVE_GROUP_THEN_YOU_NEED_TO_TYPE_REACTIVATE_IN_TEXT_BOX, ENTITY_NAME, TYPE);
		GroupDTO result = groupService.reactivate(reactivateDto);
		return ResponseEntity.ok().body(result);
	}

	@GetMapping("createBaseGroups/{companyId}")
	public ResponseEntity<String> configureBaseGroups(@PathVariable Long companyId) throws IOException {
		groupService.createBaseGroups(companyId);
		return ResponseEntity.ok().body(BASE_GROUPS_CREATED_SUCCESSFULLY);
	}

}
