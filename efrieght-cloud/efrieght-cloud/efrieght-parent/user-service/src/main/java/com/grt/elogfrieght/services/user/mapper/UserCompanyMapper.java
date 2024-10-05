package com.grt.elogfrieght.services.user.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import com.grt.elogfrieght.services.user.domain.User;
import com.grt.elogfrieght.services.user.domain.UserAccess;
import com.grt.elogfrieght.services.user.domain.UserRoles;
import com.grt.elogfrieght.services.user.feignclient.ClientModule;
import com.grtship.core.dto.UserCompanyDTO;
import com.grtship.core.dto.UserRoleDTO;

import lombok.extern.slf4j.Slf4j;

/**
 * Mapper for the entity {@link UserCompany} and its DTO {@link UserCompanyDTO}.
 */
@Slf4j
@Mapper(componentModel = "spring", uses = { UserMapper.class })
public abstract class UserCompanyMapper implements EntityMapper<UserCompanyDTO, UserAccess> {

	@Autowired
	private ClientModule clientModule;

	@Mapping(source = "companyId", target = "companyId")
	public abstract UserAccess toEntity(UserCompanyDTO userCompanyDTO);

	public UserCompanyDTO toDto(UserAccess userAccess, Set<UserRoles> userRoles) {
		UserCompanyDTO userCompany = convertToCompanyDto(userAccess);
		log.debug("userRoles " + userRoles);
		Set<UserRoleDTO> userRoleDto = getCompanyUserRoles(userRoles, userCompany);
		userCompany.setUserRoles(userRoleDto);
		return userCompany;
	}

	public List<UserCompanyDTO> toDto(User user) {
		Set<UserAccess> userAccess = user.getUserAccess();
		Set<UserRoles> userRoles = user.getUserRoles();
		List<UserCompanyDTO> userCompanyDtoList = userAccess.stream()
				.filter(obj -> obj.getCompanyId() != null).map(obj->convertToCompanyDto(obj, user))
				.collect(Collectors.toList());

		/** Fetching company name and branch names from client module */
		List<UserCompanyDTO> companyList = new ArrayList<>();
		Optional.ofNullable(clientModule.getByUserAccess(userCompanyDtoList))
				.ifPresent(obj -> companyList.addAll(obj.getBody()));

		/** Adding roles to user company list */
		companyList.stream().forEach(companyObj -> {
			if (companyObj != null) {
				if (companyObj.getAllBranches().equals(Boolean.TRUE)) {
					Set<UserRoleDTO> roles = userRoles.stream()
							.filter(roleObj -> roleObj.getCompanyId()!=null && roleObj.getCompanyId().equals(companyObj.getCompanyId()))
							.map(this::toUserRoleDTO).collect(Collectors.toSet());
					companyObj.setUserRoles(roles);
				} else {
					userRoles.stream()
						.filter(roleObj -> roleObj != null && roleObj.getBranchId() != null
								&& roleObj.getBranchId().equals(companyObj.getBranchId()))
						.forEach(obj->companyObj.addUserRole(this.getUserRolesDTO(obj))); 
				}
			}
		});
		return companyList;
	}
	
	private UserRoleDTO getUserRolesDTO(UserRoles roleObj){
		return UserRoleDTO.builder().roleId(roleObj.getRoleId())
				.branchId(roleObj.getBranchId())
				.userCompanyId(roleObj.getCompanyId()).build();
	}

	private Set<UserRoleDTO> getCompanyUserRoles(Set<UserRoles> userRoles, UserCompanyDTO userCompany) {
		return userRoles.stream().filter(userRole -> userRole != null && userRole.getCompanyId() != null
				&& userCompany.getCompanyId() != null && userRole.getCompanyId().equals(userCompany.getCompanyId())
				&& userRole.getBranchId() != null && userCompany.getBranchId() != null
				&& userRole.getBranchId().equals(userCompany.getBranchId())).map(this::toUserRoleDTO)
				.collect(Collectors.toSet());
	}

	private UserCompanyDTO convertToCompanyDto(UserAccess userAccess, User user) {
		Set<UserRoles> roles = user.getUserRoles();
		Set<UserRoleDTO> branchWiseRoles = roles.stream()
				.filter(obj->obj.getCompanyId()==userAccess.getCompanyId() 
				&& ((obj.getBranchId()!=null && obj.getBranchId().equals(userAccess.getBranchId())
				|| obj.getBranchId()==null ) ))
			.map(this::toUserRoleDTO).collect(Collectors.toSet());
		
		
		return UserCompanyDTO.builder().branchId(userAccess.getBranchId())
				.companyId(userAccess.getCompanyId())
				.allBranches((userAccess.getBranchId() == null) ? Boolean.TRUE : Boolean.FALSE)
				.userRoles(branchWiseRoles)
				.build();

	}
	
	private UserCompanyDTO convertToCompanyDto(UserAccess userAccess) {
		return UserCompanyDTO.builder().branchId(userAccess.getBranchId())
				.companyId(userAccess.getCompanyId())
				.allBranches((userAccess.getBranchId() == null) ? Boolean.TRUE : Boolean.FALSE).build();

	}
	
	

	private UserRoleDTO toUserRoleDTO(UserRoles userRole) {
		return UserRoleDTO.builder().branchId(userRole.getBranchId()).roleId(userRole.getRoleId())
				.roleName(userRole.getRoles().getName()).userCompanyId(userRole.getCompanyId()).build();
	}
	
	

	public UserAccess fromId(Long companyId) {
		if (companyId == null) {
			return null;
		}
		UserAccess userCompany = new UserAccess();
		userCompany.setCompanyId(companyId);
		return userCompany;
	}
}
