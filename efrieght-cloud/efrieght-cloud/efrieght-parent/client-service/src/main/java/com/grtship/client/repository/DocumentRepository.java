package com.grtship.client.repository;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.grtship.client.domain.Document;

/**
 * Spring Data  repository for the Document entity.
 */
@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {
	
	List<Document> findByReferenceIdInAndReferenceName(List<Long> idList, String referenceName);

	@Query("select d.id from Document d where d.referenceId = ?1 and d.referenceName = ?2")
	Set<Long> findIdsByReferenceIdReferenceName(Long referenceId, String referenceName);
	
	void deleteByIdIn(Collection<Long> idList);
}
