package com.grtship.mdm.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.grtship.core.dto.EntityMultiDropDownDTO;
import com.grtship.mdm.domain.ExternalEntity;

/**
 * Spring Data  repository for the ExternalEntity entity.
 */
@Repository
public interface ExternalEntityRepository extends JpaRepository<ExternalEntity, Long>, JpaSpecificationExecutor<ExternalEntity> {

	@Query("SELECT COUNT(e) FROM ExternalEntity e")
	Long countEntity();

	ExternalEntity findByCode(String code);

	List<ExternalEntity> findByName(String name);

	@Query("select e.id from ExternalEntity e where name = ?1 and clientId = ?2")
	List<Long> getIdByNameAndClientId(String name, Long clientId);

	@Query("select e.id from ExternalEntity e where code = ?1 and clientId = ?2")
	List<Long> getIdByCodeAndClientId(String code, Long clientId);

	Optional<ExternalEntity> findById(Long id);
	
	Page<EntityMultiDropDownDTO> findPagedProjectedByCustomerFlagAndActiveFlag(Boolean customerFlag,Boolean activeFlag,Pageable pageable);

	Page<EntityMultiDropDownDTO> findPagedProjectedByVendorFlagAndActiveFlag(Boolean vendorFlag,Boolean activeFlag,Pageable pageable);

	Page<EntityMultiDropDownDTO> findPagedProjectedByGroups_IdAndActiveFlag(Long groupId, Boolean activeFlag,Pageable pageable);
	
	List<ExternalEntity> findByCodeInAndClientIdAndCompanyId(List<String> aliasesToSave, Long clientId, Long companyId);

	List<ExternalEntity> findByNameInAndClientIdAndCompanyId(List<String> aliasesToSave, Long clientId, Long companyId);

}

