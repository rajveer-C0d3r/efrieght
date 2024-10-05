package com.grt.elogfrieght.services.user.mapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import com.grt.elogfrieght.services.user.domain.Role;
import com.grt.elogfrieght.services.user.domain.UserRoles;
import com.grt.elogfrieght.services.user.repository.RoleRepository;
import com.grtship.core.dto.UserCompanyCreationRequestDTO;
import com.grtship.core.dto.UserCompanyDTO;
import com.grtship.core.dto.UserRoleCreationRequest;
import com.grtship.core.dto.UserRoleDTO;

@Mapper(componentModel = "spring", uses = { UserCompanyMapper.class })
public abstract class UserRolesMapper implements EntityMapper<UserRoleDTO, UserRoles> {

	@Mapping(source = "roles.id", target = "roleId")
	@Mapping(source = "roles.name", target = "roleName")
	public abstract UserRoleDTO toDto(UserRoles userRole);

	@Mapping(source = "roleId", target = "roles.id")
	public abstract UserRoles toEntity(UserRoleDTO userRoleDTO);

	@Mapping(source = "roleId", target = "roles.id", defaultExpression = "java(null)")
	public abstract UserRoles toEntity(UserRoleCreationRequest userRoleDTO);
	
	@Autowired
	private RoleRepository roleRepository;

	public List<UserRoles> toEntity(List<UserRoleDTO> roleList) {
		List<UserRoles> roles = new ArrayList<>();
		if (!CollectionUtils.isEmpty(roleList)) {
			roles = roleList.stream().map(this::toEntity).collect(Collectors.toList());
		}

		return roles;
	}

	public UserRoles fromId(Long roleId) {
		if (roleId == null) {
			return null;
		}
		UserRoles userRole = new UserRoles();
		userRole.setRoleId(roleId);
		return userRole;
	}

	protected Collection<UserRoles> toUserCompanyToUserRole(List<UserCompanyDTO> userCompanies) {
		List<UserRoles> userRoles = new ArrayList<>();
		userCompanies.stream().filter(Objects::nonNull).forEach(userCompany -> {
			if (!CollectionUtils.isEmpty(userCompany.getUserRoles())) {
				userCompany.getUserRoles().stream().filter(Objects::nonNull).forEach(userRoleDto -> {
					Optional<Role> optionalRole = roleRepository.findById(userRoleDto.getRoleId());
					UserRoles userRole = new UserRoles().branchId(userCompany.getBranchId())
//							.companyId(userCompany.getCompanyId()).roles(new Role(userRoleDto.getRoleId()));
							.companyId(userCompany.getCompanyId()).roles(optionalRole.isPresent()?optionalRole.get():null);
					userRoles.add(userRole);
				});
			}
		});

		return userRoles;
	}

	public Collection<UserRoles> toUserCompanyToUserRole(Set<UserCompanyCreationRequestDTO> userCompanies) {
		List<UserRoles> userRoles = new ArrayList<>();
		userCompanies.stream().filter(Objects::nonNull).forEach(userCompany -> {
			if (!CollectionUtils.isEmpty(userCompany.getUserRoles())) {
				userCompany.getUserRoles().stream().filter(Objects::nonNull).forEach(userRoleDto -> {
					UserRoles userRole = new UserRoles().branchId(userCompany.getBranchId())
							.companyId(userCompany.getCompanyId()).roles(new Role(userRoleDto.getRoleId()));
					userRoles.add(userRole);
				});
			}
		});

		return userRoles;
	}

}
