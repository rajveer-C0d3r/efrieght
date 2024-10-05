package com.grtship.client.serviceimpl;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.grtship.client.criteria.CompanyCriteria;
import com.grtship.client.feignclient.MasterModule;
import com.grtship.client.feignclient.OAuthModule;
import com.grtship.client.service.CompanyService;
import com.grtship.core.dto.BaseDTO;
import com.grtship.core.dto.CompanyDTO;
import com.grtship.core.enumeration.DomainStatus;

@SpringBootTest(properties = "config.caching.enabled=false")
@Transactional
@ActiveProfiles(profiles = {"test"})
public class CompanyQueryServiceImplTest {
	
	@Autowired private CompanyService companyService;
	
	@Autowired private CompanyQueryServiceImpl companyQueryServiceImpl;
	
	@MockBean private MasterModule masterModule;
	
	@MockBean private OAuthModule authModule;

	@Autowired private EntityManager em;
	
	@Mock private Pageable pageable;
	
	private CompanyDTO companyDto;
	
	private CompanyCriteria companyCriteria;
	
	@BeforeEach
	void setUp() {
		companyDto=CompanyServiceImplTest.createEntity(em);
	}

	@Test
	void testFindByCriteriaCompanyCriteriaId() throws Exception {
		CompanyDTO savedCompany = companyService.save(companyDto);
		companyCriteria=new CompanyCriteria();
		companyCriteria.setId(savedCompany.getId());
		List<CompanyDTO> companyDTOs=companyQueryServiceImpl.findByCriteria(companyCriteria);
		assertThat(companyDTOs).allMatch(company -> company.getId()==companyCriteria.getId());
		assertThat(companyDTOs).allMatch(company -> company.getCode()!=null);
		assertThat(companyDTOs).allMatch(company -> company.getName()!=null);
		assertThat(companyDTOs).allMatch(company -> company.getStatus()!=null);
	}
	
	@Test
	void testFindByCriteriaCompanyCriteriaCode() throws Exception {
		CompanyDTO savedCompany = companyService.save(companyDto);
		companyCriteria=new CompanyCriteria();
		companyCriteria.setCode(savedCompany.getCode());
		List<CompanyDTO> companyDTOs=companyQueryServiceImpl.findByCriteria(companyCriteria);
		assertThat(companyDTOs).allMatch(company -> company.getId()!=null);
		assertThat(companyDTOs).allMatch(company -> company.getCode().equals(companyCriteria.getCode()));
		assertThat(companyDTOs).allMatch(company -> company.getName()!=null);
		assertThat(companyDTOs).allMatch(company -> company.getStatus()!=null);
	}
	
	@Test
	void testFindByCriteriaCompanyCriteriaName() throws Exception {
		CompanyDTO savedCompany = companyService.save(companyDto);
		companyCriteria=new CompanyCriteria();
		companyCriteria.setName(savedCompany.getName());
		List<CompanyDTO> companyDTOs=companyQueryServiceImpl.findByCriteria(companyCriteria);
		assertThat(companyDTOs).allMatch(company -> company.getId()!=null);
		assertThat(companyDTOs).allMatch(company -> company.getCode()!=null);
		assertThat(companyDTOs).allMatch(company -> company.getName().equals(companyCriteria.getName()));
		assertThat(companyDTOs).allMatch(company -> company.getStatus()!=null);
	}
	
	@Test
	void testFindByCriteriaCompanyCriteriaStatus() throws Exception {
		companyService.save(companyDto);
		companyCriteria=new CompanyCriteria();
		companyCriteria.setStatus("PENDING");
		List<CompanyDTO> companyDTOs=companyQueryServiceImpl.findByCriteria(companyCriteria);
		assertThat(companyDTOs).allMatch(company -> company.getId()!=null);
		assertThat(companyDTOs).allMatch(company -> company.getCode()!=null);
		assertThat(companyDTOs).allMatch(company -> company.getName()!=null);
		assertThat(companyDTOs).allMatch(company -> company.getStatus().equals(DomainStatus.valueOf(companyCriteria.getStatus())));
	}
	
	@Test
	void testFindByCriteriaCompanyCriteriaActiveFlag() throws Exception {
		CompanyDTO savedCompany = companyService.save(companyDto);
		companyCriteria=new CompanyCriteria();
		companyCriteria.setActiveFlag(savedCompany.getActiveFlag());
		List<CompanyDTO> companyDTOs=companyQueryServiceImpl.findByCriteria(companyCriteria);
		assertThat(companyDTOs).allMatch(company -> company.getId()!=null);
		assertThat(companyDTOs).allMatch(company -> company.getCode()!=null);
		assertThat(companyDTOs).allMatch(company -> company.getName()!=null);
	    assertThat(companyDTOs).allMatch(company -> company.getStatus()!=null);
	    assertThat(companyDTOs).allMatch(company -> company.getActiveFlag().equals(companyCriteria.getActiveFlag()));
	}
	
	@Test
	void testFindByCriteriaCompanyCriteriaPageable() throws Exception {
		CompanyDTO savedCompany = companyService.save(companyDto);
		companyCriteria = new CompanyCriteria();
		companyCriteria.setId(savedCompany.getId());
		Page<CompanyDTO> companyDTOs = companyQueryServiceImpl.findByCriteria(companyCriteria, PageRequest.of(0, 20));
		assertThat(companyDTOs.getSize()).isEqualTo(20);
		assertThat(companyDTOs.getContent()).allMatch(company -> company.getId() == companyCriteria.getId());
		assertThat(companyDTOs.getContent()).allMatch(company -> company.getCode() != null);
		assertThat(companyDTOs.getContent()).allMatch(company -> company.getName() != null);
		assertThat(companyDTOs.getContent()).allMatch(company -> company.getStatus() != null);
	}

	@Test
	void testFindOne() throws Exception {
		CompanyDTO savedCompany = companyService.save(companyDto);
		CompanyDTO getCompanyById=companyQueryServiceImpl.findOne(savedCompany.getId()).get();
		assertThat(getCompanyById.getId()).isEqualTo(savedCompany.getId());
		assertThat(getCompanyById.getName()).isEqualTo(savedCompany.getName());
		assertThat(getCompanyById.getCode()).isEqualTo(savedCompany.getCode());
		assertThat(getCompanyById.getEmailId()).isEqualTo(savedCompany.getEmailId());
		assertThat(getCompanyById.getMobileNo()).isEqualTo(savedCompany.getMobileNo());
		assertThat(getCompanyById.getActiveFlag()).isEqualTo(savedCompany.getActiveFlag());
		assertThat(getCompanyById.getStatus()).isEqualTo(savedCompany.getStatus());
		assertThat(getCompanyById.getPanNo()).isEqualTo(savedCompany.getPanNo());
		assertThat(getCompanyById.getGstNo()).isEqualTo(savedCompany.getGstNo());
		assertThat(getCompanyById.getWithholdingTaxId()).isEqualTo(savedCompany.getWithholdingTaxId());
		assertThat(getCompanyById.getIncorporationDate()).isEqualTo(savedCompany.getIncorporationDate());
		assertThat(getCompanyById.getFixedFinancialYearFlag()).isEqualTo(savedCompany.getFixedFinancialYearFlag());
		assertThat(getCompanyById.getFinancialYearStartMonth()).isEqualTo(savedCompany.getFinancialYearStartMonth());
		assertThat(getCompanyById.getFinancialYearEndMonth()).isEqualTo(savedCompany.getFinancialYearEndMonth());
		assertThat(getCompanyById.getAddress()).isNotNull();
	}

	@Test
	void testFindByClientIdIn() throws Exception {
		CompanyDTO savedCompany = companyService.save(companyDto);
		List<Long> clientIds=new ArrayList<>();
		clientIds.add(savedCompany.getClientId());
		List<BaseDTO> baseDTOs=companyQueryServiceImpl.findByClientIdIn(clientIds);
		assertThat(baseDTOs).hasSizeGreaterThanOrEqualTo(1);
		assertThat(baseDTOs).allMatch(base -> base.getId()!=null);
		assertThat(baseDTOs).allMatch(base -> base.getName()!=null);
	}
}
