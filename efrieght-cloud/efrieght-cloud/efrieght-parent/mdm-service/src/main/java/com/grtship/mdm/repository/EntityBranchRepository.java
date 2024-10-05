package com.grtship.mdm.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.grtship.mdm.domain.EntityBranch;
import com.grtship.mdm.domain.ExternalEntity;
import com.grtship.mdm.dto.EntityBranchMultiDropDownDTO;

/**
 * Spring Data  repository for the BranchDetails entity.
 */
@Repository
public interface EntityBranchRepository extends JpaRepository<EntityBranch, Long>,JpaSpecificationExecutor<EntityBranch> {
	
	@Query("SELECT COUNT(eb) FROM EntityBranch eb")
	Long countEntityBranch();

	ExternalEntity findByCode(String string);

	List<EntityBranch> findByExternalEntityId(Long id);

//	apply filter for particular client
	List<EntityBranch> findByName(String name);

	Page<EntityBranchMultiDropDownDTO> findPagedProjectedByExternalEntityId(Long entiyId,Pageable pageable);

	@Query("select eb.id from EntityBranch eb where eb.name = ?1 and eb.clientId = ?2")
	List<Long> getIdByNameAndClientId(String name, Long clientId);

	@Query("select eb.id from EntityBranch eb where eb.code = ?1 and eb.clientId = ?2")
	List<Long> getIdByCodeAndClientId(String code, Long clientId);

	@Query("select eb.id from EntityBranch eb where eb.externalEntity.id = ?1 and eb.activeFlag = ?2")
	List<Long> getIdsByExternalEntityIdAndActiveFlag(Long entityId, Boolean activeFlag);
}
