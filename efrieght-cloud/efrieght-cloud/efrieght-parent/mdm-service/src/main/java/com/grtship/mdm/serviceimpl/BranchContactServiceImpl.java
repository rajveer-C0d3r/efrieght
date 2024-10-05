package com.grtship.mdm.serviceimpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.grtship.core.annotation.Auditable;
import com.grtship.core.dto.BranchContactDTO;
import com.grtship.mdm.domain.BranchContact;
import com.grtship.mdm.mapper.BranchContactMapper;
import com.grtship.mdm.repository.BranchContactRepository;
import com.grtship.mdm.service.BranchContactService;

/**
 * Service Implementation for managing {@link BranchContact}.
 */
@Service
@Transactional
public class BranchContactServiceImpl implements BranchContactService {

	private final BranchContactRepository contactDetailsRepository;

	private final BranchContactMapper contactDetailsMapper;

	public BranchContactServiceImpl(BranchContactRepository contactDetailsRepository,
			BranchContactMapper contactDetailsMapper) {
		this.contactDetailsRepository = contactDetailsRepository;
		this.contactDetailsMapper = contactDetailsMapper;
	}

	@Override
	@Auditable
	public List<BranchContact> saveAll(List<BranchContact> contacts) {
		return contactDetailsRepository.saveAll(contacts);
	}

	@Override
	public List<BranchContactDTO> getBranchContactsByBranchId(List<Long> branchIdList) {
		return (!CollectionUtils.isEmpty(branchIdList))
				? contactDetailsMapper.toDto(contactDetailsRepository.findByEntityBranch_IdIn(branchIdList))
				: Collections.emptyList();
	}

	private Set<Long> getContactDetailsIdsByBranchId(Long branchId) {
		return contactDetailsRepository.getIdsByBranchId(branchId);
	}

	private void deleteByIdIn(Set<Long> savedContactDetailsIds) {
		if (!CollectionUtils.isEmpty(savedContactDetailsIds))
			contactDetailsRepository.deleteByIdIn(savedContactDetailsIds);
	}

	/*** used to save all branch contacts.... ***/
	@Override
	@Auditable
	public void saveAll(List<BranchContactDTO> contactDetailsDto, Long branchId) {
		if (!CollectionUtils.isEmpty(contactDetailsDto)) {
			List<BranchContact> contacts = new ArrayList<>();
			contactDetailsDto.forEach(contact -> {
				BranchContact branchContacts = contactDetailsMapper.toEntity(contact);
				branchContacts.setEntityBranchId(branchId);
				contacts.add(branchContacts);
			});
			saveAll(contacts);
		}
	}

	/*** used to delete data on edit... ***/
	@Override
	public void deleteBranchContactsOnUpdate(List<BranchContactDTO> contactDetailsDto, Long branchId) {
		Set<Long> savedContactDetailsIds = getContactDetailsIdsByBranchId(branchId);

		if (!CollectionUtils.isEmpty(contactDetailsDto)) {
			Set<Long> contatDetailsIdsToSaveOrUpdate = contactDetailsDto.stream().filter(obj -> obj.getId() != null)
					.map(BranchContactDTO::getId).collect(Collectors.toSet());
			if (!CollectionUtils.isEmpty(contatDetailsIdsToSaveOrUpdate))
				savedContactDetailsIds.removeAll(contatDetailsIdsToSaveOrUpdate);
		}
		if (!CollectionUtils.isEmpty(savedContactDetailsIds))
			deleteByIdIn(savedContactDetailsIds);
	}
}
