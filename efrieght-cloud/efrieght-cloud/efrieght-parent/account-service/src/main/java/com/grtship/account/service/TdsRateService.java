package com.grtship.account.service;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.grtship.account.domain.TdsRate;
import com.grtship.account.mapper.TdsRateMapper;
import com.grtship.account.repository.TdsRateRepository;
import com.grtship.core.annotation.Auditable;
import com.grtship.core.dto.TdsRateDTO;

/**
 * Service Implementation for managing {@link TdsRate}.
 */
@Service
@Transactional
public class TdsRateService {

	private final Logger log = LoggerFactory.getLogger(TdsRateService.class);

	private final TdsRateRepository tdsRateRepository;

	private final TdsRateMapper tdsRateMapper;

	public TdsRateService(TdsRateRepository tdsRateRepository, TdsRateMapper tdsRateMapper) {
		this.tdsRateRepository = tdsRateRepository;
		this.tdsRateMapper = tdsRateMapper;
	}

	/**
	 * Save a tdsRate.
	 *
	 * @param tdsRateDTO the entity to save.
	 * @return the persisted entity.
	 */
	@Auditable
	public TdsRateDTO save(TdsRateDTO tdsRateDTO) {
		log.debug("Request to save TdsRate : {}", tdsRateDTO);
		TdsRate tdsRate = tdsRateMapper.toEntity(tdsRateDTO);
		tdsRate = tdsRateRepository.save(tdsRate);
		return tdsRateMapper.toDto(tdsRate);
	}

	/**
	 * Get all the tdsRates.
	 *
	 * @return the list of entities.
	 */
	@Transactional(readOnly = true)
	public List<TdsRateDTO> findAll() {
		log.debug("Request to get all TdsRates");
		return tdsRateRepository.findAll().stream().map(tdsRateMapper::toDto)
				.collect(Collectors.toCollection(LinkedList::new));
	}

	/**
	 * Get one tdsRate by id.
	 *
	 * @param id the id of the entity.
	 * @return the entity.
	 */
	@Transactional(readOnly = true)
	public Optional<TdsRateDTO> findOne(Long id) {
		log.debug("Request to get TdsRate : {}", id);
		return tdsRateRepository.findById(id).map(tdsRateMapper::toDto);
	}

	/**
	 * Delete the tdsRate by id.
	 *
	 * @param id the id of the entity.
	 */
	public void delete(Long id) {
		log.debug("Request to delete TdsRate : {}", id);
		tdsRateRepository.deleteById(id);
	}
}
