package com.grtship.client.controller;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
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

import com.grtship.client.service.AliasService;
import com.grtship.client.util.HeaderUtil;
import com.grtship.client.util.ResponseUtil;
import com.grtship.common.exception.BadRequestAlertException;
import com.grtship.core.annotation.Auditable;
import com.grtship.core.dto.AliasDTO;

/**
 * REST controller for managing {@link com.grtship.efreight.client.domain.Alias}.
 */
@RestController
@RequestMapping("/alias-resource/aliases") // FIXME REMOVE THIS CONTROLLER AFTER CODE SAGGREGATION
public class AliasController {

    private final Logger log = LoggerFactory.getLogger(AliasController.class);

    private static final String ENTITY_NAME = "clientServiceAlias";
    
    @Value("${spring.application.name}")
	private String applicationName;

    private final AliasService aliasService;

    public AliasController(AliasService aliasService) {
        this.aliasService = aliasService;
    }

    /**
     * {@code POST  /aliases} : Create a new alias.
     *
     * @param aliasDTO the aliasDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new aliasDTO, or with status {@code 400 (Bad Request)} if the alias has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping
    public ResponseEntity<AliasDTO> createAlias(@RequestBody AliasDTO aliasDTO) throws URISyntaxException {
        log.debug("REST request to save Alias : {}", aliasDTO);
        if (aliasDTO.getId() != null) {
            throw new BadRequestAlertException("A new alias cannot already have an ID", ENTITY_NAME, "idexists");
        }
        AliasDTO result = aliasService.save(aliasDTO);
        return ResponseEntity.created(new URI("/api/aliases/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /aliases} : Updates an existing alias.
     *
     * @param aliasDTO the aliasDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated aliasDTO,
     * or with status {@code 400 (Bad Request)} if the aliasDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the aliasDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping
    public ResponseEntity<AliasDTO> updateAlias(@RequestBody AliasDTO aliasDTO) throws URISyntaxException {
        log.debug("REST request to update Alias : {}", aliasDTO);
        if (aliasDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        AliasDTO result = aliasService.save(aliasDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, aliasDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /aliases} : get all the aliases.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of aliases in body.
     */
    @GetMapping
    public List<AliasDTO> getAllAliases() {
        log.debug("REST request to get all Aliases");
        return aliasService.findAll();
    }

    /**
     * {@code GET  /aliases/:id} : get the "id" alias.
     *
     * @param id the id of the aliasDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the aliasDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("{id}")
    public ResponseEntity<AliasDTO> getAlias(@PathVariable Long id) {
        log.debug("REST request to get Alias : {}", id);
        Optional<AliasDTO> aliasDTO = aliasService.findOne(id);
        return ResponseUtil.wrapOrNotFound(aliasDTO);
    }

    /**
     * {@code DELETE  /aliases/:id} : delete the "id" alias.
     *
     * @param id the id of the aliasDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @Auditable
    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteAlias(@PathVariable Long id) {
        log.debug("REST request to delete Alias : {}", id);
        aliasService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }
}
