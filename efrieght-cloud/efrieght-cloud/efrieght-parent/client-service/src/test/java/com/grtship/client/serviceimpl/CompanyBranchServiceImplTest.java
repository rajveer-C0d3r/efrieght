package com.grtship.client.serviceimpl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.validation.ConstraintViolationException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.grtship.client.domain.Client;
import com.grtship.client.domain.Company;
import com.grtship.client.feignclient.MasterModule;
import com.grtship.client.service.CompanyBranchService;
import com.grtship.common.exception.InvalidDataException;
import com.grtship.common.exception.ValidationException;
import com.grtship.core.dto.AddressDTO;
import com.grtship.core.dto.BranchGstDetailsDTO;
import com.grtship.core.dto.CompanyBranchDTO;
import com.grtship.core.dto.DeactivationDTO;
import com.grtship.core.dto.ReactivationDTO;
import com.grtship.core.dto.UserAccessCompanyBranchDTO;
import com.grtship.core.dto.UserSpecificBranchDTO;
import com.grtship.core.enumeration.DomainStatus;

@SpringBootTest(properties = "config.caching.enabled=false")
@Transactional
@ActiveProfiles(profiles = {"test"})
public class CompanyBranchServiceImplTest {
	
	@Autowired private EntityManager em;
	
	@Autowired private CompanyBranchService branchService;
	
	@MockBean private MasterModule masterModule;
	
	private CompanyBranchDTO companyBranchDTO;
	
	public static CompanyBranchDTO CreateCompanyBranchDto(EntityManager em) {	
		Client client = CompanyServiceImplTest.prepareClient();
		em.persist(client);
		em.flush();
		
		Company company = prepareCompany(em, client);
		
		
		AddressDTO branchAddress = CompanyServiceImplTest.getAddress();
		branchAddress.setLocation("Andheri");
		
		Set<BranchGstDetailsDTO> gstDetails = new HashSet<>();
		BranchGstDetailsDTO branchGstDetail = new BranchGstDetailsDTO();
		branchGstDetail.setGstNo("GVH16958165KM");
		branchGstDetail.setNatureOfBusinessActivity("Xerox");
		gstDetails.add(branchGstDetail);
		
		CompanyBranchDTO branchDto = new CompanyBranchDTO();
		branchDto.setStatus(DomainStatus.PENDING);
		branchDto.setName("Test Junit Branch");
		branchDto.setClientId(client.getId());
		branchDto.setCompanyId(company.getId());
		branchDto.setAddress(branchAddress);
		branchDto.setBranchGstDetails(gstDetails);	
		return branchDto;
	}

	public static Company prepareCompany(EntityManager em, Client client) {
		Company company = new Company();
		company.setClient(client);
		company.setCode("CMP456");
		company.setName("Google_India");
		company.setStatus(DomainStatus.APPROVED);
		company.setActiveFlag(Boolean.TRUE);
		company.setSubmittedForApproval(Boolean.FALSE);
		em.persist(company);
		em.flush();
		return company;
	}

	@BeforeEach
	void setUp() throws Exception {
		companyBranchDTO=CreateCompanyBranchDto(em);
	}


	@Test
	void testSave() {
		prepareCodeGenerateMocking();
		CompanyBranchDTO savedBranch = branchService.save(companyBranchDTO);
		assertThat(savedBranch).isNotNull();
		assertThat(savedBranch.getId()).isNotNull();
		assertThat(savedBranch.getCode()).isNotBlank();
		assertThat(savedBranch.getSubmittedForApproval()).isTrue();
		assertThat(savedBranch.getName()).isEqualTo(companyBranchDTO.getName());
		assertThat(savedBranch.getStatus()).isEqualTo(companyBranchDTO.getStatus());
		assertThat(savedBranch.getActiveFlag()).isEqualTo(Boolean.FALSE);
		assertThat(savedBranch.getStatus()).isEqualTo(DomainStatus.PENDING);
	}
	
	@Test
	void testSaveAsDraft() {
		prepareCodeGenerateMocking();
		CompanyBranchDTO branchDto = companyBranchDTO;
		branchDto.setStatus(DomainStatus.DRAFT);
		
		CompanyBranchDTO savedBranch = branchService.save(branchDto);
		assertThat(savedBranch).isNotNull();
		assertThat(savedBranch.getId()).isNotNull();
		assertThat(savedBranch.getCode()).isNotNull();
		assertThat(savedBranch.getStatus()).isEqualTo(DomainStatus.DRAFT);
		assertThat(savedBranch.getName()).isEqualTo(branchDto.getName());
		assertThat(savedBranch.getClientId()).isEqualTo(branchDto.getClientId());
		assertThat(savedBranch.getCompanyId()).isEqualTo(branchDto.getCompanyId());
	}

	@Test
	void testUpdate() {
		prepareCodeGenerateMocking();
		companyBranchDTO.setStatus(DomainStatus.APPROVED);
		CompanyBranchDTO savedBranch = branchService.save(companyBranchDTO);
		savedBranch.setName("Test Junit Update");
		savedBranch.setStatus(DomainStatus.APPROVED);
		savedBranch.setActiveFlag(Boolean.TRUE);
		CompanyBranchDTO updateBranch = branchService.update(savedBranch);
		assertThat(updateBranch).isNotNull();
		assertThat(updateBranch.getId()).isEqualTo(savedBranch.getId());
		assertThat(updateBranch.getSubmittedForApproval()).isEqualTo(Boolean.TRUE);
		assertThat(updateBranch.getCode()).isEqualTo(savedBranch.getCode());
		assertThat(updateBranch.getName()).isNotEqualTo(companyBranchDTO.getName());
		assertThat(updateBranch.getStatus()).isEqualTo(DomainStatus.APPROVED);
		assertThat(updateBranch.getActiveFlag()).isEqualTo(Boolean.TRUE);
	}
	
	@Test
	void testUpdatBranchWithPendingApproval() {
		prepareCodeGenerateMocking();
		CompanyBranchDTO branchDto = companyBranchDTO;
		branchDto.setStatus(DomainStatus.PENDING);
		CompanyBranchDTO savedBranch = branchService.save(branchDto);
		assertThrows(InvalidDataException.class, () -> {
			branchService.update(savedBranch);	
		});
	}
	

	@Test
	void checkClientIdIsNull() {
		prepareCodeGenerateMocking();
		CompanyBranchDTO branchDto = CreateCompanyBranchDto(em);
		branchDto.setClientId(null);
		assertThrows(ConstraintViolationException.class, () -> {
			branchService.save(branchDto);	
		});
	}
	
	@Test
	void checkCodeIsUnique() {
		prepareCodeGenerateMocking();
		CompanyBranchDTO branchDto = CreateCompanyBranchDto(em);
		branchService.save(branchDto);
		assertThrows(ValidationException.class, () -> {
			branchService.save(branchDto);	
		});
	}
	
	@Test
	void checkNameIsUnique() {
		prepareCodeGenerateMocking();
		CompanyBranchDTO branchDto = CreateCompanyBranchDto(em);
		branchService.save(branchDto);
		branchDto.setCode("BR456");
		assertThrows(ValidationException.class, () -> {
			branchService.save(branchDto);	
		});
	}
	
	@Test
	void checkCityIsRequired() {
		prepareCodeGenerateMocking();
		CompanyBranchDTO branchDto = CreateCompanyBranchDto(em);
		branchDto.getAddress().setCityId(null);
		assertThrows(ValidationException.class, () -> {
			branchService.save(branchDto);	
		});
	}
	
	@Test
	void checkLocationIsRequired() {
		prepareCodeGenerateMocking();
		CompanyBranchDTO branchDto = CreateCompanyBranchDto(em);
		branchDto.getAddress().setLocation(null);
		assertThrows(ValidationException.class, () -> {
			branchService.save(branchDto);	
		});
	}
	
	@Test
	void checkStatusIsRequired() {
		prepareCodeGenerateMocking();
		CompanyBranchDTO branchDto = CreateCompanyBranchDto(em);
		branchDto.setStatus(null);
		assertThrows(ConstraintViolationException.class, () -> {
			branchService.save(branchDto);	
		});
	}

	private DeactivationDTO prepareDeactivationDTO() {
		DeactivationDTO deactivateDto = new DeactivationDTO();
		deactivateDto.setDeactivationWefDate(LocalDate.now());
		deactivateDto.setDeactivationReason("I no longer need this");
		deactivateDto.setType("DEACTIVATE");
		return deactivateDto;
	}
	
	@Test
	void testDeactivate() {
		prepareCodeGenerateMocking();
		CompanyBranchDTO branchDto = CreateCompanyBranchDto(em);
		CompanyBranchDTO companyBranch = branchService.save(branchDto);
		DeactivationDTO deactivateDto = prepareDeactivationDTO();
		deactivateDto.setReferenceId(companyBranch.getId());

		CompanyBranchDTO deactivatedBranch = branchService.deactivate(deactivateDto);

		assertThat(deactivatedBranch).isNotNull();
		assertThat(deactivatedBranch.getDeactivateDtls()).isNotNull();
		assertThat(deactivatedBranch.getDeactivateDtls().getDeactivationReason())
				.isEqualTo(deactivateDto.getDeactivationReason());
		assertThat(deactivatedBranch.getDeactivateDtls().getDeactivationWefDate())
				.isEqualTo(deactivateDto.getDeactivationWefDate());
		assertThat(deactivatedBranch.getDeactivateDtls().getDeactivatedDate()).isNull();
	}
	
	@Test
	void deactivationShouldThrowException() {
		DeactivationDTO deactivateDto = prepareDeactivationDTO();
		deactivateDto.setReferenceId(0L);
		Assertions.assertThrows(InvalidDataException.class, () -> {
			branchService.deactivate(deactivateDto);
		});
	}
	
	private ReactivationDTO prepareRectivationDTO() {
		ReactivationDTO activateDto = new ReactivationDTO();
		activateDto.setReactivationWefDate(LocalDate.now());
		activateDto.setReactivationReason("Approved");
		activateDto.setType("Reactivate");
		return activateDto;
	}
	
	@Test
	void testReactivate() {
		prepareCodeGenerateMocking();
		CompanyBranchDTO branchDto = CreateCompanyBranchDto(em);
		CompanyBranchDTO companyBranch = branchService.save(branchDto);
		ReactivationDTO reactivateDto = prepareRectivationDTO();
		reactivateDto.setReferenceId(companyBranch.getId());
		CompanyBranchDTO reactivatedBranch = branchService.reactivate(reactivateDto);
		assertThat(reactivatedBranch).isNotNull();
		assertThat(reactivatedBranch.getReactivateDtls()).isNotNull();
		assertThat(reactivatedBranch.getReactivateDtls().getReactivationReason())
				.isEqualTo(reactivateDto.getReactivationReason());
		assertThat(reactivatedBranch.getReactivateDtls().getReactivationWefDate())
				.isEqualTo(reactivateDto.getReactivationWefDate());
		assertThat(reactivatedBranch.getReactivateDtls().getReactivatedDate()).isNull();
	}
	
	@Test
	void reactivationShouldThrowException() {
		ReactivationDTO reactivateDto = prepareRectivationDTO();
		reactivateDto.setReferenceId(0L);
		assertThrows(InvalidDataException.class, () -> {
			branchService.reactivate(reactivateDto);
		});
	}
	

	@Test
	void testGetUserSpecificBranchDetails() {
		prepareCodeGenerateMocking();
		CompanyBranchDTO savedBranch = branchService.save(companyBranchDTO);
		UserAccessCompanyBranchDTO accessCompanyBranchDTO=new UserAccessCompanyBranchDTO();
		accessCompanyBranchDTO.setCompanyId(savedBranch.getCompanyId());
		accessCompanyBranchDTO.setAllBranches(Boolean.TRUE);
		List<UserSpecificBranchDTO> branchDTOs=branchService.getUserSpecificBranchDetails(accessCompanyBranchDTO);
		assertThat(branchDTOs).isNotEmpty();
		assertThat(branchDTOs).allMatch(branch -> branch.getCode()!=null);
		assertThat(branchDTOs).allMatch(branch -> branch.getName()!=null);
		assertThat(branchDTOs).allMatch(branch -> branch.getIsBranchDeactivated()!=null);
	}
	
	private void prepareCodeGenerateMocking() {
		when(masterModule.generateCode("Company Branch", null)).thenReturn("BRN789");
	}

}
