package com.grtship.account.service.impl;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.grtship.account.domain.Tds;
import com.grtship.account.mapper.TdsMapper;
import com.grtship.account.repository.TdsRepository;
import com.grtship.account.service.TdsRateService;
import com.grtship.account.service.TdsService;
import com.grtship.account.validator.TdsValidator;
import com.grtship.common.exception.InvalidDataException;
import com.grtship.core.annotation.Auditable;
import com.grtship.core.annotation.Auditable.ActionType;
import com.grtship.core.constant.ErrorCode;
import com.grtship.core.dto.DeactivationReactivationDto;
import com.grtship.core.dto.TdsDTO;
import com.grtship.core.dto.TdsRateDTO;

/**
 * Service Implementation for managing {@link Tds}.
 */
@Service
@Transactional
public class TdsServiceImpl implements TdsService {

	private static final String TDS_NOT_FOUND_FOR_GIVEN_ID = "Tds not found for given id.";
	private final Logger log = LoggerFactory.getLogger(TdsServiceImpl.class);

	@Autowired
	private TdsRepository tdsRepository;

	@Autowired
	private TdsMapper tdsMapper;

	@Autowired
	private TdsValidator tdsValidator;

	@Autowired
	private TdsRateService tdsRateService;

	@Override
	@Auditable(action = ActionType.SAVE,module = com.grtship.core.annotation.Auditable.Module.TDS)
	public TdsDTO save(TdsDTO tdsDto) {
		log.debug("Request to save Tds : {}", tdsDto);
		tdsValidator.saveValidations(tdsDto);
		updateChildList(tdsDto);
		Tds tds = tdsMapper.toEntity(tdsDto);
		tds = tdsRepository.save(tds);
		return tdsMapper.toDto(tds);
	}

	private void updateChildList(TdsDTO tdsDto) {
		if (tdsDto.getId() == null) {
			if (!CollectionUtils.isEmpty(tdsDto.getTdsRates())) {
				tdsDto.getTdsRates().forEach(tdsRateDto -> tdsRateDto.setVersion(1));
			}
		} else {
			if (!CollectionUtils.isEmpty(tdsDto.getTdsRates())) {
				tdsDto.getTdsRates().forEach(tdsRateDto -> {
					if (tdsRateDto.getId() != null && tdsRateDto.getVersion() != null) {
						tdsRateService.findOne(tdsRateDto.getId())
								.ifPresent(tdsRate -> updateVersion(tdsRateDto, tdsRate));
					} else {
						tdsRateDto.setVersion(1);
					}
				});
			}
		}
	}

	private void updateVersion(TdsRateDTO tdsRateDto, TdsRateDTO tdsRate) {
		if ((!tdsRate.getEffectiveFrom().equals(tdsRateDto.getEffectiveFrom()))
				|| (!tdsRate.getTdsPercentage().equals(tdsRateDto.getTdsPercentage()))
				|| (!tdsRate.getBasicRate().equals(tdsRateDto.getBasicRate()))
				|| (tdsRate.getCess() != null && tdsRateDto.getCess() != null
						&& !tdsRate.getCess().equals(tdsRateDto.getCess()))
				|| (tdsRate.getCess() == null && tdsRateDto.getCess() != null)
				|| (tdsRate.getSurcharge() != null && tdsRateDto.getSurcharge() != null
						&& !tdsRate.getSurcharge().equals(tdsRateDto.getSurcharge()))
				|| (tdsRate.getSurcharge() == null && tdsRateDto.getSurcharge() != null)
				|| (tdsRate.getReason() != null && tdsRateDto.getReason() != null
						&& !tdsRate.getReason().equals(tdsRateDto.getReason()))
				|| (tdsRate.getReason() == null && tdsRateDto.getReason() != null)) {
			tdsRateDto.setVersion(tdsRateDto.getVersion() + 1);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<TdsDTO> findOne(Long id) {
		log.debug("Request to get Tds : {}", id);
		return tdsRepository.findById(id).map(tdsMapper::toDto);
	}

	@Override
	@Auditable(action = ActionType.DELETE,module = com.grtship.core.annotation.Auditable.Module.SHIPMENT_REFERENCE)
	public void delete(Long id) {
		log.debug("Request to delete Tds : {}", id);
		tdsRepository.deleteById(id);
	}

	/**
	 * service to process deactivate Tds.
	 */
	@Override
	@Auditable(action = ActionType.DEACTIVATE,module = com.grtship.core.annotation.Auditable.Module.SHIPMENT_REFERENCE)
	public TdsDTO deactivate(DeactivationReactivationDto deactivateDto) {
		tdsValidator.deactivateValidations(deactivateDto);
		Optional<Tds> tdsById = tdsRepository.findById(deactivateDto.getReferenceId());
		if (!tdsById.isPresent())
			throw new InvalidDataException(ErrorCode.INVALID_DATA_ERROR, TDS_NOT_FOUND_FOR_GIVEN_ID);
		Tds tds = tdsById.get();
		tds.setDeactivationReason(deactivateDto.getDeactivationReason());
		tds.setDeactivationWefDate(deactivateDto.getDeactivationWefDate());
		tds = tdsRepository.save(tds);
		return tdsMapper.toDto(tds);
	}
}
