/**
 * 
 */
package com.grtship.client.serviceimpl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.grtship.client.criteria.UserLogCriteria;
import com.grtship.client.domain.UserLog;
import com.grtship.client.domain.UserLog_;
import com.grtship.client.filter.AccessFilter;
import com.grtship.client.repository.UserLogRepository;
import com.grtship.client.service.UserLogQueryService;
import com.grtship.core.dto.UserLogDTO;

/**
 * @author Ajay
 *
 */
@Service
@Transactional(readOnly = true)
public class UserLogQueryServiceImpl implements UserLogQueryService {

	private static final Logger log = LoggerFactory.getLogger(UserLogQueryServiceImpl.class);
	
	@Autowired
	private UserLogRepository userLogRepository;

	@Transactional(readOnly = true)
	@Override
	@AccessFilter(clientAccessFlag = true, companyAccessFlag = false, branchAccessFlag = false)
	public Page<UserLogDTO> findByCriteria(UserLogCriteria criteria, Pageable pageable) {
		log.debug("find by criteria : {}", criteria);
		final Specification<UserLog> specification = createSpecification(criteria);
		Page<UserLog> pageableUserLogs = userLogRepository.findAll(specification, pageable);
		List<UserLogDTO> dtos = new ArrayList<>();
		UserLogDTO userLogDTO = null;
		for (UserLog user : pageableUserLogs.getContent()) {
			userLogDTO = new UserLogDTO();
			BeanUtils.copyProperties(user, userLogDTO);
			dtos.add(userLogDTO);
		}
		return new PageImpl<>(dtos, pageable, pageableUserLogs.getTotalElements());
	}

	private Specification<UserLog> createSpecification(UserLogCriteria criteria) {
		Specification<UserLog> specification = Specification.where(null);
		if (criteria != null) {
			specification = specification.and(getClientIdSpec(criteria.getClientId()));
		}
		return specification;
	}

	private Specification<UserLog> getClientIdSpec(Long id) {
		return (id != null) ? (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(UserLog_.CLIENT_ID), id)
				: Specification.where(null);
	}

}
