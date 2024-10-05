package com.grtship.mdm.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.grtship.core.enumeration.EntityType;
import com.grtship.mdm.domain.EntityBusinessType;
import com.grtship.mdm.domain.EntityBusinessTypeId;

/**
 * Spring Data  repository for the EntityBusinessDetails entity.
 */
@Repository
public interface EntityBusinessTypeRepository extends JpaRepository<EntityBusinessType, EntityBusinessTypeId> {

	List<EntityBusinessType> findByIdExternalEntityId(Long entityId);

	void deleteByIdEntityTypeAndIdExternalEntityId(EntityType entityType, Long entityId);
}
