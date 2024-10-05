package com.grt.elogfrieght.services.user.serviceimpl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ObjectUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.grt.elogfrieght.services.user.criteria.UserCriteria;
import com.grt.elogfrieght.services.user.domain.Authority;
import com.grt.elogfrieght.services.user.domain.User;
import com.grt.elogfrieght.services.user.domain.UserRoles;
import com.grt.elogfrieght.services.user.feignclient.ClientModule;
import com.grt.elogfrieght.services.user.feignclient.MasterModule;
import com.grtship.core.constant.AuthoritiesConstants;
import com.grtship.core.dto.BaseDTO;
import com.grtship.core.dto.CsaDetailsDTO;
import com.grtship.core.dto.GsaDetailsDTO;
import com.grtship.core.dto.UserAccessDTO;
import com.grtship.core.dto.UserCreationRequestDTO;
import com.grtship.core.dto.UserDTO;
import com.grtship.core.dto.UserRoleDTO;
import com.grtship.core.enumeration.DomainStatus;
import com.grtship.core.enumeration.UserType;

import javassist.tools.rmi.ObjectNotFoundException;

@SpringBootTest(properties = "config.caching.enabled=false")
@Transactional
@ActiveProfiles(profiles = { "test" })
public class UserQueryServiceImplTest {
	
	@Autowired private UserServiceImpl userService;
	@Autowired private UserQueryServiceImpl userQueryService;
    @Mock Pageable pageable;
	@MockBean MasterModule masterModule;
	@MockBean ClientModule clientModule;
	private UserCriteria userCriteria;
	private UserCreationRequestDTO creationRequestDTO;

	@BeforeEach
	public void init() {
		creationRequestDTO=UserServiceImplTest.createUserDto();
	}
	
	@Test
	void testFindByCriteriaUserCriteriaId() throws ObjectNotFoundException {
		User savedUser = userService.createUser(creationRequestDTO);
		userCriteria=new UserCriteria();
		userCriteria.setId(savedUser.getId());
		List<UserDTO> userDTOs=userQueryService.findByCriteria(userCriteria); 
		assertThat(userDTOs).isNotEmpty();
		assertThat(userDTOs).allMatch(user -> user.getId()==userCriteria.getId());
		assertThat(userDTOs).allMatch(user -> user.getName()!=null);
		assertThat(userDTOs).allMatch(user -> user.getEmail()!=null);
		assertThat(userDTOs).allMatch(user -> user.getAllowLogin()!=null);
	}
	
	@Test
	void testFindByCriteriaUserCriteriaName() throws ObjectNotFoundException {
		User savedUser = userService.createUser(creationRequestDTO);
		userCriteria=new UserCriteria();
		userCriteria.setName(savedUser.getName());
		List<UserDTO> userDTOs=userQueryService.findByCriteria(userCriteria); 
		assertThat(userDTOs).isNotEmpty();
		assertThat(userDTOs).allMatch(user -> user.getId()!=null);
		assertThat(userDTOs).allMatch(user -> user.getName().contains(userCriteria.getName()));
		assertThat(userDTOs).allMatch(user -> user.getEmail()!=null);
		assertThat(userDTOs).allMatch(user -> user.getAllowLogin()!=null);
	}
	
	@Test
	void testFindByCriteriaUserCriteriaEmail() throws ObjectNotFoundException {
		User savedUser = userService.createUser(creationRequestDTO);
		userCriteria = new UserCriteria();
		userCriteria.setEmail(savedUser.getEmail());
		List<UserDTO> userDTOs = userQueryService.findByCriteria(userCriteria);
		assertThat(userDTOs).isNotEmpty();
		assertThat(userDTOs).allMatch(user -> user.getId() != null);
		assertThat(userDTOs).allMatch(user -> user.getName() != null);
		assertThat(userDTOs).allMatch(user -> user.getEmail().contains(userCriteria.getEmail()));
		assertThat(userDTOs).allMatch(user -> user.getAllowLogin() != null);
	}

	@Test
	void testFindByCriteriaUserCriteriaType() throws ObjectNotFoundException {
		userService.createUser(creationRequestDTO);
		userCriteria = new UserCriteria();
		userCriteria.setUserType("CLIENT");
		List<UserDTO> userDTOs = userQueryService.findByCriteria(userCriteria);
		assertThat(userDTOs).isNotEmpty();
		assertThat(userDTOs).allMatch(user -> user.getId() != null);
		assertThat(userDTOs).allMatch(user -> user.getName() != null);
		assertThat(userDTOs).allMatch(user -> user.getEmail() != null);
		assertThat(userDTOs).allMatch(user -> user.getAllowLogin() != null);
		assertThat(userDTOs).allMatch(user -> user.getUserType().equals(UserType.valueOf(userCriteria.getUserType())));
	}
	
	@Test
	void testFindByCriteriaUserCriteriaCompanyId() throws ObjectNotFoundException {
		User savedUser = userService.createUser(creationRequestDTO);
		userCriteria = new UserCriteria();
		userCriteria.setCompanyIds(new ArrayList<>(Arrays.asList(savedUser.getCompanyId())));
		List<UserDTO> userDTOs = userQueryService.findByCriteria(userCriteria);
		assertThat(userDTOs).isNotEmpty();
		assertThat(userDTOs).allMatch(user -> user.getId() != null);
		assertThat(userDTOs).allMatch(user -> user.getName() != null);
		assertThat(userDTOs).allMatch(user -> user.getEmail() != null);
		assertThat(userDTOs).allMatch(user -> user.getAllowLogin() != null);
		assertThat(userDTOs).allMatch(user -> user.getUserType() != null);
	}
	
	@Test
	void testFindByCriteriaUserCriteriaStatus() throws ObjectNotFoundException {
		userService.createUser(creationRequestDTO);
		userCriteria = new UserCriteria();
		userCriteria.setStatus("PENDING");
		List<UserDTO> userDTOs = userQueryService.findByCriteria(userCriteria);
		assertThat(userDTOs).isNotEmpty();
		assertThat(userDTOs).allMatch(user -> user.getId() != null);
		assertThat(userDTOs).allMatch(user -> user.getName() != null);
		assertThat(userDTOs).allMatch(user -> user.getEmail() != null);
		assertThat(userDTOs).allMatch(user -> user.getAllowLogin() != null);
		assertThat(userDTOs).allMatch(user -> user.getUserType() != null);
		assertThat(userDTOs).allMatch(user -> user.getStatus().equals(DomainStatus.valueOf(userCriteria.getStatus())));
	}
	
	@Test
	void testFindByCriteriaUserCriteriaActivated() throws ObjectNotFoundException {
		userService.createUser(creationRequestDTO);
		userCriteria = new UserCriteria();
		userCriteria.setActivated(Boolean.FALSE);
		List<UserDTO> userDTOs = userQueryService.findByCriteria(userCriteria);
		assertThat(userDTOs).isNotEmpty();
		assertThat(userDTOs).allMatch(user -> user.getId() != null);
		assertThat(userDTOs).allMatch(user -> user.getName() != null);
		assertThat(userDTOs).allMatch(user -> user.getEmail() != null);
		assertThat(userDTOs).allMatch(user -> user.getAllowLogin() != null);
		assertThat(userDTOs).allMatch(user -> user.getUserType() != null);
		assertThat(userDTOs).allMatch(user -> user.getStatus()!=null);
	}
	
	@Test
	void testFindByCriteriaUserCriteriaRoleId() throws ObjectNotFoundException {
		User savedUser = userService.createUser(creationRequestDTO);
		userCriteria = new UserCriteria();
		List<Long> roleIds = savedUser.getUserRoles().stream().map(UserRoles::getRoleId).collect(Collectors.toList());
		userCriteria.setRolesId(roleIds.get(0));
		List<UserDTO> userDTOs = userQueryService.findByCriteria(userCriteria);
		assertThat(userDTOs).isNotEmpty();
		assertThat(userDTOs).allMatch(user -> user.getId() != null);
		assertThat(userDTOs).allMatch(user -> user.getName() != null);
		assertThat(userDTOs).allMatch(user -> user.getEmail() != null);
		assertThat(userDTOs).allMatch(user -> user.getAllowLogin() != null);
		assertThat(userDTOs).allMatch(user -> user.getUserType() != null);
		assertThat(userDTOs).allMatch(user -> user.getUserRoles().stream().map(UserRoleDTO::getRoleId)
				.collect(Collectors.toSet()).contains(roleIds.get(0)));
	}

	@Test
	void testFindByCriteriaUserCriteriaPageable() throws ObjectNotFoundException {
		User savedUser = userService.createUser(creationRequestDTO);
		userCriteria=new UserCriteria();
		userCriteria.setName(savedUser.getName());
		Page<UserDTO> userDTOs=userQueryService.findByCriteria(userCriteria,PageRequest.of(0,20));
		assertThat(userDTOs.getSize()).isEqualTo(20);
		assertThat(userDTOs).isNotEmpty();
		assertThat(userDTOs).allMatch(user -> user.getId()!=null);
		assertThat(userDTOs).allMatch(user -> user.getName().equals(userCriteria.getName()));
		assertThat(userDTOs).allMatch(user -> user.getEmail()!=null);
		assertThat(userDTOs).allMatch(user -> user.getAllowLogin()!=null);
	}

	@Test
	void testCountByCriteria() throws ObjectNotFoundException {
		User savedUser = userService.createUser(creationRequestDTO);
		userCriteria = new UserCriteria();
		userCriteria.setId(savedUser.getId());
		Long count = userQueryService.countByCriteria(userCriteria);
		assertThat(count).isGreaterThanOrEqualTo(1);
	}

	@Test
	void testFindOne() throws ObjectNotFoundException {
		User savedUser = userService.createUser(creationRequestDTO);
		User getUserById=userService.findById(savedUser.getId()).get();
		assertEquals(getUserById.getName(),savedUser.getName());
		assertEquals(getUserById.getEmail(),savedUser.getEmail());
		assertEquals(getUserById.getContactNo(),savedUser.getContactNo());
		assertEquals(getUserById.getSubmittedForApproval(),savedUser.getSubmittedForApproval());
		assertTrue(getUserById.getAllowLogin());
		assertEquals(getUserById.getAllCompanies(),Boolean.FALSE);
		assertEquals(getUserById.getUserType(), savedUser.getUserType());
	}

	@Test
	void testGetCsaUsersByCompanyIdList() {
		userService.generateCsaUsers(UserServiceImplTest.prepareCsaDetailsDto());
		List<Long> companyIds = new LinkedList<Long>(
				Arrays.asList(UserServiceImplTest.prepareCsaDetailsDto().get(0).getCompanyId()));
		Map<Long, List<CsaDetailsDTO>> csaUsers = userQueryService.getCsaUsersByCompanyIdList(companyIds);
		assertThat(csaUsers).isNotEmpty();
		assertThat(csaUsers).hasSizeGreaterThanOrEqualTo(1);
		assertThat(csaUsers.get(companyIds.get(0))).isNotNull();
	}

	@Test
	void testGetUserType() throws ObjectNotFoundException {
		User savedUser = userService.createUser(creationRequestDTO);
		UserType userType=userQueryService.getUserType(savedUser.getLogin()).get();
		assertEquals(userType,UserType.CLIENT);
	}

	@Test
	void testGetGsaUsersByClientIdList() {
		userService.generateGsaUsers(UserServiceImplTest.prepareGsaDetailsDto());
		List<Long> clientIds = new ArrayList<Long>(
				Arrays.asList(UserServiceImplTest.prepareGsaDetailsDto().get(0).getClientId()));
		Map<Long, List<GsaDetailsDTO>> gsaUsers = userQueryService.getGsaUsersByClientIdList(clientIds);
		assertThat(gsaUsers).isNotEmpty();
		assertThat(gsaUsers).hasSizeGreaterThanOrEqualTo(1);
		assertThat(gsaUsers.get(clientIds.get(0))).isNotNull();
	}

	@Test
	void testGetUsersForDropdown() throws ObjectNotFoundException {
		userService.createUser(creationRequestDTO);
		Page<BaseDTO> baseDtos = userQueryService.getUsersForDropdown(PageRequest.of(0, 20));
		assertThat(baseDtos.getSize()).isEqualTo(20);
		assertThat(baseDtos.getContent()).allMatch(baseDto -> baseDto.getId() != null);
		assertThat(baseDtos.getContent()).allMatch(baseDto -> baseDto.getName() != null);
	}

	@Test
	void testGetUserWithAuthoritiesByLogin() throws ObjectNotFoundException {
		User savedUser = userService.createUser(creationRequestDTO);
		User user = userQueryService.getUserWithAuthoritiesByLogin(savedUser.getLogin()).get();
		assertThat(user).isNotNull();
		assertThat(user.getLogin()).isEqualTo(savedUser.getLogin());
		assertThat(user.getName()).isEqualTo(savedUser.getName());
		assertThat(user.getContactNo()).isEqualTo(savedUser.getContactNo());
		assertThat(user.getAuthorities()).isNotEmpty();
		assertThat(user.getAuthorities().stream().map(Authority::getName).collect(Collectors.toList())
				.contains(AuthoritiesConstants.USER));
	}
	
	@Test
	void testGetUserWithAuthoritiesByLoginForInvalidLogin() throws ObjectNotFoundException {
		Optional<User> user = userQueryService.getUserWithAuthoritiesByLogin("invalidUser@gmail.com");
		assertFalse(user.isPresent());
	}

	@Test //cannot mock or spy final class
	@WithMockUser(username = "test.user@gmail.com")
	void testGetUserWithAuthorities() throws ObjectNotFoundException {
		User savedUser = userService.createUser(creationRequestDTO);
	    Optional<UserDTO> user=userQueryService.getUserWithAuthorities();
	    assertTrue(user.isPresent());
	    assertThat(user.get().getEmail()).isEqualTo(savedUser.getEmail());
	    assertEquals(user.get().getName(),savedUser.getName());
		assertEquals(user.get().getEmail(),savedUser.getEmail());
		assertEquals(user.get().getContactNo(),savedUser.getContactNo());
		assertEquals(user.get().getSubmittedForApproval(),savedUser.getSubmittedForApproval());
		assertTrue(user.get().getAllowLogin());
		assertEquals(Boolean.FALSE, user.get().getAllCompanies());
		assertEquals(user.get().getUserType(), savedUser.getUserType());
		assertThat(user.get().getAuthorities()).isNotEmpty();
	}

	@Test
	void testGetAuthorities() throws ObjectNotFoundException {
		List<String> authorities=userQueryService.getAuthorities();
		assertThat(authorities).hasSizeGreaterThanOrEqualTo(1);
	}

	@Test
	void testGetUserAccessByUserId() throws ObjectNotFoundException {
		User savedUser = userService.createUser(creationRequestDTO);
		List<UserAccessDTO> accessDTOs = userQueryService.getUserAccessByUserId(savedUser.getId());
		assertThat(accessDTOs).isNotEmpty();
		assertThat(accessDTOs).allMatch(access -> ObjectUtils.isNotEmpty(access.getCompanyId()));
	}
	
	@Test
	void testGetUserAccessByUserIdForInvalidId() throws ObjectNotFoundException {
		List<UserAccessDTO> accessDTOs = userQueryService.getUserAccessByUserId(0L);
		assertThat(accessDTOs).isEmpty();
	}

	@Test
	void testExistsByRoleId() throws ObjectNotFoundException {
		User savedUser = userService.createUser(creationRequestDTO);
		Boolean exists = userQueryService.existsByRoleId(
				savedUser.getUserRoles().stream().map(UserRoles::getRoleId).collect(Collectors.toList()).get(0));
		assertTrue(exists);
	}
	
	@Test
	void testExistsByRoleIdForInvalidRole() throws ObjectNotFoundException {
		Boolean exists = userQueryService.existsByRoleId(0L);
		assertFalse(exists);
	}
}
