package com.grtship.mdm.validator;

import java.time.LocalDate;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.grtship.common.exception.InvalidDataException;
import com.grtship.common.interfaces.ValidationError;
import com.grtship.common.interfaces.Validator;
import com.grtship.common.interfaces.impl.ObjectValidationError;
import com.grtship.core.constant.ErrorCode;
import com.grtship.core.dto.DeactivationDTO;
import com.grtship.core.dto.ExternalEntityDTO;
import com.grtship.core.dto.ExternalEntityRequestDTO;
import com.grtship.core.dto.ObjectAliasDTO;
import com.grtship.core.dto.ReactivationDTO;
import com.grtship.core.enumeration.DomainStatus;
import com.grtship.core.enumeration.EntityType;
import com.grtship.core.enumeration.TdsExemption;
import com.grtship.core.enumeration.ValidationErrorType;
import com.grtship.mdm.repository.ExternalEntityRepository;
import com.grtship.mdm.service.ObjectAliasService;

@Component
public class ExternalEntityValidator implements Validator<Object> {
	private static final String PAN_NO_MUST_NOT_BE_NULL = "Pan No Must Not be null.";

	private static final String DOMICILE_MUST_NOT_BE_NULL = "Domicile Must Not be null.";

	private static final String TDS_EXEMPTION_MUST_NOT_BE_NULL = "Tds Exemption Must Not be null.";

	private static final String COMPANY_TYPE_MUST_NOT_BE_NULL = "Company Type Must Not be null.";

	private static final String LANDLINE_NUMBER_MUST_NOT_BE_NULL = "Landline Number Must Not be null.";

	private static final String CELL_NUMBER_MUST_NOT_BE_NULL = "Cell Number Must Not be null.";

	private static final String KEY_PERSON_NAME_MUST_NOT_BE_NULL = "Key Person Name Must Not be null.";

	private static final String YOU_CANT_UPDATE_THE_ENTITY_BECAUSE_APPROVAL_FOR_THIS_ENTITY_IS_PENDING = "You Cant Update the Entity because approval for this Entity is Pending.";

	private static final String DEACTIVATE_WEF_DATE_CAN_T_BE_PAST_DATE = "Deactivate WEF Date Can't Be Past Date.";
	
	private static final String REACTIVATE_WEF_DATE_CAN_T_BE_PAST_DATE = "Reactivate WEF Date Can't Be Past Date.";

	private static final String ADDRESS_IS_MANDATORY = "Address is Mandatory.";

	private static final String IF_YOU_HAVE_SELECTED_ENTITY_AS_A_VENDOR_THEN_VENDOR_CREDIT_TERMS_ARE_MANDATORY = "If you have selected Entity as a Vendor then vendor credit terms are mandatory.";

	private static final String IF_YOU_HAVE_SELECTED_ENTITY_AS_A_CUSTOMER_THEN_CUSTOMER_CREDIT_TERMS_ARE_MANDATORY = "If you have selected Entity as a Customer then customer credit terms are mandatory.";

	private static final String PLEASE_SELECT_VALID_ENTITY_DETAILS = "please select valid Entity Details";

	private static final String IF_TDS_EXEMPTION_IS_TOTAL_TDS_EXEMPTION_THEN_TDS_EXEMPTION_WEF_DATE_TDS_EXEMPTION_VALID_UPTO_DATE_TDS_EXEMPTION_PERCENTAGE_ARE_MANDATORY = "If TdsExemption Is 'Total TDS Exemption' Then TdsExemption Wef Date & TdsExemption ValidUptoDate & TdsExemption Percentage are mandatory";

	private static final String IF_TDS_EXEMPTION_IS_TOTAL_TDS_EXEMPTION_THEN_TDS_EXEMPTION_WEF_DATE_TDS_EXEMPTION_VALID_UPTO_DATE_ARE_MANDATORY = "If TdsExemption Is 'Total TDS Exemption' Then TdsExemptionWefDate & TdsExemptionValidUptoDate are mandatory";

	private static final String FOR_ENTITY_CUSTOMER_OR_VENDOR_MUST_BE_CHECKED = "For Entity Customer Or Vendor Must Be Checked.";

	private static final String DOMESTIC_ENTITY = "Domestic Entity";

	private static final String REGISTERED = "Registered";

	private static final String IF_ENTITY_GST_TYPE_IS_REGISTERED_THEN_GST_NO_IS_MANDATORY = "If Entity GST Type is Registered then GST No is Mandatory";

	private static final String IF_DOMICILE_STATUS_IS_DOMICILE_ENTITY_THEN_ENTITY_GST_TYPE_IS_MANDATORY = "If Domicile Status is Domicile Entity Then Entity GST Type is Mandatory";

	private static final String VENDOR = "vendor";

	private static final String CUSTOMER = "customer";

	private static final String ENTITY_CODE_CAN_T_BE_DUPLICATE = "Entity code can't be duplicate";

	private static final String ENTITY_NAME_CAN_T_BE_DUPLICATE = "Entity Name can't be duplicate.";
	
	private static final String COMPANY_IS_REQUIRED="Company is required";
	
	private static final String ENTITY = "External Entity";
	
	private static final String YOU_ENTERED_DUPLICATE_ALIASES_ALIASES_MUST_BE_UNIQUE = "You Entered Duplicate Aliases, Aliases Must Be Unique";

	@Autowired
	private ExternalEntityRepository externalEntityRepository;
	
	@Autowired
	private ObjectAliasService aliasService;
	
	@Override
	public List<ValidationError> validate(Object obj, String action) {
		List<ValidationError> errors=new LinkedList<>();
	    if (action.equals("save") || action.equals("update")) {
		    saveValidate((ExternalEntityRequestDTO) obj, errors);
	    }
	    if (action.equals("update")) {
			validateUpdateEntity((ExternalEntityDTO) obj, errors);
	    }
		if (action.equals("deactivate")) {
			deactivateValidation((DeactivationDTO) obj,errors);
		}
		if (action.equals("reactivate")) {
			reactivateValidation((ReactivationDTO) obj,errors);
		}
		return errors;
	}

	/**
	 * validate entity save.
	 */
	public void saveValidate(ExternalEntityRequestDTO entityDto,List<ValidationError> errors) {
		addNonEmpty(errors,validateEntityAlias(entityDto));
		addNonEmpty(errors,validateCompanyId(entityDto));
		addNonEmpty(errors,validateEntityName(entityDto));
		addNonEmpty(errors,validateEntityCode(entityDto));
		if (entityDto.getStatus() != null && !entityDto.getStatus().equals(DomainStatus.DRAFT)) {
			validateDomain(entityDto,errors);
			addNonEmpty(errors,addressValidation(entityDto));
			addNonEmpty(errors, tdsExemptionValidation(entityDto));
			addNonEmpty(errors,entityBusinessTypeValidation(entityDto.getEntityDetails()));
			addNonEmpty(errors,creditTermsValidation(entityDto));
			taxDetailsValidation(entityDto,errors);
		}
	}

	private ValidationError addressValidation(ExternalEntityRequestDTO entityDto) {
		if (entityDto.getAddress() == null || entityDto.getAddress().getCountryId() == null
				|| entityDto.getAddress().getLocation() == null || entityDto.getAddress().getPincode() == null)
			return ObjectValidationError.builder()
					.type(ValidationErrorType.ERROR)
					.errorCode(ErrorCode.INVALID_DATA_ERROR)
					.message(ADDRESS_IS_MANDATORY)
					.referenceId(entityDto.getId()==null?"":String.valueOf(entityDto.getId()))
					.build();
		return null;
	}
	
	private ValidationError validateEntityAlias(ExternalEntityRequestDTO entityDto) {

		if (!CollectionUtils.isEmpty(entityDto.getExternalEntityAlias())) {
			List<String> aliasesToSave = entityDto.getExternalEntityAlias().stream()
					.filter(objectAliasDTO -> objectAliasDTO.getName() != null).map(ObjectAliasDTO::getName)
					.collect(Collectors.toList());
			if (!CollectionUtils.isEmpty(aliasesToSave)) {
				List<String> duplicateAliases = aliasesToSave.stream()
						.filter(name -> Collections.frequency(aliasesToSave, name) > 1).collect(Collectors.toList());
				if (!CollectionUtils.isEmpty(duplicateAliases)) {
					return ObjectValidationError.builder().type(ValidationErrorType.ERROR)
							.errorCode(ErrorCode.INVALID_DATA_ERROR)
							.message(YOU_ENTERED_DUPLICATE_ALIASES_ALIASES_MUST_BE_UNIQUE)
							.referenceId(entityDto.getId() == null ? "" : String.valueOf(entityDto.getId())).build();
				}
			}

		}

		if (entityDto.getId() == null) {
			Set<String> sets = entityDto.getExternalEntityAlias().stream().map(alias -> alias.getName())
					.collect(Collectors.toSet());
			return aliasService.checkForDuplicateAlias(sets, ENTITY, entityDto.getClientId(), entityDto.getCompanyId());
		} else {
			return aliasService.checkForDuplicateAlias(ENTITY, entityDto.getExternalEntityAlias(),
					entityDto.getClientId(), entityDto.getCompanyId());
		}
	}
	
	private ValidationError validateCompanyId(ExternalEntityRequestDTO entityDto) {
		if (entityDto.getCompanyId() == 0 || entityDto.getCompanyId() == null) {
			return ObjectValidationError.builder().type(ValidationErrorType.ERROR)
					.errorCode(ErrorCode.INVALID_DATA_ERROR).message(COMPANY_IS_REQUIRED)
					.referenceId(entityDto.getId() == null ? "" : String.valueOf(entityDto.getId())).build();
		}
		return null;
	}
	
	private void validateDomain(ExternalEntityRequestDTO entityDto, List<ValidationError> errors) {
		addNonEmpty(errors, keyPersonNameValidation(entityDto));
		addNonEmpty(errors, cellNumberValidation(entityDto));
		addNonEmpty(errors, landlineNumberValidation(entityDto));
		addNonEmpty(errors, companyTypeValidation(entityDto));
		addNonEmpty(errors, tdsValidation(entityDto));
		addNonEmpty(errors, domicileValidation(entityDto));
		addNonEmpty(errors, panNoValidation(entityDto));
	}

	private ValidationError panNoValidation(ExternalEntityRequestDTO entityDto) {
		if (entityDto.getPanNoTaxId() == null)
			return ObjectValidationError.builder().type(ValidationErrorType.ERROR)
					.errorCode(ErrorCode.INVALID_DATA_ERROR).message(PAN_NO_MUST_NOT_BE_NULL)
					.referenceId(entityDto.getId() == null ? "" : String.valueOf(entityDto.getId())).build();
		return null;
	}

	private ValidationError domicileValidation(ExternalEntityRequestDTO entityDto) {
		if (entityDto.getDomicile() == null)
			return ObjectValidationError.builder().type(ValidationErrorType.ERROR)
					.errorCode(ErrorCode.INVALID_DATA_ERROR).message(DOMICILE_MUST_NOT_BE_NULL)
					.referenceId(entityDto.getId() == null ? "" : String.valueOf(entityDto.getId())).build();
		return null;
	}

	private ValidationError tdsValidation(ExternalEntityRequestDTO entityDto) {
		if (entityDto.getTdsExemption() == null)
			return ObjectValidationError.builder().type(ValidationErrorType.ERROR)
					.errorCode(ErrorCode.INVALID_DATA_ERROR).message(TDS_EXEMPTION_MUST_NOT_BE_NULL)
					.referenceId(entityDto.getId() == null ? "" : String.valueOf(entityDto.getId())).build();
		return null;
	}

	private ValidationError companyTypeValidation(ExternalEntityRequestDTO entityDto) {
		if (entityDto.getCompanyType() == null)
			return ObjectValidationError.builder().type(ValidationErrorType.ERROR)
					.errorCode(ErrorCode.INVALID_DATA_ERROR).message(COMPANY_TYPE_MUST_NOT_BE_NULL)
					.referenceId(entityDto.getId() == null ? "" : String.valueOf(entityDto.getId())).build();
		return null;
	}

	private ValidationError landlineNumberValidation(ExternalEntityRequestDTO entityDto) {
		if (StringUtils.isEmpty(entityDto.getLandlineNo()))
			return ObjectValidationError.builder().type(ValidationErrorType.ERROR)
					.errorCode(ErrorCode.INVALID_DATA_ERROR).message(LANDLINE_NUMBER_MUST_NOT_BE_NULL)
					.referenceId(entityDto.getId() == null ? "" : String.valueOf(entityDto.getId())).build();
		return null;
	}

	private ValidationError cellNumberValidation(ExternalEntityRequestDTO entityDto) {
		if (StringUtils.isEmpty(entityDto.getCellNo()))
			return ObjectValidationError.builder().type(ValidationErrorType.ERROR)
					.errorCode(ErrorCode.INVALID_DATA_ERROR).message(CELL_NUMBER_MUST_NOT_BE_NULL)
					.referenceId(entityDto.getId() == null ? "" : String.valueOf(entityDto.getId())).build();
		return null;
	}

	private ValidationError keyPersonNameValidation(ExternalEntityRequestDTO entityDto) {
		if (StringUtils.isEmpty(entityDto.getKeyPersonName()))
			return ObjectValidationError.builder().type(ValidationErrorType.ERROR)
					.errorCode(ErrorCode.INVALID_DATA_ERROR).message(KEY_PERSON_NAME_MUST_NOT_BE_NULL)
					.referenceId(entityDto.getId() == null ? "" : String.valueOf(entityDto.getId())).build();
		return null;
	}

	private ValidationError validateEntityCode(ExternalEntityRequestDTO entityDto) {
		List<Long> entityIdsByCode = externalEntityRepository.getIdByCodeAndClientId(entityDto.getCode(),
				entityDto.getClientId());
		Boolean entityByCodeFlag = entityIdsByCode.stream()
				.filter(obj -> obj != null && entityDto.getId() != null && !(obj.equals(entityDto.getId())))
				.map(obj -> obj).findAny().isPresent();
		if ((entityDto.getId() == null && !CollectionUtils.isEmpty(entityIdsByCode))
				|| (entityDto.getId() != null && entityByCodeFlag.equals(Boolean.TRUE)))
			return ObjectValidationError.builder().type(ValidationErrorType.ERROR)
					.errorCode(ErrorCode.INVALID_DATA_ERROR).message(ENTITY_CODE_CAN_T_BE_DUPLICATE)
					.referenceId(entityDto.getId() == null ? "" : String.valueOf(entityDto.getId())).build();
		return null;

	}

	private ValidationError validateEntityName(ExternalEntityRequestDTO entityDto) {
		List<Long> entityIdsByName = externalEntityRepository.getIdByNameAndClientId(entityDto.getName(),
				entityDto.getClientId());
		Boolean entityByNameFlag = entityIdsByName.stream()
				.filter(obj -> obj != null && entityDto.getId() != null && !(obj.equals(entityDto.getId())))
				.map(obj -> obj).findAny().isPresent();
		if ((entityDto.getId() == null && !CollectionUtils.isEmpty(entityIdsByName))
				|| (entityDto.getId() != null && entityByNameFlag.equals(Boolean.TRUE)))
			return ObjectValidationError.builder().type(ValidationErrorType.ERROR)
					.errorCode(ErrorCode.INVALID_DATA_ERROR).message(ENTITY_NAME_CAN_T_BE_DUPLICATE)
					.referenceId(entityDto.getId() == null ? "" : String.valueOf(entityDto.getId())).build();
		return null;
		   
	}

	/** validate tdsExempation */
	private ValidationError tdsExemptionValidation(ExternalEntityRequestDTO entityDto) {
		if (entityDto.getTdsExemption() != null && entityDto.getTdsExemption().equals(TdsExemption.TOTAL)
				&& (entityDto.getTdsExemptionWefDate() == null || entityDto.getTdsExemptionValidUptoDate() == null))
			return ObjectValidationError.builder().type(ValidationErrorType.ERROR)
					.errorCode(ErrorCode.INVALID_DATA_ERROR)
					.message(
							IF_TDS_EXEMPTION_IS_TOTAL_TDS_EXEMPTION_THEN_TDS_EXEMPTION_WEF_DATE_TDS_EXEMPTION_VALID_UPTO_DATE_ARE_MANDATORY)
					.build();

		if (entityDto.getTdsExemption() != null && entityDto.getTdsExemption().equals(TdsExemption.PARTIAL)
				&& (entityDto.getTdsExemptionWefDate() == null || entityDto.getTdsExemptionValidUptoDate() == null
						|| entityDto.getTdsExemptionPercentage() == null))
			return ObjectValidationError.builder().type(ValidationErrorType.ERROR)
					.errorCode(ErrorCode.INVALID_DATA_ERROR)
					.message(
							IF_TDS_EXEMPTION_IS_TOTAL_TDS_EXEMPTION_THEN_TDS_EXEMPTION_WEF_DATE_TDS_EXEMPTION_VALID_UPTO_DATE_TDS_EXEMPTION_PERCENTAGE_ARE_MANDATORY)
					.build();
		return null;
	}

	/** validate entity business types... */
	private ValidationError entityBusinessTypeValidation(Set<String> entityDetails) {

		List<String> entityTypes = Stream.of(EntityType.values()).map(Enum::name).collect(Collectors.toList());

		if (!CollectionUtils.isEmpty(entityDetails)) {
			for (String entityType : entityDetails) {
				if (!entityTypes.contains(entityType) || (entityTypes.contains(entityType)
						&& (entityType.equalsIgnoreCase(CUSTOMER) || (entityType.equalsIgnoreCase(VENDOR)))))
					return ObjectValidationError.builder().type(ValidationErrorType.ERROR)
							.errorCode(ErrorCode.INVALID_DATA_ERROR).message(PLEASE_SELECT_VALID_ENTITY_DETAILS)
							.build();
			}
		}
		return null;
	}

	/** credit terms validations. **/
	private ValidationError creditTermsValidation(ExternalEntityRequestDTO entityDto) {

		if (entityDto.getCustomerFlag().equals(Boolean.FALSE) && entityDto.getVendorFlag().equals(Boolean.FALSE))
			return ObjectValidationError.builder().type(ValidationErrorType.ERROR)
					.errorCode(ErrorCode.INVALID_DATA_ERROR).message(FOR_ENTITY_CUSTOMER_OR_VENDOR_MUST_BE_CHECKED)
					.referenceId(entityDto.getId() == null ? "" : String.valueOf(entityDto.getId())).build();

		if (entityDto.getCustomerFlag().equals(Boolean.TRUE) && (ObjectUtils.isEmpty(entityDto.getCustomerEntityLevel())
				|| entityDto.getCustomerCreditAmount() == null || entityDto.getCustomerCreditDays() == null))
			return ObjectValidationError.builder().type(ValidationErrorType.ERROR)
					.errorCode(ErrorCode.INVALID_DATA_ERROR)
					.message(IF_YOU_HAVE_SELECTED_ENTITY_AS_A_CUSTOMER_THEN_CUSTOMER_CREDIT_TERMS_ARE_MANDATORY)
					.referenceId(entityDto.getId() == null ? "" : String.valueOf(entityDto.getId())).build();

		if (entityDto.getVendorFlag().equals(Boolean.TRUE) && (ObjectUtils.isEmpty(entityDto.getVendorEntityLevel())
				|| entityDto.getVendorCreditAmount() == null || entityDto.getVendorCreditDays() == null))
			return ObjectValidationError.builder().type(ValidationErrorType.ERROR)
					.errorCode(ErrorCode.INVALID_DATA_ERROR)
					.message(IF_YOU_HAVE_SELECTED_ENTITY_AS_A_VENDOR_THEN_VENDOR_CREDIT_TERMS_ARE_MANDATORY)
					.referenceId(entityDto.getId() == null ? "" : String.valueOf(entityDto.getId())).build();
		return null;
	}

	/** tax details validations **/
	private void taxDetailsValidation(ExternalEntityRequestDTO entityDto,List<ValidationError> errors) {
		addNonEmpty(errors,gstTypeValidation(entityDto));
        addNonEmpty(errors,gstNoValidation(entityDto));
	}

	private ValidationError gstNoValidation(ExternalEntityRequestDTO entityDto) {
		if (entityDto.getGstType() != null && entityDto.getGstType().getLabel().equals(REGISTERED)
				&& (StringUtils.isEmpty(entityDto.getGstNo())))
			return ObjectValidationError.builder().type(ValidationErrorType.ERROR)
					.errorCode(ErrorCode.INVALID_DATA_ERROR)
					.message(IF_ENTITY_GST_TYPE_IS_REGISTERED_THEN_GST_NO_IS_MANDATORY)
					.referenceId(entityDto.getId() == null ? "" : String.valueOf(entityDto.getId())).build();
		return null;
	}

	private ValidationError gstTypeValidation(ExternalEntityRequestDTO entityDto) {
		if (entityDto.getDomicile() != null && entityDto.getDomicile().getLabel().equals(DOMESTIC_ENTITY)
				&& entityDto.getGstType() == null)
			return ObjectValidationError.builder().type(ValidationErrorType.ERROR)
					.errorCode(ErrorCode.INVALID_DATA_ERROR)
					.message(IF_DOMICILE_STATUS_IS_DOMICILE_ENTITY_THEN_ENTITY_GST_TYPE_IS_MANDATORY)
					.referenceId(entityDto.getId() == null ? "" : String.valueOf(entityDto.getId())).build();
		return null;
	}

	public void deactivateValidation(DeactivationDTO deactivateDto, List<ValidationError> errors) {
		if (deactivateDto.getDeactivationWefDate() != null
				&& deactivateDto.getDeactivationWefDate().isBefore(LocalDate.now()))
			addNonEmpty(errors,
					ObjectValidationError.builder().type(ValidationErrorType.ERROR)
							.errorCode(ErrorCode.INVALID_DATA_ERROR).message(DEACTIVATE_WEF_DATE_CAN_T_BE_PAST_DATE)
							.referenceId(deactivateDto.getReferenceId() == null ? ""
									: String.valueOf(deactivateDto.getReferenceId()))
							.build());
	}
	
	public void reactivateValidation(ReactivationDTO reactivateDto, List<ValidationError> errors) {
		if (reactivateDto.getReactivationWefDate() != null
				&& reactivateDto.getReactivationWefDate().isBefore(LocalDate.now()))
			addNonEmpty(errors,
					ObjectValidationError.builder().type(ValidationErrorType.ERROR)
							.errorCode(ErrorCode.INVALID_DATA_ERROR).message(REACTIVATE_WEF_DATE_CAN_T_BE_PAST_DATE)
							.referenceId(reactivateDto.getReferenceId() == null ? ""
									: String.valueOf(reactivateDto.getReferenceId()))
							.build());
	}

	/***
	 * entity update validations...
	 **/
	public void validateUpdateEntity(ExternalEntityDTO entityDto, List<ValidationError> errors) {
		if (entityDto.getDeactivateDtls() != null && entityDto.getDeactivateDtls().getDeactivationWefDate() != null)
			addNonEmpty(errors,
					ObjectValidationError.builder().type(ValidationErrorType.ERROR)
							.errorCode(ErrorCode.INVALID_DATA_ERROR)
							.message(YOU_CANT_UPDATE_THE_ENTITY_BECAUSE_APPROVAL_FOR_THIS_ENTITY_IS_PENDING)
							.referenceId(entityDto.getId() == null ? "" : String.valueOf(entityDto.getId())).build());
		saveValidate(entityDto, errors);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void addNonEmpty(List list,Object o) {
		if(null!=o) {
			list.add(o);
		}
	}
}
