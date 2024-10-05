package com.grtship.client.serviceimpl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.grtship.client.criteria.CompanyBranchCriteria;
import com.grtship.client.feignclient.MasterModule;
import com.grtship.client.service.CompanyBranchQueryService;
import com.grtship.client.service.CompanyBranchService;
import com.grtship.core.dto.CompanyBranchBaseDTO;
import com.grtship.core.dto.CompanyBranchDTO;
import com.grtship.core.dto.DeactivationDTO;
import com.grtship.core.dto.UserCompanyDTO;
import com.grtship.core.enumeration.DomainStatus;

@SpringBootTest(properties = "config.caching.enabled=false")
@Transactional
@ActiveProfiles(profiles = {"test"})
public class CompanyBranchQueryServiceImplTest {
	
	@Autowired private EntityManager em;
		
	@Autowired private CompanyBranchService branchService;
	
	@MockBean private MasterModule masterModule;
	
	@Autowired private CompanyBranchQueryService branchQueryService;
	
	private CompanyBranchDTO companyBranchDTO;
	
	private CompanyBranchCriteria branchCriteria;

	@BeforeEach
	void setUp() throws Exception {
		companyBranchDTO=CompanyBranchServiceImplTest.CreateCompanyBranchDto(em);
	}

	@Test
	void testFindByCriteriaCompanyBranchCriteriaName() {
		prepareCodeGenerateMocking();
		CompanyBranchDTO savedBranch = branchService.save(companyBranchDTO);
		branchCriteria=new CompanyBranchCriteria();
		branchCriteria.setName(savedBranch.getName());
		List<CompanyBranchDTO> companyBranchDTOs=branchQueryService.findByCriteria(branchCriteria);
		assertThat(companyBranchDTOs).allMatch(branchObj -> branchObj.getId()!=null);
		assertThat(companyBranchDTOs).allMatch(branchObj -> branchObj.getName().equals(branchCriteria.getName()));
		assertThat(companyBranchDTOs).allMatch(branchObj -> branchObj.getCode()!=null);
		assertThat(companyBranchDTOs).allMatch(branchObj -> branchObj.getActiveFlag()!=null);
	}
	
	@Test
	void testFindByCriteriaCompanyBranchCriteriaCode() {
		prepareCodeGenerateMocking();
		CompanyBranchDTO savedBranch = branchService.save(companyBranchDTO);
		branchCriteria=new CompanyBranchCriteria();
		branchCriteria.setCode(savedBranch.getCode());
		List<CompanyBranchDTO> companyBranchDTOs=branchQueryService.findByCriteria(branchCriteria);
		assertThat(companyBranchDTOs).allMatch(branchObj -> branchObj.getId()!=null);
		assertThat(companyBranchDTOs).allMatch(branchObj -> branchObj.getName()!=null);
		assertThat(companyBranchDTOs).allMatch(branchObj -> branchObj.getCode().equals(branchCriteria.getCode()));
		assertThat(companyBranchDTOs).allMatch(branchObj -> branchObj.getActiveFlag()!=null);
	}
	
	@Test
	void testFindByCriteriaCompanyBranchCriteriaStatus() {
		prepareCodeGenerateMocking();
		branchService.save(companyBranchDTO);
		branchCriteria = new CompanyBranchCriteria();
		branchCriteria.setStatus("PENDING");
		List<CompanyBranchDTO> companyBranchDTOs = branchQueryService.findByCriteria(branchCriteria);
		assertThat(companyBranchDTOs).allMatch(branchObj -> branchObj.getId() != null);
		assertThat(companyBranchDTOs).allMatch(branchObj -> branchObj.getName() != null);
		assertThat(companyBranchDTOs).allMatch(branchObj -> branchObj.getCode() != null);
		assertThat(companyBranchDTOs).allMatch(branchObj -> branchObj.getActiveFlag() != null);
		assertThat(companyBranchDTOs)
				.allMatch(branchObj -> branchObj.getStatus().equals(DomainStatus.valueOf(branchCriteria.getStatus())));
	}
	
	@Test
	void testFindByCriteriaCompanyBranchCriteriaCompanyId() {
		prepareCodeGenerateMocking();
		CompanyBranchDTO savedBranch = branchService.save(companyBranchDTO);
		branchCriteria=new CompanyBranchCriteria();
		branchCriteria.setCompanyId(savedBranch.getCompanyId());
		List<CompanyBranchDTO> companyBranchDTOs=branchQueryService.findByCriteria(branchCriteria);
		assertThat(companyBranchDTOs).allMatch(branchObj -> branchObj.getId()!=null);
		assertThat(companyBranchDTOs).allMatch(branchObj -> branchObj.getName()!=null);
		assertThat(companyBranchDTOs).allMatch(branchObj -> branchObj.getCode()!=null);
		assertThat(companyBranchDTOs).allMatch(branchObj -> branchObj.getActiveFlag()!=null);
		assertThat(companyBranchDTOs).allMatch(branchObj -> branchObj.getCompanyId()==branchCriteria.getCompanyId());
	}
	
	@Test
	void testFindByCriteriaCompanyBranchCriteriaLocation() {
		prepareCodeGenerateMocking();
		CompanyBranchDTO savedBranch = branchService.save(companyBranchDTO);
		branchCriteria = new CompanyBranchCriteria();
		branchCriteria.setLocation(savedBranch.getAddress().getLocation());
		List<CompanyBranchDTO> companyBranchDTOs = branchQueryService.findByCriteria(branchCriteria);
		assertThat(companyBranchDTOs).allMatch(branchObj -> branchObj.getId() != null);
		assertThat(companyBranchDTOs).allMatch(branchObj -> branchObj.getName() != null);
		assertThat(companyBranchDTOs).allMatch(branchObj -> branchObj.getCode() != null);
		assertThat(companyBranchDTOs).allMatch(branchObj -> branchObj.getActiveFlag() != null);
		assertThat(companyBranchDTOs).allMatch(branchObj -> branchObj.getCompanyId() != null);
		assertThat(companyBranchDTOs)
				.allMatch(branchObj -> branchObj.getAddress().getLocation().equals(branchCriteria.getLocation()));
	}
	
	@Test
	void testFindByCriteriaCompanyBranchCriteriaActiveFlag() {
		prepareCodeGenerateMocking();
		CompanyBranchDTO savedBranch = branchService.save(companyBranchDTO);
		branchCriteria = new CompanyBranchCriteria();
		branchCriteria.setActiveFlag(savedBranch.getActiveFlag());
		List<CompanyBranchDTO> companyBranchDTOs = branchQueryService.findByCriteria(branchCriteria);
		assertThat(companyBranchDTOs).allMatch(branchObj -> branchObj.getId() != null);
		assertThat(companyBranchDTOs).allMatch(branchObj -> branchObj.getName() != null);
		assertThat(companyBranchDTOs).allMatch(branchObj -> branchObj.getCode() != null);
		assertThat(companyBranchDTOs).allMatch(branchObj -> branchObj.getActiveFlag() != null);
		assertThat(companyBranchDTOs).allMatch(branchObj -> branchObj.getCompanyId() != null);
		assertThat(companyBranchDTOs)
				.allMatch(branchObj -> branchObj.getActiveFlag().equals(branchCriteria.getActiveFlag()));
	}
	
	@Test
	void testFindByCriteriaCompanyBranchSubmittedForApproval() {
		prepareCodeGenerateMocking();
		CompanyBranchDTO savedBranch = branchService.save(companyBranchDTO);
		branchCriteria = new CompanyBranchCriteria();
		branchCriteria.setSubmitForApproval(savedBranch.getSubmittedForApproval());
		List<CompanyBranchDTO> companyBranchDTOs = branchQueryService.findByCriteria(branchCriteria);
		assertThat(companyBranchDTOs).allMatch(branchObj -> branchObj.getId() != null);
		assertThat(companyBranchDTOs).allMatch(branchObj -> branchObj.getName() != null);
		assertThat(companyBranchDTOs).allMatch(branchObj -> branchObj.getCode() != null);
		assertThat(companyBranchDTOs).allMatch(branchObj -> branchObj.getActiveFlag() != null);
		assertThat(companyBranchDTOs).allMatch(
				branchObj -> branchObj.getSubmittedForApproval().equals(branchCriteria.getSubmitForApproval()));
	}
	
	@Test
	void testFindByCriteriaCompanyBranchCityId() {
		prepareCodeGenerateMocking();
		CompanyBranchDTO savedBranch = branchService.save(companyBranchDTO);
		branchCriteria = new CompanyBranchCriteria();
		branchCriteria.setCityId(savedBranch.getAddress().getCityId());
		List<CompanyBranchDTO> companyBranchDTOs = branchQueryService.findByCriteria(branchCriteria);
		assertThat(companyBranchDTOs).allMatch(branchObj -> branchObj.getId() != null);
		assertThat(companyBranchDTOs).allMatch(branchObj -> branchObj.getName() != null);
		assertThat(companyBranchDTOs).allMatch(branchObj -> branchObj.getCode() != null);
		assertThat(companyBranchDTOs).allMatch(branchObj -> branchObj.getActiveFlag() != null);
		assertThat(companyBranchDTOs)
				.allMatch(branchObj -> branchObj.getAddress().getCityId() == branchCriteria.getCityId());
	}
	

	@Test
	void testFindByCriteriaCompanyBranchCriteriaPageable() {
		prepareCodeGenerateMocking();
		CompanyBranchDTO savedBranch = branchService.save(companyBranchDTO);
		branchCriteria = new CompanyBranchCriteria();
		branchCriteria.setName(savedBranch.getName());
		Page<CompanyBranchDTO> companyBranchDTOs = branchQueryService.findByCriteria(branchCriteria,
				PageRequest.of(0, 20));
		assertThat(companyBranchDTOs.getSize()).isEqualTo(20);
		assertThat(companyBranchDTOs.getContent()).allMatch(branchObj -> branchObj.getId() != null);
		assertThat(companyBranchDTOs.getContent())
				.allMatch(branchObj -> branchObj.getName().equals(branchCriteria.getName()));
		assertThat(companyBranchDTOs.getContent()).allMatch(branchObj -> branchObj.getCode() != null);
		assertThat(companyBranchDTOs.getContent()).allMatch(branchObj -> branchObj.getActiveFlag() != null);
	}

	@Test
	void testFindOne() {
		prepareCodeGenerateMocking();
		CompanyBranchDTO savedBranch = branchService.save(companyBranchDTO);
		Optional<CompanyBranchDTO> getBranchById=branchQueryService.findOne(savedBranch.getId());
		assertTrue(getBranchById.isPresent());
		assertThat(savedBranch.getId()).isNotNull();
		assertThat(savedBranch.getCode()).isNotBlank();
		assertThat(savedBranch.getSubmittedForApproval()).isTrue();
		assertThat(savedBranch.getName()).isEqualTo(getBranchById.get().getName());
		assertThat(savedBranch.getCode()).isEqualTo(getBranchById.get().getCode());
		assertThat(savedBranch.getStatus()).isEqualTo(getBranchById.get().getStatus());
		assertThat(savedBranch.getActiveFlag()).isEqualTo(Boolean.FALSE);
		assertThat(savedBranch.getStatus()).isEqualTo(DomainStatus.PENDING);
	}

	@Test
	void testFindByCompanyIdForMultiDropdown() {
		prepareCodeGenerateMocking();
		CompanyBranchDTO savedBranch = branchService.save(companyBranchDTO);
		List<Long> companyIds = new ArrayList<>();
		companyIds.add(savedBranch.getCompanyId());
		List<CompanyBranchBaseDTO> branchBaseDTOs = branchQueryService.findByCompanyIdForMultiDropdown(companyIds);
		assertThat(branchBaseDTOs).isNotNull();
		assertThat(branchBaseDTOs).allMatch(baseObj -> baseObj.getId() != null);
		assertThat(branchBaseDTOs).allMatch(baseObj -> baseObj.getName() != null);
		assertThat(branchBaseDTOs).allMatch(baseObj -> baseObj.getCode() != null);
	}

	@Test
	void testFindByIdForMultiDropdown() {
		prepareCodeGenerateMocking();
		CompanyBranchDTO savedBranch = branchService.save(companyBranchDTO);
		List<Long> companyIds = new ArrayList<>();
		companyIds.add(savedBranch.getCompanyId());
		List<CompanyBranchBaseDTO> branchBaseDTOs = branchQueryService.findByCompanyIdForMultiDropdown(companyIds);
		assertThat(branchBaseDTOs).isNotNull();
		assertThat(branchBaseDTOs).hasSize(1);
		assertThat(branchBaseDTOs).allMatch(baseObj -> baseObj.getId() != null);
		assertThat(branchBaseDTOs).allMatch(baseObj -> baseObj.getName() != null);
		assertThat(branchBaseDTOs).allMatch(baseObj -> baseObj.getCode() != null);
	}

	@Test
	void testGetBranchIdsByCompanyIdAndActiveFlag() {
		prepareCodeGenerateMocking();
		companyBranchDTO.setActiveFlag(Boolean.TRUE);
		CompanyBranchDTO savedBranch = branchService.save(companyBranchDTO);
		List<Long> branchIds = branchQueryService.getBranchIdsByCompanyIdAndActiveFlag(savedBranch.getCompanyId(),
				savedBranch.getActiveFlag());
		assertThat(branchIds).hasSizeGreaterThanOrEqualTo(1);
	}
	
	private DeactivationDTO prepareDeactivationDTO() {
		DeactivationDTO deactivateDto = new DeactivationDTO();
		deactivateDto.setDeactivationWefDate(LocalDate.now());
		deactivateDto.setDeactivationReason("I no longer need this");
		deactivateDto.setType("DEACTIVATE");
		deactivateDto.setDeactivateAutoGenerateId("BHJJ984851KM");
		return deactivateDto;
	}

	@Test
	void testGetBranchIdsByCompanyIdAndActiveFlagAndDeactivateAutoGenerateId() {
		prepareCodeGenerateMocking();
		companyBranchDTO.setActiveFlag(Boolean.TRUE);
		CompanyBranchDTO savedBranch = branchService.save(companyBranchDTO);
		DeactivationDTO deactivationDTO=prepareDeactivationDTO();
		deactivationDTO.setReferenceId(savedBranch.getId());
		CompanyBranchDTO deactivatedBranch = branchService.deactivate(deactivationDTO);
		CompanyBranchDTO getBranchById=branchQueryService.findOne(savedBranch.getId()).get();
		System.out.println("Deactivate Id"+getBranchById.getDeactivateDtls().getDeactivateAutoGenerateId());
		List<Long> branchIds = branchQueryService.getBranchIdsByCompanyIdAndActiveFlagAndDeactivateAutoGenerateId(
				savedBranch.getCompanyId(), savedBranch.getActiveFlag(),
				deactivatedBranch.getDeactivateDtls().getDeactivateAutoGenerateId());
		assertThat(branchIds).hasSizeGreaterThanOrEqualTo(1);
	}

	@Test
	void testFindByCompanyIdWithAllBranches() {
		prepareCodeGenerateMocking();
		CompanyBranchDTO savedBranch = branchService.save(companyBranchDTO);
		List<Long> companyIds = new ArrayList<>();
		companyIds.add(savedBranch.getCompanyId());
		List<UserCompanyDTO> userCompanyDTOs=branchQueryService.findByCompanyIdWithAllBranches(companyIds);
		assertThat(userCompanyDTOs).isNotEmpty();
		assertThat(userCompanyDTOs).allMatch(userCompany -> userCompany.getBranchId()!=null);
		assertThat(userCompanyDTOs).allMatch(userCompany -> userCompany.getBranchName()!=null);
	}

	@Test
	void testFindByIdWithSpecificBranches() {
		prepareCodeGenerateMocking();
		CompanyBranchDTO savedBranch = branchService.save(companyBranchDTO);
		List<Long> companyIds = new ArrayList<>();
		companyIds.add(savedBranch.getCompanyId());
		List<UserCompanyDTO> userCompanyDTOs=branchQueryService.findByCompanyIdWithAllBranches(companyIds);
		assertThat(userCompanyDTOs).isNotEmpty();
		assertThat(userCompanyDTOs).allMatch(userCompany -> userCompany.getBranchId()!=null);
		assertThat(userCompanyDTOs).allMatch(userCompany -> userCompany.getBranchName()!=null);
	}

	private void prepareCodeGenerateMocking() {
		when(masterModule.generateCode("Company Branch", null)).thenReturn("BRN789");
	}
}
