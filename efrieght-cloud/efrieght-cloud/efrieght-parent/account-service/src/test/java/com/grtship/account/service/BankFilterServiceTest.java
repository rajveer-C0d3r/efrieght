package com.grtship.account.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.grtship.account.criteria.BankCriteria;
import com.grtship.account.feignclient.MasterModule;
import com.grtship.account.service.impl.BankServiceImplTest;
import com.grtship.core.dto.BankDTO;

@SpringBootTest
@Transactional
@ActiveProfiles(profiles = {"test"})
class BankFilterServiceTest {
	
	@Autowired
	private BankService bankService;
	
	@Autowired
	private BankFilterService bankFilterService;

	private BankDTO bankDto;
	
	@MockBean
	private MasterModule masterModule;
	
	private BankCriteria bankCriteria;

	@BeforeEach
	void setUp() throws Exception {
		bankDto=BankServiceImplTest.createEntity();
	}

	@Test
	void testFindByCriteriaBankCriteriaName() {
		prepareMockAddress();
		BankDTO savedBank = bankService.save(bankDto);
		bankCriteria=new BankCriteria();
		bankCriteria.setName(savedBank.getName());
		List<BankDTO> bankDTOs=bankFilterService.findByCriteria(bankCriteria);
		assertThat(bankDTOs).allMatch(bankObj -> bankObj.getName().equals(bankCriteria.getName()));
		assertThat(bankDTOs).allMatch(bankObj -> bankObj.getCode()!=null);
		assertThat(bankDTOs).allMatch(bankObj -> bankObj.getAccountNo()!=null);
		assertThat(bankDTOs).allMatch(bankObj -> bankObj.getBranchName()!=null);
	}
	
	@Test
	void testFindByCriteriaBankCriteriaCode() {
		prepareMockAddress();
		BankDTO savedBank = bankService.save(bankDto);
		bankCriteria=new BankCriteria();
		bankCriteria.setCode(savedBank.getCode());
		List<BankDTO> bankDTOs=bankFilterService.findByCriteria(bankCriteria);
		assertThat(bankDTOs).allMatch(bankObj -> bankObj.getName()!=null);
		assertThat(bankDTOs).allMatch(bankObj -> bankObj.getCode().equals(bankCriteria.getCode()));
		assertThat(bankDTOs).allMatch(bankObj -> bankObj.getAccountNo()!=null);
		assertThat(bankDTOs).allMatch(bankObj -> bankObj.getBranchName()!=null);
	}
	
	@Test
	void testFindByCriteriaBankCriteriaBranchName() {
		prepareMockAddress();
		BankDTO savedBank = bankService.save(bankDto);
		bankCriteria=new BankCriteria();
		bankCriteria.setBranchName(savedBank.getBranchName());
		List<BankDTO> bankDTOs=bankFilterService.findByCriteria(bankCriteria);
		assertThat(bankDTOs).allMatch(bankObj -> bankObj.getName()!=null);
		assertThat(bankDTOs).allMatch(bankObj -> bankObj.getCode()!=null);
		assertThat(bankDTOs).allMatch(bankObj -> bankObj.getAccountNo()!=null);
		assertThat(bankDTOs).allMatch(bankObj -> bankObj.getBranchName().equals(bankCriteria.getBranchName()));
	}
	
	@Test
	void testFindByCriteriaBankCriteriaAccountNo() {
		prepareMockAddress();
		BankDTO savedBank = bankService.save(bankDto);
		bankCriteria=new BankCriteria();
		bankCriteria.setAccountNo(savedBank.getAccountNo());
		List<BankDTO> bankDTOs=bankFilterService.findByCriteria(bankCriteria);
		assertThat(bankDTOs).allMatch(bankObj -> bankObj.getName()!=null);
		assertThat(bankDTOs).allMatch(bankObj -> bankObj.getCode()!=null);
		assertThat(bankDTOs).allMatch(bankObj -> bankObj.getAccountNo().equals(bankCriteria.getAccountNo()));
		assertThat(bankDTOs).allMatch(bankObj -> bankObj.getBranchName()!=null);
	}

	@Test
	void testFindByCriteriaBankCriteriaPageable() {
		prepareMockAddress();
		BankDTO savedBank = bankService.save(bankDto);
		bankCriteria=new BankCriteria();
		bankCriteria.setName(savedBank.getName());
		Page<BankDTO> pageableBankDtos=bankFilterService.findByCriteria(bankCriteria, PageRequest.of(0, 20));
		assertThat(pageableBankDtos.getNumber()).isEqualTo(0);
		assertThat(pageableBankDtos.getContent()).hasSizeLessThanOrEqualTo(20);
		assertThat(pageableBankDtos.getContent()).isNotNull();
		assertThat(pageableBankDtos.getContent()).allMatch(bankObj -> bankObj.getName().equals(bankCriteria.getName()));
		assertThat(pageableBankDtos.getContent()).allMatch(bankObj -> bankObj.getName()!=null);
		assertThat(pageableBankDtos.getContent()).allMatch(bankObj -> bankObj.getCode()!=null);
		assertThat(pageableBankDtos.getContent()).allMatch(bankObj -> bankObj.getAccountNo()!=null);
		assertThat(pageableBankDtos.getContent()).allMatch(bankObj -> bankObj.getBranchName()!=null);
	}
	
	public void prepareMockAddress() {
		when(masterModule.saveAddress(BankServiceImplTest.getAddress())).thenReturn(BankServiceImplTest.getAddress());
	}
}
