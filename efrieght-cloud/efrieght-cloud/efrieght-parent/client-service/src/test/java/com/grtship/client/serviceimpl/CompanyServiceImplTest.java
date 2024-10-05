package com.grtship.client.serviceimpl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.EntityManager;
import javax.validation.ConstraintViolationException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.grtship.client.domain.Address;
import com.grtship.client.domain.Client;
import com.grtship.client.feignclient.MasterModule;
import com.grtship.client.feignclient.OAuthModule;
import com.grtship.client.service.CompanyService;
import com.grtship.common.exception.InvalidDataException;
import com.grtship.common.exception.ValidationException;
import com.grtship.core.dto.AddressDTO;
import com.grtship.core.dto.CompanyDTO;
import com.grtship.core.dto.CsaDetailsDTO;
import com.grtship.core.dto.DeactivationDTO;
import com.grtship.core.dto.ReactivationDTO;
import com.grtship.core.dto.UserAccessCompanyBranchDTO;
import com.grtship.core.dto.UserSpecificCompanyDTO;
import com.grtship.core.enumeration.DomainStatus;
import com.grtship.core.enumeration.GstVatType;
import com.grtship.core.enumeration.UserType;

@SpringBootTest(properties = "config.caching.enabled=false")
@Transactional
@ActiveProfiles(profiles = {"test"})
public class CompanyServiceImplTest {
	
	@Autowired private CompanyService companyService;
	
	@MockBean private MasterModule masterModule;
	
	@MockBean private OAuthModule authModule;

	@Autowired private EntityManager em;
	
	@Mock private Pageable pageable;
	
	private CompanyDTO companyDto;
	
	public static CompanyDTO createEntity(EntityManager em) {
		Client client = prepareClient();
		em.persist(client);
		CompanyDTO companyDto = new CompanyDTO();
		companyDto.setClientCode("C001");
		companyDto.setClientName("Google India");
		companyDto.setCode("CM001");
		companyDto.setName("Google_India_Mumbai");
		companyDto.setMobileNo("8898617911");
		companyDto.setEmailId("google.India@gmail.com");
		companyDto.setPanNo("SDCHTRF2145");
		companyDto.setGstNo("GDTRE5412458");
		companyDto.setGstVatType(GstVatType.GST);
		companyDto.setWithholdingTaxId("DFERT21548");
		companyDto.setIncorporationDate(LocalDate.now());
		companyDto.setFixedFinancialYearFlag(true);
		companyDto.setFinancialYearStartMonth(Month.APRIL);
		companyDto.setFinancialYearEndMonth(Month.MARCH);
		companyDto.setActiveFlag(Boolean.FALSE);
		companyDto.setStatus(DomainStatus.PENDING);
		companyDto.setCreatedBy("admin@localhost.com");
		companyDto.setClientId(client.getId());
		companyDto.setAddress(getAddress());
		companyDto.setCsaDetails(getCsaDetails());
		return companyDto;
	}

	public static Client prepareClient() {
		Address address = new Address();
		address.setAddress("JB Nagar");
		address.setCountryId(1L);
		address.setCity(1L);
		address.setState(1L);
		address.setPincode("435678");
		Client client = new Client();
		client.setCode("C001");
		client.setName("Google India");
		client.setMobileNo("9495623548");
		client.setEmailId("google@gmail.com");
		client.setPanNo("SDFD451255");
		client.setSalesTaxId("DFER5452");
		client.setAddress(address);
		return client;
	}

	public static AddressDTO getAddress() {
		AddressDTO addressDto = new AddressDTO();
		addressDto.setAddress("JB Nagar");
		addressDto.setCountryId(1L);
		addressDto.setCityId(1L);
		addressDto.setStateId(1L);
		addressDto.setPincode("435678");
		return addressDto;
	}
	
	private static List<CsaDetailsDTO> getCsaDetails() {
		List<CsaDetailsDTO> csaDetails = new ArrayList<>();
		CsaDetailsDTO dto = CsaDetailsDTO.builder().name("Ankit").email("akit@gmail.com").langKey("en")
				.contactNo("9562315478").build();
		csaDetails.add(dto);
		return csaDetails;
	}

	@BeforeEach
	void setUp() throws Exception {
		companyDto=createEntity(em);
	}

	@Test
	void testSave() throws Exception {
		CompanyDTO savedCompany = companyService.save(companyDto);
		assertThat(savedCompany.getName()).isEqualTo(companyDto.getName());
		assertThat(savedCompany.getCode()).isEqualTo(companyDto.getCode());
		assertThat(savedCompany.getEmailId()).isEqualTo(companyDto.getEmailId());
		assertThat(savedCompany.getMobileNo()).isEqualTo(companyDto.getMobileNo());
		assertThat(savedCompany.getActiveFlag()).isEqualTo(companyDto.getActiveFlag());
		assertThat(savedCompany.getStatus()).isEqualTo(companyDto.getStatus());
		assertThat(savedCompany.getPanNo()).isEqualTo(companyDto.getPanNo());
		assertThat(savedCompany.getGstNo()).isEqualTo(companyDto.getGstNo());
		assertThat(savedCompany.getWithholdingTaxId()).isEqualTo(companyDto.getWithholdingTaxId());
		assertThat(savedCompany.getIncorporationDate()).isEqualTo(companyDto.getIncorporationDate());
		assertThat(savedCompany.getFixedFinancialYearFlag()).isEqualTo(companyDto.getFixedFinancialYearFlag());
		assertThat(savedCompany.getFinancialYearStartMonth()).isEqualTo(companyDto.getFinancialYearStartMonth());
		assertThat(savedCompany.getFinancialYearEndMonth()).isEqualTo(companyDto.getFinancialYearEndMonth());
		assertThat(savedCompany.getAddress()).isNotNull();
		assertThat(savedCompany.getClientId()).isEqualTo(companyDto.getClientId());
	}
	
	@Test
	void testSaveAsDraft() throws Exception {
		CompanyDTO dto = new CompanyDTO();
		dto.setStatus(DomainStatus.DRAFT);
		dto.setCode("CMP789");
		dto.setName("Google_Mumbai_Malad");
		dto.setEmailId("google.malad@gmail.com");
		dto.setClientId(companyDto.getClientId());
		CompanyDTO savedCompany=companyService.save(dto);
		assertEquals(savedCompany.getName(),dto.getName());
		assertEquals(savedCompany.getCode(),dto.getCode());
		assertEquals(savedCompany.getEmailId(),dto.getEmailId());
		assertEquals(savedCompany.getStatus(),DomainStatus.DRAFT);
	}

	@Test
	void testUpdate() throws Exception {
		CompanyDTO savedCompany = companyService.save(companyDto);
		savedCompany.setName("VRL Logistics");
		savedCompany.setEmailId("vrl@asf.co");
		CompanyDTO updatedCompany = companyService.update(savedCompany);
		assertThat(updatedCompany.getId()).isEqualTo(savedCompany.getId());
		assertThat(updatedCompany.getName()).isNotEqualTo(companyDto.getName());
		assertThat(updatedCompany.getEmailId()).isNotEqualTo(companyDto.getEmailId());
		assertThat(savedCompany.getWithholdingTaxId()).isEqualTo(companyDto.getWithholdingTaxId());
		assertThat(savedCompany.getIncorporationDate()).isEqualTo(companyDto.getIncorporationDate());
		assertThat(savedCompany.getFixedFinancialYearFlag()).isEqualTo(companyDto.getFixedFinancialYearFlag());
		assertThat(savedCompany.getFinancialYearStartMonth()).isEqualTo(companyDto.getFinancialYearStartMonth());
		assertThat(savedCompany.getFinancialYearEndMonth()).isEqualTo(companyDto.getFinancialYearEndMonth());
	}
	
	@Test
	void checkCompanyNameIsRequired() throws Exception {
		companyDto.setName(null);
		assertThrows(ConstraintViolationException.class, () -> 
		      companyService.save(companyDto)
		);
	}
	
	@Test
	@WithMockUser(username = "admin@localhost.com")		
	void checkClientCodeIsRequiredForAdminUser() {
		when(authModule.getUserType("admin@localhost.com")).thenReturn(Optional.of(UserType.ADMIN));
		companyDto.setClientCode(null);
		assertThrows(ValidationException.class, () -> 
		      companyService.save(companyDto)
		);
	}

	@Test
	@WithMockUser(username = "jayesh.gsa@gmail.com")
	void checkClientCodeIsNotRequiredForClientUser() {
		when(authModule.getUserType("jayesh.gsa@gmail.com")).thenReturn(Optional.of(UserType.CLIENT));
		companyDto.setClientCode(null);
		assertDoesNotThrow(() -> companyService.save(companyDto));
	}
	
	@Test
	@WithMockUser(username = "admin@localhost.com")
	void checkClientNameIsRequiredForAdminUser() {
		when(authModule.getUserType("admin@localhost.com")).thenReturn(Optional.of(UserType.ADMIN));
		companyDto.setClientName(null);
		assertThrows(ValidationException.class, () -> companyService.save(companyDto));
	}

	@Test
	@WithMockUser(username = "jayesh.gsa@gmail.com")
	void checkClientNameIsNotRequiredForClientUser() {
		when(authModule.getUserType("jayesh.gsa@gmail.com")).thenReturn(Optional.of(UserType.CLIENT));
		companyDto.setClientName(null);
		assertDoesNotThrow(() -> companyService.save(companyDto));
	}

	@Test
	@WithMockUser(username = "admin@localhost.com")
	void checkCompanyCodeIsRequiredForAdminUser() {
		when(authModule.getUserType("admin@localhost.com")).thenReturn(Optional.of(UserType.ADMIN));
		companyDto.setCode(null);
		assertThrows(ConstraintViolationException.class, () -> 
		       companyService.save(companyDto)
		);
	}

	@Test
	@WithMockUser(username = "jayesh.gsa@gmail.com")
	void checkCompanyCodeIsRequiredForClientUser() {
		when(authModule.getUserType("jayesh.gsa@gmail.com")).thenReturn(Optional.of(UserType.CLIENT));
		companyDto.setCode(null);
		assertThrows(ConstraintViolationException.class, () -> 
		        companyService.save(companyDto)
		);
	}

	@Test
	@WithMockUser(username = "admin@localhost.com")
	void checkEmailIdIsNotRequiredForAdminUser() {
		when(authModule.getUserType("admin@localhost.com")).thenReturn(Optional.of(UserType.ADMIN));
		companyDto.setEmailId(null);
		assertDoesNotThrow(() -> companyService.save(companyDto));
	}

	@Test
	@WithMockUser(username = "jayesh.gsa@gmail.com")
	void checkEmailIdIsRequiredForClientUser() {
		when(authModule.getUserType("jayesh.gsa@gmail.com")).thenReturn(Optional.of(UserType.CLIENT));
		companyDto.setEmailId(null);
		assertThrows(ValidationException.class, () -> companyService.save(companyDto));
	}

	@Test
	@WithMockUser(username = "admin@localhost.com")
	void checkPanNoIsNotRequiredForAdminUser() {
		when(authModule.getUserType("admin@localhost.com")).thenReturn(Optional.of(UserType.ADMIN));
		companyDto.setPanNo(null);
		assertDoesNotThrow(() -> companyService.save(companyDto));
	}

	@Test
	@WithMockUser(username = "jayesh.gsa@gmail.com")
	void checkPanNoIsRequiredForClientUser() {
		when(authModule.getUserType("jayesh.gsa@gmail.com")).thenReturn(Optional.of(UserType.CLIENT));
		companyDto.setPanNo(null);
		assertThrows(ValidationException.class, () -> 
		    companyService.save(companyDto)
		);
	}

	@Test
	@WithMockUser(username = "admin@localhost.com")
	void checkGstVatTypeIsNotRequiredForAdminUser() {
		when(authModule.getUserType("admin@localhost.com")).thenReturn(Optional.of(UserType.ADMIN));
		companyDto.setGstVatType(null);
		assertDoesNotThrow(() -> companyService.save(companyDto));
	}

	@Test
	@WithMockUser(username = "jayesh.gsa@gmail.com")
	void checkGstVatTypeIsRequiredForClientUser() {
		when(authModule.getUserType("jayesh.gsa@gmail.com")).thenReturn(Optional.of(UserType.CLIENT));
		companyDto.setGstVatType(null);
		assertThrows(ValidationException.class, () -> companyService.save(companyDto));
	}

	@Test
	@WithMockUser(username = "admin@localhost.com")
	void checkGstNoIsNotRequiredForAdminUser() {
		when(authModule.getUserType("admin@localhost.com")).thenReturn(Optional.of(UserType.ADMIN));
		companyDto.setGstNo(null);
		assertDoesNotThrow(() -> companyService.save(companyDto));
	}

	@Test
	@WithMockUser(username = "jayesh.gsa@gmail.com")
	void checkGstNoIsRequiredForClientUser() {
		when(authModule.getUserType("jayesh.gsa@gmail.com")).thenReturn(Optional.of(UserType.CLIENT));
		companyDto.setGstNo(null);
		assertThrows(ValidationException.class, () -> companyService.save(companyDto));
	}

	@Test
	@WithMockUser(username = "admin@localhost.com")
	void checkIncorporationDateIsNotRequiredForAdminUser() {
		when(authModule.getUserType("admin@localhost.com")).thenReturn(Optional.of(UserType.ADMIN));
		companyDto.setIncorporationDate(null);
		assertDoesNotThrow(() -> companyService.save(companyDto));
	}

	@Test
	@WithMockUser(username = "jayesh.gsa@gmail.com")
	void checkIncorporationDateIsRequiredForClientUser() {
		when(authModule.getUserType("jayesh.gsa@gmail.com")).thenReturn(Optional.of(UserType.CLIENT));
		companyDto.setIncorporationDate(null);
		assertThrows(ValidationException.class, () -> companyService.save(companyDto));
	}

	@Test
	@WithMockUser(username = "admin@localhost.com")
	void checkFixedFinancialYearFlagIsNotRequiredForAdminUser() {
		when(authModule.getUserType("admin@localhost.com")).thenReturn(Optional.of(UserType.ADMIN));
		companyDto.setFixedFinancialYearFlag(null);
		assertDoesNotThrow(() -> companyService.save(companyDto));
	}

	@Test
	@WithMockUser(username = "jayesh.gsa@gmail.com")
	void checkFixedFinancialYearFlagIsRequiredForClientUser() {
		when(authModule.getUserType("jayesh.gsa@gmail.com")).thenReturn(Optional.of(UserType.CLIENT));
		companyDto.setFixedFinancialYearFlag(null);
		assertThrows(ValidationException.class, () -> companyService.save(companyDto));
	}
	
	@Test
	@WithMockUser(username = "admin@localhost.com")
	void checkFinancialYearStartDateIsNotRequiredForAdminUser() {
		when(authModule.getUserType("admin@localhost.com")).thenReturn(Optional.of(UserType.ADMIN));
		companyDto.setFinancialYearStartMonth(null);
		assertDoesNotThrow(() -> companyService.save(companyDto));
	}

	@Test
	@WithMockUser(username = "jayesh.gsa@gmail.com")
	void checkFinancialYearStartDateIsRequiredForClientUser() {
		when(authModule.getUserType("jayesh.gsa@gmail.com")).thenReturn(Optional.of(UserType.CLIENT));
		companyDto.setFinancialYearStartMonth(null);;
		assertThrows(ValidationException.class, () -> companyService.save(companyDto));
	}
	
	@Test
	@WithMockUser(username = "admin@localhost.com")
	void checkFinancialYearEndDateIsNotRequiredForAdminUser() {
		when(authModule.getUserType("admin@localhost.com")).thenReturn(Optional.of(UserType.ADMIN));
		companyDto.setFinancialYearEndMonth(null);
		assertDoesNotThrow(() -> companyService.save(companyDto));
	}

	@Test
	@WithMockUser(username = "jayesh.gsa@gmail.com")
	void checkFinancialYearEndDateIsRequiredForClientUser() {
		when(authModule.getUserType("jayesh.gsa@gmail.com")).thenReturn(Optional.of(UserType.CLIENT));
		companyDto.setFinancialYearEndMonth(null);
		assertThrows(ValidationException.class, () -> companyService.save(companyDto));
	}

	@Test
	@WithMockUser(username = "admin@localhost.com")
	void checkHeadquarterAddressIsNotRequiredForAdminUser() {
		when(authModule.getUserType("admin@localhost.com")).thenReturn(Optional.of(UserType.ADMIN));
		companyDto.getAddress().setAddress(null);
		assertDoesNotThrow(() -> companyService.save(companyDto));
	}

	@Test
	@WithMockUser(username = "jayesh.gsa@gmail.com")
	void checkHeadquarterAddressIsRequiredForClientUser() {
		when(authModule.getUserType("jayesh.gsa@gmail.com")).thenReturn(Optional.of(UserType.CLIENT));
		companyDto.getAddress().setAddress(null);
		assertThrows(ValidationException.class, () -> companyService.save(companyDto));
	}

	@Test
	@WithMockUser(username = "admin@localhost.com")
	void checkCityIsNotRequiredForAdminUser() {
		prepareMocking();
		when(authModule.getUserType("admin@localhost.com")).thenReturn(Optional.of(UserType.ADMIN));
		companyDto.getAddress().setCityId(null);
		assertDoesNotThrow(() -> companyService.save(companyDto));
	}

	@Test
	@WithMockUser(username = "jayesh.gsa@gmail.com")
	void checkCityIsRequiredForClientUser() {
		when(authModule.getUserType("jayesh.gsa@gmail.com")).thenReturn(Optional.of(UserType.CLIENT));
		companyDto.getAddress().setCityId(null);
		assertThrows(ValidationException.class, () -> companyService.save(companyDto));
	}

	@Test
	@WithMockUser(username = "admin@localhost.com")
	void checkStateIsNotRequiredForAdminUser() {
		prepareMocking();
		when(authModule.getUserType("admin@localhost.com")).thenReturn(Optional.of(UserType.ADMIN));
		companyDto.getAddress().setStateId(null);
		assertDoesNotThrow(() -> companyService.save(companyDto));
	}

	@Test
	@WithMockUser(username = "jayesh.gsa@gmail.com")
	void checkStateIsRequiredForClientUser() {
		prepareMocking();
		when(authModule.getUserType("jayesh.gsa@gmail.com")).thenReturn(Optional.of(UserType.CLIENT));
		companyDto.getAddress().setStateId(null);
		assertThrows(ValidationException.class, () -> companyService.save(companyDto));
	}

	@Test
	@WithMockUser(username = "admin@localhost.com")
	void checkPincodeIsNotRequiredForAdminUser() {
		when(authModule.getUserType("admin@localhost.com")).thenReturn(Optional.of(UserType.ADMIN));
		companyDto.getAddress().setPincode(null);
		assertDoesNotThrow(() -> companyService.save(companyDto));
	}

	@Test
	@WithMockUser(username = "jayesh.gsa@gmail.com")
	void checkPincodeIsRequiredForClientUser() {
		when(authModule.getUserType("jayesh.gsa@gmail.com")).thenReturn(Optional.of(UserType.CLIENT));
		companyDto.getAddress().setPincode(null);
		assertThrows(ValidationException.class, () -> companyService.save(companyDto));
	}

	@Test
	void checkCompanyCountryIsRequiredForAdminAndClientUser() {
		companyDto.getAddress().setCountryId(null);
		assertThrows(ValidationException.class, () -> companyService.save(companyDto));
	}

	@Test
	void checkCsaDetailsIsRequiredForAdminAndClientUser() {
		companyDto.setCsaDetails(null);
		assertThrows(ValidationException.class, () -> companyService.save(companyDto));
	}
	
	@Test
	void checkWithholdingTaxIdIsUnique() throws Exception {
		companyService.save(companyDto);
		companyDto.setCode("CMP005");
		companyDto.setName("Google_Mumbai_andheri");
		companyDto.setGstNo("VU84665165KM");
		companyDto.setPanNo("VHM8546151MK");
		assertThrows(ValidationException.class, () -> companyService.save(companyDto));
	}
	
	@Test
	void checkGstNoIsUnique() throws Exception {
		companyService.save(companyDto);
		companyDto.setCode("CMP005");
		companyDto.setName("Google_Mumbai_andheri");
		companyDto.setWithholdingTaxId("VYUB84651MK");
		assertThrows(ValidationException.class, () -> companyService.save(companyDto));
	}

	@Test
	void checkPanNoIsUnique() throws Exception {
		companyService.save(companyDto);
		companyDto.setCode("CMP005");
		companyDto.setName("Google_Mumbai_andheri");
		companyDto.setGstNo("VU84665165KM");
		companyDto.setWithholdingTaxId("VYUB84651MK");
		assertThrows(ValidationException.class, () -> companyService.save(companyDto));
	}

	
	@Test
	@WithMockUser(username = "user1@gmail.com")
	void checkFinancialYearStartMonthNotRequiredForClientUserForFalseFixedFinancialYearFlag() {
		prepareMocking();
		when(authModule.getUserType("admin@localhost.com")).thenReturn(Optional.of(UserType.CLIENT));
		companyDto.setFixedFinancialYearFlag(false);
		companyDto.setFinancialYearStartMonth(null);
		assertDoesNotThrow(() -> companyService.save(companyDto));
	}

	@Test
	@WithMockUser(username = "jayesh.gsa@gmail.com")
	void checkFinancialYearEndMonthNotRequiredForClientUserForFalseFixedFinancialYearFlag() {
		prepareMocking();
		when(authModule.getUserType("jayesh.gsa@gmail.com")).thenReturn(Optional.of(UserType.CLIENT));
		companyDto.setFixedFinancialYearFlag(false);
		companyDto.setFinancialYearEndMonth(null);
		assertDoesNotThrow(() -> companyService.save(companyDto));
	}
	
	private DeactivationDTO prepareDeactivationDTO() {
		DeactivationDTO deactivateDto = new DeactivationDTO();
		deactivateDto.setDeactivationWefDate(LocalDate.now());
		deactivateDto.setDeactivationReason("Data is not properly Filled");
		deactivateDto.setType("DEACTIVATE");
		return deactivateDto;
	}

	@Test
	void testDeactivate() throws Exception {
		CompanyDTO company=companyService.save(companyDto);
		DeactivationDTO deactivateDto = prepareDeactivationDTO();
		deactivateDto.setReferenceId(company.getId());
		CompanyDTO dto = companyService.deactivate(deactivateDto);
		assertThat(dto).isNotNull();
		assertThat(dto.getDeactivateDtls().getDeactivationReason()).isEqualTo(deactivateDto.getDeactivationReason());
		assertThat(dto.getDeactivateDtls().getDeactivationWefDate()).isEqualTo(deactivateDto.getDeactivationWefDate());
	}

	@Test
	void testDeactivateShouldThrowExceptionForWefIsPast() throws Exception {
		CompanyDTO company=companyService.save(companyDto);
		DeactivationDTO deactivateDto = prepareDeactivationDTO();
		deactivateDto.setReferenceId(company.getId());
		deactivateDto.setDeactivationWefDate(LocalDate.now().minusDays(50));
		assertThrows(ValidationException.class, () -> {
			companyService.deactivate(deactivateDto);
		});
	}
	
	@Test
	void testDeactivateShouldThrowExceptionForInvalidCompany() {
		DeactivationDTO deactivateDto = prepareDeactivationDTO();
		deactivateDto.setReferenceId(0L);
		assertThrows(InvalidDataException.class, () -> {
			companyService.deactivate(deactivateDto);
		});
	}

	private ReactivationDTO prepareActivationDTO() {
		ReactivationDTO activateDto = new ReactivationDTO();
		activateDto.setReactivationWefDate(LocalDate.now());
		activateDto.setReactivationReason("I want to reactivate this Company");
		activateDto.setType("REACTIVATE");
		return activateDto;
	}

	@Test
	void testReactivate() throws Exception {
		DeactivationDTO deactivateDto = prepareDeactivationDTO();
		deactivateDto.setDeactivateAutoGenerateId("1");
		companyDto.setDeactivateDtls(deactivateDto);
		CompanyDTO company=companyService.save(companyDto);
		ReactivationDTO activateDto = prepareActivationDTO();
		activateDto.setReferenceId(company.getId());
		CompanyDTO dto = companyService.reactivate(activateDto);
		assertThat(dto).isNotNull();
		assertThat(dto.getReactivateDtls().getReactivationReason())
				.isEqualTo(activateDto.getReactivationReason());
		assertThat(dto.getReactivateDtls().getReactivationWefDate())
				.isEqualTo(activateDto.getReactivationWefDate());

	}

	@Test
	void testReactivateShouldThrowExceptionForWefIsPast() throws Exception {
		CompanyDTO company=companyService.save(companyDto);
		ReactivationDTO activateDto = prepareActivationDTO();
		activateDto.setReferenceId(company.getId());
		activateDto.setReactivationWefDate(LocalDate.now().minusDays(50));
		Assertions.assertThrows(ValidationException.class, () -> {
			companyService.reactivate(activateDto);
		});
	}
	
	@Test
	void testReactivateShouldThrowExceptionForInvalidCompany() {
		ReactivationDTO activateDto = prepareActivationDTO();
		activateDto.setReferenceId(0L);
		Assertions.assertThrows(InvalidDataException.class, () -> {
			companyService.reactivate(activateDto);
		});
	}


	@Test
	void testUserSpecificCompanyDetails() throws Exception {
		CompanyDTO savedCompany = companyService.save(companyDto);
		UserAccessCompanyBranchDTO accessCompanyBranchDTO=new UserAccessCompanyBranchDTO();
		Set<Long> companyIds=new TreeSet<>();
		companyIds.add(savedCompany.getId());
		accessCompanyBranchDTO.setCompanyIds(companyIds);
        List<UserSpecificCompanyDTO> specificCompanyDTOs=companyService.userSpecificCompanyDetails(accessCompanyBranchDTO);
        assertThat(specificCompanyDTOs).isNotEmpty();
        assertThat(specificCompanyDTOs).allMatch(company -> company.getName()!=null);
        assertThat(specificCompanyDTOs).allMatch(company -> company.getCode()!=null);
        assertThat(specificCompanyDTOs).allMatch(company -> company.getCompanyId()==savedCompany.getId());
        assertThat(specificCompanyDTOs).allMatch(company -> company.getIsCompanyDeactivated()!=null);
	}
	
	private void prepareMocking() {
		when(masterModule.isStateMandatoryForGivenCountry(1L)).thenReturn(true);
	}

}
