package com.grtship.mdm.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.grtship.mdm.domain.CreditTerms;

/**
 * Spring Data  repository for the CreditTerms entity.
 */
@Repository
public interface CreditTermsRepository extends JpaRepository<CreditTerms, Long> {

	Set<CreditTerms> findByReferenceNameAndReferenceIdIn(String referenceName,List<Long> referenceIdList);
	
	@Override Optional<CreditTerms> findById(Long id);

	@Query("select id from CreditTerms where referenceId = ?1 and referenceName = ?2")
	Set<Long> findIdsByReferenceIdAndreferenceName(Long referenceId, String referenceName);

	void deleteByReferenceNameAndIdIn(String referenceName, Set<Long> savedCreditTermsIdsForentity);
}
