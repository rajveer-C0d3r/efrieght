package com.grtship.account.controller;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import com.grtship.account.service.ObjectAliasService;
import com.grtship.account.util.HeaderUtil;
import com.grtship.account.util.PaginationUtil;
import com.grtship.account.util.ResponseUtil;
import com.grtship.common.exception.BadRequestAlertException;
import com.grtship.core.annotation.Auditable;
import com.grtship.core.dto.ObjectAliasDTO;

/**
 * REST controller for managing {@link com.grt.efreight.account.domain.ObjectAlias}.
 */
@RestController
@RequestMapping("/api")
public class ObjectAliasController {

    private final Logger log = LoggerFactory.getLogger(ObjectAliasController.class);

    private static final String ENTITY_NAME = "accountServiceObjectAlias";

    @Value("${spring.application.name}")
	private String applicationName;

    private final ObjectAliasService objectAliasService;

    public ObjectAliasController(ObjectAliasService objectAliasService) {
        this.objectAliasService = objectAliasService;
    }

    /**
     * {@code POST  /object-aliases} : Create a new objectAlias.
     *
     * @param objectAliasDTO the objectAliasDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new objectAliasDTO, or with status {@code 400 (Bad Request)} if the objectAlias has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/object-aliases")
    public ResponseEntity<ObjectAliasDTO> createObjectAlias(@RequestBody ObjectAliasDTO objectAliasDTO) throws URISyntaxException {
        log.debug("REST request to save ObjectAlias : {}", objectAliasDTO);
        if (objectAliasDTO.getId() != null) {
            throw new BadRequestAlertException("A new objectAlias cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ObjectAliasDTO result = objectAliasService.save(objectAliasDTO);
        return ResponseEntity.created(new URI("/api/object-aliases/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /object-aliases} : Updates an existing objectAlias.
     *
     * @param objectAliasDTO the objectAliasDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated objectAliasDTO,
     * or with status {@code 400 (Bad Request)} if the objectAliasDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the objectAliasDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/object-aliases")
    public ResponseEntity<ObjectAliasDTO> updateObjectAlias(@RequestBody ObjectAliasDTO objectAliasDTO) throws URISyntaxException {
        log.debug("REST request to update ObjectAlias : {}", objectAliasDTO);
        if (objectAliasDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        ObjectAliasDTO result = objectAliasService.save(objectAliasDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, objectAliasDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /object-aliases} : get all the objectAliases.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of objectAliases in body.
     */
    @GetMapping("/object-aliases")
    public ResponseEntity<List<ObjectAliasDTO>> getAllObjectAliases(Pageable pageable) {
        log.debug("REST request to get a page of ObjectAliases");
        Page<ObjectAliasDTO> page = objectAliasService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /object-aliases/:id} : get the "id" objectAlias.
     *
     * @param id the id of the objectAliasDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the objectAliasDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/object-aliases/{id}")
    public ResponseEntity<ObjectAliasDTO> getObjectAlias(@PathVariable Long id) {
        log.debug("REST request to get ObjectAlias : {}", id);
        Optional<ObjectAliasDTO> objectAliasDTO = objectAliasService.findOne(id);
        return ResponseUtil.wrapOrNotFound(objectAliasDTO);
    }

    /**
     * {@code DELETE  /object-aliases/:id} : delete the "id" objectAlias.
     *
     * @param id the id of the objectAliasDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @Auditable
    @DeleteMapping("/object-aliases/{id}")
    public ResponseEntity<Void> deleteObjectAlias(@PathVariable Long id) {
        log.debug("REST request to delete ObjectAlias : {}", id);
        objectAliasService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString())).build();
    }
}
