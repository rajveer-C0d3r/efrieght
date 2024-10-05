package com.grt.elogfrieght.services.user.serviceimpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.grt.elogfrieght.services.user.criteria.RoleCriteria;
import com.grt.elogfrieght.services.user.domain.Permission;
import com.grt.elogfrieght.services.user.domain.Role;
import com.grt.elogfrieght.services.user.domain.Role_;
import com.grt.elogfrieght.services.user.generic.QueryService;
import com.grt.elogfrieght.services.user.mapper.PermissionMapper;
import com.grt.elogfrieght.services.user.mapper.RoleMapper;
import com.grt.elogfrieght.services.user.repository.PermissionRepository;
import com.grt.elogfrieght.services.user.repository.RoleRepository;
import com.grtship.core.annotation.AccessFilter;
import com.grtship.core.dto.PermissionDTO;
import com.grtship.core.dto.RoleDTO;
import com.grtship.core.enumeration.DomainStatus;
import com.grtship.core.filter.LongFilter;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(readOnly = true)
public class RoleQueryServiceImpl extends QueryService<Role> {

	private final RoleRepository roleRepository;
	private final RoleMapper roleMapper;
	private final PermissionRepository permissionRepository;
	private final PermissionMapper permissionMapper;

	public RoleQueryServiceImpl(RoleRepository roleRepository, RoleMapper roleMapper,
			PermissionRepository permissionRepository, PermissionMapper permissionMapper) {
		this.roleRepository = roleRepository;
		this.roleMapper = roleMapper;
		this.permissionRepository = permissionRepository;
		this.permissionMapper = permissionMapper;
	}

	@AccessFilter(clientAccessFlag = true, companyAccessFlag = true, branchAccessFlag = true)
	public List<RoleDTO> findByCriteria(RoleCriteria criteria) {
		log.debug("find by criteria : {}", criteria);
		final Specification<Role> specification = createSpecification(criteria);
		List<Role> temp = roleRepository.findAll(specification);
		log.debug("roles in findByCriteria {}", temp);
		List<RoleDTO> roles = roleRepository.findAll(specification).stream().map(roleMapper::toDto)
				.collect(Collectors.toList());
		log.debug("Roles in findByCriteria {}", roles);
		return roles;
	}

	@AccessFilter(allowAdminData = true,  clientAccessFlag = true, companyAccessFlag = true)
	public Page<RoleDTO> findByCriteria(RoleCriteria criteria, Pageable page) {
		log.debug("find by criteria : {}, page: {}", criteria, page);
		final Specification<Role> specification = createSpecification(criteria);
		Page<Role> roles = roleRepository.findAll(specification, page);
		List<RoleDTO> roleDtos = roleMapper.toDto(roles.getContent());
		return new PageImpl<>(roleDtos, page, roles.getTotalElements());
	}

	public long countByCriteria(RoleCriteria criteria) {
		log.debug("count by criteria : {}", criteria);
		final Specification<Role> specification = createSpecification(criteria);
		return roleRepository.count(specification);
	}

	private Specification<Role> createSpecification(RoleCriteria criteria) {
		Specification<Role> specification = Specification.where(null);
		if (criteria != null) {
			if (criteria.getId() != null) {
				specification = specification.and(buildRangeSpecification(criteria.getId(), Role_.id));
			}
			if (criteria.getName() != null) {
				String name = StringUtils.remove(criteria.getName().trim(), "%");
				specification = specification
						.and((root, query, builder) -> builder.like(root.get(Role_.name), "%" + name + "%"));
			}
			if (criteria.getStatus() != null) {
				specification = specification.and((root, query, builder) -> builder.equal(root.get(Role_.status),
						DomainStatus.valueOf(criteria.getStatus())));
			}
			if (criteria.getActive() != null) {
				specification = specification
						.and((root, query, builder) -> builder.equal(root.get(Role_.active), criteria.getActive()));
			}
			if (criteria.getIsPublic() != null) {
				specification = specification
						.and((root, query, builder) -> builder.equal(root.get(Role_.isPublic), criteria.getIsPublic()));
			}
			if (criteria.getSubmittedForApproval() != null) {
				specification = specification.and((root, query, builder) -> builder
						.equal(root.get(Role_.submittedForApproval), criteria.getSubmittedForApproval()));
			}
			if (criteria.getIsSystemCreated() != null) {
				specification = specification.and((root, query, builder) -> builder
						.equal(root.get(Role_.isSystemCreated), criteria.getIsSystemCreated()));
			}
		}
		return specification;
	}

	public Optional<RoleDTO> findOne(Long id) {
		log.debug("Request to get Role : {}", id);
		LongFilter idFilter = new LongFilter();
		idFilter.setEquals(id);

		RoleCriteria criteria = new RoleCriteria();
		criteria.setId(idFilter);

		List<RoleDTO> roles = findByCriteria(criteria);
		if (!CollectionUtils.isEmpty(roles)) {
			return Optional.ofNullable(roles.get(0));
		}
		return Optional.empty();
	}

	private List<RoleDTO> getByIds(List<Long> ids) {
		log.debug("Request to get Roles : {}", ids);
		if (CollectionUtils.isEmpty(ids)) {
			return Collections.emptyList();
		}
		return roleRepository.findAllById(ids).stream().filter(Objects::nonNull).map(roleMapper::toDto)
				.collect(Collectors.toList());
	}

	public List<GrantedAuthority> getRolePermissionsByIds(List<Long> roleIds) {
		log.debug("Request to fetch permissions for ids{}", roleIds);
		Set<String> permissions = getPermissionCodes(roleIds);
		return permissions.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
	}

	public Set<String> getPermissionCodes(List<Long> roleIds) {
		Set<String> permissions = new HashSet<>();
		List<RoleDTO> roles = getByIds(roleIds);
		if (!CollectionUtils.isEmpty(roles)) {
			roles.stream().forEach(roleObj -> {
				if (!CollectionUtils.isEmpty(roleObj.getPermissions())) {
					List<String> permissionCodes = roleObj.getPermissions().stream()
							.filter(obj -> obj != null && obj.getPermissionCode() != null)
							.map(PermissionDTO::getPermissionCode).collect(Collectors.toList());
					permissions.addAll(permissionCodes);
				}
			});
		}
		return permissions;
	}

	public Map<String, List<String>> getPermissionMapByUserRoles(List<Long> roleIds) {
		Set<String> permissionCodes = getPermissionCodes(roleIds);
		List<Permission> permissions = permissionRepository.findByPermissionCodeIn(new ArrayList<>(permissionCodes));
		return permissions.stream().filter(permission -> ObjectUtils.isNotEmpty(permission.getModuleName())).collect(Collectors.groupingBy(Permission::getModuleName,Collectors.mapping(Permission::getPermissionCode,Collectors.toList())));
	}

}
