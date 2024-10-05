package com.grtship.mdm.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.grtship.mdm.domain.EntityGroup;
import com.grtship.mdm.domain.ExternalEntity;

/**
 * Spring Data  repository for the EntityGroups entity.
 */
@Repository
public interface EntityGroupRepository extends JpaRepository<EntityGroup, Long>,JpaSpecificationExecutor<EntityGroup> {
	
	@Query("SELECT COUNT(e) FROM EntityGroup e")
	Long countEntityGroup();

	ExternalEntity findByCode(String string);

	@Query("Select e from EntityGroup e where e.name=?1")
	EntityGroup getByName(String name);

	List<EntityGroup> findByCodeOrName(String code,String name);
}
