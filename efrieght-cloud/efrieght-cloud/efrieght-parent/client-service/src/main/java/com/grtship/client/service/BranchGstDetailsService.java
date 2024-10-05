package com.grtship.client.service;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.grtship.client.domain.BranchGstDetails;
import com.grtship.client.mapper.BranchGstDetailsMapper;
import com.grtship.client.repository.BranchGstDetailsRepository;
import com.grtship.core.dto.BranchGstDetailsDTO;

/**
 * Service Implementation for managing {@link BranchGstDetails}.
 */
@Service
@Transactional
public class BranchGstDetailsService {

    private final Logger log = LoggerFactory.getLogger(BranchGstDetailsService.class);

    private final BranchGstDetailsRepository branchGstDetailsRepository;

    private final BranchGstDetailsMapper branchGstDetailsMapper;

    public BranchGstDetailsService(BranchGstDetailsRepository branchGstDetailsRepository, BranchGstDetailsMapper branchGstDetailsMapper) {
        this.branchGstDetailsRepository = branchGstDetailsRepository;
        this.branchGstDetailsMapper = branchGstDetailsMapper;
    }

    /**
     * Save a branchGstDetails.
     *
     * @param branchGstDetailsDTO the entity to save.
     * @return the persisted entity.
     */
    public BranchGstDetailsDTO save(BranchGstDetailsDTO branchGstDetailsDTO) {
        log.debug("Request to save BranchGstDetails : {}", branchGstDetailsDTO);
        BranchGstDetails branchGstDetails = branchGstDetailsMapper.toEntity(branchGstDetailsDTO);
        branchGstDetails = branchGstDetailsRepository.save(branchGstDetails);
        return branchGstDetailsMapper.toDto(branchGstDetails);
    }

    /**
     * Get all the branchGstDetails.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<BranchGstDetailsDTO> findAll() {
        log.debug("Request to get all BranchGstDetails");
        return branchGstDetailsRepository.findAll().stream()
            .map(branchGstDetailsMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }


    /**
     * Get one branchGstDetails by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<BranchGstDetailsDTO> findOne(Long id) {
        log.debug("Request to get BranchGstDetails : {}", id);
        return branchGstDetailsRepository.findById(id)
            .map(branchGstDetailsMapper::toDto);
    }

    /**
     * Delete the branchGstDetails by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete BranchGstDetails : {}", id);
        branchGstDetailsRepository.deleteById(id);
    }
}
