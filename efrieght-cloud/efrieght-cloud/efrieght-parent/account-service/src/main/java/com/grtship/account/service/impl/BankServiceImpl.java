package com.grtship.account.service.impl;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.grtship.account.criteria.BankCriteria;
import com.grtship.account.domain.Bank;
import com.grtship.account.feignclient.MasterModule;
import com.grtship.account.mapper.BankMapper;
import com.grtship.account.repository.BankRepository;
import com.grtship.account.service.BankFilterService;
import com.grtship.account.service.BankService;
import com.grtship.account.validator.BankValidator;
import com.grtship.core.annotation.Auditable;
import com.grtship.core.annotation.Auditable.ActionType;
import com.grtship.core.annotation.Validate;
import com.grtship.core.dto.AddressDTO;
import com.grtship.core.dto.BankDTO;

/**
 * Service Implementation for managing {@link Bank}.
 */
@Service
@Transactional
public class BankServiceImpl implements BankService {

	private final Logger log = LoggerFactory.getLogger(BankServiceImpl.class);

	@Autowired
	private BankRepository bankRepository;

	@Autowired
	private BankMapper bankMapper;

	@Autowired
	private MasterModule masterModule;

	@Autowired
	private BankFilterService bankFilterService;

	@Autowired
	private BankValidator bankValidator;

	@Override
	@Auditable(action = ActionType.SAVE, module = com.grtship.core.annotation.Auditable.Module.BANK)
	@Validate(validator = "bankValidatorImpl",action = "save")
	public BankDTO save(BankDTO bankDto) {
		log.debug("Request to save Bank : {}", bankDto);
//		bankValidator.saveValidations(bankDto);
		AddressDTO address = bankDto.getAddress();
		AddressDTO addressDto = (bankDto.getId() != null) ? masterModule.updateAddress(address)
				: masterModule.saveAddress(address);
		Bank bank = bankMapper.toEntity(bankDto);
		bank.setAddressId(addressDto);
		bank = bankRepository.save(bank);
		BankDTO dto = bankMapper.toDto(bank);
		dto.setAddress(addressDto);
		return dto;
	}

	@Override
	@Transactional(readOnly = true)
	public Page<BankDTO> findAll(Pageable pageable) {
		log.debug("Request to get all Banks");
		return bankRepository.findAll(pageable).map(bankMapper::toDto);
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<BankDTO> findOne(Long id) {
		log.debug("Request to get Bank : {}", id);
		BankCriteria criteria = new BankCriteria();
		criteria.setId(id);
		List<BankDTO> banks = bankFilterService.findByCriteria(criteria);
		return (!CollectionUtils.isEmpty(banks)) ? Optional.of(banks.get(0)) : Optional.empty();
	}

	@Override
	@Auditable(action = ActionType.DELETE, module = com.grtship.core.annotation.Auditable.Module.BANK)
	public void delete(Long id) {
		log.debug("Request to delete Bank : {}", id);
		bankRepository.findById(id).filter(bank -> bank.getAddressId() != null).map(Bank::getAddressId)
				.ifPresent(addressId -> masterModule.deleteAddress(addressId));
		bankRepository.deleteById(id);
	}
}
