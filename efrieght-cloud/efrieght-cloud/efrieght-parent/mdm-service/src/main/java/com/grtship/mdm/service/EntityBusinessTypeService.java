package com.grtship.mdm.service;

import java.util.Set;

import com.grtship.mdm.domain.ExternalEntity;

/**
 * Service Interface for managing
 * {@link com.grt.efreight.domain.EntityBusinessType}.
 */
public interface EntityBusinessTypeService {

	Set<String> getEntityBusinessTypesByEntityId(Long entityId);

	void saveAll(Set<String> entityDetails, ExternalEntity savedEntity);

	void deleteEntityBusinessTypeOnUpdate(Set<String> entityDetails, Long entityId);// used to hard-delete data on edit
}
