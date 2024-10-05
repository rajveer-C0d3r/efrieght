/**
 * 
 */
package com.grtship.client.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.grtship.client.criteria.UserLogCriteria;
import com.grtship.core.dto.UserLogDTO;

/**
 * @author Ajay
 *
 */
public interface UserLogQueryService {

	Page<UserLogDTO> findByCriteria(UserLogCriteria criteria, Pageable pageable);

}
