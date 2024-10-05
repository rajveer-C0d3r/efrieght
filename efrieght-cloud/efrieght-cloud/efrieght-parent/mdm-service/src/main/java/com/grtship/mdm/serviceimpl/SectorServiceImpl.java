package com.grtship.mdm.serviceimpl;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.grtship.core.annotation.AccessFilter;
import com.grtship.core.annotation.Auditable;
import com.grtship.core.annotation.Auditable.ActionType;
import com.grtship.core.annotation.Validate;
import com.grtship.core.dto.SectorDTO;
import com.grtship.mdm.domain.Sector;
import com.grtship.mdm.mapper.SectorMapper;
import com.grtship.mdm.repository.SectorRepository;
import com.grtship.mdm.service.CodeGeneratorService;
import com.grtship.mdm.service.CountryService;
import com.grtship.mdm.service.SectorService;

/**
 * Service Implementation for managing {@link Sector}.
 */
@Service
@Transactional
public class SectorServiceImpl implements SectorService {

	private static final String SECTOR = "Sector";

	private final Logger log = LoggerFactory.getLogger(SectorServiceImpl.class);

	@Autowired
	private SectorRepository sectorRepository;

	@Autowired
	private SectorMapper sectorMapper;

	@Autowired
	private CountryService countryService;

	@Autowired
	private CodeGeneratorService codeGeneratorService;

	@Override
	@Auditable(action = ActionType.SAVE, module = com.grtship.core.annotation.Auditable.Module.SECTOR)
	@Validate(validator = "sectorValidator",action = "save")
	public SectorDTO save(SectorDTO sectorDto) {
		log.debug("Request to save Sector : {}", sectorDto);
		if (sectorDto.getId() == null)
			sectorDto.setCode(codeGeneratorService.generateCode(SECTOR, null));
		Sector sector = sectorMapper.toEntity(sectorDto);
		sector = sectorRepository.save(sector);
		return sectorMapper.toDto(sector);
	}

	@Override
	@Transactional(readOnly = true)
	@AccessFilter(clientAccessFlag = true, companyAccessFlag = true, branchAccessFlag = false)
	public List<SectorDTO> findAll() {
		log.debug("Request to get all Sectors");
		return sectorRepository.findAll().stream().map(sectorMapper::toDto)
				.collect(Collectors.toCollection(LinkedList::new));
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<SectorDTO> findOne(Long id) {
		log.debug("Request to get Sector : {}", id);
		return sectorRepository.findById(id).map(sectorMapper::toDto);
	}

	@Override
	public Map<Long, String> findSectorNameByIdList(Set<Long> idList) {
		return sectorRepository.findAllById(idList).stream().filter(sector -> sector.getId() != null)
				.collect(Collectors.toMap(Sector::getId, Sector::getName));
	}

	@Override
	@Auditable(action = ActionType.DELETE, module = com.grtship.core.annotation.Auditable.Module.SECTOR)
	public void delete(Long id) {
		log.debug("Request to delete Sector : {}", id);
		sectorRepository.deleteById(id);
	}

	@Override
	public Optional<SectorDTO> getByCountryId(Long countryId) {
		Long sectorId = countryService.getSectorIdByCountryId(countryId);
		log.debug("Sector id fetched from country {}", sectorId);
		if (sectorId == null) {
			return Optional.empty();
		}
		return sectorRepository.findById(sectorId).map(sectorMapper::toDto);
	}
}
