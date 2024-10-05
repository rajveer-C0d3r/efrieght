package com.grt.elogfrieght.services.user.mapper;

import org.mapstruct.Mapper;

import com.grt.elogfrieght.services.user.domain.RolePermission;
import com.grtship.core.dto.RolePermissionDTO;

/**
 * Mapper for the entity {@link RolePermission} and its DTO
 * {@link RolePermissionDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface RolePermissionMapper extends EntityMapper<RolePermissionDTO, RolePermission> {

	default RolePermission fromId(String permissionCode) {
		if (permissionCode == null) {
			return null;
		}
		RolePermission rolePermission = new RolePermission();
		rolePermission.setPermissionCode(permissionCode);
		return rolePermission;
	}
}
