package com.grtship.account.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.Period;
import java.util.HashSet;
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

import com.grtship.account.adaptor.MasterModuleAdapter;
import com.grtship.account.domain.Group;
import com.grtship.account.domain.Ledger;
import com.grtship.account.repository.LedgerRepository;
import com.grtship.account.service.LedgerService;
import com.grtship.common.exception.InvalidDataException;
import com.grtship.common.exception.ValidationException;
import com.grtship.core.dto.DeactivationDTO;
import com.grtship.core.dto.LedgerDTO;
import com.grtship.core.dto.ObjectAliasDTO;
import com.grtship.core.dto.ReactivationDTO;
import com.grtship.core.enumeration.DebitCredit;
import com.grtship.core.enumeration.DomainStatus;
import com.grtship.core.enumeration.NatureOfGroup;

@SpringBootTest(properties = "config.caching.enabled=false")
@Transactional
@ActiveProfiles(profiles = { "test" })
public class LedgerServiceImplTest {

	private static final String LEDGER = "Ledger";

	@Autowired
	private LedgerRepository ledgerRepository;

	@Autowired
	private LedgerService ledgerService;

	@MockBean
	private MasterModuleAdapter masterModuleAdapter;

	private LedgerDTO ledgerDTO;
	
	@Autowired
	private EntityManager em;

	public static Ledger prepareLedger() {
		Ledger ledger = new Ledger();
		ledger.setClientId(1L);
		ledger.setCompanyId(1L);
		ledger.setName("ledger1");
		ledger.setStatus(DomainStatus.PENDING);
		ledger.setCostCenterApplicableFlag(Boolean.TRUE);
		ledger.setOpeningBalanceDate(LocalDate.now());
		ledger.setOpeningBalance(850.50);
		ledger.setDebitCredit(DebitCredit.CREDIT);
		return ledger;
	}
	
	public static LedgerDTO prepareLedgerDTO(EntityManager em) {
		LedgerDTO ledgerDTO=new LedgerDTO();
		ledgerDTO.setName(LEDGER);
		ledgerDTO.setClientId(1L);
		ledgerDTO.setCompanyId(1L);
		ledgerDTO.setName("ledger1");
		ledgerDTO.setStatus(DomainStatus.PENDING);
		ledgerDTO.setCostCenterApplicableFlag(Boolean.TRUE);
		ledgerDTO.setOpeningBalanceDate(LocalDate.now());
		ledgerDTO.setOpeningBalance(850.50);
		ledgerDTO.setDebitCredit(DebitCredit.CREDIT);
		ledgerDTO.setActiveFlag(Boolean.FALSE);
		Group group = prepareGroup();
		em.persist(group);
		ledgerDTO.setGroupId(group.getId());
		ledgerDTO.setAlias(prepareAliases());
		return ledgerDTO;
	}

	private static Group prepareGroup() {
		Group group = new Group();
		group.activeFlag(Boolean.FALSE);
		group.affectsGrossProfitFlag(Boolean.FALSE);
		group.code("GRP123");
		group.name("Group Ledge");
		group.natureOfGroup(NatureOfGroup.ASSETS);
		group.netBalanceFlag(Boolean.FALSE);
		group.subGroupFlag(Boolean.FALSE);
		return group;
	}

	public static Set<ObjectAliasDTO> prepareAliases() {
		Set<ObjectAliasDTO> aliases = new HashSet<>();
		aliases.add(ObjectAliasDTO.builder().name("ledge1").build());
		aliases.add(ObjectAliasDTO.builder().name("ledge2").build());
		return aliases;
	}

	@BeforeEach
	void setUp() throws Exception {
		ledgerDTO = prepareLedgerDTO(em);
	}

	@Test
	void testSave() {
		prepareCodeGenerateMocking();
		LedgerDTO savedLedger = ledgerService.save(ledgerDTO);
		assertThat(savedLedger.getCode()).isEqualTo(ledgerDTO.getCode());
		assertThat(savedLedger.getName()).isEqualTo(ledgerDTO.getName());
		assertThat(savedLedger.getCompanyId()).isEqualTo(ledgerDTO.getCompanyId());
		assertThat(savedLedger.getClientId()).isEqualTo(ledgerDTO.getClientId());
		assertThat(savedLedger.getSubmittedForApproval()).isEqualTo(Boolean.TRUE);
		assertThat(savedLedger.getStatus()).isEqualTo(DomainStatus.PENDING);
		assertThat(savedLedger.getActiveFlag()).isEqualTo(Boolean.FALSE);
	}

	@Test
	void testUpdate() {
		prepareCodeGenerateMocking();
		LedgerDTO savedLedger = ledgerService.save(ledgerDTO);
		savedLedger.setOpeningBalance(856.7);
		savedLedger.setCostCenterApplicableFlag(Boolean.FALSE);
		savedLedger.setDebitCredit(DebitCredit.DEBIT);
		LedgerDTO updatedLedger = ledgerService.update(savedLedger);
		assertThat(updatedLedger.getId()).isEqualTo(savedLedger.getId());
		assertThat(updatedLedger.getCode()).isEqualTo(savedLedger.getCode());
		assertThat(updatedLedger.getOpeningBalance()).isNotEqualTo(ledgerDTO.getOpeningBalance());
		assertThat(updatedLedger.getCostCenterApplicableFlag()).isNotEqualTo(ledgerDTO.getCostCenterApplicableFlag());
		assertThat(updatedLedger.getOpeningBalanceDate()).isEqualTo(savedLedger.getOpeningBalanceDate());
		assertThat(updatedLedger.getDebitCredit()).isEqualTo(DebitCredit.DEBIT);
	}

	@Test
	public void shouldCheckAliasIsRequired() {
		prepareCodeGenerateMocking();
		ledgerDTO.setAlias(null);
		Assertions.assertThrows(ValidationException.class, () -> {
			ledgerService.save(ledgerDTO);
		});
	}

	@Test
	public void shouldCheckNameIsRequired() {
		prepareCodeGenerateMocking();
		ledgerDTO.setName(null);
		Assertions.assertThrows(ValidationException.class, () -> {
			ledgerService.save(ledgerDTO);
		});
	}

	@Test
	public void shouldCheckCodeIsRequired() {
		ledgerDTO.setCode(null);
		Assertions.assertThrows(ConstraintViolationException.class, () -> {
			ledgerService.save(ledgerDTO);
		});
	}

	@Test
	public void shouldCheckNameCostApplicableFlagIsRequired() {
		prepareCodeGenerateMocking();
		ledgerDTO.setCostCenterApplicableFlag(null);
		Assertions.assertThrows(ConstraintViolationException.class, () -> {
			ledgerService.save(ledgerDTO);
		});
	}

	@Test
	public void shouldCheckOpeningBalanceDateIsRequired() {
		prepareCodeGenerateMocking();
		ledgerDTO.setOpeningBalanceDate(null);
		Assertions.assertThrows(ConstraintViolationException.class, () -> {
			ledgerService.save(ledgerDTO);
		});
	}

	@Test
	public void shouldCheckOpeningBalanceIsRequired() {
		prepareCodeGenerateMocking();
		ledgerDTO.setOpeningBalance(null);
		Assertions.assertThrows(ConstraintViolationException.class, () -> {
			ledgerService.save(ledgerDTO);
		});
	}

	@Test
	public void shouldCheckDebitCreditIsRequired() {
		prepareCodeGenerateMocking();
		ledgerDTO.setDebitCredit(null);
		Assertions.assertThrows(ConstraintViolationException.class, () -> {
			ledgerService.save(ledgerDTO);
		});
	}

	public DeactivationDTO prepareDeactivationDTO() {
		DeactivationDTO deactivateDto = new DeactivationDTO();
		deactivateDto.setDeactivationWefDate(LocalDate.now());
		deactivateDto.setDeactivationReason("I no longer need this Ledger");
		deactivateDto.setType("DEACTIVATE");
		return deactivateDto;
	}

	public ReactivationDTO prepareReactivationDto() {
		ReactivationDTO reactivateDto = new ReactivationDTO();
		reactivateDto.setReactivationWefDate(LocalDate.now());
		reactivateDto.setReactivationReason("I want to reactivate this ledger");
		reactivateDto.setType("REACTIVATE");
		return reactivateDto;
	}

	@Test
	public void testDeactivateLedger() {
		prepareCodeGenerateMocking();
		LedgerDTO savedLedger = ledgerService.save(ledgerDTO);
		DeactivationDTO deactivateDto = prepareDeactivationDTO();
		deactivateDto.setReferenceId(savedLedger.getId());

		LedgerDTO ledgerDto = ledgerService.deactivate(deactivateDto);

		assertThat(ledgerDto).isNotNull();
		assertThat(ledgerDto.getDeactivateDtls().getDeactivationReason()).isEqualTo(deactivateDto.getDeactivationReason());
		assertThat(ledgerDto.getDeactivateDtls().getDeactivationWefDate()).isEqualTo(deactivateDto.getDeactivationWefDate());

	}
	
	@Test
	void testDeactivateShouldThrowExceptionWhenDeactivationDateIsPast() throws Exception {
		prepareCodeGenerateMocking();
		LedgerDTO savedLedger = ledgerService.save(ledgerDTO);
		DeactivationDTO deactivateDto = prepareDeactivationDTO();
		deactivateDto.setReferenceId(savedLedger.getId());
		deactivateDto.setDeactivationWefDate(LocalDate.now().minus(Period.ofDays(1)));
		assertThrows(ValidationException.class, () -> {
			ledgerService.deactivate(deactivateDto);
		});
	}

	@Test
	void testDeactivateShouldThrowExceptionWhenReferenceIdNotFound() throws Exception {
		DeactivationDTO deactivateDto = prepareDeactivationDTO();
		deactivateDto.setReferenceId(0L);
		Assertions.assertThrows(InvalidDataException.class, () -> {
			ledgerService.deactivate(deactivateDto);
		});
	}

	@Test
	public void testReactivateLedger() {
		prepareCodeGenerateMocking();
		LedgerDTO savedLedger = ledgerService.save(ledgerDTO);
		ReactivationDTO reactivateDto = prepareReactivationDto();
		reactivateDto.setReferenceId(savedLedger.getId());

		LedgerDTO ledgerDto = ledgerService.reactivate(reactivateDto);

		assertThat(ledgerDto).isNotNull();
		assertThat(ledgerDto.getReactivateDtls().getReactivationReason())
				.isEqualTo(reactivateDto.getReactivationReason());
		assertThat(ledgerDto.getReactivateDtls().getReactivationWefDate())
				.isEqualTo(reactivateDto.getReactivationWefDate());

	}
	
	@Test
	void testReactivateShouldThrowExceptionWhenReferenceIdNotFound() throws Exception {
		ReactivationDTO reactivateDto = prepareReactivationDto();
		reactivateDto.setReferenceId(0L);
		Assertions.assertThrows(InvalidDataException.class, () -> {
			ledgerService.reactivate(reactivateDto);
		});
	}
	
	@Test
	void testReactivateShouldThrowExceptionWhenDeactivationDateIsPast() throws Exception {
		prepareCodeGenerateMocking();
		LedgerDTO savedLedger = ledgerService.save(ledgerDTO);
		ReactivationDTO reactivationDTO = prepareReactivationDto();
		reactivationDTO.setReferenceId(savedLedger.getId());
		reactivationDTO.setReactivationWefDate(LocalDate.now().minus(Period.ofDays(1)));
		assertThrows(ValidationException.class, () -> {
			ledgerService.reactivate(reactivationDTO);
		});
	}

	private void prepareCodeGenerateMocking() {
		when(masterModuleAdapter.generateCode(LEDGER, null)).thenReturn("LR001");
	}
}
