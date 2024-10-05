package com.grtship.account.repository;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.grtship.account.domain.ObjectAlias;

/**
 * Spring Data repository for the ObjectAlias entity.
 */
@Repository
public interface ObjectAliasRepository extends JpaRepository<ObjectAlias, Long> {

	List<ObjectAlias> findByReferenceNameAndReferenceIdIn(String referenceName, Collection<Long> referenceIdList);

	Set<ObjectAlias> findByReferenceIdAndReferenceName(Long referenceId, String referenceName);

	void deleteByReferenceNameAndIdIn(String referenceName, Collection<Long> aliasIdsToDelete);

	ObjectAlias findByNameAndReferenceNameAndClientIdAndCompanyId(String name, String referenceName, Long clientId,
			Long companyId);

	ObjectAlias findByIdNotAndNameAndReferenceNameAndClientIdAndCompanyId(Long id, String name, String referenceName,
			Long clientId, Long companyId);
}
