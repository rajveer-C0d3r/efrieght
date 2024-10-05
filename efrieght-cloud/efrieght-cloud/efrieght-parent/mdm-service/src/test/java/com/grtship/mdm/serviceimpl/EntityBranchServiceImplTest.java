package com.grtship.mdm.serviceimpl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.audit.AuditAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.metrics.KafkaMetricsAutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.grtship.common.exception.InvalidDataException;
import com.grtship.common.exception.ValidationException;
import com.grtship.core.dto.AddressDTO;
import com.grtship.core.dto.BranchContactDTO;
import com.grtship.core.dto.DeactivationDTO;
import com.grtship.core.dto.EntityBranchDTO;
import com.grtship.core.dto.EntityBranchRequestDTO;
import com.grtship.core.dto.EntityBranchTaxDTO;
import com.grtship.core.dto.ReactivationDTO;
import com.grtship.core.enumeration.DomainStatus;
import com.grtship.core.enumeration.EntityLevel;
import com.grtship.mdm.domain.BranchContact;
import com.grtship.mdm.domain.CreditTerms;
import com.grtship.mdm.domain.EntityBranchTax;
import com.grtship.mdm.domain.ExternalEntity;
import com.grtship.mdm.mapper.BranchContactMapper;
import com.grtship.mdm.mapper.ExternalEntityMapper;
import com.grtship.mdm.repository.BranchContactRepository;
import com.grtship.mdm.repository.CreditTermsRepository;
import com.grtship.mdm.repository.EntityBranchTaxRepository;
import com.grtship.mdm.service.EntityBranchService;

@SpringBootTest(properties = "config.caching.enabled=false")
@Transactional
@EnableAutoConfiguration(exclude = { KafkaAutoConfiguration.class, KafkaMetricsAutoConfiguration.class,
		AuditAutoConfiguration.class })
@ActiveProfiles(profiles = { "test" })
public class EntityBranchServiceImplTest {
	
	private static final String ENTITY_BRANCH = "Entity Branch";
	@Autowired private EntityBranchService entityBranchService;
	@Autowired private EntityManager em;
	@Autowired BranchContactRepository contactRepository;
	@Autowired BranchContactMapper contactMapper;
	@Autowired private ExternalEntityMapper entityMapper;
	@Autowired private CreditTermsRepository creditTermsRepository;
	@Autowired private EntityBranchTaxRepository branchTaxRepository;
	@Autowired private BranchContactRepository branchContactRepository;

	private EntityBranchRequestDTO entityBranchReqDto;


	public static EntityBranchRequestDTO createEntityBranchReqDto(EntityManager em, ExternalEntityMapper entityMapper) {
		Set<String> cellNumbers = new HashSet<>();
		cellNumbers.add("8898617911");
		cellNumbers.add("8898685830");

		AddressDTO address = AddressServiceImplTest.setUp(em);
		ExternalEntity entity = entityMapper.toEntity(ExternalEntityServiceImplTest.createEntityRequestDto(em));
		em.persist(entity);
		em.flush();
		
		List<BranchContactDTO> contactDetails = prepareBranchContactDetails();
		
		List<EntityBranchTaxDTO> taxDetialsDto = prepareBranchTaxDetails();

		EntityBranchRequestDTO entityBranchReqDto = new EntityBranchRequestDTO();
		entityBranchReqDto.setCode("EEB78");
		entityBranchReqDto.setName("Vimal Xerox Marol");
		entityBranchReqDto.setExternalEntityId(entity.getId());
		entityBranchReqDto.setSez(Boolean.FALSE);
		entityBranchReqDto.setCellNumbers(cellNumbers);
		entityBranchReqDto.setCustomerFlag(Boolean.TRUE);
		entityBranchReqDto.setCustomerEntityLevel(EntityLevel.BRANCH);
		entityBranchReqDto.setCustomerCreditAmount(1123.0);
		entityBranchReqDto.setCustomerCreditDays(44);
		entityBranchReqDto.setVendorFlag(Boolean.FALSE);
		entityBranchReqDto.setDefaultBranchFlag(Boolean.FALSE);
		entityBranchReqDto.setAddress(address);
		entityBranchReqDto.setStatus(DomainStatus.PENDING);		
		entityBranchReqDto.setClientId(1L);
		entityBranchReqDto.setCompanyId(1L);        
		entityBranchReqDto.setTaxDetialsDto(taxDetialsDto);
        entityBranchReqDto.setContactDetailsDto(contactDetails);
		return entityBranchReqDto;
	}

	private static List<BranchContactDTO> prepareBranchContactDetails() {
		List<BranchContactDTO> contactDetails = new ArrayList<>();
		BranchContactDTO contactsDetails = new BranchContactDTO();
		contactsDetails.setName("Jayesh");
		contactsDetails.setCellNumber("8898681618");
		contactsDetails.setDepartmentName("Account");
		contactsDetails.setDesignationName("Junior Accountant");
		contactsDetails.setEmail("ad@gmail.com");
		contactDetails.add(contactsDetails);
		return contactDetails;
	}

	private static List<EntityBranchTaxDTO> prepareBranchTaxDetails() {
		List<EntityBranchTaxDTO> taxDetialsDto = new ArrayList<>();
		EntityBranchTaxDTO taxDetails = new EntityBranchTaxDTO();
		taxDetails.setGstNo("FCV49816516KM");
		taxDetails.setDescription("First GST No");
		taxDetialsDto.add(taxDetails);
		return taxDetialsDto;
	}

	@BeforeEach
	public void setUp() {
		entityBranchReqDto = createEntityBranchReqDto(em,entityMapper);
	}
	
	@Test
	void testSave() {
		EntityBranchRequestDTO branchDto = entityBranchReqDto;
		EntityBranchDTO savedEntityBranch = entityBranchService.save(branchDto);
		assertThat(savedEntityBranch).isNotNull();
		assertThat(savedEntityBranch.getName()).isEqualTo(branchDto.getName());
		assertThat(savedEntityBranch.getCode()).isEqualTo(branchDto.getCode());
		assertThat(savedEntityBranch.getAddress().getId()).isNotNull();
		assertThat(savedEntityBranch.getExternalEntityId()).isEqualTo(branchDto.getExternalEntityId());
		assertThat(savedEntityBranch.getId()).isNotNull();
		assertThat(savedEntityBranch.getActiveFlag()).isEqualTo(Boolean.FALSE);
		assertThat(savedEntityBranch.getStatus()).isEqualTo(DomainStatus.PENDING);
		assertThat(savedEntityBranch.getVendorFlag()).isEqualTo(branchDto.getVendorFlag());
		assertThat(savedEntityBranch.getCustomerFlag()).isEqualTo(branchDto.getCustomerFlag());
		assertThat(savedEntityBranch.getCustomerEntityLevel()).isEqualTo(branchDto.getCustomerEntityLevel());
		assertThat(savedEntityBranch.getVendorFlag()).isEqualTo(branchDto.getVendorFlag());
		assertThat(savedEntityBranch.getSez()).isFalse();
		assertThat(savedEntityBranch.getSezValidUptoDate()).isEqualTo(branchDto.getSezValidUptoDate());
		assertThat(savedEntityBranch.getSezWEFDate()).isEqualTo(branchDto.getSezWEFDate());
		Set<CreditTerms> savedCreditTerms = creditTermsRepository.findByReferenceNameAndReferenceIdIn(ENTITY_BRANCH,
				Arrays.asList(savedEntityBranch.getId()));
		assertThat(savedCreditTerms).isNotEmpty();
		assertThat(savedCreditTerms).hasSize(1);
		List<Long> entityBranchIds=new LinkedList<Long>();
		entityBranchIds.add(savedEntityBranch.getId());
		List<EntityBranchTax> savedBranchTaxes = branchTaxRepository
				.findByEntityBranch_IdIn(entityBranchIds);
		assertThat(savedBranchTaxes).isNotEmpty();
		assertThat(savedBranchTaxes).hasSize(entityBranchReqDto.getTaxDetialsDto().size());
		assertThat(savedBranchTaxes).allMatch(tax -> tax.getId() != null);
		assertThat(savedBranchTaxes).allMatch(tax -> tax.getGstNo() != null);
		assertThat(savedBranchTaxes).allMatch(tax -> tax.getEntityBranch().getId().equals(savedEntityBranch.getId()));
		List<Long> branchIds=new LinkedList<Long>();
		entityBranchIds.add(savedEntityBranch.getId());
		List<BranchContact> savedBranchContacts = branchContactRepository
				.findByEntityBranch_IdIn(branchIds);
		assertThat(savedBranchContacts).isNotEmpty();
		assertThat(savedBranchContacts).hasSize(entityBranchReqDto.getContactDetailsDto().size());
		assertThat(savedBranchContacts).allMatch(contact -> contact.getId() != null);
		assertThat(savedBranchContacts).allMatch(contact -> contact.getName() != null);
		assertThat(savedBranchContacts).allMatch(contact -> contact.getEmail() != null);
		assertThat(savedBranchContacts).allMatch(contact -> contact.getCellNumber() != null);
		assertThat(savedBranchContacts)
				.allMatch(contact -> contact.getEntityBranch().getId().equals(savedEntityBranch.getId()));
	}
	
	@Test 
	public void testtCustomerAndVendorIsRequired() {
		EntityBranchRequestDTO branchDto = entityBranchReqDto;
		branchDto.setCustomerFlag(Boolean.FALSE);
		branchDto.setVendorFlag(Boolean.FALSE);
		assertThrows(ValidationException.class, () -> {
			entityBranchService.save(branchDto);
		});
	}

	@Test
	public void testEntityLevelIsRequiredWhenCustomerOrVendorIsTrue() {
		EntityBranchRequestDTO branchDto = entityBranchReqDto;
		branchDto.setCustomerFlag(Boolean.TRUE);
		branchDto.setCustomerEntityLevel(null);
		assertThrows(ValidationException.class, () -> {
			entityBranchService.save(branchDto);
		});
	}

	@Test
	public void testCustomerCreditIsRequiredWhenCustomerOrVendorIsTrue() {
		EntityBranchRequestDTO branchDto = entityBranchReqDto;
		branchDto.setCustomerCreditAmount(null);
		assertThrows(ValidationException.class, () -> {
			entityBranchService.save(branchDto);
		});
	}
	
	@Test
	public void testCustomerCreditDaysIsRequiredWhenCustomerOrVendorIsTrue() {
		EntityBranchRequestDTO branchDto = entityBranchReqDto;
		branchDto.setCustomerCreditDays(null);
		assertThrows(ValidationException.class, () -> {
			entityBranchService.save(branchDto);
		});
	}
	
	@Test
	public void testCompanyIsRequired() {
		EntityBranchRequestDTO branchDto = entityBranchReqDto;
		branchDto.setCompanyId(null);
		assertThrows(ValidationException.class, () -> {
			entityBranchService.save(branchDto);
		});
	}
	
	@Test
	public void testAddressIsRequired() {	
		EntityBranchRequestDTO branchDto = entityBranchReqDto;
		branchDto.setAddress(null);
		assertThrows(ValidationException.class, () -> {
			entityBranchService.save(branchDto);
		});
	}
	
	@Test
	public void testCityIsRequired() {	
		EntityBranchRequestDTO branchDto = entityBranchReqDto;
		branchDto.getAddress().setCityId(null);
		assertThrows(ValidationException.class, () -> {
			entityBranchService.save(branchDto);
		});
	}
	
	@Test
	public void testLocationIsRequired() {	
		EntityBranchRequestDTO branchDto = entityBranchReqDto;
		branchDto.getAddress().setLocation(null);
		assertThrows(ValidationException.class, () -> {
			entityBranchService.save(branchDto);
		});
	}
	
	@Test
	public void testExternalEntityIsRequired() {	
		EntityBranchRequestDTO branchDto = entityBranchReqDto;
		branchDto.setExternalEntityId(null);
		assertThrows(ValidationException.class, () -> {
			entityBranchService.save(branchDto);
		});
	}
	
	@Test
	public void testExternalEntityIsValid() {	
		EntityBranchRequestDTO branchDto = entityBranchReqDto;
		branchDto.setExternalEntityId(0L);
		assertThrows(ValidationException.class, () -> {
			entityBranchService.save(branchDto);
		});
	}
	
	@Test
	public void testNameIsUnique() {	
		entityBranchService.save(entityBranchReqDto);
		assertThrows(ValidationException.class, () -> {
			entityBranchService.save(entityBranchReqDto);
		});
	}
	
	@SuppressWarnings("unused")
	@Test
	public void testCodeIsUnique() {	
		EntityBranchRequestDTO branchDto = entityBranchReqDto;
		EntityBranchDTO savedEntityBranch = entityBranchService.save(branchDto);
		branchDto.setName("Vimal MIDC");
		assertThrows(ValidationException.class, () -> {
			entityBranchService.save(branchDto);
		});
	}
	
	@Test
	public void testSezWefIsRequiredWhenSezIsTrue() {	
		EntityBranchRequestDTO branchDto = entityBranchReqDto;
		branchDto.setSez(Boolean.TRUE);
		branchDto.setSezWEFDate(null);
		assertThrows(ValidationException.class, () -> {
			entityBranchService.save(branchDto);
		});
	}
	
	@Test
	public void testSezValidUptoIsRequiredWhenSezIsTrue() {	
		EntityBranchRequestDTO branchDto = entityBranchReqDto;
		branchDto.setSez(Boolean.TRUE);
		branchDto.setSezWEFDate(new Date());
		branchDto.setSezValidUptoDate(null);
		assertThrows(ValidationException.class, () -> {
			entityBranchService.save(branchDto);
		});
	}

	@Test
	void testUpdate() {
		EntityBranchRequestDTO branchDto = entityBranchReqDto;
		EntityBranchDTO savedEntityBranch = entityBranchService.save(branchDto);
		prepareBranchdetailsForUpdate(savedEntityBranch);
		EntityBranchDTO updatedBranch = entityBranchService.update(savedEntityBranch);
		assertThat(updatedBranch).isNotNull();
		assertThat(savedEntityBranch.getId()).isEqualTo(updatedBranch.getId());
		assertThat(savedEntityBranch.getName()).isNotEqualTo(entityBranchReqDto.getName());
		assertThat(savedEntityBranch.getCode()).isNotEqualTo(entityBranchReqDto.getCode());
		assertThat(updatedBranch.getAddress().getId()).isNotNull();
		assertThat(updatedBranch.getExternalEntityId()).isEqualTo(savedEntityBranch.getExternalEntityId());
		assertThat(updatedBranch.getActiveFlag()).isEqualTo(Boolean.FALSE);
		assertThat(updatedBranch.getStatus()).isEqualTo(DomainStatus.PENDING);
		assertThat(updatedBranch.getVendorFlag()).isEqualTo(savedEntityBranch.getVendorFlag());
		assertThat(savedEntityBranch.getCustomerFlag()).isEqualTo(updatedBranch.getCustomerFlag());
		assertThat(savedEntityBranch.getCustomerEntityLevel()).isEqualTo(updatedBranch.getCustomerEntityLevel());
		assertThat(updatedBranch.getSez()).isEqualTo(savedEntityBranch.getSez());
		assertThat(updatedBranch.getCellNumbers()).hasSize(savedEntityBranch.getCellNumbers().size());
		Set<CreditTerms> savedCreditTerms = creditTermsRepository.findByReferenceNameAndReferenceIdIn(ENTITY_BRANCH,
				Arrays.asList(updatedBranch.getId()));
		assertThat(savedCreditTerms).isNotEmpty();
		assertThat(savedCreditTerms).hasSize(1);
		List<Long> entityBranchIds=new LinkedList<Long>();
		entityBranchIds.add(updatedBranch.getId());
		List<EntityBranchTax> savedBranchTaxes = branchTaxRepository
				.findByEntityBranch_IdIn(entityBranchIds);
		assertThat(savedBranchTaxes).isEmpty();
		List<BranchContact> savedBranchContacts = branchContactRepository
				.findByEntityBranch_IdIn(entityBranchIds);
		assertThat(savedBranchContacts).isEmpty();
	}

	private void prepareBranchdetailsForUpdate(EntityBranchDTO savedEntityBranch) {
		savedEntityBranch.setName("Test Branch update");
		savedEntityBranch.setCode("TSTBR");
		Set<String> cellNumbers=new TreeSet<>();
        cellNumbers.add("8898617911");
        cellNumbers.add("8898685830");
        savedEntityBranch.setCellNumbers(cellNumbers);
		savedEntityBranch.setContactDetailsDto(null);
		savedEntityBranch.setTaxDetialsDto(null);
		savedEntityBranch.setCustomerEntityLevel(EntityLevel.BRANCH);
		savedEntityBranch.setCustomerCreditAmount(1123.0);
		savedEntityBranch.setCustomerCreditDays(44);
	}

	public DeactivationDTO prepareDeactivationDTO() {
    	DeactivationDTO deactivateDto = new DeactivationDTO();
    	deactivateDto.setDeactivationWefDate(LocalDate.now());
    	deactivateDto.setDeactivationReason("I no longer need this Entity Branch");
    	deactivateDto.setType("DEACTIVATE");
		return deactivateDto;
    }
	
	@Test
	void testDeactivate() {
		EntityBranchRequestDTO branchDto = entityBranchReqDto;
		EntityBranchDTO savedEntityBranch = entityBranchService.save(branchDto);
		DeactivationDTO deactivateDto = prepareDeactivationDTO();
		deactivateDto.setReferenceId(savedEntityBranch.getId());

		EntityBranchDTO entityBranchDTO=entityBranchService.deactivate(deactivateDto);

		assertThat(entityBranchDTO.getDeactivateDtls()).isNotNull();
		assertThat(entityBranchDTO.getDeactivateDtls().getDeactivationReason()).isEqualTo(deactivateDto.getDeactivationReason());
		assertThat(entityBranchDTO.getDeactivateDtls().getDeactivationWefDate()).isEqualTo(deactivateDto.getDeactivationWefDate());
	}

	@Test
	void testDeactivateShouldThrowExceptionWhenDeactivationDateIsPast() throws Exception {
		EntityBranchDTO savedEntityBranch = entityBranchService.save(entityBranchReqDto);
		DeactivationDTO deactivateDto = prepareDeactivationDTO();
		deactivateDto.setReferenceId(savedEntityBranch.getId());
		deactivateDto.setDeactivationWefDate(LocalDate.now().minus(Period.ofDays(1)));
		assertThrows(ValidationException.class, () -> {
			entityBranchService.deactivate(deactivateDto);
		});
	}

	@Test
	void testDeactivateShouldThrowExceptionWhenReferenceIdNotFound() throws Exception {
		DeactivationDTO deactivateDto = prepareDeactivationDTO();
		deactivateDto.setReferenceId(1L);
		assertThrows(InvalidDataException.class, () -> {
			entityBranchService.deactivate(deactivateDto);
		});
	}
	
	public ReactivationDTO prepareReactivationDTO() {
    	ReactivationDTO reactivateDto = new ReactivationDTO();
    	reactivateDto.setReactivationWefDate(LocalDate.now());
    	reactivateDto.setReactivationReason("I want to reactivate this Entity Branch");
    	reactivateDto.setType("REACTIVATE");
		return reactivateDto;
    }
	
	@Test
	void testReactivate() {
		EntityBranchDTO savedEntityBranch = entityBranchService.save(entityBranchReqDto);
		ReactivationDTO reactivateDto = prepareReactivationDTO();
		reactivateDto.setReferenceId(savedEntityBranch.getId());

		EntityBranchDTO entityDTO=entityBranchService.reactivate(reactivateDto);
		assertThat(entityDTO.getReactivateDtls()).isNotNull();
		assertThat(entityDTO.getReactivateDtls().getReactivationReason()).isEqualTo(reactivateDto.getReactivationReason());
		assertThat(entityDTO.getReactivateDtls().getReactivationWefDate()).isEqualTo(reactivateDto.getReactivationWefDate());
	}

	@Test
	void testReactivateShouldThrowExceptionWhenReactivationDateIsPast() throws Exception {
		EntityBranchRequestDTO branchDto = entityBranchReqDto;
		EntityBranchDTO savedEntityBranch = entityBranchService.save(branchDto);
		ReactivationDTO reactivateDto = prepareReactivationDTO();
		reactivateDto.setReferenceId(savedEntityBranch.getId());
		reactivateDto.setReactivationWefDate(LocalDate.now().minus(Period.ofDays(1)));
		assertThrows(ValidationException.class, () -> {
			entityBranchService.reactivate(reactivateDto);
		});
	}

	@Test
	void testReactivateShouldThrowExceptionWhenReferenceIdNotFound() throws Exception {
		ReactivationDTO reactivateDto = prepareReactivationDTO();
		reactivateDto.setReferenceId(1L);
		assertThrows(InvalidDataException.class, () -> {
			entityBranchService.reactivate(reactivateDto);
		});
	}
	
	@Test
	void testSaveBranchTax() {
		EntityBranchDTO savedEntityBranch = entityBranchService.save(entityBranchReqDto);
		List<EntityBranchTaxDTO> prepareBranchTaxDetails = prepareBranchTaxDetails();
		prepareBranchTaxDetails.get(0).setEntityBranchId(savedEntityBranch.getId());
		List<EntityBranchTaxDTO> savedBranchTaxes = entityBranchService.saveBranchTax(prepareBranchTaxDetails);
		checkTaxAssertion(savedEntityBranch, savedBranchTaxes);
	}

	private void checkTaxAssertion(EntityBranchDTO savedEntityBranch, List<EntityBranchTaxDTO> savedBranchTaxes) {
		assertThat(savedBranchTaxes).isNotEmpty();
		assertThat(savedBranchTaxes).allMatch(tax -> tax.getId() != null);
		assertThat(savedBranchTaxes).allMatch(tax -> tax.getGstNo() != null);
		assertThat(savedBranchTaxes).allMatch(tax -> tax.getEntityBranchId().equals(savedEntityBranch.getId()));
	}

	@Test
	void testSaveBranchContacts() {
		EntityBranchDTO savedEntityBranch = entityBranchService.save(entityBranchReqDto);
		List<BranchContactDTO> prepareBranchContactDetails = prepareBranchContactDetails();
		prepareBranchContactDetails.get(0).setEntityBranchId(savedEntityBranch.getId());
		List<BranchContactDTO> savedBranchContacts = entityBranchService
				.saveBranchContacts(prepareBranchContactDetails);
		checkContactAssertion(savedEntityBranch, savedBranchContacts);
	}

	private void checkContactAssertion(EntityBranchDTO savedEntityBranch, List<BranchContactDTO> savedBranchContacts) {
		assertThat(savedBranchContacts).isNotEmpty();
		assertThat(savedBranchContacts).allMatch(contact -> contact.getId() != null);
		assertThat(savedBranchContacts).allMatch(contact -> contact.getName() != null);
		assertThat(savedBranchContacts).allMatch(contact -> contact.getEmail() != null);
		assertThat(savedBranchContacts).allMatch(contact -> contact.getCellNumber() != null);
		assertThat(savedBranchContacts)
				.allMatch(contact -> contact.getEntityBranchId().equals(savedEntityBranch.getId()));
	}

}
