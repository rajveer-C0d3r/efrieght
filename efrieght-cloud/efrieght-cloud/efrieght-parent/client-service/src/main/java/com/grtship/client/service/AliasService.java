package com.grtship.client.service;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.grtship.client.domain.Alias;
import com.grtship.client.mapper.AliasMapper;
import com.grtship.client.repository.AliasRepository;
import com.grtship.core.annotation.Auditable;
import com.grtship.core.dto.AliasDTO;

/**
 * Service Implementation for managing {@link Alias}.
 */
@Service
@Transactional
public class AliasService {

    private final Logger log = LoggerFactory.getLogger(AliasService.class);

    private final AliasRepository aliasRepository;

    private final AliasMapper aliasMapper;

    public AliasService(AliasRepository aliasRepository, AliasMapper aliasMapper) {
        this.aliasRepository = aliasRepository;
        this.aliasMapper = aliasMapper;
    }

    /**
     * Save a alias.
     *
     * @param aliasDTO the entity to save.
     * @return the persisted entity.
     */
    @Auditable
    public AliasDTO save(AliasDTO aliasDTO) {
        log.debug("Request to save Alias : {}", aliasDTO);
        Alias alias = aliasMapper.toEntity(aliasDTO);
        alias = aliasRepository.save(alias);
        return aliasMapper.toDto(alias);
    }

    /**
     * Get all the aliases.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<AliasDTO> findAll() {
        log.debug("Request to get all Aliases");
        return aliasRepository.findAll().stream()
            .map(aliasMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }


    /**
     * Get one alias by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<AliasDTO> findOne(Long id) {
        log.debug("Request to get Alias : {}", id);
        return aliasRepository.findById(id)
            .map(aliasMapper::toDto);
    }

    /**
     * Delete the alias by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Alias : {}", id);
        aliasRepository.deleteById(id);
    }
}
