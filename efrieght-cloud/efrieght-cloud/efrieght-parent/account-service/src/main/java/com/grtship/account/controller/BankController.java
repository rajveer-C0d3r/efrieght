package com.grtship.account.controller;

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

import com.grtship.account.criteria.BankCriteria;
import com.grtship.account.service.BankFilterService;
import com.grtship.account.service.BankService;
import com.grtship.account.util.HeaderUtil;
import com.grtship.account.util.PaginationUtil;
import com.grtship.account.util.ResponseUtil;
import com.grtship.common.exception.BadRequestAlertException;
import com.grtship.core.annotation.Auditable;
import com.grtship.core.dto.BankDTO;
import com.grtship.core.dto.PageableEntityDTO;

/**
 * REST controller for managing {@link com.grt.efreight.account.domain.Bank}.
 */
@RestController
@RequestMapping("/api/banks")
public class BankController {

    private final Logger log = LoggerFactory.getLogger(BankController.class);

    private static final String ENTITY_NAME = "accountServiceBank";

    @Value("${spring.application.name}")
	private String applicationName;

    @Autowired
    private BankService bankService;

    @Autowired
    private BankFilterService bankFilterService;

    /**
     * {@code POST  /banks} : Create a new bank.
     *
     * @param bankDTO the bankDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new bankDTO, or with status {@code 400 (Bad Request)} if the bank has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping
    public ResponseEntity<BankDTO> createBank(@Valid @RequestBody BankDTO bankDTO) throws URISyntaxException {
        log.debug("REST request to save Bank : {}", bankDTO);
        if (bankDTO.getId() != null) {
            throw new BadRequestAlertException("A new bank cannot already have an ID", ENTITY_NAME, "idexists");
        }
        BankDTO result = bankService.save(bankDTO);
        return ResponseEntity.created(new URI("/api/banks/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /banks} : Updates an existing bank.
     *
     * @param bankDTO the bankDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated bankDTO,
     * or with status {@code 400 (Bad Request)} if the bankDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the bankDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping
    public ResponseEntity<BankDTO> updateBank(@Valid @RequestBody BankDTO bankDTO) throws URISyntaxException {
        log.debug("REST request to update Bank : {}", bankDTO);
        if (bankDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        BankDTO result = bankService.save(bankDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, bankDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /banks} : get all the banks.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of banks in body.
     */
    @GetMapping
    public ResponseEntity<PageableEntityDTO<BankDTO>> getAllBanks(BankCriteria criteria, Pageable pageable) {
        log.debug("REST request to get Banks by criteria: {}", criteria);
        Page<BankDTO> page = bankFilterService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(new PageableEntityDTO<BankDTO>(page.getContent(), page.getTotalElements()));
    }

    /**
     * {@code GET  /banks/:id} : get the "id" bank.
     *
     * @param id the id of the bankDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the bankDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("{id}")
    public ResponseEntity<BankDTO> getBank(@PathVariable Long id) {
        log.debug("REST request to get Bank : {}", id);
        Optional<BankDTO> bankDTO = bankService.findOne(id);
        return ResponseUtil.wrapOrNotFound(bankDTO);
    }

    /**
     * {@code DELETE  /banks/:id} : delete the "id" bank.
     *
     * @param id the id of the bankDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @Auditable
    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteBank(@PathVariable Long id) {
        log.debug("REST request to delete Bank : {}", id);
        bankService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString())).build();
    }
}
