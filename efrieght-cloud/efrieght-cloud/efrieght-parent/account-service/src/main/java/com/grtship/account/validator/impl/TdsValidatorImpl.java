package com.grtship.account.validator.impl;

import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.grtship.account.repository.TdsRepository;
import com.grtship.account.validator.TdsValidator;
import com.grtship.common.exception.InvalidDataException;
import com.grtship.core.constant.ErrorCode;
import com.grtship.core.dto.DeactivationReactivationDto;
import com.grtship.core.dto.TdsDTO;
import com.grtship.core.dto.TdsRateDTO;
import com.grtship.core.enumeration.TdsRateStatus;

@Component
public class TdsValidatorImpl implements TdsValidator {

	private static final String THE_CALCULATED_TDS_IS_WRONG = "The calculated tds is wrong.";
	private static final String IF_EFFECTIVE_TO_DATE_IS_SMALLER_THAN_CURRENT_DATE_THEN_TDS_RATE_STATUS_MUST_BE_INACTIVE = "If 'Effective To' Date is smaller than current date then tds rate status must be 'Inactive'.";
	private static final String IF_EFFECTIVE_FROM_DATE_IS_GREATER_THAN_CURRENT_DATE_THEN_TDS_RATE_STATUS_MUST_BE_UPCOMING = "If 'Effective From' date is greater than current date then tds rate status must be 'Upcoming'.";
	private static final String IF_EFFECTIVE_FROM_DATE_IS_LESS_THAN_OR_EQUAL_TO_CURRENT_DATE_AND_EFFECTIVE_TO_DATE_IS_GREATER_THAN_OR_EQUAL_TO_CURRENT_DATE_OR_IS_BLANK_THEN_DISPLAY_TDS_RATE_STATUS_ACTIVE = "If 'Effective from' date is less than or equal to current date and ‘Effective To’ date is greater than or equal to current date or is blank then display tds rate status Active.";
	private static final String AT_A_TIME_ONLY_ONE_TDS_RATE_WILL_BE_ACTIVE = "At a time only one tds rate will be active.";
	private static final String SELECTED_LEDGER_IS_ALREADY_LINKED_WITH_OTHER_TDS_PLEASE_SELECT_OTHER_LEDGER = "Selected Ledger is already linked with other Tds, Please select other Ledger.";
	private static final String TDS_CODE_ALREADY_EXISTS_ENTER_ANOTHER_TDS_CODE = "Tds Code Already Exists, Enter Another Tds Code";
	private static final String WEF_DATE_CAN_T_BE_PAST_DATE = "WEF Date can't be past Date..!!";

	@Autowired
	private TdsRepository tdsRepository;

	@Override
	public void saveValidations(TdsDTO tdsDto) {
		validateTdsCode(tdsDto);
		validateLedger(tdsDto);
		validateTdsRates(tdsDto);
	}

	private void validateTdsCode(TdsDTO tdsDto) {
		if (tdsDto.getId() == null) {
			if (tdsRepository.findByCode(tdsDto.getCode()) != null) {
				throw new InvalidDataException(ErrorCode.INVALID_DATA_ERROR,TDS_CODE_ALREADY_EXISTS_ENTER_ANOTHER_TDS_CODE);
			}
		} else {
			if (tdsRepository.findByCodeAndIdNot(tdsDto.getCode(), tdsDto.getId()) != null) {
				throw new InvalidDataException(ErrorCode.INVALID_DATA_ERROR,TDS_CODE_ALREADY_EXISTS_ENTER_ANOTHER_TDS_CODE);
			}
		}
	}

	private void validateLedger(TdsDTO tdsDto) {
		if (tdsDto.getId() == null) {
			if (tdsRepository.findByLedgerId(tdsDto.getLedgerId()) != null) {
				throw new InvalidDataException(ErrorCode.INVALID_DATA_ERROR,
						SELECTED_LEDGER_IS_ALREADY_LINKED_WITH_OTHER_TDS_PLEASE_SELECT_OTHER_LEDGER);
			}
		} else {
			if (tdsRepository.findByLedgerIdAndIdNot(tdsDto.getLedgerId(), tdsDto.getId()) != null) {
				throw new InvalidDataException(ErrorCode.INVALID_DATA_ERROR,
						SELECTED_LEDGER_IS_ALREADY_LINKED_WITH_OTHER_TDS_PLEASE_SELECT_OTHER_LEDGER);
			}
		}
	}

	private void validateTdsRates(TdsDTO tdsDto) {
		if (!CollectionUtils.isEmpty(tdsDto.getTdsRates())) {
			Set<TdsRateDTO> tdsRates = tdsDto.getTdsRates();
			Integer count = 0;
			for (TdsRateDTO tdsRate : tdsRates) {
				validateTdsRateStatus(tdsRate);
				validateCalculatedTds(tdsRate);
				if (tdsRate.getStatus().equals(TdsRateStatus.ACTIVE)) {
					count++;
				}
			}
			if (count > 1) {
				throw new InvalidDataException(ErrorCode.INVALID_DATA_ERROR,AT_A_TIME_ONLY_ONE_TDS_RATE_WILL_BE_ACTIVE);
			}
		}
	}

	private void validateCalculatedTds(TdsRateDTO tdsRate) {
		Double basic = 0d;
		Double surcharge = 0d;
		Double cess = 0d;
		if (tdsRate.getBasicRate() != null) {
			basic = tdsRate.getBasicRate();
		}
		if (tdsRate.getSurcharge() != null) {
			surcharge = tdsRate.getSurcharge();
		}
		if (tdsRate.getCess() != null) {
			cess = tdsRate.getCess();
		}
		Double calculatedTds = basic + (basic * (surcharge / 100)) + (basic * (cess / 100));
		if (tdsRate.getTdsPercentage() != null && !calculatedTds.equals(tdsRate.getTdsPercentage())) {
			throw new InvalidDataException(ErrorCode.INVALID_DATA_ERROR,THE_CALCULATED_TDS_IS_WRONG);
		}
	}

	private void validateTdsRateStatus(TdsRateDTO tdsRate) {
		if ((tdsRate.getEffectiveFrom().isBefore(LocalDate.now())
				|| tdsRate.getEffectiveFrom().isEqual(LocalDate.now()))
				&& (tdsRate.getEffectiveTo() == null || (tdsRate.getEffectiveTo().isAfter(LocalDate.now())
						|| tdsRate.getEffectiveTo().isEqual(LocalDate.now())))
				&& !tdsRate.getStatus().equals(TdsRateStatus.ACTIVE)) {
			throw new InvalidDataException(ErrorCode.INVALID_DATA_ERROR,
					IF_EFFECTIVE_FROM_DATE_IS_LESS_THAN_OR_EQUAL_TO_CURRENT_DATE_AND_EFFECTIVE_TO_DATE_IS_GREATER_THAN_OR_EQUAL_TO_CURRENT_DATE_OR_IS_BLANK_THEN_DISPLAY_TDS_RATE_STATUS_ACTIVE);
		} else if (tdsRate.getEffectiveFrom().isAfter(LocalDate.now())
				&& !tdsRate.getStatus().equals(TdsRateStatus.UPCOMING)) {
			throw new InvalidDataException(ErrorCode.INVALID_DATA_ERROR,
					IF_EFFECTIVE_FROM_DATE_IS_GREATER_THAN_CURRENT_DATE_THEN_TDS_RATE_STATUS_MUST_BE_UPCOMING);
		} else if ((tdsRate.getEffectiveTo() != null && tdsRate.getEffectiveTo().isBefore(LocalDate.now()))
				&& (tdsRate.getStatus() != null && !tdsRate.getStatus().equals(TdsRateStatus.INACTIVE))) {
			throw new InvalidDataException(ErrorCode.INVALID_DATA_ERROR,
					IF_EFFECTIVE_TO_DATE_IS_SMALLER_THAN_CURRENT_DATE_THEN_TDS_RATE_STATUS_MUST_BE_INACTIVE);
		}

	}

	@Override
	public void deactivateValidations(DeactivationReactivationDto deactivateDto) {
		if (deactivateDto.getDeactivationWefDate().truncatedTo(ChronoUnit.DAYS)
				.isBefore(Instant.now().truncatedTo(ChronoUnit.DAYS)))
			throw new InvalidDataException(ErrorCode.INVALID_DATA_ERROR,WEF_DATE_CAN_T_BE_PAST_DATE);
	}

}
