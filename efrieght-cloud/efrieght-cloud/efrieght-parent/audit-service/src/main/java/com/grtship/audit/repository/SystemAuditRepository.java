/**
 * 
 */
package com.grtship.audit.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.grtship.audit.domain.SystemAudit;

/**
 * @author Ajay
 *
 */
@Repository
public interface SystemAuditRepository extends JpaRepository<SystemAudit, Long>, JpaSpecificationExecutor<SystemAudit> {
	List<SystemAudit> findByReferenceIdAndReferenceTypeOrderByVersionDesc(Long referenceId,
			String referenceType);
}
