package com.grtship.authorisation.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.grtship.authorisation.domain.ObjectApproval;

@Repository
public interface ObjectApprovalRepository
		extends JpaRepository<ObjectApproval, Long>, JpaSpecificationExecutor<ObjectApproval> {

	Optional<ObjectApproval> findByReferenceIdAndObjectName(Long referenceId, String moduleName);
}
