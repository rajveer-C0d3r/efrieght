package com.grtship.mdm.service;

import java.util.List;

import com.grtship.core.dto.ObjectAliasDTO;
import com.grtship.mdm.criteria.ObjectAliasCriteria;

public interface ObjectAliasQueryService {
	public List<ObjectAliasDTO> findByCriteria(ObjectAliasCriteria aliasCriteria);

	public List<ObjectAliasDTO> getListOfAliasByReferanceIdAndReferenceName(Long referenceId, String referenceName);

	List<ObjectAliasDTO> getListOfAliasByReferanceIdListAndReferenceName(List<Long> referenceIds, String referenceName);
}
