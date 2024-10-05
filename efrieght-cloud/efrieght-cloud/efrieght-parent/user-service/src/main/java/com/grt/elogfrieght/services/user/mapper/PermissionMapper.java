package com.grt.elogfrieght.services.user.mapper;

import org.mapstruct.Mapper;

import com.grt.elogfrieght.services.user.domain.Permission;
import com.grtship.core.dto.PermissionDTO;

/**
 * Mapper for the entity {@link Permission} and its DTO {@link PermissionDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface PermissionMapper extends EntityMapper<PermissionDTO, Permission> {

	default Permission fromId(String permissionCode) {
		if (permissionCode == null) {
			return null;
		}
		Permission permission = new Permission();
		permission.setPermissionCode(permissionCode);
		return permission;
	}
}
