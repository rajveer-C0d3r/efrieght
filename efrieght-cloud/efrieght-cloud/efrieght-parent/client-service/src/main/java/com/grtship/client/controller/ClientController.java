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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.grtship.client.criteria.ClientCriteria;
import com.grtship.client.service.ClientService;
import com.grtship.client.serviceimpl.ClientQueryServiceImpl;
import com.grtship.client.util.HeaderUtil;
import com.grtship.client.util.PaginationUtil;
import com.grtship.client.util.ResponseUtil;
import com.grtship.common.exception.BadRequestAlertException;
import com.grtship.common.exception.InvalidDataException;
import com.grtship.core.annotation.Auditable;
import com.grtship.core.constant.ErrorCode;
import com.grtship.core.dto.ClientDTO;
import com.grtship.core.dto.DeactivationDTO;
import com.grtship.core.dto.PageableEntityDTO;
import com.grtship.core.dto.ReactivationDTO;

/**
 * REST controller for managing
 * {@link com.grtship.efreight.client.domain.Client}.
 */
@RestController
@RequestMapping("/api/clients")
public class ClientController {

	private final Logger log = LoggerFactory.getLogger(ClientController.class);

	private static final String ENTITY_NAME = "clientServiceClient";

	private static final String DEACTIVATE = "DEACTIVATE";

	private static final String REACTIVATE = "REACTIVATE";

	private static final String IF_YOU_WANT_TO_DEACTIVATE_CLIENT_THEN_WRITE_DEACTIVATE_IN_DEACTIVATION_TEXT_BOX = "If you want to deactivate client then, write 'DEACTIVATE' in deactivation Text Box.";

	private static final String IF_YOU_WANT_TO_REACTIVATE_CLIENT_THEN_WRITE_REACTIVATE_IN_DEACTIVATION_TEXT_BOX = "If you want to reactivate client then, write 'REACTIVATE' in deactivation Text Box.";

	@Value("${spring.application.name}")
	private String applicationName;

	@Autowired
	private ClientService clientService;

	@Autowired
	private ClientQueryServiceImpl clientQueryService;

	/**
	 * {@code POST  /clients} : Create a new client.
	 *
	 * @param clientDTO the clientDTO to create.
	 * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with
	 *         body the new clientDTO, or with status {@code 400 (Bad Request)} if
	 *         the client has already an ID.
	 * @throws URISyntaxException if the Location URI syntax is incorrect.
	 */
	@PostMapping
	public ResponseEntity<ClientDTO> createClient(@Valid @RequestBody ClientDTO clientDTO) throws URISyntaxException {
		log.debug("REST request to save Client : {}", clientDTO);
		if (clientDTO.getId() != null) {
			throw new BadRequestAlertException("A new client cannot already have an ID", ENTITY_NAME, "idexists");
		}
		ClientDTO result = clientService.save(clientDTO);
		return ResponseEntity
				.created(new URI("/api/clients/" + result.getId())).headers(HeaderUtil
						.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
				.body(result);
	}

	/**
	 * {@code PUT  /clients} : Updates an existing client.
	 *
	 * @param clientDTO the clientDTO to update.
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
	 *         the updated clientDTO, or with status {@code 400 (Bad Request)} if
	 *         the clientDTO is not valid, or with status
	 *         {@code 500 (Internal Server Error)} if the clientDTO couldn't be
	 *         updated.
	 * @throws URISyntaxException if the Location URI syntax is incorrect.
	 */
	@PutMapping
	public ResponseEntity<ClientDTO> updateClient(@Valid @RequestBody ClientDTO clientDTO) throws URISyntaxException {
		log.debug("REST request to update Client : {}", clientDTO);
		if (clientDTO.getId() == null) {
			throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
		}
		ClientDTO result = clientService.update(clientDTO);
		return ResponseEntity.ok().headers(
				HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, clientDTO.getId().toString()))
				.body(result);
	}

	/**
	 * {@code GET  /clients} : get all the clients.
	 *
	 * @param pageable the pagination information.
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list
	 *         of clients in body.
	 */
	@GetMapping
	public ResponseEntity<PageableEntityDTO<ClientDTO>> getAllClients(ClientCriteria clientCriteria,
			Pageable pageable) {
		log.debug("REST request to get a page of Clients");
		Page<ClientDTO> page = clientQueryService.findByCriteria(clientCriteria, pageable);
		HttpHeaders headers = PaginationUtil
				.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
		return ResponseEntity.ok().headers(headers)
				.body(new PageableEntityDTO<ClientDTO>(page.getContent(), page.getTotalElements()));
	}

	/**
	 * {@code GET  /clients/:id} : get the "id" client.
	 *
	 * @param id the id of the clientDTO to retrieve.
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
	 *         the clientDTO, or with status {@code 404 (Not Found)}.
	 */
	@GetMapping("{id}")
	public ResponseEntity<ClientDTO> getClient(@PathVariable Long id) {
		log.debug("REST request to get Client : {}", id);
		Optional<ClientDTO> clientDTO = clientQueryService.findOne(id);
		return ResponseUtil.wrapOrNotFound(clientDTO);
	}

	/**
	 * {@code DELETE  /clients/:id} : delete the "id" client.
	 *
	 * @param id the id of the clientDTO to delete.
	 * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
	 */
	@Auditable
	@DeleteMapping("{id}")
	public ResponseEntity<Void> deleteClient(@PathVariable Long id) {
		log.debug("REST request to delete Client : {}", id);
		clientService.delete(id);
		return ResponseEntity.noContent()
				.headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
				.build();
	}

	@PostMapping("getByIds")
	public ResponseEntity<List<ClientDTO>> getByIds(@RequestBody List<Long> clientIds) {
		log.debug("Request to get Clients based on client ids {}", clientIds);
		List<ClientDTO> companies = clientQueryService.getByIds(clientIds);
		return ResponseEntity.ok().body(companies);
	}

	@PostMapping("deactivate")
	public ResponseEntity<ClientDTO> deactivate(@Valid @RequestBody DeactivationDTO deactivateDto) {
		log.debug("REST request to deactivate Client : {}", deactivateDto);
		if (StringUtils.isEmpty(deactivateDto.getType()) || !deactivateDto.getType().equals(DEACTIVATE))
			throw new InvalidDataException(ErrorCode.INVALID_DATA_ERROR,
					IF_YOU_WANT_TO_DEACTIVATE_CLIENT_THEN_WRITE_DEACTIVATE_IN_DEACTIVATION_TEXT_BOX);
		ClientDTO result = clientService.deactivate(deactivateDto);
		return ResponseEntity.ok().body(result);
	}

	@PostMapping("activate")
	public ResponseEntity<ClientDTO> activate(@Valid @RequestBody ReactivationDTO activateDto) {
		log.debug("REST request to deactivate Client : {}", activateDto);
		if (StringUtils.isEmpty(activateDto.getType()) || (!activateDto.getType().equals(REACTIVATE)))
			throw new InvalidDataException(ErrorCode.INVALID_DATA_ERROR,
					IF_YOU_WANT_TO_REACTIVATE_CLIENT_THEN_WRITE_REACTIVATE_IN_DEACTIVATION_TEXT_BOX);
		ClientDTO result = clientService.activate(activateDto);
		return ResponseEntity.ok().body(result);
	}
}
