package com.grtship.mdm.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.grtship.mdm.domain.Document;

/**
 * Spring Data repository for the Document entity.
 */
@Repository
public interface DocumentRepository extends JpaRepository<Document, Long>, JpaSpecificationExecutor<Document> {

	@Query("SELECT COUNT(d) FROM Document d")
	Long countDocuments();

	Document findByCode(String code);

	List<Document> findByReferenceIdIn(List<Long> enittyIdList);

	@Query("select id from Document where referenceId = ?1 and referenceName = ?2")
	Set<Long> findIdsByReferenceIdReferenceName(Long referenceId, String referenceName);

	void deleteByReferenceNameAndIdIn(String referenceName, Set<Long> documentIds);
	
	//FIX ME add client id filter
	@Query("select id from Document where code = ?1")
	Long findIdByCode(String documentCode);

	List<Document> findByIdIn(List<Long> documentIds);

	boolean existsByCodeAndCompanyId(String code, Long companyId);

	boolean existsByCodeAndCompanyIdAndIdIsNot(String code, Long companyId, Long documentId);

	boolean existsByNameAndCompanyId(String name, Long companyId);

	boolean existsByNameAndCompanyIdAndIdIsNot(String name, Long companyId, Long id);
}
