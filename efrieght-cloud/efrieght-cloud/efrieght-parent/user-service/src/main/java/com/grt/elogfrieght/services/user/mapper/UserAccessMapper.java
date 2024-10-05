package com.grt.elogfrieght.services.user.mapper;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.mapstruct.Mapper;
import org.springframework.util.CollectionUtils;

import com.grt.elogfrieght.services.user.domain.User;
import com.grt.elogfrieght.services.user.domain.UserAccess;
import com.grtship.core.dto.UserAccessDTO;

/**
 * Mapper for the entity {@link UserAccess} and its DTO {@link UserAccessDTO}.
 */
@Mapper(componentModel = "spring")
public abstract class UserAccessMapper implements EntityMapper<UserAccessDTO, UserAccess> {
	
	public abstract Set<UserAccessDTO> toDto(Set<UserAccess> userAccesses);
	
	public Set<UserAccess> userToUserAccess(User user) {
		Set<UserAccess> userAccesses = new HashSet<>();
		if (user != null && !CollectionUtils.isEmpty(user.getUserRoles())) {
			user.getUserRoles().stream().filter(Objects::nonNull).forEach(userRole -> {
				if (userRole.getCompanyId() != null) {
					UserAccess userAccessObj = UserAccess.builder().branchId(userRole.getBranchId())
							.allBranches(Boolean.valueOf(userRole.getBranchId()==null))
							.companyId(userRole.getCompanyId()).build();
					
					userAccesses.add(userAccessObj);
				}
			});
		}
		return userAccesses;
	}

	public UserAccess fromId(Long companyId) {
		if (companyId == null) {
			return null;
		}
		UserAccess userAccess = new UserAccess();
		userAccess.setCompanyId(companyId);
		return userAccess;
	}
}
