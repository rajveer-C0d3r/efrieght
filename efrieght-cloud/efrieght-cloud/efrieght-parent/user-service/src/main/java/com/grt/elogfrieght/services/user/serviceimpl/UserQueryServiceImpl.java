package com.grt.elogfrieght.services.user.serviceimpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.criteria.Predicate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.grt.elogfrieght.services.user.criteria.UserCriteria;
import com.grt.elogfrieght.services.user.domain.Authority;
import com.grt.elogfrieght.services.user.domain.Role_;
import com.grt.elogfrieght.services.user.domain.User;
import com.grt.elogfrieght.services.user.domain.UserAccess_;
import com.grt.elogfrieght.services.user.domain.UserApprovalRequest;
import com.grt.elogfrieght.services.user.domain.UserRoles;
import com.grt.elogfrieght.services.user.domain.UserRoles_;
import com.grt.elogfrieght.services.user.domain.User_;
import com.grt.elogfrieght.services.user.generic.BaseFilterService;
import com.grt.elogfrieght.services.user.mapper.UserAccessMapper;
import com.grt.elogfrieght.services.user.mapper.UserApprovalRequestMapper;
import com.grt.elogfrieght.services.user.mapper.UserMapper;
import com.grt.elogfrieght.services.user.repository.AuthorityRepository;
import com.grt.elogfrieght.services.user.repository.UserApprovalRequestRepository;
import com.grt.elogfrieght.services.user.repository.UserRepository;
import com.grt.elogfrieght.services.user.util.SecurityUtils;
import com.grtship.core.annotation.AccessFilter;
import com.grtship.core.constant.AuthoritiesConstants;
import com.grtship.core.constant.Constants;
import com.grtship.core.dto.BaseDTO;
import com.grtship.core.dto.CsaDetailsDTO;
import com.grtship.core.dto.GsaDetailsDTO;
import com.grtship.core.dto.UserAccessDTO;
import com.grtship.core.dto.UserApprovalRequestDTO;
import com.grtship.core.dto.UserDTO;
import com.grtship.core.enumeration.FilterType;
import com.grtship.core.enumeration.UserDeactivationType;
import com.grtship.core.enumeration.UserType;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(readOnly = true)
public class UserQueryServiceImpl extends BaseFilterService<User> {

	private final UserRepository userRepository;
	private final UserMapper userMapper;
	private final RoleQueryServiceImpl roleService;
	private final AuthorityRepository authorityRepository;
	private final UserAccessMapper userAccessMapper;
	
	@Autowired
	private UserApprovalRequestRepository approvalRequestRepository;
	
	@Autowired
	private UserApprovalRequestMapper approvalRequestMapper;

	public UserQueryServiceImpl(UserRepository usersRepository, UserMapper userMapper, RoleQueryServiceImpl roleService,
			AuthorityRepository authorityRepository, UserAccessMapper userAccessMapper) {
		this.userRepository = usersRepository;
		this.userMapper = userMapper;
		this.roleService = roleService;
		this.authorityRepository = authorityRepository;
		this.userAccessMapper = userAccessMapper;
	}

	@AccessFilter(clientAccessFlag = true, companyAccessFlag = true, branchAccessFlag = true)
	@Transactional(readOnly = true)
	public List<UserDTO> findByCriteria(UserCriteria criteria) {
		log.debug("find by criteria : {}", criteria);
		final Specification<User> specification = createSpecification(criteria);
		return userRepository.findAll(specification).stream().map(userMapper::toDto).collect(Collectors.toList());
	}

	@AccessFilter(clientAccessFlag = true,companyAccessFlag = false)
	@Transactional(readOnly = true)
	public Page<UserDTO> findByCriteria(UserCriteria criteria, Pageable page) {
		log.debug("find by criteria : {}, page: {}", criteria, page);
		final Specification<User> specification = createSpecification(criteria);
		Page<User> users = userRepository.findAll(specification, page);
		List<UserDTO> userDtos = userMapper.toDto(users.getContent());
		return new PageImpl<>(userDtos, page, users.getTotalElements());
	}

	@Transactional(readOnly = true)
	public long countByCriteria(UserCriteria criteria) {
		log.debug("count by criteria : {}", criteria);
		final Specification<User> specification = createSpecification(criteria);
		return userRepository.count(specification);
	}

	private Specification<User> createSpecification(UserCriteria criteria) {
		Specification<User> specification = Specification.where(null);
		if (criteria != null) {

			specification = specification.and((root, query, builder) -> {
				Predicate deactivationTypeTemporaryPredicate = builder.equal(
						root.get(User_.deactivationType).as(String.class), UserDeactivationType.TEMPORARY.toString());
				Predicate deactivationTypeNullPredicate = builder
						.isNull(root.get(User_.deactivationType).as(String.class));
				return builder.or(deactivationTypeTemporaryPredicate, deactivationTypeNullPredicate);
			});
			specification = getIdSpecification(criteria, specification);
			specification = getIdInSpecification(criteria, specification);
			specification = getCompanyIdsInSpecification(criteria, specification);
			specification = getBranchIdsInSpecification(criteria, specification);
			specification = getCompanyIdSpecification(criteria, specification);
			specification = getBranchIdSpecification(criteria, specification);
			specification = getIdSpecification(criteria, specification);
			specification = getEmailSpecification(criteria, specification);
			specification = getActivatedSpecification(criteria, specification);
			specification = getNameSpecification(criteria, specification);
			specification = getRoleIdSpecification(criteria, specification);
			specification = getRoleNameSpecification(criteria, specification);
			specification = getUserTypeSpecification(criteria, specification);
			specification = getStatusSpecification(criteria, specification);
			// Always add this specification. It should not fetch system generated users.
			specification = specification
					.and((root, query, builder) -> builder.not(root.get(User_.LOGIN).in(Constants.ANONYMOUS_USER)));

		}
		return specification;
	}

	private Specification<User> getStatusSpecification(UserCriteria criteria, Specification<User> specification) {
		if (criteria.getStatus() != null) {
			specification = specification.and((root, query, builder) -> builder
					.equal(root.get(User_.status).as(String.class), criteria.getStatus()));
		}
		return specification;
	}

	private Specification<User> getUserTypeSpecification(UserCriteria criteria, Specification<User> specification) {
		if (criteria.getUserType() != null) {
			specification = specification.and((root, query, builder) -> builder
					.equal(root.get(User_.userType).as(String.class), criteria.getUserType()));
		}
		return specification;
	}

	private Specification<User> getRoleNameSpecification(UserCriteria criteria, Specification<User> specification) {
		if (criteria.getRoleName() != null) {
			specification = specification.and((root, query, builder) -> builder.like(
					root.join(User_.USER_ROLES).get(UserRoles_.ROLES).get(Role_.NAME),
					"%" + criteria.getRoleName() + "%"));
		}
		return specification;
	}

	private Specification<User> getRoleIdSpecification(UserCriteria criteria, Specification<User> specification) {
		if (criteria.getRolesId() != null) {
			specification = specification.and((root, query, builder) -> builder
					.equal(root.join(User_.USER_ROLES).get(UserRoles_.ROLES).get(Role_.ID), criteria.getRolesId()));
		}
		return specification;
	}

	private Specification<User> getNameSpecification(UserCriteria criteria, Specification<User> specification) {
		if (criteria.getName() != null) {
			specification = specification
					.and((root, query, builder) -> builder.like(root.get(User_.name), "%" + criteria.getName() + "%"));
		}
		return specification;
	}

	private Specification<User> getEmailSpecification(UserCriteria criteria, Specification<User> specification) {
		if (criteria.getEmail() != null) {
			specification = specification.and(buildIgnoreCaseSpecification(criteria.getEmail(), User_.EMAIL));
		}
		return specification;
	}

	private Specification<User> getActivatedSpecification(UserCriteria criteria, Specification<User> specification) {
		if (criteria.getActivated() != null) {
			specification = specification
					.and(buildSpecification(criteria.getActivated(), User_.activated, FilterType.EQUALS));
		}
		return specification;
	}

	private Specification<User> getBranchIdSpecification(UserCriteria criteria, Specification<User> specification) {
		if (criteria.getBranchId() != null) {
			specification = specification
					.and(buildSpecification(criteria.getBranchId(), User_.branchId, FilterType.EQUALS));
		}
		return specification;
	}

	private Specification<User> getCompanyIdSpecification(UserCriteria criteria, Specification<User> specification) {
		if (criteria.getCompanyId() != null) {
			specification = specification
					.and((root, query, builder) -> root.join(User_.USER_ACCESS).get(UserAccess_.COMPANY_ID).in(criteria.getCompanyIds()));
		}
		return specification;
	}

	private Specification<User> getBranchIdsInSpecification(UserCriteria criteria, Specification<User> specification) {
		if (!CollectionUtils.isEmpty(criteria.getBranchIds())) {
			specification = specification
					.and((root, query, builder) -> root.join(User_.USER_ACCESS).get(UserAccess_.BRANCH_ID).in(criteria.getBranchIds()));
		}
		return specification;
	}

	private Specification<User> getCompanyIdsInSpecification(UserCriteria criteria, Specification<User> specification) {
		if (!CollectionUtils.isEmpty(criteria.getCompanyIds())) {
			specification = specification
					.and((root, query, builder) -> root.join(User_.USER_ACCESS).get(UserAccess_.COMPANY_ID).in(criteria.getCompanyIds())); 
//					root.get(User_.companyId).in(criteria.getCompanyIds()));
		}
		return specification;
	}

	private Specification<User> getIdInSpecification(UserCriteria criteria, Specification<User> specification) {
		if (!CollectionUtils.isEmpty(criteria.getIds())) {
			specification = specification.and((root, query, builder) -> root.get(User_.id).in(criteria.getIds()));
		}
		return specification;
	}

	private Specification<User> getIdSpecification(UserCriteria criteria, Specification<User> specification) {
		if (criteria.getId() != null) {
			specification = specification.and(buildSpecification(criteria.getId(), User_.id, FilterType.EQUALS));
		}
		return specification;
	}

	public Optional<UserDTO> findOne(Long id) {
		return userRepository.findById(id).map(userMapper::toDto);
	}

	public Map<Long, List<CsaDetailsDTO>> getCsaUsersByCompanyIdList(List<Long> companyIdList) {
		List<User> users = userRepository.findByUserAccessCompanyIdIn(companyIdList);
		log.debug("Users : {}", users);
		if (!CollectionUtils.isEmpty(users)) {
			List<CsaDetailsDTO> csaUserList = users.stream()
					.filter(user -> user.getId() != null
							&& user.getAuthorities().contains(new Authority(AuthoritiesConstants.CSA)))
					.map(userMapper::toCsaDetailsDto).collect(Collectors.toList());
			List<CsaDetailsDTO> finalCsaUserList=new LinkedList<CsaDetailsDTO>();
			csaUserList.stream().forEach(csaObj -> {
				csaObj.setCompanyId(companyIdList.get(0));
				finalCsaUserList.add(csaObj);
			});
			log.debug("Converted csa users {}", csaUserList);
			return finalCsaUserList.stream().filter(obj -> obj.getCompanyId() != null)
					.collect(Collectors.groupingBy(CsaDetailsDTO::getCompanyId));
		}
		return Collections.emptyMap();
	}

	public Optional<UserType> getUserType(String login) {
		return userRepository.findOneByLogin(login).filter(user -> user.getUserType() != null).map(User::getUserType);
	}

	@Transactional(readOnly = true)
	public Map<Long, List<GsaDetailsDTO>> getGsaUsersByClientIdList(List<Long> clientIdList) {
		if (!CollectionUtils.isEmpty(clientIdList)) {
			List<User> userList = userRepository.findByClientIdIn(clientIdList);
			List<GsaDetailsDTO> gsaUserList = new ArrayList<>();
			if (!CollectionUtils.isEmpty(userList)) {
				gsaUserList = userList.stream()
						.filter(user -> user.getId() != null
								&& user.getAuthorities().contains(new Authority(AuthoritiesConstants.GSA)))
						.map(userMapper::toGsaDetailsDto).collect(Collectors.toList());
				log.debug("Gsa User List : {}", gsaUserList);
			}
			Map<Long, List<GsaDetailsDTO>> gsaUserMap = new HashMap<>();
			if (!CollectionUtils.isEmpty(gsaUserList)) {
				gsaUserMap = gsaUserList.stream().filter(gsaDetailsDTO -> gsaDetailsDTO.getClientId() != null)
						.collect(Collectors.groupingBy(GsaDetailsDTO::getClientId, Collectors.toList()));
			}
			log.debug("Gsa User Map : {}", gsaUserMap);
			return gsaUserMap;
		}
		return Collections.emptyMap();
	}

	@Transactional(readOnly = true)
	public Page<BaseDTO> getUsersForDropdown(Pageable pageable) {
		return userRepository.findAllByLoginNot(pageable, Constants.ANONYMOUS_USER)
				.map(obj -> new BaseDTO(obj.getId(), obj.getName()));
	}

	@Transactional(readOnly = true)
	public Optional<User> getUserWithAuthoritiesByLogin(String login) {
		return userRepository.findOneWithAuthoritiesByLogin(login);
	}

	@Transactional(readOnly = true)
	public Optional<UserDTO> getUserWithAuthorities() {
		log.debug("SecurityUtils.getCurrentUserLogin() {}", SecurityUtils.getCurrentUserLogin());
		Optional<User> userOptional = SecurityUtils.getCurrentUserLogin()
				.flatMap(userRepository::findOneWithAuthoritiesByLogin);
		log.debug("userOptional {}", userOptional);
		UserDTO userDto = new UserDTO();
		if (userOptional.isPresent()) {
			User user = userOptional.get();
			userDto = userMapper.toDto(user);
			List<Long> roleIds = user.getUserRoles().stream().filter(obj -> obj != null && obj.getRoleId() != null)
					.map(UserRoles::getRoleId).collect(Collectors.toList());
			Set<String> permissions = roleService.getPermissionCodes(roleIds);
			if (!CollectionUtils.isEmpty(user.getAuthorities())) {
				List<String> authorities = user.getAuthorities().stream().map(Authority::getName)
						.collect(Collectors.toList());
				permissions.addAll(authorities);
			}
			userDto.addAuthorities(permissions);
			log.debug("Returning from getUserWithAuthorities {}", userDto);
		}
		return Optional.ofNullable(userDto);
	}

	@Transactional(readOnly = true)
	public List<String> getAuthorities() {
		return authorityRepository.findAll().stream().map(Authority::getName).collect(Collectors.toList());
	}

	public List<UserAccessDTO> getUserAccessByUserId(Long userId) {
		List<UserAccessDTO> userAccess = new ArrayList<>();
		Optional<User> userOptional = userRepository.findById(userId);
		if (userOptional.isPresent()) {
			User user = userOptional.get();
			log.debug("User Fetched by id {}", userOptional.get());
			if (user != null && !CollectionUtils.isEmpty(user.getUserAccess())) {
				userAccess = userAccessMapper.toDto(new ArrayList<>(user.getUserAccess()));
			}
		}
		return userAccess;
	}

	// FIXME NEED HELP FOR THIS QUERY AS IT IS NOT WORKING AS EXPECTED.
	public Boolean existsByRoleId(Long roleId) {
		log.debug("Role id " + roleId);
		List<Long> userIds = userRepository.findByUserRolesRolesId(roleId);
		log.debug("userRepository.existsByUserRolesRolesId(roleId) " + userIds);
		return (CollectionUtils.isEmpty(userIds)) ? Boolean.FALSE : Boolean.TRUE;
	}
	
	// Get User Approval Request Sent Data
	public UserApprovalRequestDTO getUserApprovalRequestSentData(UserApprovalRequestDTO approvalRequestDTO) {
		Optional<UserApprovalRequest> userApprovalRequest = approvalRequestRepository
				.findByEmailAndReferenceIdAndModuleNameAndPermissionCode(approvalRequestDTO.getEmail(),
						approvalRequestDTO.getReferenceId(), approvalRequestDTO.getModuleName(),
						approvalRequestDTO.getPermissionCode());
		if (userApprovalRequest.isPresent()) {
			return approvalRequestMapper.toDto(userApprovalRequest.get());
		}
		return null;
	}
}
