/**
 * 
 */
package com.grtship.audit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.grtship.audit.domain.AuditPropertyKey;

/**
 * @author hp
 *
 */
@Repository
public interface AuditPropertyKeyRepository extends JpaRepository<AuditPropertyKey, Long>{

}
