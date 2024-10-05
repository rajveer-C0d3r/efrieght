package com.grt.elogfrieght.services.user.serviceimpl;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.grt.elogfrieght.services.user.criteria.PermissionCriteria;
import com.grt.elogfrieght.services.user.criteria.UserCriteria;
import com.grt.elogfrieght.services.user.domain.Permission;
import com.grt.elogfrieght.services.user.domain.Permission_;
import com.grt.elogfrieght.services.user.domain.User;
import com.grt.elogfrieght.services.user.domain.UserAccess_;
import com.grt.elogfrieght.services.user.domain.User_;
import com.grt.elogfrieght.services.user.generic.QueryService;
import com.grt.elogfrieght.services.user.mapper.PermissionMapper;
import com.grt.elogfrieght.services.user.repository.PermissionRepository;
import com.grtship.core.dto.PermissionDTO;
import com.grtship.core.enumeration.PermissionType;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PermissionQueryServiceImpl extends QueryService<Permission> {

	private final PermissionRepository permissionRepository;
	private final PermissionMapper permissionMapper;

	public PermissionQueryServiceImpl(PermissionRepository permissionRepository, PermissionMapper permissionMapper) {
		this.permissionRepository = permissionRepository;
		this.permissionMapper = permissionMapper;
	}

	@Transactional(readOnly = true)
	public List<PermissionDTO> findByCriteria(PermissionCriteria criteria) {
		log.debug("find by criteria : {}, page: {}", criteria);
		final Specification<Permission> specification = createSpecification(criteria);
		List<Permission> roles = permissionRepository.findAll(specification);
		return permissionMapper.toDto(roles);
	}

	@Transactional(readOnly = true)
	public Page<PermissionDTO> findByCriteria(PermissionCriteria criteria, Pageable page) {
		log.debug("find by criteria : {}, page: {}", criteria, page);
		final Specification<Permission> specification = createSpecification(criteria);
		Page<Permission> roles = permissionRepository.findAll(specification, page);
		List<PermissionDTO> roleDtos = permissionMapper.toDto(roles.getContent());
		return new PageImpl<>(roleDtos, page, roles.getTotalElements());
	}

	private Specification<Permission> createSpecification(PermissionCriteria criteria) {
		Specification<Permission> specification = Specification.where(null);
		if (criteria != null) {
			if (criteria.getPermissionCode() != null) {
				specification = specification
						.and(buildStringSpecification(criteria.getPermissionCode(), Permission_.permissionCode));
			}
			if (criteria.getModuleName() != null) {
				specification = specification
						.and(buildStringSpecification(criteria.getModuleName(), Permission_.moduleName));
			}
			if (criteria.getType() != null) {
				specification = specification.and((root, query, builder) -> builder
						.equal(root.get(Permission_.permissionType), PermissionType.valueOf(criteria.getType())));
			}
			if(!CollectionUtils.isEmpty(criteria.getTypes())) {
				List<PermissionType> types=new LinkedList<PermissionType>();
				criteria.getTypes().stream().forEach(type -> {
					types.add(PermissionType.valueOf(type));
				});
				specification = specification.and((root, query, builder) -> root.get(Permission_.PERMISSION_TYPE).in(types));
			}
		}
		return specification;
	}

	public Optional<PermissionDTO> findOne(String permissionCode) {
		log.debug("Request to get Permission : {}", permissionCode);
		return permissionRepository.findById(permissionCode).map(permissionMapper::toDto);
	}

	public List<PermissionDTO> getByPermissionCodes(List<String> permissionCodes) {
		if (CollectionUtils.isEmpty(permissionCodes)) {
			return Collections.emptyList();
		}
		return permissionRepository.findByPermissionCodeIn(permissionCodes).stream().map(permissionMapper::toDto)
				.collect(Collectors.toList());
	}
	
	@Transactional(readOnly = true)
	public List<String> getPermissionCodes(PermissionCriteria criteria) {
		log.debug("find Permission Codes by criteria : {}", criteria);
		final Specification<Permission> specification = createSpecification(criteria);
		List<Permission> permissions = permissionRepository.findAll(specification);
		return permissions.stream().filter(permission -> checkPermissionFilterCondition(permission))
				.map(Permission::getPermissionCode).collect(Collectors.toList());
	}

	private boolean checkPermissionFilterCondition(Permission permission) {
		return !permission.getPermissionCode().contains("_ADD") && !permission.getPermissionCode().contains("_DELETE")
				&& !permission.getPermissionCode().contains("_EDIT")
				&& !permission.getPermissionCode().contains("_VIEW")
				&& !permission.getPermissionCode().contains("_DEACTIVATE")
				&& !permission.getPermissionCode().contains("_REACTIVATE");
	}

}
