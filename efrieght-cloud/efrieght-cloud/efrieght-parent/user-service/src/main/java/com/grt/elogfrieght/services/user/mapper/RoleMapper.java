package com.grt.elogfrieght.services.user.mapper;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import com.grt.elogfrieght.services.user.domain.Role;
import com.grt.elogfrieght.services.user.domain.RolePermission;
import com.grt.elogfrieght.services.user.serviceimpl.PermissionQueryServiceImpl;
import com.grtship.core.dto.PermissionDTO;
import com.grtship.core.dto.RoleDTO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Mapper(componentModel = "spring", uses = { DeactivationMapper.class, ReactivationMapper.class })
public abstract class RoleMapper implements EntityMapper<RoleDTO, Role> {

	@Autowired
	private PermissionQueryServiceImpl permissionQueryService;

	@Mapping(source = "active", target = "activeFlag", defaultExpression = "java(Boolean.FALSE)")
	@Mapping(source = "submittedForApproval", target = "submittedForApproval", defaultExpression = "java(Boolean.FALSE)")
	@Mapping(target = "permissions", expression = "java(this.getPermissions(role))")
	public abstract RoleDTO toDto(Role role);

	protected Set<PermissionDTO> getPermissions(Role role) {
		log.debug("Request to getPermissions{} ", role);
		Set<PermissionDTO> permissions = new HashSet<>();
		if (!CollectionUtils.isEmpty(role.getPermissions())) {
			List<String> permissionCodes = role.getPermissions().stream()
					.filter(rolePermission -> rolePermission != null && rolePermission.getPermissionCode() != null)
					.map(RolePermission::getPermissionCode).collect(Collectors.toList());
			List<PermissionDTO> permissionDtoList = permissionQueryService.getByPermissionCodes(permissionCodes);
			Map<String, PermissionDTO> permissionMap = permissionDtoList.stream()
					.filter(obj -> obj != null && obj.getPermissionCode() != null)
					.collect(Collectors.toMap(PermissionDTO::getPermissionCode, value -> value));
			role.getPermissions().forEach(permission -> {
				PermissionDTO permissionObj = permissionMap.getOrDefault(permission.getPermissionCode(), null);
				if (permissionObj != null) {
					permissionObj.setAllPermissions(permission.getAllPermissions());
					permissions.add(permissionObj);
				}
			});
		}
		return permissions;
	}

	@Mapping(source = "isPublic", target = "isPublic", defaultExpression = "java(Boolean.FALSE)")
	@Mapping(source = "submittedForApproval", target = "submittedForApproval", defaultExpression = "java(Boolean.TRUE)")
	@Mapping(source = "isSystemCreated", target = "isSystemCreated", defaultExpression = "java(Boolean.FALSE)")
	@Mapping(source = "activeFlag", target = "active", defaultExpression = "java(Boolean.FALSE)")
	public abstract Role toEntity(RoleDTO roleDTO);

}
