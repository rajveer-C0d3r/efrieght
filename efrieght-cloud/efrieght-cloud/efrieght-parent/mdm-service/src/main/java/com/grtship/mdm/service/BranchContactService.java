package com.grtship.mdm.service;

import java.util.List;

import com.grtship.core.dto.BranchContactDTO;
import com.grtship.mdm.domain.BranchContact;

/**
 * Service Interface for managing {@link com.grt.efreight.domain.BranchContact}.
 */
public interface BranchContactService {

	List<BranchContactDTO> getBranchContactsByBranchId(List<Long> branchIdList);

	void saveAll(List<BranchContactDTO> contactDetailsDto, Long branchId);

	void deleteBranchContactsOnUpdate(List<BranchContactDTO> contactDetailsDto, Long branchId);// used to delete data on edit

	List<BranchContact> saveAll(List<BranchContact> entity);
}
