package com.grtship.mdm.serviceimpl;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.grtship.common.exception.InvalidDataException;
import com.grtship.core.annotation.Auditable;
import com.grtship.core.annotation.Auditable.ActionType;
import com.grtship.core.annotation.Validate;
import com.grtship.core.constant.ErrorCode;
import com.grtship.core.constant.ReferenceNameConstant;
import com.grtship.core.dto.DestinationDTO;
import com.grtship.core.dto.ObjectAliasDTO;
import com.grtship.mdm.domain.Destination;
import com.grtship.mdm.mapper.DestinationMapper;
import com.grtship.mdm.repository.DestinationRepository;
import com.grtship.mdm.service.DestinationService;
import com.grtship.mdm.service.ObjectAliasService;

/**
 * Service Implementation for managing {@link Destination}.
 */
@Service
@Transactional
public class DestinationServiceImpl implements DestinationService {

	private static final String ENTITY_ID = "Entity Id";
	private static final String DESTINATION = "Destination";

	private final Logger log = LoggerFactory.getLogger(DestinationServiceImpl.class);

	@Autowired
	private DestinationRepository destinationRepository;

	@Autowired
	private ObjectAliasService aliasService;

	@Autowired
	private DestinationMapper destinationMapper;

	@Override
	@Auditable
	@Validate(validator="destinationValidator",action="save")
	public DestinationDTO save(DestinationDTO destinationDto) {
		log.debug("Request to save Destination : {}", destinationDto);
		return destinationMapper.toDto(saveDto(destinationDto));
	}
	
	public Destination saveDto(DestinationDTO destinationDto) {
		Destination destination = getMapper().toEntity(destinationDto);
		Destination createdDestination =  getRepository().save(destination);
		if (createdDestination != null) {
			if (!CollectionUtils.isEmpty(destinationDto.getAliases())) {
				aliasService.saveAll(destinationDto.getAliases(), createdDestination.getId(),
						ReferenceNameConstant.DESTINATION, destinationDto.getClientId(), destinationDto.getCompanyId(),
						destinationDto.getBranchId());
			}
		}
		return createdDestination;
	}

	private DestinationMapper getMapper() {
		return destinationMapper;
	}

	private DestinationRepository getRepository() {
		return destinationRepository;
	}	
	
	@Auditable(action = ActionType.UPDATE, module = com.grtship.core.annotation.Auditable.Module.DESTINATION)
	@Override
	@Validate(validator = "destinationValidator", action = "update")
	public DestinationDTO update(DestinationDTO destinationDto) {
		log.debug("Request to update Destination : {}", destinationDto);
		Optional<Destination> optionalGroup = destinationRepository.findById(destinationDto.getId());
		if (optionalGroup.isPresent()) {
			updateChildList(destinationDto);
			Destination destination = destinationMapper.toEntity(destinationDto);
			Destination createdDestination = destinationRepository.save(destination);
			if (!CollectionUtils.isEmpty(destinationDto.getAliases())) {
				aliasService.saveAll(destinationDto.getAliases(), createdDestination.getId(),
						ReferenceNameConstant.DESTINATION, destinationDto.getClientId(), destinationDto.getCompanyId(),
						destinationDto.getBranchId());
			}
			return destinationMapper.toDto(destination);
		} else {
			throw new InvalidDataException(ErrorCode.INVALID_DATA_ERROR, ENTITY_ID);
		}
	}
	
	// after completion of ELF-245 use alias service update method and remove this
	// code.
	private void updateChildList(DestinationDTO destinationDto) {
		Set<Long> savedAliasIdsSetForDestination = aliasService
				.getAliasIdByReferenceIdAndReferenceName(destinationDto.getId(), DESTINATION);
		if (!CollectionUtils.isEmpty(destinationDto.getAliases())) {
			List<Long> aliasIdsToSaveOrUpdate = destinationDto.getAliases().stream()
					.filter(objectAliasDto -> ObjectUtils.isNotEmpty(objectAliasDto.getId())).map(ObjectAliasDTO::getId)
					.collect(Collectors.toList());

			savedAliasIdsSetForDestination.removeAll(aliasIdsToSaveOrUpdate);
		}
		if (!CollectionUtils.isEmpty(savedAliasIdsSetForDestination)) {
			aliasService.deleteByReferenceNameAndIdIn(DESTINATION, savedAliasIdsSetForDestination);
		}
	}

	@Override
	@Auditable(action = ActionType.DELETE, module = com.grtship.core.annotation.Auditable.Module.DESIGNATION)
	public void delete(Long id) {
		log.debug("Request to delete Destination : {}", id);
		Set<Long> aliasIds = aliasService.getAliasIdByReferenceIdAndReferenceName(id,
				ReferenceNameConstant.DESTINATION);
		if (CollectionUtils.isEmpty(aliasIds))
			aliasService.deleteByReferenceNameAndIdIn(ReferenceNameConstant.DESTINATION, aliasIds);
		destinationRepository.deleteById(id);
	}
}
