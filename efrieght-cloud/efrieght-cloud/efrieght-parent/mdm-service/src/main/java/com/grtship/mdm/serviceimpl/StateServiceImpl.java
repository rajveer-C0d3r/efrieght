package com.grtship.mdm.serviceimpl;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.grtship.core.annotation.Auditable;
import com.grtship.core.annotation.Auditable.ActionType;
import com.grtship.core.dto.StateDTO;
import com.grtship.mdm.domain.Country;
import com.grtship.mdm.domain.State;
import com.grtship.mdm.mapper.StateMapper;
import com.grtship.mdm.repository.StateRepository;
import com.grtship.mdm.service.StateService;

/**
 * Service Implementation for managing {@link State}.
 */
@Service
@Transactional
public class StateServiceImpl implements StateService {

	private final Logger log = LoggerFactory.getLogger(StateServiceImpl.class);

	private final StateRepository stateRepository;

	private final StateMapper stateMapper;

	public StateServiceImpl(StateRepository stateRepository, StateMapper stateMapper) {
		this.stateRepository = stateRepository;
		this.stateMapper = stateMapper;
	}

	@Override
	@Auditable(action = ActionType.SAVE, module = com.grtship.core.annotation.Auditable.Module.STATE)
	public StateDTO save(StateDTO stateDTO) {
		log.debug("Request to save State : {}", stateDTO);
		State state = stateMapper.toEntity(stateDTO);
		state = stateRepository.save(state);
		return stateMapper.toDto(state);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<StateDTO> findAll(Pageable pageable) {
		log.debug("Request to get all States");
		return stateRepository.findAll(pageable).map(stateMapper::toDto);
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<StateDTO> findOne(Long id) {
		log.debug("Request to get State : {}", id);
		return stateRepository.findById(id).map(stateMapper::toDto);
	}

	@Override
	@Auditable(action = ActionType.DELETE, module = com.grtship.core.annotation.Auditable.Module.STATE)
	public void delete(Long id) {
		log.debug("Request to delete State : {}", id);
		stateRepository.deleteById(id);
	}

	@Override
	public List<State> saveAll(Set<State> states) {
		log.debug("Request to save all States : {}", states);
		return stateRepository.saveAll(states);
	}

	@Override
	public List<StateDTO> getStatesByIds(List<Long> ids) {
		log.debug("Request to get all States assoiciated with ids: {}", ids);
		return stateRepository.findAllById(ids).stream().map(stateMapper::toDto).collect(Collectors.toList());
	}

	@Override
	public List<Long> getStatesIdsByCountryId(Long id) {
		log.debug("Request to get all States ids assoiciated with Country: {}", id);
		Country country = new Country();
		country.setId(id);
		return stateRepository.findStateIdsByCountry(country);
	}

	@Override
	public String getStateNameById(Long id) {
		log.debug("Request to get State Name by State id: {}", id);
		return stateRepository.findNameById(id);
	}

	@Override
	public List<StateDTO> getByCountryId(Long countryId) {
		Country country = new Country();
		country.setId(countryId);
		return stateRepository.findByCountry(country).stream().map(stateMapper::toDto).collect(Collectors.toList());
	}
}
