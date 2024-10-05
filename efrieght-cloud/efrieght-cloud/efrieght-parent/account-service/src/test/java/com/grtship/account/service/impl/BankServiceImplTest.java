package com.grtship.account.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import javax.validation.ConstraintViolationException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.grtship.account.feignclient.MasterModule;
import com.grtship.account.service.BankService;
import com.grtship.common.exception.ValidationException;
import com.grtship.core.dto.AddressDTO;
import com.grtship.core.dto.BankDTO;
import com.grtship.core.enumeration.AccountType;

@SpringBootTest(properties = "config.caching.enabled=false")
@Transactional
@ActiveProfiles(profiles = { "test"})
public class BankServiceImplTest {

	@Autowired
	private BankService bankService;

	@MockBean
	private MasterModule masterModule;

	@Mock
	private Pageable pageable;

	private BankDTO bankDto;
	
	public static  AddressDTO getAddress() {
		AddressDTO addressDto = new AddressDTO();
		addressDto.setId(1L);
		addressDto.setAddress("Kondivita");
		addressDto.setCityId(1L);
		addressDto.setStateId(1L);
		addressDto.setLocation("MIDC");
		return addressDto;
	}

	public static BankDTO createEntity() {
		BankDTO bankDto  = new BankDTO();
		bankDto.setCode("CNR");
		bankDto.setName("Canara Bank");
		bankDto.setBranchName("MIDC");
		bankDto.setIfscCode("CNR4816516KM");
		bankDto.setAccountNo("34040112987");
		bankDto.setAccountType(AccountType.SAVING_ACCOUNT);
		bankDto.setSwiftCode("CNRCNR79");
		bankDto.setRelationshipManager("Jayesh Jain");
		bankDto.setEmailId("jayesh.test@localhost.com");
		bankDto.setContactNo("8898617911");
		AddressDTO addressDto = getAddress();
		bankDto.setId(null);
		bankDto.setAddress(addressDto);
		return bankDto;
	}

	@BeforeEach
	public void setup() {
		bankDto=createEntity();
	}

	@Test
	public void testSave() {
		prepareMockAddress();
		BankDTO bankDTO=bankDto;
		BankDTO savedBank = bankService.save(bankDTO);
		assertThat(savedBank).isNotNull();
		assertThat(savedBank.getCode()).isEqualTo(bankDto.getCode());
		assertThat(savedBank.getName()).isEqualTo(bankDto.getName());
		assertThat(savedBank.getBranchName()).isEqualTo(bankDto.getBranchName());
		assertThat(savedBank.getIfscCode()).isEqualTo(bankDto.getIfscCode());
		assertThat(savedBank.getAccountNo()).isEqualTo(bankDto.getAccountNo());
		assertThat(savedBank.getAccountType()).isEqualTo(bankDto.getAccountType());
		assertThat(savedBank.getSwiftCode()).isEqualTo(bankDto.getSwiftCode());
		assertThat(savedBank.getRelationshipManager()).isEqualTo(bankDto.getRelationshipManager());
		assertThat(savedBank.getEmailId()).isEqualTo(bankDto.getEmailId());
		assertThat(savedBank.getContactNo()).isEqualTo(bankDto.getContactNo());
	}
	
	@Test
	public void testGetAllBanks() {
		prepareMockAddress();
		bankService.save(bankDto);
		List<BankDTO> banks = bankService.findAll(pageable).getContent();
		assertThat(banks).hasSizeGreaterThanOrEqualTo(1);
		assertThat(banks).allMatch(bank -> bank.getAccountNo()!=null);
		assertThat(banks).allMatch(bank -> bank.getName()!=null);
		assertThat(banks).allMatch(bank -> bank.getCode()!=null);
		assertThat(banks).allMatch(bank -> bank.getBranchName()!=null);
	}
	
    
	@Test
	public void checkBankCodeIsRequired() {
		bankDto.setCode(null);
		assertThrows(ConstraintViolationException.class, () -> 
		    bankService.save(bankDto)
		);
	}

	@Test
	public void checkBankNameIsRequired() {
		bankDto.setName(null);
		assertThrows(ConstraintViolationException.class, () -> 
	         bankService.save(bankDto)
	    );
	}
	
	@Test
	public void checkBranchNameIsRequired() {
		bankDto.setBranchName(null);
		assertThrows(ConstraintViolationException.class, () -> 
		    bankService.save(bankDto)
		);
	}
	
	@Test
	public void checkAccountNoIsRequired() {
		prepareMockAddress();
		bankDto.setAccountNo(null);
		assertThrows(ConstraintViolationException.class, () -> 
		    bankService.save(bankDto)
		);
	}
	
	@Test
	public void checkAccountTypeIsRequired() {
		prepareMockAddress();
		bankDto.setAccountType(null);
		assertThrows(ConstraintViolationException.class, () -> 
		    bankService.save(bankDto)
		);
	}
	
	@Test
	public void checkAddressIsRequired() {
		bankDto.setAddress(null);
		assertThrows(ConstraintViolationException.class, () -> 
		    bankService.save(bankDto)
		);
	}
	
	@Test
	public void checkBankCodeIsUnique() {
		prepareMockAddress();
		bankService.save(bankDto);
		assertThrows(ValidationException.class, () -> 
	         bankService.save(bankDto)
	    );
	}

	@Test
	public void checkAccontNoIsUnique() {
		prepareMockAddress();
		bankService.save(bankDto);
		bankDto.setCode("CNR789");
		assertThrows(ValidationException.class, () -> 
             bankService.save(bankDto)
        );
	}
	
	@Test
	public void testUpdate() {
		prepareMockAddress();
		BankDTO bankDTO=bankDto;
		BankDTO savedBank = bankService.save(bankDTO);
		savedBank.setRelationshipManager("Jazz");
		savedBank.setEmailId("jay.test@localhost.com");
		BankDTO updatedBank = bankService.save(savedBank);
		assertThat(updatedBank).isNotNull();
		assertThat(updatedBank.getId()).isEqualTo(savedBank.getId());
		assertThat(updatedBank.getBranchName()).isEqualTo(bankDTO.getBranchName());
		assertThat(updatedBank.getAccountNo()).isEqualTo(bankDTO.getAccountNo());
		assertThat(updatedBank.getRelationshipManager()).isNotEqualTo(bankDTO.getRelationshipManager());
		assertThat(updatedBank.getEmailId()).isNotEqualTo(bankDTO.getEmailId());
	}

	@Test
	void testFindOne() {
		prepareMockAddress();
		BankDTO savedBank = bankService.save(bankDto);
		BankDTO getBankById=bankService.findOne(savedBank.getId()).get();
		assertThat(savedBank).isNotNull();
		assertThat(savedBank.getId()).isEqualTo(getBankById.getId());
		assertThat(savedBank.getCode()).isEqualTo(getBankById.getCode());
		assertThat(savedBank.getName()).isEqualTo(getBankById.getName());
		assertThat(savedBank.getBranchName()).isEqualTo(getBankById.getBranchName());
		assertThat(savedBank.getIfscCode()).isEqualTo(getBankById.getIfscCode());
		assertThat(savedBank.getAccountNo()).isEqualTo(getBankById.getAccountNo());
		assertThat(savedBank.getAccountType()).isEqualTo(getBankById.getAccountType());
		assertThat(savedBank.getSwiftCode()).isEqualTo(getBankById.getSwiftCode());
		assertThat(savedBank.getRelationshipManager()).isEqualTo(getBankById.getRelationshipManager());
	}

	@Test
	void testDelete() {
		prepareMockAddress();
		BankDTO savedBank = bankService.save(bankDto);
		bankService.delete(savedBank.getId());
		Optional<BankDTO> optionalBank=bankService.findOne(savedBank.getId());
		assertFalse(optionalBank.isPresent());
	}
	
    public void prepareMockAddress() {
    	when(masterModule.saveAddress(getAddress())).thenReturn(getAddress());
    }

}
