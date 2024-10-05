package com.grtship.account.service.impl;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.grtship.account.adaptor.MasterModuleAdapter;
import com.grtship.account.domain.DomainDeactivate;
import com.grtship.account.domain.DomainReactivate;
import com.grtship.account.domain.Ledger;
import com.grtship.account.mapper.LedgerMapper;
import com.grtship.account.repository.LedgerRepository;
import com.grtship.account.service.LedgerService;
import com.grtship.account.service.ObjectAliasService;
import com.grtship.account.validator.LedgerValidator;
import com.grtship.common.exception.InvalidDataException;
import com.grtship.core.annotation.Auditable;
import com.grtship.core.annotation.Auditable.ActionType;
import com.grtship.core.annotation.Validate;
import com.grtship.core.constant.ErrorCode;
import com.grtship.core.constant.ReferenceNameConstant;
import com.grtship.core.dto.DeactivationDTO;
import com.grtship.core.dto.LedgerCreationDTO;
import com.grtship.core.dto.LedgerDTO;
import com.grtship.core.dto.ObjectAliasDTO;
import com.grtship.core.dto.ReactivationDTO;

/**
 * Service Implementation for managing {@link Ledger}.
 */
@Service
@Transactional
public class LedgerServiceImpl implements LedgerService {

	private static final String LEDGER = "Ledger";

	private static final String LEDGER_NOT_FOUND_FOR_GIVEN_ID = "Ledger not found for given Id..!!";

	private final LedgerRepository ledgerRepository;

	private final LedgerMapper ledgerMapper;

	@Autowired
	private ObjectAliasService aliasService;

	@Autowired
	private LedgerValidator ledgerValidator;

	@Autowired
	private MasterModuleAdapter masterModuleAdapter;

	public LedgerServiceImpl(LedgerRepository ledgerRepository, LedgerMapper ledgerMapper) {
		this.ledgerRepository = ledgerRepository;
		this.ledgerMapper = ledgerMapper;
	}

	@Override
	@Transactional
	@Auditable(action = ActionType.SAVE, module = com.grtship.core.annotation.Auditable.Module.LEDGER)
	@Validate(validator = "ledgerValidator", action = "save")
	public LedgerDTO save(LedgerCreationDTO ledgerDto) {
		ledgerDto.setCode(masterModuleAdapter.generateCode(ReferenceNameConstant.LEDGER, null));
		Ledger ledger = ledgerMapper.toEntity(ledgerDto);
		Ledger savedLedger = ledgerRepository.save(ledger);
		aliasService.saveAll(ledgerDto.getAlias(), savedLedger.getId(), ReferenceNameConstant.LEDGER);
		return ledgerMapper.toDto(ledger);
	}

	@Override
	@Auditable(action = ActionType.UPDATE, module = com.grtship.core.annotation.Auditable.Module.LEDGER)
	@Validate(validator = "ledgerValidator", action = "update")
	public LedgerDTO update(@Valid LedgerDTO ledgerDto) {
		Ledger ledger = ledgerMapper.toEntity(ledgerDto);
		updateChildList(ledgerDto);
		Ledger savedLedger = ledgerRepository.save(ledger);
		aliasService.saveAll(ledgerDto.getAlias(), savedLedger.getId(), ReferenceNameConstant.LEDGER);
		return ledgerMapper.toDto(savedLedger);
	}

	/**
	 * updateChildList will do hard delete and update child lists
	 * 
	 */

	private void updateChildList(LedgerDTO ledgerDTO) {
		if (ledgerDTO.getId() != null) {
			Set<Long> savedAliasIdsSetForLedger = aliasService
					.getAliasIdByReferenceIdAndReferenceName(ledgerDTO.getId(), LEDGER);
			if (!CollectionUtils.isEmpty(ledgerDTO.getAlias())) {
				Set<Long> aliasIdsToSaveOrUpdate = ledgerDTO.getAlias().stream().filter(obj -> obj.getId() != null)
						.map(ObjectAliasDTO::getId).collect(Collectors.toSet());
				if (!CollectionUtils.isEmpty(aliasIdsToSaveOrUpdate))
					savedAliasIdsSetForLedger.removeAll(aliasIdsToSaveOrUpdate);
			}
			if (!CollectionUtils.isEmpty(savedAliasIdsSetForLedger)) {
				aliasService.deleteByReferenceNameAndIdIn(LEDGER, savedAliasIdsSetForLedger);
			}
		}
	}

	/**
	 * service to process deactivate ledger..
	 */
	@Override
	@Transactional
	// FIXME if ledger((any entity object) is Deactivated and user process
	// reactivation then after reactivation Approval set ledger deactivation fields
	// to null and vice versa..
	@Auditable(action = ActionType.DEACTIVATE, module = com.grtship.core.annotation.Auditable.Module.LEDGER)
	@Validate(validator = "ledgerValidator", action = "deactivate")
	public LedgerDTO deactivate(DeactivationDTO deactivateDto) {
		Optional<Ledger> ledgerById = ledgerRepository.findById(deactivateDto.getReferenceId());
		if (!ledgerById.isPresent())
			throw new InvalidDataException(ErrorCode.INVALID_DATA_ERROR, LEDGER_NOT_FOUND_FOR_GIVEN_ID);

		Ledger ledger = ledgerById.get();
		DomainDeactivate deactivate = new DomainDeactivate();
		deactivate.setReason(deactivateDto.getDeactivationReason());
		deactivate.setWefDate(deactivateDto.getDeactivationWefDate());
		ledger.setSubmittedForApproval(Boolean.TRUE);
		ledger.setDeactivateDtls(deactivate);
		return ledgerMapper.toDto(ledgerRepository.save(ledger));
	}

	@Override
	@Transactional
	// FIXME if ledger(any entity object) is Deactivated and user process
	// reactivation then after reactivation Approval set ledger deactivation fields
	// to null and vice versa..
	@Auditable(action = ActionType.REACTIVATE, module = com.grtship.core.annotation.Auditable.Module.LEDGER)
	@Validate(validator = "ledgerValidator", action = "reactivate")
	public LedgerDTO reactivate(ReactivationDTO reactivationDto) {
		Optional<Ledger> ledgerById = ledgerRepository.findById(reactivationDto.getReferenceId());
		if (!ledgerById.isPresent())
			throw new InvalidDataException(ErrorCode.INVALID_DATA_ERROR, LEDGER_NOT_FOUND_FOR_GIVEN_ID);

		Ledger ledger = ledgerById.get();
		DomainReactivate reactivate = new DomainReactivate();
		reactivate.setWefDate(reactivationDto.getReactivationWefDate());
		reactivate.setReason(reactivationDto.getReactivationReason());
		ledger.setSubmittedForApproval(Boolean.TRUE);// Submited For Reactivation Approval..
		ledger.setReactivateDtls(reactivate);
		return ledgerMapper.toDto(ledgerRepository.save(ledger));
	}
}
