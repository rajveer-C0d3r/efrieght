package com.grtship.account.validator;

import java.time.LocalDate;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.grtship.account.criteria.LedgerCriteria;
import com.grtship.account.domain.Ledger;
import com.grtship.account.mapper.LedgerMapper;
import com.grtship.account.repository.LedgerRepository;
import com.grtship.account.service.LedgerQueryService;
import com.grtship.common.interfaces.ValidationError;
import com.grtship.common.interfaces.Validator;
import com.grtship.common.interfaces.impl.ObjectValidationError;
import com.grtship.core.annotation.AccessFilter;
import com.grtship.core.constant.ErrorCode;
import com.grtship.core.dto.DeactivationDTO;
import com.grtship.core.dto.LedgerCreationDTO;
import com.grtship.core.dto.ObjectAliasDTO;
import com.grtship.core.dto.ReactivationDTO;
import com.grtship.core.enumeration.ValidationErrorType;
import com.grtship.core.filter.LongFilter;
import com.grtship.core.filter.StringFilter;

@Component
public class LedgerValidator implements Validator<Object> {

	private static final String ALIAS_CAN_T_BE_DUPLICATE = "Alias can't be duplicate.";
	private static final String THIS_LEDGER_IS_ALREADY_ACTIVE = "This Ledger is Already Active";
	private static final String LEDGER_IS_ALREADY_DEACTIVATED = "Ledger is Already Deactivated.";
	private static final String DEACTIVATION_WEF_DATE_CAN_T_BE_PAST_DATE = "Deactivation WEF Date can't be past Date.";
	private static final String ALIAS_IS_REQUIRED = "Alias is required.";
	private static final String CODE_IS_ALREADY_PRESENT_FOR_ANOTHER_LEDGER = "Code is already present for another ledger.";
	private static final String NAME_IS_ALREADY_PRESENT_FOR_ANOTHER_LEDGER = "Name is already present for another ledger";
	private static final String COMPANY_IS_REQUIRED = "Company is required";
	private static final String REACTIVATION_WEF_DATE_CAN_T_BE_PAST_DATE = "Reactivation WEF Date can't be past Date.";
	private static final String YOU_ENTERED_DUPLICATE_ALIASES_ALIASES_MUST_BE_UNIQUE = "You Entered Duplicate Aliases, Aliases Must Be Unique.";

	@Autowired
	LedgerQueryService ledgerQueryService;
	
	@Autowired
	LedgerRepository ledgerRepository;
	
	@Autowired
	private LedgerMapper ledgerMapper;
	
	
	@Override
	public List<ValidationError> validate(Object object, String action) {
		if (action.equals("save")) {
			return saveValidations(ledgerMapper.toEntity((LedgerCreationDTO) object));
		}
		if (action.equals("update")) {
			return updateValidations(ledgerMapper.toEntity((LedgerCreationDTO) object));
		}
		if (action.equals("deactivate")) {
			return deactivateValidation((DeactivationDTO) object);
		}
		if (action.equals("reactivate")) {
			return reactivateValidations((ReactivationDTO) object);
		}
		return null;
	}


	/*** ledger save validations ***/
	public List<ValidationError> saveValidations(Ledger ledger) {
		List<ValidationError> errors=new LinkedList<>();
		addNonEmpty(errors,companyIdValidate(ledger));
		addNonEmpty(errors,nameValidateForSave(ledger));
		addNonEmpty(errors,codeValidateForSave(ledger));
		addNonEmpty(errors,aliasValidate(ledger.getAlias(), ledger.getId()));
		return errors;
	}


	@AccessFilter(clientAccessFlag = true, companyAccessFlag = true)
	private ValidationError aliasValidate(Set<ObjectAliasDTO> alias, Long ledgerId) {
		if (CollectionUtils.isEmpty(alias))
			return ObjectValidationError.builder().type(ValidationErrorType.ERROR)
					.errorCode(ErrorCode.INVALID_DATA_ERROR).message(ALIAS_IS_REQUIRED)
					.referenceId(ledgerId == null ? "" : String.valueOf(ledgerId)).build();

		if (!CollectionUtils.isEmpty(alias)) {
			List<String> aliasesToSave = alias.stream().filter(objectAliasDto -> objectAliasDto.getName() != null)
					.map(ObjectAliasDTO::getName).collect(Collectors.toList());
			if (!CollectionUtils.isEmpty(aliasesToSave)) {
				List<String> duplicateAliases = aliasesToSave.stream()
						.filter(name -> Collections.frequency(aliasesToSave, name) > 1).collect(Collectors.toList());
				if (!CollectionUtils.isEmpty(duplicateAliases)) {
					return ObjectValidationError.builder().type(ValidationErrorType.ERROR)
							.errorCode(ErrorCode.INVALID_DATA_ERROR)
							.message(YOU_ENTERED_DUPLICATE_ALIASES_ALIASES_MUST_BE_UNIQUE)
							.referenceId(ledgerId == null ? "" : String.valueOf(ledgerId)).build();
				}
			}
		}

		LedgerCriteria criteria = new LedgerCriteria();
		criteria.setAliasList(alias.stream().filter(obj -> obj.getName() != null).map(ObjectAliasDTO::getName)
				.collect(Collectors.toList()));
		if (ledgerId == null && ledgerQueryService.countByCriteria(criteria) > 0)
			return aliasValidate(ledgerId);

		if (ledgerId != null) {
			LongFilter idFilter = new LongFilter();
			idFilter.setNotEquals(ledgerId);
			criteria.setId(idFilter);
			if (ledgerQueryService.countByCriteria(criteria) > 0)
				return aliasValidate(ledgerId);
		}
		return null;
	}

	private ValidationError aliasValidate(Long ledgerId) {
		return ObjectValidationError.builder()
				.type(ValidationErrorType.ERROR)
				.errorCode(ErrorCode.INVALID_DATA_ERROR)
				.message(ALIAS_CAN_T_BE_DUPLICATE)
				.referenceId(ledgerId==null?"":String.valueOf(ledgerId))
				.build();
	}

	private ValidationError codeValidateForSave(Ledger ledger) {
		LedgerCriteria criteria = new LedgerCriteria();
		StringFilter codeFilter = new StringFilter();
		if(ObjectUtils.isNotEmpty(ledger.getCode())) {
			codeFilter.setEquals(ledger.getCode());
			criteria.setCode(codeFilter);	
			if (ledgerQueryService.countByCriteria(criteria) > 0)
				return ObjectValidationError.builder()
						.type(ValidationErrorType.ERROR)
						.errorCode(ErrorCode.INVALID_DATA_ERROR)
						.message(CODE_IS_ALREADY_PRESENT_FOR_ANOTHER_LEDGER)
						.referenceId(ledger.getId()==null?"":String.valueOf(ledger.getId()))
						.build();
		}
		return null;	
	}

	private ValidationError nameValidateForSave(Ledger ledger) {
		LedgerCriteria criteria = new LedgerCriteria();
		StringFilter nameFilter = new StringFilter();
		nameFilter.setEquals(ledger.getName());
		criteria.setName(nameFilter);
		if (ledgerQueryService.countByCriteria(criteria) > 0)
			return ObjectValidationError.builder()
					.type(ValidationErrorType.ERROR)
					.errorCode(ErrorCode.INVALID_DATA_ERROR)
					.message(NAME_IS_ALREADY_PRESENT_FOR_ANOTHER_LEDGER)
					.referenceId(ledger.getId()==null?"":String.valueOf(ledger.getId()))
					.build();
		return null;
	}
	
	private ValidationError companyIdValidate(Ledger ledger){
		if(ledger.getCompanyId()==0 || ledger.getCompanyId()==null) {
			return ObjectValidationError.builder()
					.type(ValidationErrorType.ERROR)
					.errorCode(ErrorCode.INVALID_DATA_ERROR)
					.message(COMPANY_IS_REQUIRED)
					.referenceId(ledger.getId()==null?"":String.valueOf(ledger.getId()))
					.build();
		}
		return null;
	}

	private ValidationError nameValidForUpdate(Ledger ledger) {
		LedgerCriteria criteria = new LedgerCriteria();
		StringFilter nameFilter = new StringFilter();
		LongFilter idFilter = new LongFilter();
		idFilter.setNotEquals(ledger.getId());
		nameFilter.setEquals(ledger.getName());
		criteria.setName(nameFilter);
		criteria.setId(idFilter);
		if (ledgerQueryService.countByCriteria(criteria) > 0)
			return ObjectValidationError.builder()
					.type(ValidationErrorType.ERROR)
					.errorCode(ErrorCode.INVALID_DATA_ERROR)
					.message(NAME_IS_ALREADY_PRESENT_FOR_ANOTHER_LEDGER)
					.referenceId(ledger.getId()==null?"":String.valueOf(ledger.getId()))
					.build();
		return null;
	}

	private ValidationError codeValidForUpdate(Ledger ledger) {
		LedgerCriteria criteria = new LedgerCriteria();
		StringFilter codeFilter = new StringFilter();
		LongFilter idFilter = new LongFilter();
		idFilter.setNotEquals(ledger.getId());
		codeFilter.setEquals(ledger.getCode());
		criteria.setCode(codeFilter);
		criteria.setId(idFilter);
		if (ledgerQueryService.countByCriteria(criteria) > 0)
			return ObjectValidationError.builder()
					.type(ValidationErrorType.ERROR)
					.errorCode(ErrorCode.INVALID_DATA_ERROR)
					.message(CODE_IS_ALREADY_PRESENT_FOR_ANOTHER_LEDGER)
					.referenceId(ledger.getId()==null?"":String.valueOf(ledger.getId()))
					.build();
		return null;
	}
	
	

	/*** validations on ledger update... **/
	public List<ValidationError> updateValidations(Ledger ledger) {
		List<ValidationError> errors=new LinkedList<>();
		addNonEmpty(errors,companyIdValidate(ledger));
		addNonEmpty(errors,nameValidForUpdate(ledger));
		addNonEmpty(errors,codeValidForUpdate(ledger));
		addNonEmpty(errors,aliasValidate(ledger.getAlias(), ledger.getId()));
		return errors;
	}

	/**
	 * ledger deactivate validation.....
	 */
	public List<ValidationError> deactivateValidation(@Valid DeactivationDTO deactivateDto) {
		List<ValidationError> errors = new LinkedList<ValidationError>();
		if (deactivateDto.getDeactivationWefDate() != null
				&& deactivateDto.getDeactivationWefDate().isBefore(LocalDate.now()))
			addNonEmpty(errors,
					ObjectValidationError.builder().type(ValidationErrorType.ERROR)
							.errorCode(ErrorCode.INVALID_DATA_ERROR).message(DEACTIVATION_WEF_DATE_CAN_T_BE_PAST_DATE)
							.referenceId(deactivateDto.getReferenceId() == null ? ""
									: String.valueOf(deactivateDto.getReferenceId()))
							.build());

		ledgerRepository.findById(deactivateDto.getReferenceId()).ifPresent(ledger -> {
			if (ledger.getDeactivateDtls() != null && ledger.getDeactivateDtls().getDeactivatedDate() != null)
				addNonEmpty(errors,
						ObjectValidationError.builder().type(ValidationErrorType.ERROR)
								.errorCode(ErrorCode.INVALID_DATA_ERROR).message(LEDGER_IS_ALREADY_DEACTIVATED)
								.referenceId(deactivateDto.getReferenceId() == null ? ""
										: String.valueOf(deactivateDto.getReferenceId()))
								.build());
		});
		return errors;
	}

	/**
	 * ledger reactivate validation.....
	 */
	public List<ValidationError> reactivateValidations(ReactivationDTO reactivationDto) {
		List<ValidationError> errors=new LinkedList<ValidationError>();
		if (reactivationDto.getReactivationWefDate() != null
				&& reactivationDto.getReactivationWefDate().isBefore(LocalDate.now()))
			addNonEmpty(errors,
					ObjectValidationError.builder().type(ValidationErrorType.ERROR)
							.errorCode(ErrorCode.INVALID_DATA_ERROR).message(REACTIVATION_WEF_DATE_CAN_T_BE_PAST_DATE)
							.referenceId(reactivationDto.getReferenceId() == null ? ""
									: String.valueOf(reactivationDto.getReferenceId()))
							.build());

		ledgerRepository.findById(reactivationDto.getReferenceId()).ifPresent(ledger -> {
			if (ledger.getDeactivateDtls() != null && ledger.getDeactivateDtls().getDeactivatedDate() == null)
				addNonEmpty(errors,
						ObjectValidationError.builder().type(ValidationErrorType.ERROR)
								.errorCode(ErrorCode.INVALID_DATA_ERROR).message(THIS_LEDGER_IS_ALREADY_ACTIVE)
								.referenceId(reactivationDto.getReferenceId() == null ? ""
										: String.valueOf(reactivationDto.getReferenceId()))
								.build());

		});
		return errors;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void addNonEmpty(List list, Object o) {
		if(null!=o){
			list.add(o);
		}
	}

}
