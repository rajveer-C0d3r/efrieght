package com.grtship.mdm.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.grtship.core.dto.ObjectAliasDTO;
import com.grtship.mdm.domain.ObjectAlias;

/**
 * Spring Data  repository for the ObjectAlias entity.
 */
@Repository
public interface ObjectAliasRepository extends JpaRepository<ObjectAlias, Long>, JpaSpecificationExecutor<ObjectAlias>{
	
	List<ObjectAlias> findByReferenceIdIn(List<Long> referenceIdList);

	@Query("select id from ObjectAlias where referenceId = ?1 and referenceName = ?2")
	Set<Long> findIdsByReferenceIdAndReferenceName(Long referenceId, String referenceName);

	void deleteByReferenceNameAndIdIn(String referenceName, Set<Long> savedAliasIdsSetForReferenceId);
	
	@Query("select o from ObjectAlias o where o.name = ?1 and o.referenceName = ?2")
	ObjectAlias findByNameAndReferenceName(String name, String referenceName);
	
	ObjectAlias findByIdNotAndNameAndReferenceName(Long id,String name, String referenceName);
	
	@Query("select o from ObjectAlias o where o.name = ?1 and o.referenceName = ?2 and o.id != ?3")
	ObjectAlias findByNameAndReferenceNameAndId(String name, String referenceName, Long id);

	@Query("select o.name from ObjectAlias o where o.name in (:name) and o.referenceName = :referenceName and o.id not in (:aliasIdList)")
	List<String> getAliasNamesBasedOnNamesClientIdAnComId(@Param("name")List<String> aliasNames,@Param("referenceName")String referenceName,@Param("aliasIdList") Set<Long> aliasIdList);

	@Query("select distinct o.name from ObjectAlias o where o.name = ?1 and o.referenceName = ?2 and o.clientId= ?3 and o.companyId= ?4")
	String findByNameAndReferenceNameAndClientIdAndCompanyId(String name, String referenceName, Long clientId,
			Long companyId);

	ObjectAlias findByIdNotAndNameAndReferenceNameAndClientIdAndCompanyId(Long id, String name, String referenceName,
			Long clientId, Long companyId);
	
	
//	@Query("select o.name from ObjectAlias o where o.name in (:name) and and o.referenceName = :referenceName and o.clientId= :clientId and o.companyId= :companyId")
//	Set<String> getAliasNamesListBasedOnNamesClientIdAnComId(@Param("name")Set<String> aliasNames,@Param("referenceName") String referenceName, @Param("clientId") Long clientId,
//			@Param("companyId")	Long companyId);

}
