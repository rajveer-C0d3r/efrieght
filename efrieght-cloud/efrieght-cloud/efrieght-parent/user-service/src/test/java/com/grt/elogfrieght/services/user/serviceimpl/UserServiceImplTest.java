package com.grt.elogfrieght.services.user.serviceimpl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.validation.ConstraintViolationException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.grt.elogfrieght.services.user.domain.User;
import com.grt.elogfrieght.services.user.feignclient.ClientModule;
import com.grt.elogfrieght.services.user.feignclient.MasterModule;
import com.grt.elogfrieght.services.user.mapper.UserAccessMapper;
import com.grt.elogfrieght.services.user.repository.UserRepository;
import com.grt.elogfrieght.services.user.util.SecurityUtils;
import com.grtship.common.exception.InvalidDataException;
import com.grtship.common.exception.ValidationException;
import com.grtship.core.dto.BranchResponse;
import com.grtship.core.dto.ClientIdDTO;
import com.grtship.core.dto.CompanyBranchResponse;
import com.grtship.core.dto.CompanyDTO;
import com.grtship.core.dto.CsaDetailsDTO;
import com.grtship.core.dto.EmailPresentDto;
import com.grtship.core.dto.GsaDetailsDTO;
import com.grtship.core.dto.UserCompanyCreationRequestDTO;
import com.grtship.core.dto.UserCreationRequestDTO;
import com.grtship.core.dto.UserDTO;
import com.grtship.core.dto.UserDeactivationRequest;
import com.grtship.core.dto.UserRoleCreationRequest;
import com.grtship.core.dto.UserRoleDTO;
import com.grtship.core.dto.UserSpecificCBRequest;
import com.grtship.core.dto.UserSpecificCBResponse;
import com.grtship.core.enumeration.DomainStatus;
import com.grtship.core.enumeration.UserDeactivationType;
import com.grtship.core.enumeration.UserType;

import javassist.tools.rmi.ObjectNotFoundException;

@SpringBootTest(properties = "config.caching.enabled=false")
@Transactional
@ActiveProfiles(profiles = { "test" })
public class UserServiceImplTest {
	
	@Autowired private UserRepository userRepository;
	@Autowired private UserServiceImpl userService;
	@Autowired private PasswordEncoder passwordEncoder;
	@Autowired private UserQueryServiceImpl userQueryService;
	@Autowired private UserAccessMapper accessMapper;
    @Mock Pageable pageable;
	@MockBean MasterModule masterModule;
	@MockBean ClientModule clientModule;
	private UserCreationRequestDTO creationRequestDTO;
	
	public static UserCreationRequestDTO createUserDto() {
		Set<UserRoleCreationRequest> userRoles = prepareRoleDtos();

		Set<UserCompanyCreationRequestDTO> userCompanies = preapreUserCompanyCreationDtos(userRoles);

		UserCreationRequestDTO creationRequestDTO = new UserCreationRequestDTO();
		creationRequestDTO.setEmail("test.user@gmail.com");
		creationRequestDTO.setName("Test Save User");
		creationRequestDTO.setContactNo("8898617911");
		creationRequestDTO.setAllowLogin(Boolean.TRUE);
		creationRequestDTO.setSubmittedForApproval(Boolean.TRUE);
		creationRequestDTO.setAllCompanies(Boolean.FALSE);
		creationRequestDTO.setActiveFlag(Boolean.FALSE);
		creationRequestDTO.setClientId(1L);
		creationRequestDTO.setCompanyId(1L);
		creationRequestDTO.setDepartmentId(1L);
		creationRequestDTO.setDesignationId(1L);
		creationRequestDTO.setUserType(UserType.CLIENT);
		creationRequestDTO.setStatus(DomainStatus.PENDING);
		creationRequestDTO.setUserCompanies(userCompanies);
		return creationRequestDTO;
	}

	private static Set<UserCompanyCreationRequestDTO> preapreUserCompanyCreationDtos(
			Set<UserRoleCreationRequest> userRoles) {
		Set<UserCompanyCreationRequestDTO> userCompanies = new HashSet<UserCompanyCreationRequestDTO>();
		userCompanies
				.add(UserCompanyCreationRequestDTO.builder().companyId(1L).branchId(1L).userRoles(userRoles).build());
		userCompanies.add(UserCompanyCreationRequestDTO.builder().companyId(2L).userRoles(userRoles)
				.allBranches(Boolean.TRUE).build());
		return userCompanies;
	}

	private static Set<UserRoleCreationRequest> prepareRoleDtos() {
		Set<UserRoleCreationRequest> userRoles=new HashSet<UserRoleCreationRequest>();
		userRoles.add(UserRoleCreationRequest.builder().roleId(1L).build());
		userRoles.add(UserRoleCreationRequest.builder().roleId(2L).build());
		return userRoles;
	}
	
	@BeforeEach
	public void init() {
		creationRequestDTO=createUserDto();
	}

	@Test
	void testActivateRegistration() throws ObjectNotFoundException {
		User user = userService.createUser(creationRequestDTO);
		user.setActivationKey("Abc@1234");
		userRepository.save(user);
		User activatedUser = userService.activateRegistration(user.getActivationKey()).get();
		assertNull(activatedUser.getActivationKey());
		assertTrue(activatedUser.isActivated());
	}
	
	@Test
	void testActivateRegistrationForInvalidKey() throws ObjectNotFoundException {
		Optional<User> activatedUser = userService.activateRegistration("BCD@123");
		assertFalse(activatedUser.isPresent());
	}

	@Test
	void testCompletePasswordReset() throws ObjectNotFoundException {
		User user = userService.createUser(creationRequestDTO);
		user.setResetKey("Abc@1234");
		user.setResetDate(Instant.now());
		userRepository.save(user);
		String newPassword="admin";
		User changedUser=userService.completePasswordReset(newPassword,user.getResetKey()).get();
		assertNull(changedUser.getResetKey());
		assertNull(changedUser.getResetDate());
	}
	
	@Test
	void testCompletePasswordResetForInvalidResetKey() throws ObjectNotFoundException {
		String newPassword="admin";
		Optional<User> changedUser=userService.completePasswordReset(newPassword,"KLKL");
		assertFalse(changedUser.isPresent());
	}

	@Test
	void testRequestPasswordReset() throws ObjectNotFoundException {
		User user = userService.createUser(creationRequestDTO);
		user.setActivated(true);
		userRepository.save(user);
		User optionalUser=userService.requestPasswordReset(user.getEmail()).get();
		assertNotNull(optionalUser.getResetKey());
		assertNotNull(optionalUser.getVerificationCode());
		assertNotNull(optionalUser.getResetDate());
	}
	
	@Test
	void testRequestPasswordResetForInvalidEmail() throws ObjectNotFoundException {
		Optional<User> optionalUser = userService.requestPasswordReset("test.invalidUser@localhost.com");
		assertFalse(optionalUser.isPresent());
	}

	@Test
	void testCreateUser() throws ObjectNotFoundException {
		User savedUser = userService.createUser(creationRequestDTO);
		assertEquals(creationRequestDTO.getName(),savedUser.getName());
		assertEquals(creationRequestDTO.getEmail(),savedUser.getEmail());
		assertEquals(creationRequestDTO.getContactNo(),savedUser.getContactNo());
		assertEquals(creationRequestDTO.getSubmittedForApproval(),savedUser.getSubmittedForApproval());
		assertTrue(savedUser.getAllowLogin());
		assertEquals(Boolean.FALSE, savedUser.getAllCompanies());
		assertEquals(creationRequestDTO.getUserType(), savedUser.getUserType());
		assertEquals(creationRequestDTO.getUserCompanies().size(), savedUser.getUserAccess().size());
		assertEquals(creationRequestDTO.getCompanyId(),savedUser.getCompanyId());
		assertEquals(creationRequestDTO.getClientId(),savedUser.getClientId());
		assertEquals(creationRequestDTO.getUserCompanies().size(),savedUser.getUserAccess().size());
		assertNotNull(savedUser.getUserRoles().size());
	}
	
	@Test
	public void checkEmailIsUnique() throws ObjectNotFoundException {
		userService.createUser(creationRequestDTO);
		assertThrows(ValidationException.class,() -> {
			userService.createUser(creationRequestDTO);
		});
	}
	
	@Test
	public void checkRolesIsRequired() throws ObjectNotFoundException {
		Set<UserCompanyCreationRequestDTO> userCompanies = new HashSet<UserCompanyCreationRequestDTO>();
		userCompanies.add(UserCompanyCreationRequestDTO.builder().companyId(1L).branchId(1L).build());
		userCompanies.add(UserCompanyCreationRequestDTO.builder().companyId(2L).allBranches(Boolean.TRUE).build());
		creationRequestDTO.setUserCompanies(userCompanies);
		assertThrows(ValidationException.class, () -> {
			userService.createUser(creationRequestDTO);
		});
	}
	
	//hibernate validation
	@Test 
	public void checkEmailIsValid() throws ObjectNotFoundException {
		creationRequestDTO.setEmail("jayeshjain");
		assertThrows(ConstraintViolationException.class,() -> {
			userService.createUser(creationRequestDTO);
		});
	}

	@Test
	void testDeleteUser() throws ObjectNotFoundException {
		User savedUser = userService.createUser(creationRequestDTO);
		userService.deleteUser(savedUser.getId());
		Optional<UserDTO> getUserById=userQueryService.findOne(savedUser.getId());
		assertFalse(getUserById.isPresent());
	}

	@Test
	@WithMockUser(username = "test.user@gmail.com")
	void testChangePassword() throws ObjectNotFoundException {
		User savedUser = userService.createUser(creationRequestDTO);
		User getUserById = userRepository.findById(savedUser.getId()).get();
		getUserById.setPassword(passwordEncoder.encode("user1"));
		userRepository.save(getUserById);
		String newPassword = "admin1";
		userService.changePassword("user1", newPassword);
		User getNewUserById = userRepository.findById(savedUser.getId()).get();
		assertTrue(passwordEncoder.matches(newPassword, getNewUserById.getPassword()));
	}
	
	@Test
	@WithMockUser(username = "test.user@gmail.com")
	void testChangePasswordThrowsExceptionForInvalidRawPassword() throws ObjectNotFoundException {
		userService.createUser(creationRequestDTO);
		String newPassword = "admin1";
		assertThrows(InvalidDataException.class, () -> {
			userService.changePassword("user1", newPassword);
		});
	}

	@Test
	void testGenerateGsaUsers() {
		List<GsaDetailsDTO> gsaDetailsDTOs = prepareGsaDetailsDto();
		Boolean value=userService.generateGsaUsers(gsaDetailsDTOs);
		assertTrue(value);
		User getUserByEmail=userService.findByEmail(gsaDetailsDTOs.get(0).getEmail());
		assertThat(getUserByEmail).isNotNull();
		assertThat(getUserByEmail.getId()).isNotNull();
		assertThat(getUserByEmail.getName()).isEqualTo(gsaDetailsDTOs.get(0).getName());
		assertThat(getUserByEmail.getContactNo()).isEqualTo(gsaDetailsDTOs.get(0).getContactNo());
		assertThat(getUserByEmail.getEmail()).isEqualTo(gsaDetailsDTOs.get(0).getEmail().toLowerCase());
		assertThat(getUserByEmail.getUserRoles()).isNotEmpty();
	}

	public static List<GsaDetailsDTO> prepareGsaDetailsDto() {
		List<GsaDetailsDTO> gsaDetailsDTOs = new LinkedList<>();
		gsaDetailsDTOs.add(GsaDetailsDTO.builder().clientId(1L).contactNo("8898685830")
				.email("testJunit.gsa@localhost.com").name("test gsa").userRoles(prepareRoleDtosForCsaGsa()).build());
		return gsaDetailsDTOs;
	}

	public static List<UserRoleDTO> prepareRoleDtosForCsaGsa() {
		List<UserRoleDTO> roleDTOs = new LinkedList<UserRoleDTO>();
		roleDTOs.add(UserRoleDTO.builder().roleId(1L).build());
		return roleDTOs;
	}

	@Test
	void testGenerateCsaUsers() {
		List<CsaDetailsDTO> csaDetailsDTOs = prepareCsaDetailsDto();
		Boolean value = userService.generateCsaUsers(csaDetailsDTOs);
		assertTrue(value);
		User getUserByEmail = userService.findByEmail(csaDetailsDTOs.get(0).getEmail());
		assertThat(getUserByEmail).isNotNull();
		assertThat(getUserByEmail.getId()).isNotNull();
		assertThat(getUserByEmail.getName()).isEqualTo(csaDetailsDTOs.get(0).getName());
		assertThat(getUserByEmail.getContactNo()).isEqualTo(csaDetailsDTOs.get(0).getContactNo());
		assertThat(getUserByEmail.getEmail()).isEqualTo(csaDetailsDTOs.get(0).getEmail().toLowerCase());
		assertThat(getUserByEmail.getUserRoles()).isNotEmpty();
	}

	public static List<CsaDetailsDTO> prepareCsaDetailsDto() {
		List<CsaDetailsDTO> csaDetailsDTOs = new LinkedList<>();
		csaDetailsDTOs.add(CsaDetailsDTO.builder().name("test csa").email("testJunit.csa@localhost.com")
				.contactNo("8898617911").clientId(1L).companyId(1L).userRoles(prepareRoleDtosForCsaGsa()).build());
		return csaDetailsDTOs;
	}
	
	@Test
	void testLockUser() throws ObjectNotFoundException {
		User savedUser = userService.createUser(creationRequestDTO);
		userService.lockUser(savedUser.getId(), Boolean.TRUE);
		User getUserById = userRepository.findById(savedUser.getId()).get();
		assertEquals(getUserById.getAccountLocked(), Boolean.TRUE);
	}

	@Test
	void testGetNewEmail() throws ObjectNotFoundException {
		User savedUser = userService.createUser(creationRequestDTO);
		String email=userService.getNewEmail(savedUser.getEmail(),savedUser.getId());
		assertThat(email).isEqualTo("x"+savedUser.getId()+"."+savedUser.getEmail());
	}
	
	@Test
	void testCompletePasswordByResetKeyAndVerficationCode() throws ObjectNotFoundException {
		User user = userService.createUser(creationRequestDTO);
		user.setResetKey("ABC@8898");
		user.setVerificationCode("889868");
		user.setPassword(passwordEncoder.encode("admin"));
		String newPassword = "user1";
		User savedUser=userRepository.save(user);
		User getUser=userService.completePasswordByResetKeyAndVerficationCode(newPassword, user.getResetKey(),
				user.getVerificationCode()).get();
		assertThat(getUser.getId()).isEqualTo(savedUser.getId());
		assertThat(getUser.getName()).isEqualTo(savedUser.getName());
		assertNull(getUser.getVerificationCode());
		assertNull(getUser.getResetKey());
		assertNull(getUser.getResetDate());
	}

	@Test
	void testFindOneWithAuthoritiesByEmailIgnoreCase() throws ObjectNotFoundException {
		User savedUser = userService.createUser(creationRequestDTO);
		User optionalUser = userService.findOneWithAuthoritiesByEmailIgnoreCase(savedUser.getEmail()).get();
		assertEquals(optionalUser.getName(), savedUser.getName());
		assertEquals(optionalUser.getEmail(), savedUser.getEmail());
		assertEquals(optionalUser.getContactNo(), savedUser.getContactNo());
		assertEquals(optionalUser.getSubmittedForApproval(), savedUser.getSubmittedForApproval());
		assertTrue(savedUser.getAllowLogin());
		assertEquals(Boolean.FALSE, savedUser.getAllCompanies());
		assertEquals(optionalUser.getUserType(), savedUser.getUserType());
		assertEquals(optionalUser.getCompanyId(), savedUser.getCompanyId());
		assertEquals(optionalUser.getClientId(), savedUser.getClientId());
		assertNotNull(savedUser.getUserRoles().size());
		assertThat(optionalUser.getUserAccess()).isNotEmpty();
		assertThat(optionalUser.getUserAccess()).hasSizeGreaterThanOrEqualTo(1);
		assertThat(optionalUser.getAuthorities()).isNotEmpty();
		assertThat(optionalUser.getAuthorities()).hasSizeGreaterThanOrEqualTo(1);
	}

	@Test
	void testFindById() throws ObjectNotFoundException {
		User savedUser = userService.createUser(creationRequestDTO);
		User getUserById=userService.findById(savedUser.getId()).get();
		assertEquals(getUserById.getName(),savedUser.getName());
		assertEquals(getUserById.getEmail(),savedUser.getEmail());
		assertEquals(getUserById.getContactNo(),savedUser.getContactNo());
		assertEquals(getUserById.getSubmittedForApproval(),savedUser.getSubmittedForApproval());
		assertTrue(savedUser.getAllowLogin());
		assertEquals(Boolean.FALSE, savedUser.getAllCompanies());
		assertEquals(getUserById.getUserType(), savedUser.getUserType());
		assertEquals(getUserById.getCompanyId(),savedUser.getCompanyId());
		assertEquals(getUserById.getClientId(),savedUser.getClientId());
	}

	@Test
	void testFindByEmail() throws ObjectNotFoundException {
		User savedUser = userService.createUser(creationRequestDTO);
		User getUserByEmail = userService.findByEmail(savedUser.getEmail());
		assertEquals(getUserByEmail.getName(), savedUser.getName());
		assertEquals(getUserByEmail.getEmail(), savedUser.getEmail());
		assertEquals(getUserByEmail.getContactNo(), savedUser.getContactNo());
		assertEquals(getUserByEmail.getSubmittedForApproval(), savedUser.getSubmittedForApproval());
		assertTrue(getUserByEmail.getAllowLogin());
		assertEquals(Boolean.FALSE, getUserByEmail.getAllCompanies());
		assertEquals(getUserByEmail.getUserType(), savedUser.getUserType());
		assertEquals(getUserByEmail.getCompanyId(), savedUser.getCompanyId());
		assertEquals(getUserByEmail.getClientId(), savedUser.getClientId());
	}

	@Test 
	@WithMockUser(username = "test.user@gmail.com")
	void testFindOneWithAuthoritiesByLogin() throws ObjectNotFoundException {
		User savedUser = userService.createUser(creationRequestDTO);
		User optionalUser=userService.findOneWithAuthoritiesByLogin(savedUser.getLogin()).get();
		assertEquals(optionalUser.getName(), savedUser.getName());
		assertEquals(optionalUser.getEmail(), savedUser.getEmail());
		assertEquals(optionalUser.getContactNo(), savedUser.getContactNo());
		assertEquals(optionalUser.getSubmittedForApproval(), savedUser.getSubmittedForApproval());
		assertTrue(savedUser.getAllowLogin());
		assertEquals(Boolean.FALSE, savedUser.getAllCompanies());
		assertEquals(optionalUser.getUserType(), savedUser.getUserType());
		assertEquals(optionalUser.getCompanyId(), savedUser.getCompanyId());
		assertEquals(optionalUser.getClientId(), savedUser.getClientId());
		assertNotNull(savedUser.getUserRoles().size());
		assertThat(optionalUser.getUserAccess()).isNotEmpty();
		assertThat(optionalUser.getUserAccess()).hasSizeGreaterThanOrEqualTo(1);
		assertThat(optionalUser.getAuthorities()).isNotEmpty();
		assertThat(optionalUser.getAuthorities()).hasSizeGreaterThanOrEqualTo(1);
	}

	@Test
	@WithMockUser(username = "test.user@gmail.com")
	void testGetUserSpecificCompanyDetails() throws ObjectNotFoundException {
		userService.createUser(creationRequestDTO);
		prepareClientModuleMock();
		UserSpecificCBResponse cbResponse=userService.getUserSpecificCompanyDetails();
		assertThat(cbResponse.getContent()).isNotEmpty();
		assertThat(cbResponse.getContent()).hasSizeGreaterThanOrEqualTo(1);
	}

	@Test 
	@WithMockUser(username = "test.user@gmail.com")
	void testGetClientIdAsPerLogggedInUser() throws ObjectNotFoundException {
		userService.createUser(creationRequestDTO);
		ClientIdDTO clientIdDTO = userService.getClientIdAsPerLogggedInUser();
		assertThat(clientIdDTO.getClientId()).isNotNull();
		assertThat(clientIdDTO.getClientId()).isEqualTo(creationRequestDTO.getClientId());
	}

	@Test
	void testCheckEmailPresent() throws ObjectNotFoundException {
		User savedUser = userService.createUser(creationRequestDTO);
		List<String> emails=new ArrayList<>();
		emails.add(savedUser.getEmail());
		emails.add("test email");
		EmailPresentDto emailPresentDto=userService.checkEmailPresent(emails);
		assertEquals(emailPresentDto.getEmailIds().size(),1);
	}
	
	@Test
	void testDeactivateTemporary() throws ObjectNotFoundException {
		User savedUser = userService.createUser(creationRequestDTO);
		UserDeactivationRequest deactivationRequest=new UserDeactivationRequest();
    	deactivationRequest.setIsPermanent(Boolean.FALSE);
		deactivationRequest.setUserId(savedUser.getId());

		userService.deactivate(deactivationRequest.getUserId(),deactivationRequest.getIsPermanent());

		User user=userService.findById(savedUser.getId()).get();
		assertThat(user.getDeactivationWefDate()).isNotNull();
		assertThat(user.getDeactivationType()).isEqualTo(UserDeactivationType.TEMPORARY);
	}
	
	@Test
	void testDeactivatePermanent() throws ObjectNotFoundException {
		User savedUser = userService.createUser(creationRequestDTO);
		UserDeactivationRequest deactivationRequest=new UserDeactivationRequest();
    	deactivationRequest.setIsPermanent(Boolean.TRUE);
		deactivationRequest.setUserId(savedUser.getId());

		userService.deactivate(deactivationRequest.getUserId(),deactivationRequest.getIsPermanent());

		User user=userService.findById(savedUser.getId()).get();
		assertThat(user.getDeactivationWefDate()).isNotNull();
		assertThat(user.getDeactivationType()).isEqualTo(UserDeactivationType.PERMANENT);
		assertTrue(user.getEmail().startsWith("x"));
	}
	
	@Test
	void testReactivate() throws ObjectNotFoundException {
		User savedUser = userService.createUser(creationRequestDTO);
		userService.reactivate(savedUser.getId());
		User getUserById=userService.findById(savedUser.getId()).get();
		assertThat(getUserById.getReactivate().getWefDate()).isNotNull();
		assertThat(getUserById.getDeactivationType()).isNull();
		assertEquals(getUserById.getSubmittedForApproval(),Boolean.TRUE);
	}
	
	@Test //if email already present
	void testValidateCsaUsersThrowsException() throws ObjectNotFoundException {
		userService.createUser(creationRequestDTO);
		CompanyDTO companyDto = new CompanyDTO();
		companyDto.setCode("CM001");
		companyDto.setName("Google_India_Mumbai");
		companyDto.setMobileNo("8898617911");
		companyDto.setEmailId("google.India@gmail.com");
		companyDto.setClientId(2L);
		companyDto.setCsaDetails(getCsaDetails());
		String existingEmails = userService.validateCsaUsers(companyDto);
		assertNotNull(existingEmails);
		assertTrue(existingEmails.contains(getCsaDetails().get(0).getEmail()));
	}
	
	@Test
	void testValidateCsaUsersDoesNotThrowsException() throws ObjectNotFoundException {
		userService.createUser(creationRequestDTO);
		CompanyDTO companyDto = new CompanyDTO();
		companyDto.setCode("CM001");
		companyDto.setName("Google_India_Mumbai");
		companyDto.setMobileNo("8898617911");
		companyDto.setEmailId("google.India@gmail.com");
		companyDto.setClientId(1L);
		companyDto.setCsaDetails(getCsaDetails());
		String existingEmails = userService.validateCsaUsers(companyDto);
		assertNull(existingEmails);
	}
	
	private static List<CsaDetailsDTO> getCsaDetails() {
		List<CsaDetailsDTO> csaDetails = new ArrayList<>();
		CsaDetailsDTO dto = CsaDetailsDTO.builder().name("Ankit").email("test.user@gmail.com").langKey("en")
				.contactNo("9562315478").build();
		csaDetails.add(dto);
		return csaDetails;
	}
	
	private void prepareClientModuleMock() {
		UserSpecificCBResponse cbResponse = new UserSpecificCBResponse();
		List<CompanyBranchResponse> companyBranchResponses = new ArrayList<>();
		companyBranchResponses
				.add(CompanyBranchResponse.builder()
						.branches(new ArrayList<>(Arrays.asList(BranchResponse.builder().companyId(1L)
								.isBranchDeactivated(Boolean.FALSE).branchId(1L).build())))
						.clientId(1L).companyId(1L).build());
		cbResponse.setContent(companyBranchResponses);
		UserSpecificCBRequest detailDto = new UserSpecificCBRequest();
		Optional<String> currentUserLogin = SecurityUtils.getCurrentUserLogin();
		if(currentUserLogin.isPresent()) {
			String email = currentUserLogin.get();
			Optional<User> optionalUser = userRepository.findOneWithAuthoritiesByEmailIgnoreCase(email);
			if(optionalUser.isPresent()) {
				User user = optionalUser.get();
				detailDto.setAllCompanies(user.getAllCompanies());
				detailDto.setClientId(user.getClientId());
				detailDto.setUserAccess(accessMapper.toDto(user.getUserAccess()));
			}
		}
		when(clientModule.userSpecificCBDetails(detailDto)).thenReturn(cbResponse);
	}

}
