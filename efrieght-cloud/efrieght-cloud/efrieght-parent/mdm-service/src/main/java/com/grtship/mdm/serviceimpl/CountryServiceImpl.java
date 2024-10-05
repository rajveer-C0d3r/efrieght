package com.grtship.mdm.serviceimpl;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.grtship.common.exception.InvalidDataException;
import com.grtship.core.annotation.AccessFilter;
import com.grtship.core.annotation.Auditable;
import com.grtship.core.annotation.Auditable.ActionType;
import com.grtship.core.annotation.Validate;
import com.grtship.core.constant.ErrorCode;
import com.grtship.core.dto.CountryDTO;
import com.grtship.core.dto.DocumentDTO;
import com.grtship.core.dto.KeyLabelBeanDTO;
import com.grtship.core.dto.ObjectAliasDTO;
import com.grtship.core.dto.StateDTO;
import com.grtship.core.enumeration.DomainStatus;
import com.grtship.core.enumeration.GstVatType;
import com.grtship.core.filter.LongFilter;
import com.grtship.mdm.criteria.CountryCriteria;
import com.grtship.mdm.domain.Country;
import com.grtship.mdm.domain.Document;
import com.grtship.mdm.domain.ObjectAlias;
import com.grtship.mdm.domain.State;
import com.grtship.mdm.mapper.CountryMapper;
import com.grtship.mdm.mapper.DocumentMapper;
import com.grtship.mdm.mapper.ObjectAliasMapper;
import com.grtship.mdm.mapper.StateMapper;
import com.grtship.mdm.repository.CountryRepository;
import com.grtship.mdm.service.CodeGeneratorService;
import com.grtship.mdm.service.CountryService;
import com.grtship.mdm.service.DocumentService;
import com.grtship.mdm.service.ObjectAliasService;
import com.grtship.mdm.service.StateService;
import com.grtship.mdm.validator.CountryValidator;

/**
 * Service Implementation for managing {@link Country}.
 */
@Service
@Transactional
public class CountryServiceImpl implements CountryService {

	private static final String DOCUMENT = "Document";

	private static final String COUNTRY = "Country";

	private static final String REQUEST_IS_ALREADY_SUBMITTED_FOR_APPROVAL_YOU_CAN_T_UPDATE_ANYTHING = "Request is already submitted for approval. You can.t update anything.";

	private final Logger log = LoggerFactory.getLogger(CountryServiceImpl.class);

	@Autowired
	private CountryRepository countryRepository;

	@Autowired
	private StateService stateService;

	@Autowired
	private DocumentQueryServiceImpl documentQueryService;
	@Autowired
	private DocumentService documentService;

	@Autowired
	private CountryMapper countryMapper;

	@Autowired
	private DocumentMapper documentMapper;

	@Autowired
	private StateMapper stateMapper;

	@Autowired
	private CodeGeneratorService codeGeneratorService;

	@Autowired
	private ObjectAliasMapper aliasMapper;

	@Autowired
	private ObjectAliasService aliasService;

	@Autowired
	private CountryFilterServiceImpl countryFilterService;

	@Autowired
	private CountryValidator countryValidator;

	@Override
	@Auditable(action = ActionType.SAVE, module = com.grtship.core.annotation.Auditable.Module.COUNTRY)
	@Validate(validator = "countryValidator", action = "save")
	public CountryDTO save(CountryDTO countryDto) {
		log.debug("Request to save Country : {}", countryDto);
		Country country = countryMapper.toEntity(countryDto);
//		countryValidator.saveValidation(countryDto);
		if (countryDto.getId() != null) {
			updateChildList(countryDto);
			if (countryDto.getSubmittedForApproval() != null
					&& countryDto.getSubmittedForApproval().equals(Boolean.TRUE))
				throw new InvalidDataException(ErrorCode.INVALID_DATA_ERROR,
						REQUEST_IS_ALREADY_SUBMITTED_FOR_APPROVAL_YOU_CAN_T_UPDATE_ANYTHING);
			country.setSubmittedForApproval(true);
		} else {
			country.setSubmittedForApproval(true);
			country.setStatus(DomainStatus.PENDING);
			country.setActiveFlag(false);
		}
		Country createdCountry = countryRepository.save(country);
		saveCountryAlias(countryDto, createdCountry);
		saveDocuments(countryDto, createdCountry);
		saveStates(countryDto, createdCountry);
		return countryMapper.toDto(country);
	}

	private void saveStates(CountryDTO countryDto, Country createdCountry) {
		if (!CollectionUtils.isEmpty(countryDto.getStates())) {
			Set<State> states = new HashSet<>();
			countryDto.getStates().stream().filter(
					stateDto -> !StringUtils.isBlank(stateDto.getCode()) && !StringUtils.isBlank(stateDto.getName()))
					.forEach(stateDto -> {
						State state = stateMapper.toEntity(stateDto);
						state.setCountry(createdCountry);
						states.add(state);
					});
			stateService.saveAll(states);
		}
	}

	private void saveCountryAlias(CountryDTO countryDto, Country createdCountry) {
		if (!CollectionUtils.isEmpty(countryDto.getAliases())) {
			Set<ObjectAlias> aliase = new HashSet<>();
			countryDto.getAliases().stream().filter(aliasDto -> !StringUtils.isBlank(aliasDto.getName()))
					.forEach(aliasDTO -> {
						ObjectAlias alias = aliasMapper.toEntity(aliasDTO);
						alias.setReferenceId(createdCountry.getId());
						alias.setReferenceName(COUNTRY);
						aliase.add(alias);
					});
			aliasService.saveAll(aliase);
		}
	}

	private void saveDocuments(CountryDTO countryDto, Country createdCountry) {
		if (!CollectionUtils.isEmpty(countryDto.getDocuments())) {
			Set<Document> documents = new HashSet<>();
			countryDto.getDocuments().stream()
					.filter(documentDto -> !StringUtils.isBlank(documentDto.getName()) && documentDto.getType() != null)
					.forEach(documentDto -> {
						Document document = documentMapper.toEntity(documentDto);
						document.setReferenceId(createdCountry.getId());
						document.setReferenceName(COUNTRY);
						document.setId(documentDto.getId());
						if (StringUtils.isBlank(document.getCode()))
							document.setCode(codeGeneratorService.generateCode(DOCUMENT, null));
						documents.add(document);
					});
			documentService.saveAll(documents);
		}
	}

	private void updateChildList(CountryDTO countryDto) {
		if (countryDto.getId() != null) {
			updateStateList(countryDto);
			updateAliasList(countryDto);
			updateDocumentList(countryDto);
		}
	}

	private void updateStateList(CountryDTO countryDto) {
		List<Long> savedStateIds = stateService.getStatesIdsByCountryId(countryDto.getId());
		List<Long> statesIds = countryDto.getStates().stream().filter(stateDTO -> stateDTO.getId() != null)
				.map(StateDTO::getId).collect(Collectors.toList());
		List<Long> statesIdsToDelete = savedStateIds.stream().filter(savedStateId -> !statesIds.contains(savedStateId))
				.collect(Collectors.toList());
		log.debug("statesIdTodelete : {}", statesIdsToDelete);
		statesIdsToDelete.forEach(id -> stateService.delete(id));
	}

	private void updateAliasList(CountryDTO countryDto) {
		Set<Long> savedAliasIdsSetForCountry = aliasService.getAliasIdByReferenceIdAndReferenceName(countryDto.getId(),
				COUNTRY);
		if (!CollectionUtils.isEmpty(countryDto.getAliases())) {
			Set<Long> aliasIdsToSaveOrUpdate = countryDto.getAliases().stream().filter(obj -> obj.getId() != null)
					.map(ObjectAliasDTO::getId).collect(Collectors.toSet());
			if (!CollectionUtils.isEmpty(aliasIdsToSaveOrUpdate)
					&& !CollectionUtils.isEmpty(savedAliasIdsSetForCountry))
				savedAliasIdsSetForCountry.removeAll(aliasIdsToSaveOrUpdate);
		}
		if (!CollectionUtils.isEmpty(savedAliasIdsSetForCountry)) {
			aliasService.deleteByReferenceNameAndIdIn(COUNTRY, savedAliasIdsSetForCountry);
		}
	}

	private void updateDocumentList(CountryDTO countryDto) {
		Set<Long> savedDocumentIds = documentQueryService.getIdByReferenceIdAndReferenceName(countryDto.getId(),
				COUNTRY);
		if (!CollectionUtils.isEmpty(countryDto.getDocuments())) {
			Set<Long> documentIdsToSaveOrUpdate = countryDto.getDocuments().stream()
					.filter(documentDto -> documentDto.getId() != null).map(DocumentDTO::getId)
					.collect(Collectors.toSet());
			if (!CollectionUtils.isEmpty(documentIdsToSaveOrUpdate) && !CollectionUtils.isEmpty(savedDocumentIds))
				savedDocumentIds.removeAll(documentIdsToSaveOrUpdate);
		}
		if (!CollectionUtils.isEmpty(savedDocumentIds)) {
			documentService.deleteByReferenceNameAndIdIn(COUNTRY, savedDocumentIds);
		}
	}

	@Override
	@Transactional(readOnly = true)
	@AccessFilter(allowAdminData = true, clientAccessFlag = true, companyAccessFlag = false, branchAccessFlag = false)
	public Page<CountryDTO> findAll(CountryCriteria countryCriteria, Pageable pageable) {
		log.debug("Request to get all Countries");
		return countryFilterService.findByCriteria(countryCriteria, pageable);
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<CountryDTO> findOne(Long id) {
		CountryCriteria countryCriteria = new CountryCriteria();
		LongFilter longFilter = new LongFilter();
		longFilter.setEquals(id);
		countryCriteria.setId(longFilter);

		List<CountryDTO> findByIdcountry = countryFilterService.findByCriteria(countryCriteria);
		if (!CollectionUtils.isEmpty(findByIdcountry)) {
			CountryDTO countryDto = countryFilterService.findByCriteria(countryCriteria).get(0);
			return Optional.of(countryDto);
		}
		return Optional.ofNullable(null);
	}

	@Override
	public String getCountryNameById(Long id) {
		log.debug("Request to get Country Name: {}", id);
		Optional<String> name = countryRepository.findById(id).filter(country -> country.getName() != null)
				.map(Country::getName);
		if (name.isPresent())
			return name.get();
		return null;
	}

	@Override
	public List<CountryDTO> getCountriesByIdList(Set<Long> idList) {
		List<Country> countries = countryRepository.findAllById(idList);
		return countries.stream().filter(obj -> obj.getId() != null).map(countryMapper::toDto)
				.collect(Collectors.toList());
	}

	@Override
	public Long getSectorIdByCountryId(Long countryId) {
		if (countryId == null) {
			return null;
		}
		return countryRepository.getSectorIdByCountryId(countryId);
	}

	@Override
	public Boolean isStateMandatoryForGivenCountry(Long countryId) {
		Optional<Boolean> isStateMandatory = countryRepository.findById(countryId)
				.filter(country -> country.getIsStateMandatory() != null).map(Country::getIsStateMandatory);
		if (isStateMandatory.isPresent())
			return isStateMandatory.get();
		return Boolean.FALSE;
	}

	@Override
	public KeyLabelBeanDTO getGstVatType(Long countryId) {
		Optional<GstVatType> gstType = countryRepository.findById(countryId)
				.filter(country -> country.getGstOrVatType() != null).map(Country::getGstOrVatType);
		if (gstType.isPresent()) {
			String key = gstType.get().name();
			String label = GstVatType.valueOf(key).toString();
			return new KeyLabelBeanDTO(key, label);
		}
		return new KeyLabelBeanDTO();
	}
}
