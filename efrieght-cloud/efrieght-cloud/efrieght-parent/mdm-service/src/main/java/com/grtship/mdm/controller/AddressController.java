package com.grtship.mdm.controller;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.grtship.common.exception.BadRequestAlertException;
import com.grtship.core.dto.AddressDTO;
import com.grtship.mdm.service.AddressService;
import com.grtship.mdm.util.HeaderUtil;
import com.grtship.mdm.util.ResponseUtil;

/**
 * REST controller for managing {@link com.grt.efreight.domain.Address}.
 */
@RestController
@RequestMapping("/address")
public class AddressController {

	private final Logger log = LoggerFactory.getLogger(AddressController.class);

	private static final String ENTITY_NAME = "masterDataManagementServiceAddress";
	
	@Value("${spring.application.name}")
	private String applicationName;

	private final AddressService addressService;

	public AddressController(AddressService addressService) {
		this.addressService = addressService;
	}

	/**
	 * {@code POST  /addresses} : Create a new address.
	 *
	 * @param addressDTO the addressDTO to create.
	 * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with
	 *         body the new addressDTO, or with status {@code 400 (Bad Request)} if
	 *         the address has already an ID.
	 * @throws URISyntaxException if the Location URI syntax is incorrect.
	 */
	@PostMapping
	public ResponseEntity<AddressDTO> createAddress(@RequestBody AddressDTO addressDTO) throws URISyntaxException {
		log.debug("REST request to save Address : {}", addressDTO);
		if (addressDTO.getId() != null) {
			throw new BadRequestAlertException("A new address cannot already have an ID", ENTITY_NAME, "idexists");
		}
		AddressDTO result = addressService.save(addressDTO);
		return ResponseEntity
				.created(new URI("/api/addresses/" + result.getId())).headers(HeaderUtil
						.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
				.body(result);
	}

	/**
	 * {@code PUT  /addresses} : Updates an existing address.
	 *
	 * @param addressDTO the addressDTO to update.
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
	 *         the updated addressDTO, or with status {@code 400 (Bad Request)} if
	 *         the addressDTO is not valid, or with status
	 *         {@code 500 (Internal Server Error)} if the addressDTO couldn't be
	 *         updated.
	 * @throws URISyntaxException if the Location URI syntax is incorrect.
	 */
	@PutMapping
	public ResponseEntity<AddressDTO> updateAddress(@RequestBody AddressDTO addressDTO) throws URISyntaxException {
		log.debug("REST request to update Address : {}", addressDTO);
		if (addressDTO.getId() == null) {
			throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
		}
		AddressDTO result = addressService.save(addressDTO);
		return ResponseEntity.ok().headers(
				HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, addressDTO.getId().toString()))
				.body(result);
	}

	/**
	 * {@code GET  /addresses} : get all the addresses.
	 *
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list
	 *         of addresses in body.
	 */
	@GetMapping
	public List<AddressDTO> getAllAddresses() {
		log.debug("REST request to get all Addresses");
		return addressService.findAll();
	}

	/**
	 * {@code GET  /addresses/:id} : get the "id" address.
	 *
	 * @param id the id of the addressDTO to retrieve.
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
	 *         the addressDTO, or with status {@code 404 (Not Found)}.
	 */
	@GetMapping("{id}")
	public ResponseEntity<AddressDTO> getAddress(@PathVariable Long id) {
		log.debug("REST request to get Address : {}", id);
		Optional<AddressDTO> addressDTO = addressService.findOne(id);
		return ResponseUtil.wrapOrNotFound(addressDTO);
	}

	/**
	 * {@code DELETE  /addresses/:id} : delete the "id" address.
	 *
	 * @param id the id of the addressDTO to delete.
	 * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
	 */
	@DeleteMapping("{id}")
	public ResponseEntity<Void> deleteAddress(@PathVariable Long id) {
		log.debug("REST request to delete Address : {}", id);
		addressService.delete(id);
		return ResponseEntity.noContent()
				.headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
				.build();
	}

	@PostMapping("/getAllAddressesByIdList")
	public Map<Long, AddressDTO> getAllAddressesByIdList(@RequestBody List<Long> addressIdList) {
		return addressService.getAllAddressesByIdList(addressIdList);
	}
}
