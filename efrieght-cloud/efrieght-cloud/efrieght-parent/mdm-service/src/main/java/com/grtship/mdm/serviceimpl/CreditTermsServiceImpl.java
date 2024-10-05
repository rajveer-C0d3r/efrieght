package com.grtship.mdm.serviceimpl;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.grtship.core.annotation.Auditable;
import com.grtship.core.annotation.Auditable.ActionType;
import com.grtship.core.dto.CreditTermsDTO;
import com.grtship.mdm.domain.CreditTerms;
import com.grtship.mdm.mapper.CreditTermsMapper;
import com.grtship.mdm.repository.CreditTermsRepository;
import com.grtship.mdm.service.CreditTermsService;

/**
 * Service Implementation for managing {@link CreditTerms}.
 */
@Service
@Transactional
public class CreditTermsServiceImpl implements CreditTermsService {

	private final Logger log = LoggerFactory.getLogger(CreditTermsServiceImpl.class);

	private final CreditTermsRepository creditTermsRepository;

	private final CreditTermsMapper creditTermsMapper;

	public CreditTermsServiceImpl(CreditTermsRepository creditTermsRepository, CreditTermsMapper creditTermsMapper) {
		this.creditTermsRepository = creditTermsRepository;
		this.creditTermsMapper = creditTermsMapper;
	}

	@Override
	@Transactional
	@Auditable(action = ActionType.SAVE,module = com.grtship.core.annotation.Auditable.Module.CREDIT_TERMS)
	public CreditTermsDTO save(CreditTermsDTO creditTermsDTO) {
		log.debug("Request to save CreditTerms : {}", creditTermsDTO);
		CreditTerms creditTerms = creditTermsMapper.toEntity(creditTermsDTO);
		creditTerms = creditTermsRepository.save(creditTerms);
		return creditTermsMapper.toDto(creditTerms);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<CreditTermsDTO> findAll(Pageable pageable) {
		log.debug("Request to get all CreditTerms");
		return creditTermsRepository.findAll(pageable).map(creditTermsMapper::toDto);
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<CreditTermsDTO> findOne(Long id) {
		return creditTermsRepository.findById(id).map(creditTermsMapper::toDto);
	}

	@Override
	@Auditable(action = ActionType.DELETE,module = com.grtship.core.annotation.Auditable.Module.CREDIT_TERMS)
	public void delete(Long id) {
		creditTermsRepository.deleteById(id);
	}

	@Override
	@Auditable(action = ActionType.SAVE,module = com.grtship.core.annotation.Auditable.Module.CREDIT_TERMS)
	public List<CreditTerms> saveAll(List<CreditTerms> creditTermsList) {
		return creditTermsRepository.saveAll(creditTermsList);
	}

	@Override
	public Set<CreditTermsDTO> getCreditTermsByReferenceIdsAndReferenceName(List<Long> referenceIdList,
			String referenceName) {
		if (!CollectionUtils.isEmpty(referenceIdList))
			return creditTermsMapper
					.toDto(creditTermsRepository.findByReferenceNameAndReferenceIdIn(referenceName, referenceIdList));
		return Collections.emptySet();
	}

	@Override
	public Set<Long> getCreditTermsIdByReferenceIdAndReferenceName(Long referenceId, String referenceName) {
		return creditTermsRepository.findIdsByReferenceIdAndreferenceName(referenceId, referenceName);
	}

	@Override
	public void deleteByReferenceNameAndIdIn(String referenceName, Set<Long> savedCreditTermsIdsForentity) {
		if (!CollectionUtils.isEmpty(savedCreditTermsIdsForentity))
			creditTermsRepository.deleteByReferenceNameAndIdIn(referenceName, savedCreditTermsIdsForentity);
	}

	@Override
	public void saveAll(List<CreditTermsDTO> list, String referenceName, Long referenceId) {
		List<CreditTerms> creditTerms = creditTermsMapper.toEntity(list);
		if (!CollectionUtils.isEmpty(creditTerms)) {
			creditTerms.forEach(creditTerm -> {
				creditTerm.setReferenceId(referenceId);
				creditTerm.setReferenceName(referenceName);
			});
			saveAll(creditTerms);
		}
	}

	/*** this service is used to perform hard-delete on update... ***/
	@Override
	public void deleteCreditTermOnUpdate(List<CreditTermsDTO> creditTermsList, String referenceName, Long referenceId) {
		Set<Long> savedCreditTermsIdsForentity = getCreditTermsIdByReferenceIdAndReferenceName(referenceId,
				referenceName);
		if (!CollectionUtils.isEmpty(creditTermsList)) {
			Set<Long> creditTermsIdsToSaveOrUpdate = creditTermsList.stream().filter(obj -> obj.getId() != null)
					.map(CreditTermsDTO::getId).collect(Collectors.toSet());
			if (!CollectionUtils.isEmpty(creditTermsIdsToSaveOrUpdate))
				savedCreditTermsIdsForentity.removeAll(creditTermsIdsToSaveOrUpdate);
		}

		if (!CollectionUtils.isEmpty(savedCreditTermsIdsForentity)) {
			deleteByReferenceNameAndIdIn(referenceName, savedCreditTermsIdsForentity);
		}
	}
}
