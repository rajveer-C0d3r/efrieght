package com.grtship.account.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;

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

import com.grtship.account.adaptor.MasterModuleAdapter;
import com.grtship.account.criteria.LedgerCriteria;
import com.grtship.account.service.LedgerService;
import com.grtship.core.dto.LedgerDTO;
import com.grtship.core.enumeration.DomainStatus;
import com.grtship.core.filter.StringFilter;

@SpringBootTest(properties = "config.caching.enabled=false")
@Transactional
@ActiveProfiles(profiles = { "test" })
public class LedgerQueryServiceImplTest {
	
	private static final String LEDGER = "Ledger";

	@Autowired
	private LedgerService ledgerService;
	
	@Autowired 
	private LedgerQueryServiceImpl ledgerQueryServiceImpl;

	@MockBean
	private MasterModuleAdapter masterModuleAdapter;

	private LedgerDTO ledgerDTO;
	
	@Autowired
	private EntityManager em;
	
	private LedgerCriteria ledgerCriteria;
	
	@BeforeEach
	void prepareLedgerDto() {
		ledgerDTO=LedgerServiceImplTest.prepareLedgerDTO(em);
	}

	@Test
	void testFindByCriteriaLedgerCriteriaName() {
		prepareCodeGenerateMocking();
		LedgerDTO savedLedger = ledgerService.save(ledgerDTO);
		ledgerCriteria=new LedgerCriteria();
		StringFilter stringFilter=new StringFilter();
		stringFilter.setEquals(savedLedger.getName());
		ledgerCriteria.setName(stringFilter);
		List<LedgerDTO> ledgerDTOs=ledgerQueryServiceImpl.findByCriteria(ledgerCriteria);
		assertThat(ledgerDTOs).hasSizeGreaterThanOrEqualTo(1);
		assertThat(ledgerDTOs).allMatch(ledger -> ledger.getName().equals(savedLedger.getName()));
		assertThat(ledgerDTOs).allMatch(ledger -> ledger.getActiveFlag()!=null);
		assertThat(ledgerDTOs).allMatch(ledger -> ledger.getCode()!=null);
		assertThat(ledgerDTOs).allMatch(ledger -> ledger.getCostCenterApplicableFlag()!=null);
		assertThat(ledgerDTOs).allMatch(ledger -> ledger.getDebitCredit()!=null);
	}
	
	@Test
	void testFindByCriteriaLedgerCriteriaCode() {
		prepareCodeGenerateMocking();
		LedgerDTO savedLedger = ledgerService.save(ledgerDTO);
		ledgerCriteria=new LedgerCriteria();
		StringFilter stringFilter=new StringFilter();
		stringFilter.setEquals(savedLedger.getCode());
		ledgerCriteria.setCode(stringFilter);
		List<LedgerDTO> ledgerDTOs=ledgerQueryServiceImpl.findByCriteria(ledgerCriteria);
		assertThat(ledgerDTOs).hasSizeGreaterThanOrEqualTo(1);
		assertThat(ledgerDTOs).allMatch(ledger -> ledger.getName()!=null);
		assertThat(ledgerDTOs).allMatch(ledger -> ledger.getActiveFlag()!=null);
		assertThat(ledgerDTOs).allMatch(ledger -> ledger.getCode().equals(savedLedger.getCode()));
		assertThat(ledgerDTOs).allMatch(ledger -> ledger.getCostCenterApplicableFlag()!=null);
		assertThat(ledgerDTOs).allMatch(ledger -> ledger.getDebitCredit()!=null);
	}
	
	@Test
	void testFindByCriteriaLedgerCriteriaActiveFlag() {
		prepareCodeGenerateMocking();
		LedgerDTO savedLedger = ledgerService.save(ledgerDTO);
		ledgerCriteria=new LedgerCriteria();
		ledgerCriteria.setActiveFlag(savedLedger.getActiveFlag());
		List<LedgerDTO> ledgerDTOs=ledgerQueryServiceImpl.findByCriteria(ledgerCriteria);
		assertThat(ledgerDTOs).hasSizeGreaterThanOrEqualTo(1);
		assertThat(ledgerDTOs).allMatch(ledger -> ledger.getName()!=null);
		assertThat(ledgerDTOs).allMatch(ledger -> ledger.getActiveFlag().equals(Boolean.FALSE));
		assertThat(ledgerDTOs).allMatch(ledger -> ledger.getCode()!=null);
		assertThat(ledgerDTOs).allMatch(ledger -> ledger.getCostCenterApplicableFlag()!=null);
		assertThat(ledgerDTOs).allMatch(ledger -> ledger.getDebitCredit()!=null);
	}
	
	@Test
	void testFindByCriteriaLedgerCriteriaStatus() {
		prepareCodeGenerateMocking();
		ledgerService.save(ledgerDTO);
		ledgerCriteria = new LedgerCriteria();
		ledgerCriteria.setStatus("PENDING");
		List<LedgerDTO> ledgerDTOs = ledgerQueryServiceImpl.findByCriteria(ledgerCriteria);
		assertThat(ledgerDTOs).hasSizeGreaterThanOrEqualTo(1);
		assertThat(ledgerDTOs).allMatch(ledger -> ledger.getName() != null);
		assertThat(ledgerDTOs).allMatch(ledger -> ledger.getActiveFlag() != null);
		assertThat(ledgerDTOs).allMatch(ledger -> ledger.getCode() != null);
		assertThat(ledgerDTOs).allMatch(ledger -> ledger.getCostCenterApplicableFlag() != null);
		assertThat(ledgerDTOs).allMatch(ledger -> ledger.getDebitCredit() != null);
		assertThat(ledgerDTOs)
				.allMatch(ledger -> ledger.getStatus().equals(DomainStatus.valueOf(ledgerCriteria.getStatus())));
	}
	
	@Test
	void testFindByCriteriaLedgerCriteriaAlias() {
		prepareCodeGenerateMocking();
		ledgerService.save(ledgerDTO);
		ledgerCriteria = new LedgerCriteria();
		ledgerCriteria.setAlias("ledge1");
		List<LedgerDTO> ledgerDTOs = ledgerQueryServiceImpl.findByCriteria(ledgerCriteria);
		assertThat(ledgerDTOs).hasSizeGreaterThanOrEqualTo(1);
		assertThat(ledgerDTOs).allMatch(ledger -> ledger.getName() != null);
		assertThat(ledgerDTOs).allMatch(ledger -> ledger.getActiveFlag() != null);
		assertThat(ledgerDTOs).allMatch(ledger -> ledger.getCode() != null);
		assertThat(ledgerDTOs).allMatch(ledger -> ledger.getCostCenterApplicableFlag() != null);
		assertThat(ledgerDTOs).allMatch(ledger -> ledger.getAlias().stream()
				.filter(alias -> alias.getName().equals(ledgerCriteria.getAlias())).count() > 0);
	}
	
	@Test
	void testFindByCriteriaLedgerCriteriaGroupId() {
		prepareCodeGenerateMocking();
		LedgerDTO savedLedger = ledgerService.save(ledgerDTO);
		ledgerCriteria=new LedgerCriteria();
		ledgerCriteria.setGroupId(savedLedger.getGroupId());
		List<LedgerDTO> ledgerDTOs=ledgerQueryServiceImpl.findByCriteria(ledgerCriteria);
		assertThat(ledgerDTOs).hasSizeGreaterThanOrEqualTo(1);
		assertThat(ledgerDTOs).allMatch(ledger -> ledger.getName()!=null);
		assertThat(ledgerDTOs).allMatch(ledger -> ledger.getActiveFlag()!=null);
		assertThat(ledgerDTOs).allMatch(ledger -> ledger.getCode()!=null);
		assertThat(ledgerDTOs).allMatch(ledger -> ledger.getCostCenterApplicableFlag()!=null);
		assertThat(ledgerDTOs).allMatch(ledger -> ledger.getDebitCredit()!=null);
		assertThat(ledgerDTOs).allMatch(ledger -> ledger.getGroupId()==savedLedger.getGroupId());
	}

	@Test
	void testFindByCriteriaLedgerCriteriaPageable() {
		prepareCodeGenerateMocking();
		LedgerDTO savedLedger = ledgerService.save(ledgerDTO);
		ledgerCriteria=new LedgerCriteria();
		ledgerCriteria.setGroupId(savedLedger.getGroupId());
		Page<LedgerDTO> ledgerDTOs=ledgerQueryServiceImpl.findByCriteria(ledgerCriteria,PageRequest.of(0,20));
		assertThat(ledgerDTOs.getNumber()).isEqualTo(0);
		assertThat(ledgerDTOs.getContent()).hasSizeLessThanOrEqualTo(20);
		assertThat(ledgerDTOs.getContent()).allMatch(ledger -> ledger.getName()!=null);
		assertThat(ledgerDTOs.getContent()).allMatch(ledger -> ledger.getActiveFlag()!=null);
		assertThat(ledgerDTOs.getContent()).allMatch(ledger -> ledger.getCode()!=null);
		assertThat(ledgerDTOs.getContent()).allMatch(ledger -> ledger.getCostCenterApplicableFlag()!=null);
		assertThat(ledgerDTOs.getContent()).allMatch(ledger -> ledger.getDebitCredit()!=null);
	}

	@Test
	void testFindOne() {
		prepareCodeGenerateMocking();
		LedgerDTO savedLedger = ledgerService.save(ledgerDTO);
		LedgerDTO getLedgerById=ledgerQueryServiceImpl.findOne(savedLedger.getId()).get();
		assertThat(savedLedger.getCode()).isEqualTo(getLedgerById.getCode());
		assertThat(savedLedger.getName()).isEqualTo(getLedgerById.getName());
		assertThat(savedLedger.getCompanyId()).isEqualTo(getLedgerById.getCompanyId());
		assertThat(savedLedger.getClientId()).isEqualTo(getLedgerById.getClientId());
		assertThat(savedLedger.getStatus()).isEqualTo(getLedgerById.getStatus());
		assertThat(savedLedger.getActiveFlag()).isEqualTo(Boolean.FALSE);
	}

	@Test
	void testCountByCriteria() {
		prepareCodeGenerateMocking();
		LedgerDTO savedLedger = ledgerService.save(ledgerDTO);
		ledgerCriteria=new LedgerCriteria();
		StringFilter stringFilter=new StringFilter();
		stringFilter.setEquals(savedLedger.getName());
		ledgerCriteria.setName(stringFilter);
		Long count=ledgerQueryServiceImpl.countByCriteria(ledgerCriteria);
		assertThat(count).isGreaterThan(0);
	}

	private void prepareCodeGenerateMocking() {
		when(masterModuleAdapter.generateCode(LEDGER, null)).thenReturn("LR001");
	}
}
