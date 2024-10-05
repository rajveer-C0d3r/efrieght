package com.grtship.mdm.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.grtship.mdm.domain.DocumentStorage;

/**
 * Spring Data  repository for the DocumentStorage entity.
 */
@Repository
public interface DocumentStorageRepository extends JpaRepository<DocumentStorage, Long> {

	@Query("select ds.originalFileName from DocumentStorage ds where ds.fileStorage = ?1")
	String findOriginalFileNameByFileStorage(String fileName);

	Optional<List<DocumentStorage>> findByReferenceNameAndReferenceIdIn(String referenceName, List<Long> enittyIdList);
}
