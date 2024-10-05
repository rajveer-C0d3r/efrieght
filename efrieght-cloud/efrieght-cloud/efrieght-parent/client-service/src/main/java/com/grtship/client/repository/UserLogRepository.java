/**
 * 
 */
package com.grtship.client.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.grtship.client.domain.UserLog;

/**
 * @author Ajay
 *
 */
@Repository
public interface UserLogRepository extends JpaRepository<UserLog, Long>, JpaSpecificationExecutor<UserLog>{

}
