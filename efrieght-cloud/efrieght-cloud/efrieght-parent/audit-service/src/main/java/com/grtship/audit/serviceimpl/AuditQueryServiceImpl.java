/**
 * 
 */
package com.grtship.audit.serviceimpl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.grtship.audit.criteria.AuditCriteria;
import com.grtship.audit.domain.AuditPropertyKey;
import com.grtship.audit.domain.SystemAudit;
import com.grtship.audit.domain.SystemAudit_;
import com.grtship.audit.mapper.SystemAuditMapper;
import com.grtship.audit.repository.SystemAuditRepository;
import com.grtship.audit.service.AuditQueryService;
import com.grtship.audit.util.AuditPropertyKeyHolder;
import com.grtship.core.annotation.AccessFilter;
import com.grtship.core.dto.SystemAuditDTO;
import com.grtship.core.dto.SystemAuditDataDto;

/**
 * @author Ajay
 *
 */
@Service
public class AuditQueryServiceImpl implements AuditQueryService {

	@Autowired
	private SystemAuditRepository auditRepository;

	@Autowired
	private SystemAuditMapper systemAuditMapper;

	@Autowired
	private AuditPropertyKeyHolder auditPropertyKeyHolder;

	private static final Logger log = LoggerFactory.getLogger(AuditQueryServiceImpl.class);

	@Transactional(readOnly = true)
	@Override
	@AccessFilter(clientAccessFlag = true, companyAccessFlag = false, branchAccessFlag = false)
	public Page<SystemAuditDTO> findByCriteria(AuditCriteria criteria, Pageable pageable) {
		log.debug("find by criteria : {}, page: {}", criteria, pageable);
		final Specification<SystemAudit> specification = createSpecification(criteria);
		Page<SystemAudit> systemAudit = auditRepository.findAll(specification, pageable);
		List<SystemAuditDTO> systemAuditDTOs = systemAuditMapper.toDto(systemAudit.getContent());
		return new PageImpl<>(setkeyForAuditData(criteria, systemAuditDTOs), pageable, systemAudit.getTotalElements());
	}

	private List<SystemAuditDTO> setkeyForAuditData(AuditCriteria criteria, List<SystemAuditDTO> systemAuditDTOs) {
		List<SystemAuditDTO> data = new ArrayList<>();
		if (ObjectUtils.isNotEmpty(criteria) && StringUtils.isNotEmpty(criteria.getReferenceType())
				&& CollectionUtils.isNotEmpty(systemAuditDTOs)) {
			List<AuditPropertyKey> auditPropertyKeyByReference = auditPropertyKeyHolder
					.getAuditPropertyKeyByReference(criteria.getReferenceType());
			data = SetPropertyKeyForData(systemAuditDTOs, auditPropertyKeyByReference);

		}
		return data;
	}

	private List<SystemAuditDTO> SetPropertyKeyForData(List<SystemAuditDTO> systemAuditDTOs,
			List<AuditPropertyKey> auditPropertyKeyByReference) {
		getpropertyName(systemAuditDTOs, auditPropertyKeyByReference);
		return systemAuditDTOs;
	}

	private void getpropertyName(List<SystemAuditDTO> systemAuditDTOs,
			List<AuditPropertyKey> auditPropertyKeyByReference) {
		if (CollectionUtils.isNotEmpty(systemAuditDTOs)) {
			for (SystemAuditDTO systemAuditDTO : systemAuditDTOs) {
				if (ObjectUtils.isNotEmpty(systemAuditDTO)
						&& CollectionUtils.isNotEmpty(systemAuditDTO.getAuditData())) {
					for (SystemAuditDataDto auditDataDto : systemAuditDTO.getAuditData()) {
						if (StringUtils.isNotEmpty(auditDataDto.getPropertyName())) {
							checkFieldPropetyNameFromData(auditPropertyKeyByReference, auditDataDto);
						}
					}
				}
			}
		}
	}

	private void checkFieldPropetyNameFromData(List<AuditPropertyKey> auditPropertyKeyByReference,
			SystemAuditDataDto auditDataDto) {
		if (CollectionUtils.isNotEmpty(auditPropertyKeyByReference)) {
			for (AuditPropertyKey auditPropertyKey : auditPropertyKeyByReference) {
				if (StringUtils.isNotEmpty(auditPropertyKey.getPropertyName())) {
					checkFieldIsPresent(auditDataDto, auditPropertyKey);
				}
			}
		}
	}

	private void checkFieldIsPresent(SystemAuditDataDto auditDataDto, AuditPropertyKey auditPropertyKey) {
		if (ObjectUtils.isNotEmpty(auditDataDto) && ObjectUtils.isNotEmpty(auditPropertyKey)) {
			if (StringUtils.isNotEmpty(auditDataDto.getPropertyName())
					&& StringUtils.isNotEmpty(auditPropertyKey.getPropertyName())) {
				if (auditDataDto.getPropertyName().equalsIgnoreCase(auditPropertyKey.getPropertyName())) {
					auditDataDto.setPropertyName(auditPropertyKey.getMappingPropertyName());
				}
			}
		}
	}

	private Specification<SystemAudit> createSpecification(AuditCriteria criteria) {
		Specification<SystemAudit> specification = Specification.where(null);
		if (criteria != null) {
			if (criteria.getReferenceId() != null && criteria.getReferenceId() > 0L) {
				specification = specification.and(getReferenceIdSpec(criteria.getReferenceId()));
			}
			if (StringUtils.isNotEmpty(criteria.getReferenceType())) {
				specification = specification.and(getReferenceTypeSpec(criteria.getReferenceType()));
			}
			if (null != criteria.getStartDate() && null != criteria.getEndDate()) {
				specification = specification.and(getBetweenDateSpec(criteria.getStartDate(), criteria.getEndDate()));
			}
		}
		return specification;
	}

	private Specification<SystemAudit> getBetweenDateSpec(LocalDate startDate, LocalDate endDate) {
		return (null != startDate && null != endDate) ? (root, query, criteriaBuilder) -> criteriaBuilder
				.between(root.get(SystemAudit_.CREATED_ON_DATE), startDate, endDate) : Specification.where(null);
	}

	private Specification<SystemAudit> getReferenceTypeSpec(String referenceType) {
		return (!StringUtils.isBlank(referenceType)) ? (root, query, criteriaBuilder) -> criteriaBuilder
				.like(root.get(SystemAudit_.REFERENCE_TYPE), "%" + referenceType + "%") : Specification.where(null);
	}

	private Specification<SystemAudit> getReferenceIdSpec(Long referenceId) {
		return (referenceId != null)
				? (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(SystemAudit_.REFERENCE_ID),
						referenceId)
				: Specification.where(null);
	}

}
