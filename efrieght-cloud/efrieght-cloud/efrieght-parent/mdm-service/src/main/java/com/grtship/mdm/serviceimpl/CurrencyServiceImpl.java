package com.grtship.mdm.serviceimpl;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.grtship.core.annotation.Auditable;
import com.grtship.core.annotation.Auditable.ActionType;
import com.grtship.core.annotation.Validate;
import com.grtship.core.dto.BaseDTO;
import com.grtship.core.dto.CurrencyDTO;
import com.grtship.mdm.domain.Currency;
import com.grtship.mdm.mapper.CurrencyMapper;
import com.grtship.mdm.repository.CurrencyRepository;
import com.grtship.mdm.service.CurrencyService;

/**
 * Service Implementation for managing {@link Currency}.
 */
@Service
@Transactional
public class CurrencyServiceImpl implements CurrencyService {

	private static final String CURRENCY_NAME_ALREADY_EXISTS_ENTER_ANOTHER_CURRENCY_NAME = "Currency Name Already Exists, Enter Another Currency Name";

	private static final String CURRENCY_CODE_ALREADY_EXISTS_ENTER_ANOTHER_CURRENCY_CODE = "Currency Code Already Exists, Enter Another Currency Code";

	private final Logger log = LoggerFactory.getLogger(CurrencyServiceImpl.class);

	@Autowired
	private CurrencyRepository currencyRepository;

	@Autowired
	private CurrencyMapper currencyMapper;

	@Override
	@Auditable(action = ActionType.SAVE, module = com.grtship.core.annotation.Auditable.Module.CURRENCY)
	@Validate(validator = "currencyValidator", action = "save")
	public CurrencyDTO save(CurrencyDTO currencyDTO) {
		log.debug("Request to save Currency : {}", currencyDTO);
		Currency currency = currencyMapper.toEntity(currencyDTO);
		currency = currencyRepository.save(currency);
		return currencyMapper.toDto(currency);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<CurrencyDTO> findAll(Pageable pageable) {
		log.debug("Request to get all Currencies");
		return currencyRepository.findAll(pageable).map(currencyMapper::toDto);
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<CurrencyDTO> findOne(Long id) {
		log.debug("Request to get Currency : {}", id);
		return currencyRepository.findById(id).map(currencyMapper::toDto);
	}

	@Override
	public Map<Long, String> findCurrencyNameByIdList(Set<Long> idList) {
		return currencyRepository.findAllById(idList).stream().filter(currency -> currency.getId() != null)
				.collect(Collectors.toMap(Currency::getId, Currency::getName));
	}

	@Override
	@Auditable(action = ActionType.DELETE, module = com.grtship.core.annotation.Auditable.Module.CURRENCY)
	public void delete(Long id) {
		log.debug("Request to delete Currency : {}", id);
		currencyRepository.deleteById(id);
	}

	@Override
	public Map<Long, String> getCurrencyIdNameByIds(List<Long> currencyIdList) {
		List<BaseDTO> currencyIdNameList = currencyRepository.currencyRepository(currencyIdList);
		return currencyIdNameList.stream().filter(obj -> obj.getId() != null)
				.collect(Collectors.toMap(obj -> obj.getId(), obj -> obj.getName()));
	}

	@Override
	public Map<Long, CurrencyDTO> getAllCurrenciesIdList(List<Long> currencyIdList) {
		return currencyRepository.findAllById(currencyIdList).stream()
				.filter(currency -> currency != null && currency.getId() != null)
				.collect(Collectors.toMap(Currency::getId, currencyMapper::toDto));
	}
}
