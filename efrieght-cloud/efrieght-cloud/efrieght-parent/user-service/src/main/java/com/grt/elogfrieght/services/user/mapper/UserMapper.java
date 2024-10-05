package com.grt.elogfrieght.services.user.mapper;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.CollectionUtils;

import com.grt.elogfrieght.services.user.domain.Authority;
import com.grt.elogfrieght.services.user.domain.Role;
import com.grt.elogfrieght.services.user.domain.User;
import com.grt.elogfrieght.services.user.domain.UserAccess;
import com.grt.elogfrieght.services.user.domain.UserRoles;
import com.grt.elogfrieght.services.user.feignclient.MasterModule;
import com.grt.elogfrieght.services.user.repository.AuthorityRepository;
import com.grt.elogfrieght.services.user.repository.RoleRepository;
import com.grtship.core.constant.AuthoritiesConstants;
import com.grtship.core.dto.CsaDetailsDTO;
import com.grtship.core.dto.DepartmentDTO;
import com.grtship.core.dto.DesignationDTO;
import com.grtship.core.dto.GsaDetailsDTO;
import com.grtship.core.dto.NotificationDTO;
import com.grtship.core.dto.UserCompanyDTO;
import com.grtship.core.dto.UserCreationRequestDTO;
import com.grtship.core.dto.UserDTO;
import com.grtship.core.dto.UserRoleDTO;
import com.grtship.core.enumeration.DomainStatus;
import com.grtship.core.enumeration.NotificationType;
import com.grtship.core.enumeration.UserType;

/**
 * Mapper for the entity {@link User} and its DTO called {@link UserDTO}.
 *
 * Normal mappers are generated using MapStruct, this one is hand-coded as
 * MapStruct support is still in beta, and requires a manual step with an IDE.
 */
@Mapper(componentModel = "spring", uses = { UserRolesMapper.class, UserAccessMapper.class, DeactivationMapper.class,
		ReactivationMapper.class,RoleRepository.class}, nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_DEFAULT)
public abstract class UserMapper {


	@Autowired
	protected PasswordEncoder passwordEncoder;
	@Autowired
	private UserCompanyMapper userCompanyMapper;
	@Autowired
	private UserRolesMapper userRolesMapper;
	@Autowired
	private MasterModule masterModule;
	@Autowired
	private AuthorityRepository authorityRepository;
	@Autowired
	private RoleRepository roleRepository;

	@Mapping(source = "allCompanies", target = "allCompanies", defaultExpression = "java(Boolean.FALSE)")
	@Mapping(source = "allowLogin", target = "allowLogin", defaultExpression = "java(Boolean.FALSE)")
	@Mapping(source = "langKey", target = "langKey", defaultExpression = "java(new String(\"en\"))")
	@Mapping(target = "userRoles", expression = "java(this.getUserRoles(userDTO))")
	@Mapping(target = "userAccess", ignore = true)
	@Mapping(source = "email", target = "login")
	@Mapping(source = "branchId", target = "branchId") // Removing this will not set branchId to entity.
	@Mapping(target = "submittedForApproval", expression = "java(true)")
	@Mapping(source = "status", target = "status", defaultExpression = "java(this.getStatus())")
	@Mapping(target = "authorities", expression = "java(this.getAuthorities(userDTO))")
	@Mapping(target = "deactivate", ignore = true)
	@Mapping(target = "reactivate", ignore = true)
	@Mapping(source = "activeFlag", target = "activated", defaultValue = "false")
	@Mapping(source = "isDeactivated", target = "isDeactivated", defaultValue = "false")
	public abstract User toEntity(UserCreationRequestDTO userDTO);

	@Mapping(source = "activated", target = "activeFlag", defaultExpression = "java(false)")
	@Mapping(source = "branchId", target = "branchId")
	@Mapping(source = "clientId", target = "clientId")
	@Mapping(source = "companyId", target = "companyId")
	@Mapping(target = "authorities", expression = "java(this.getAuthorities(user.getAuthorities()))")
	@Mapping(target = "userRoles", expression="java(this.getUserRoles(user))")
	@Mapping(target = "userCompanies", expression = "java(this.getUserCompanies(user))")
	@Mapping(target = "departmentName", expression = "java(this.getDepartmentName(user.getDepartmentId()))")
	@Mapping(target = "designationName", expression = "java(this.getDesignationName(user.getDesignationId()))")
	public abstract UserDTO toDto(User user);

	// FETCH DESTINATION AND DESIGNATION MAP BEFORE SETTING DATA
	
	public abstract List<UserDTO> toDto(List<User> user);

	/************** GSA MAPPER ***********************/
	public abstract GsaDetailsDTO toGsaDetailsDto(User user);

	public abstract List<User> toEntity(List<GsaDetailsDTO> userDTOs);

	@Mapping(target = "password", expression = "java(passwordEncoder.encode(gsaDetailsDto.getName()))")
	@Mapping(target = "status", expression = "java(this.getStatus())")
	@Mapping(target = "resetDate", expression = "java(java.time.Instant.now())")
	@Mapping(target = "resetKey", expression = "java(com.grt.elogfrieght.services.user.util.RandomUtil.generateResetKey())")
	@Mapping(target = "userType", expression = "java(this.getClientUserType())")
	@Mapping(target = "activationKey", expression = "java(com.grt.elogfrieght.services.user.util.RandomUtil.generateActivationKey())")
	@Mapping(target = "allCompanies", constant = "true")
	@Mapping(target = "submittedForApproval", expression = "java(true)")
	@Mapping(target = "allowLogin", constant = "true")
	@Mapping(target = "activated", constant = "false")
	@Mapping(source = "email", target = "login")
	@Mapping(target = "authorities", expression = "java(this.getGsaAuthorities())")
	@Mapping(target = "userRoles", expression = "java(this.getGSAUserRoles(gsaDetailsDto))")
	@Mapping(source = "langKey", target = "langKey", defaultExpression = "java(\"en\")")
	@Mapping(target = "isDeactivated", constant = "false")
	public abstract User toEntity(GsaDetailsDTO gsaDetailsDto);

	/************** CSA MAPPER ***********************/

	@Mapping(source = "email", target = "login")
	@Mapping(source = "contactNo", target = "contactNo")
	@Mapping(target = "password", expression = "java(passwordEncoder.encode(csaDetailsDto.getName()))")
	@Mapping(target = "status", expression = "java(this.getStatus())")
	@Mapping(target = "resetDate", expression = "java(java.time.Instant.now())")
	@Mapping(target = "resetKey", expression = "java(com.grt.elogfrieght.services.user.util.RandomUtil.generateResetKey())")
	@Mapping(target = "userType", expression = "java(this.getClientUserType())")
	@Mapping(target = "activationKey", expression = "java(com.grt.elogfrieght.services.user.util.RandomUtil.generateActivationKey())")
	@Mapping(target = "allCompanies", constant = "false")
	@Mapping(target = "allowLogin", constant = "true")
	@Mapping(target = "activated", constant = "false")
	@Mapping(target = "submittedForApproval", expression = "java(true)")
	@Mapping(source = "langKey", target = "langKey", defaultExpression = "java(\"en\")")
	@Mapping(target = "authorities", expression = "java(this.getCsaAuthorities())")
	@Mapping(target = "userAccess", expression = "java(this.getUserAccess(csaDetailsDto))")
	@Mapping(target = "userRoles", expression = "java(this.getCSAUserRoles(csaDetailsDto))")
	@Mapping(target = "isDeactivated", constant = "false")
	public abstract User toEntity(CsaDetailsDTO csaDetailsDto);

	public abstract List<User> csaToEntity(List<CsaDetailsDTO> userDTOs);

	public abstract CsaDetailsDTO toCsaDetailsDto(User user);
	
	public abstract NotificationDTO toNotificationDto(User user);
	
	public abstract List<NotificationDTO> toNotificationDtos(List<User> users);
	
	protected UserType getClientUserType() {
		return UserType.CLIENT;
	}
	
	protected List<UserRoleDTO> getUserRoles(User user){
		if(CollectionUtils.isEmpty(user.getUserRoles())) {
			return null;
		}else {
			return user.getUserRoles().stream().map(userRolesMapper::toDto).collect(Collectors.toList());
		}
		
	}

	/** CSA user will always have single company id */
	protected Long getCompanyId(User user) {
		List<Long> companyId = user.getUserAccess().stream().map(UserAccess::getCompanyId).collect(Collectors.toList());
		return companyId.get(0);
	}

	/** get user access list from csa user dto */
	protected Set<UserAccess> getUserAccess(CsaDetailsDTO userDto) {
		Set<UserAccess> userAccess = new HashSet<>();
		userAccess.add(new UserAccess().companyId(userDto.getCompanyId()).allBranches(Boolean.TRUE));
		return userAccess;
	}

	protected Set<Authority> getGsaAuthorities() {
		Set<Authority> authorities = new HashSet<>();
		authorities.add(new Authority(AuthoritiesConstants.GSA)); // GSA USER WILL ALWAYS HAVE GSA AUTHORITY
		return authorities;
	}

	protected Set<Authority> getCsaAuthorities() {
		Set<Authority> authorities = new HashSet<>();
		authorities.add(new Authority(AuthoritiesConstants.CSA)); // CSA USER WILL ALWAYS HAVE CSA AUTHORITY
		return authorities;
	}

	////////////////////////////////////

	protected DomainStatus getStatus() {
		return DomainStatus.PENDING;
	}

	protected Set<Authority> getAuthorities(UserCreationRequestDTO userDTO) {
		Set<Authority> authorities = new HashSet<>();
		if (userDTO.getUserType() != null && userDTO.getUserType().equals(UserType.ADMIN)) {
			authorityRepository.findById(AuthoritiesConstants.ADMIN).ifPresent(authorities::add);
		} else {
			authorityRepository.findById(AuthoritiesConstants.USER).ifPresent(authorities::add);
		}

		return authorities;
	}

	@Named("toUserRoles")
	public Set<UserRoles> getUserRoles(UserCreationRequestDTO userDTO) {
		if (CollectionUtils.isEmpty(userDTO.getUserCompanies()) && CollectionUtils.isEmpty(userDTO.getUserRoles())) {
			return null; // sending blank set it causing exception as it is trying to save blank record
		}
		Set<UserRoles> userRoles = new HashSet<>();
		if (!CollectionUtils.isEmpty(userDTO.getUserCompanies())) {
			userRoles.addAll(userRolesMapper.toUserCompanyToUserRole(userDTO.getUserCompanies()));
		} else {
			userRoles = userDTO.getUserRoles().stream().filter(obj -> obj.getRoleId() != null)
					.map(userRolesMapper::toEntity).collect(Collectors.toSet());
		}
		return userRoles;
	}

	protected Map<Long, String> getDesignationMap(List<Long> designationIds) {
		Map<Long, String> designationMap = new HashMap<>();
		if (!CollectionUtils.isEmpty(designationIds)) {
			ResponseEntity<List<DesignationDTO>> designationResonse = masterModule.getDesignationsByIds(designationIds);
			if (designationResonse != null && designationResonse.getBody() != null) {
				List<DesignationDTO> designations = designationResonse.getBody();
				designationMap = designations.stream().filter(obj -> obj.getId() != null)
						.collect(Collectors.toMap(DesignationDTO::getId, DesignationDTO::getName));
			}
		}
		return designationMap;
	}

	protected Map<Long, String> getDepartmentMap(List<Long> departmentIds) {
		Map<Long, String> departmentMap = new HashMap<>();
		if (!CollectionUtils.isEmpty(departmentIds)) {
			ResponseEntity<List<DepartmentDTO>> departmentResponse = masterModule.getDepartmentsByIds(departmentIds);
			if (departmentResponse != null && departmentResponse.getBody() != null) {
				List<DepartmentDTO> departments = departmentResponse.getBody();
				departmentMap = departments.stream().filter(obj -> obj.getId() != null)
						.collect(Collectors.toMap(DepartmentDTO::getId, DepartmentDTO::getName));
			}
		}
		return departmentMap;
	}

	protected Set<String> getAuthorities(Set<Authority> authorities) {
		return authorities.stream().map(Authority::getName).collect(Collectors.toSet());
	}

	protected String getDepartmentName(Long departmentId) {
		Map<Long, String> departmentMap = getDepartmentMap(Arrays.asList(departmentId));
		return departmentMap.getOrDefault(departmentId, null);
	}

	protected String getDesignationName(Long designationId) {
		Map<Long, String> departmentMap = getDepartmentMap(Arrays.asList(designationId));
		return departmentMap.getOrDefault(designationId, null);
	}

	protected List<UserCompanyDTO> getUserCompanies(User user) {
		if (CollectionUtils.isEmpty(user.getUserAccess())) {
			return Collections.emptyList();
		}
		return userCompanyMapper.toDto(user);
	}
	
	protected NotificationType setNotificationType(User user) {
		return NotificationType.EMAIL;
	}
	
	public Set<UserRoles> getGSAUserRoles(GsaDetailsDTO gsaDetailsDto) {
		Set<UserRoles> userRoles = new HashSet<>();
		Optional<Role> optionalRole = roleRepository.findFirstByName("GSA");
		UserRoles userRole = new UserRoles().roles(optionalRole.isPresent()?optionalRole.get():null);
		userRoles.add(userRole);	
		return userRoles;
	}
	
	public Set<UserRoles> getCSAUserRoles(CsaDetailsDTO csaDetailsDto) {
		Set<UserRoles> userRoles = new HashSet<>();
		Optional<Role> optionalRole = roleRepository.findFirstByName("CSA");
		UserRoles userRole = new UserRoles().roles(optionalRole.isPresent()?optionalRole.get():null);
		userRoles.add(userRole);	
		return userRoles;
	}

	
}
