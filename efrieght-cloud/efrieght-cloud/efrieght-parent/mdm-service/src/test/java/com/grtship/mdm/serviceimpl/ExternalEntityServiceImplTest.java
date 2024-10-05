package com.grtship.mdm.serviceimpl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.Period;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.validation.ConstraintViolationException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.audit.AuditAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.metrics.KafkaMetricsAutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.grtship.common.exception.InvalidDataException;
import com.grtship.common.exception.ValidationException;
import com.grtship.core.dto.AddressDTO;
import com.grtship.core.dto.DeactivationDTO;
import com.grtship.core.dto.EntityGroupDTO;
import com.grtship.core.dto.ExternalEntityDTO;
import com.grtship.core.dto.ExternalEntityRequestDTO;
import com.grtship.core.dto.ObjectAliasDTO;
import com.grtship.core.dto.ReactivationDTO;
import com.grtship.core.enumeration.CompanyType;
import com.grtship.core.enumeration.DomainStatus;
import com.grtship.core.enumeration.Domicile;
import com.grtship.core.enumeration.EntityGstType;
import com.grtship.core.enumeration.EntityLevel;
import com.grtship.core.enumeration.EntityType;
import com.grtship.core.enumeration.TdsExemption;
import com.grtship.mdm.domain.CreditTerms;
import com.grtship.mdm.domain.Currency;
import com.grtship.mdm.domain.EntityBranch;
import com.grtship.mdm.domain.EntityBusinessType;
import com.grtship.mdm.domain.ExternalEntity;
import com.grtship.mdm.mapper.ExternalEntityMapper;
import com.grtship.mdm.repository.CreditTermsRepository;
import com.grtship.mdm.repository.EntityBranchRepository;
import com.grtship.mdm.repository.EntityBusinessTypeRepository;
import com.grtship.mdm.repository.ExternalEntityRepository;
import com.grtship.mdm.service.ExternalEntityService;

@SpringBootTest(properties = "config.caching.enabled=false")
@Transactional
@EnableAutoConfiguration(exclude = { KafkaAutoConfiguration.class, KafkaMetricsAutoConfiguration.class,
		AuditAutoConfiguration.class })
@ActiveProfiles(profiles = { "test" })
public class ExternalEntityServiceImplTest {
	
    private static final String EXTERNAL_ENTITY = "External Entity";

	private static final String keyPersonName="JayeshJain JayeshJain JayeshJain JayeshJain JayeshJain JayeshJain";
	
	private static final String email="JayeshJainJayeshJainJayeshJain@JayeshJain.com";
	
	private static final String DEACTIVATE="DEACTIVATE";
	
	private static final String REACTIVATE="REACTIVATE";
	
	@Autowired private EntityManager em;
	@Autowired private ExternalEntityMapper entityMapper;
	@Autowired private ExternalEntityService entityService;
	@Autowired private ExternalEntityRepository entityRepository;
	@Autowired private CreditTermsRepository creditTermsRepository;
	@Mock private Pageable pageable;
	@Autowired private EntityBusinessTypeRepository entityBusinessDetailsRepository;
	@Autowired private EntityBranchRepository branchRepository;

	private ExternalEntityRequestDTO entityReqDto;

	public static ExternalEntityRequestDTO createEntityRequestDto(EntityManager em) {
		Currency currency = new Currency();
		currency.setCode("Rup");
		currency.setName("Indian Rupee");
		em.persist(currency);
		em.flush();

		EntityGroupDTO groups = new EntityGroupDTO();
		groups.setName("Vimal Group");
		groups.setCode("EG001");
		
		AddressDTO addressDTO=AddressServiceImplTest.setUp(em);
		
		Set<ObjectAliasDTO> objectAliasDTOs=new HashSet<>();
		objectAliasDTOs.add(ObjectAliasDTO.builder().name("Vimal1").build());
		objectAliasDTOs.add(ObjectAliasDTO.builder().name("Vimal2").build());

		Set<String> entityDetails = prepareEntityDetails();
		
		ExternalEntityRequestDTO entityReqDto = new ExternalEntityRequestDTO();
		entityReqDto.setCode("ENT123");
		entityReqDto.setName("Test Entity Save");
		entityReqDto.setLegalName("Test Save");
		entityReqDto.setKeyPersonName("Jayesh");
		entityReqDto.setEmail("vimal@localhost.com");
		entityReqDto.setCellNo("8898617911");
		entityReqDto.setLandlineNo("2804978");
		entityReqDto.setCompanyType(CompanyType.PRIVATE);
		entityReqDto.setTdsExemption(TdsExemption.TOTAL);
		entityReqDto.setTdsExemptionValidUptoDate(LocalDate.now());
		entityReqDto.setTdsExemptionWefDate(LocalDate.now());
		entityReqDto.setDomicile(Domicile.DOMESTIC);
		entityReqDto.setGstType(EntityGstType.REGISTERED);
		entityReqDto.setGstNo("ANJN5165165165KM");
		entityReqDto.setPanNoTaxId("ABJDL6861M");
		entityReqDto.setCustomerEntityLevel(EntityLevel.COMPANY);
		entityReqDto.setCustomerFlag(Boolean.TRUE);
		entityReqDto.setCustomerCreditAmount(1123.0);
		entityReqDto.setCustomerCreditDays(44);
		entityReqDto.setVendorFlag(Boolean.FALSE);
		entityReqDto.setCurrencyId(currency.getId());
		entityReqDto.setGroups(groups);
		entityReqDto.setAddress(addressDTO);
		entityReqDto.setStatus(DomainStatus.PENDING);
		entityReqDto.setExternalEntityAlias(objectAliasDTOs);
		entityReqDto.setClientId(100L);
		entityReqDto.setCompanyId(100L);
		entityReqDto.setEntityDetails(entityDetails);
		return entityReqDto;
	}

	private static Set<String> prepareEntityDetails() {
		Set<String> entityDetails=new HashSet<String>();
		entityDetails.add(EntityType.EXPORTER.name());
		entityDetails.add(EntityType.IMPORTER.name());
		return entityDetails;
	}
	
	@BeforeEach
	public void setUp() {
		entityReqDto=createEntityRequestDto(em);
	}


	@Test
	void testSave() {
		ExternalEntityDTO savedEntity = entityService.save(entityReqDto);
		assertThat(savedEntity.getName()).isEqualTo(entityReqDto.getName());
		assertThat(savedEntity.getCellNo()).isEqualTo(entityReqDto.getCellNo());
		assertThat(savedEntity.getEmail()).isEqualTo(entityReqDto.getEmail());
		assertThat(savedEntity.getCode()).isEqualTo(entityReqDto.getCode());
		assertThat(savedEntity.getVendorFlag()).isEqualTo(entityReqDto.getVendorFlag());
		assertThat(savedEntity.getCustomerFlag()).isEqualTo(entityReqDto.getCustomerFlag());
		assertThat(savedEntity.getExternalEntityAlias()).hasSize(entityReqDto.getExternalEntityAlias().size());
		List<EntityBusinessType> savedEntityDetails = entityBusinessDetailsRepository
				.findByIdExternalEntityId(savedEntity.getId());
		assertThat(savedEntityDetails).isNotEmpty();
		assertThat(savedEntityDetails).hasSize(2);
		Set<CreditTerms> savedCreditTerms = creditTermsRepository.findByReferenceNameAndReferenceIdIn(EXTERNAL_ENTITY,
				Arrays.asList(savedEntity.getId()));
		assertThat(savedCreditTerms).isNotEmpty();
		assertThat(savedCreditTerms).hasSize(1);
		List<EntityBranch> findByExternalEntityId = branchRepository.findByExternalEntityId(savedEntity.getId());
		assertThat(findByExternalEntityId).isNotEmpty();
		assertThat(findByExternalEntityId).allMatch(branch -> branch.getName().equals(savedEntity.getName()));
		assertThat(findByExternalEntityId).allMatch(branch -> branch.getStatus().equals(DomainStatus.DRAFT));
		assertThat(findByExternalEntityId).allMatch(branch -> branch.getDefaultBranchFlag().equals(Boolean.TRUE));
		assertThat(findByExternalEntityId).allMatch(branch -> branch.getCode()!=null);
		assertThat(findByExternalEntityId).allMatch(branch -> branch.getAddress()!=null);
	}
	

	@Test
	public void testSaveEntityAsDraft() {
		 ExternalEntityRequestDTO entityRequestDTO = entityReqDto;
	 	 entityRequestDTO.setStatus(DomainStatus.DRAFT);
	 	 entityRequestDTO.setCustomerFlag(Boolean.FALSE);
		 ExternalEntityDTO savedEntity = entityService.save(entityRequestDTO);
		 assertThat(savedEntity).isNotNull();
		 assertThat(savedEntity.getStatus()).isEqualTo(DomainStatus.DRAFT);
		 assertThat(savedEntity.getCode()).isNotNull();
	}
	
	@Test
	void testUpdate() {
		ExternalEntityDTO savedEntity = entityService.save(entityReqDto);
		ExternalEntity getEntityById = entityRepository.findById(savedEntity.getId()).get();
		ExternalEntityDTO prepareEntityToUpdate = prepareEntityForUpdate(getEntityById);
		ExternalEntityDTO updatedEntity = entityService.update(prepareEntityToUpdate);
		assertThat(savedEntity.getName()).isNotEqualTo(updatedEntity.getName());
		assertThat(savedEntity.getCellNo()).isNotEqualTo(updatedEntity.getCellNo());
		assertThat(savedEntity.getKeyPersonName()).isNotEqualTo(updatedEntity.getKeyPersonName());
		assertThat(savedEntity.getCompanyType()).isNotEqualTo(updatedEntity.getCompanyType());
		assertThat(savedEntity.getEmail()).isEqualTo(updatedEntity.getEmail());
		assertThat(savedEntity.getCode()).isEqualTo(updatedEntity.getCode());
		assertThat(savedEntity.getVendorFlag()).isEqualTo(updatedEntity.getVendorFlag());
		assertThat(savedEntity.getCustomerFlag()).isEqualTo(updatedEntity.getCustomerFlag());
		assertThat(savedEntity.getExternalEntityAlias()).hasSize(updatedEntity.getExternalEntityAlias().size());
		List<EntityBusinessType> updatedEntityDetails = entityBusinessDetailsRepository
				.findByIdExternalEntityId(updatedEntity.getId());
		assertThat(updatedEntityDetails).isNotEmpty();
		assertThat(updatedEntityDetails).hasSize(3);
		Set<CreditTerms> updatedCreditTerms = creditTermsRepository.findByReferenceNameAndReferenceIdIn(EXTERNAL_ENTITY,
				Arrays.asList(updatedEntity.getId()));
		assertThat(updatedCreditTerms).isNotEmpty();
		assertThat(updatedCreditTerms).hasSize(1);
	}

	private ExternalEntityDTO prepareEntityForUpdate(ExternalEntity getEntityById) {
		ExternalEntityDTO prepareEntityToUpdate = entityMapper.toDto(getEntityById);
		prepareEntityToUpdate.setName("Test Entity Update");
		prepareEntityToUpdate.setKeyPersonName("shailesh");
		prepareEntityToUpdate.setCellNo("909098978");
		prepareEntityToUpdate.setCompanyType(CompanyType.PARTNERSHIP);
		prepareEntityToUpdate.setCustomerCreditAmount(1123.0);
		prepareEntityToUpdate.setCustomerCreditDays(44);
		Set<String> entityDetails = prepareEntityDetails();
		entityDetails.add(EntityType.CUSTOMSBROKER.name());
		prepareEntityToUpdate.setEntityDetails(entityDetails);
		return prepareEntityToUpdate;
	}
	
	@Test
	void checkNameIsRequired() {
		ExternalEntityRequestDTO entityRequestDTO=entityReqDto;
		entityRequestDTO.setName(null);
		assertThrows(ConstraintViolationException.class,() -> {
			entityService.save(entityRequestDTO);
		});
	}
	
	@Test
	void checkNameIsOfProperLength() {
		ExternalEntityRequestDTO entityRequestDTO=entityReqDto;
		entityRequestDTO.setName(keyPersonName+"Shailesh");
		assertThrows(ConstraintViolationException.class,() -> {
			entityService.save(entityRequestDTO);
		});
	}
	
	@Test
	void checkLegalNameIsOfProperLength() {
		ExternalEntityRequestDTO entityRequestDTO=entityReqDto;
		entityRequestDTO.setLegalName(keyPersonName+"ShaileshJain");
		assertThrows(ConstraintViolationException.class,() -> {
			entityService.save(entityRequestDTO);
		});
	}
	
	@Test
	void checkKeyPersonNameIsOfProperLength() {
		ExternalEntityRequestDTO entityRequestDTO=entityReqDto;
		entityRequestDTO.setKeyPersonName(keyPersonName);
		assertThrows(ConstraintViolationException.class,() -> {
			entityService.save(entityRequestDTO);
		});
	}
	
	@Test
	void checkCellNoIsOfProperLength() {
		ExternalEntityRequestDTO entityRequestDTO=entityReqDto;
		entityRequestDTO.setCellNo("889868583088");
		assertThrows(ConstraintViolationException.class,() -> {
			entityService.save(entityRequestDTO);
		});
	}	
	
	@Test
	void checkLandlineNoIsOfProperLenght() {
		ExternalEntityRequestDTO entityRequestDTO=entityReqDto;
		entityRequestDTO.setLandlineNo("02228304978");
		assertThrows(ConstraintViolationException.class,() -> {
			entityService.save(entityRequestDTO);
		});
	}	
	
	@Test
	void checkEmailIsRequired() {
		ExternalEntityRequestDTO entityRequestDTO=entityReqDto;
		entityRequestDTO.setEmail(null);
		assertThrows(ConstraintViolationException.class,() -> {
			entityService.save(entityRequestDTO);
		});
	}
	
	@Test
	void checkEmailIsOfProperLength() {
		ExternalEntityRequestDTO entityRequestDTO=entityReqDto;
		entityRequestDTO.setEmail(email);
		assertThrows(ConstraintViolationException.class,() -> {
			entityService.save(entityRequestDTO);
		});
	}
	
	@Test
	void checkPanNoIsOfProperLength() {
		ExternalEntityRequestDTO entityRequestDTO=entityReqDto;
		entityRequestDTO.setPanNoTaxId("BDAPJBDAJ86811KPLL555");
		assertThrows(ConstraintViolationException.class,() -> {
			entityService.save(entityRequestDTO);
		});
	}
	
	//manual validation test
	@Test
	void checkNameIsUnique() {
		ExternalEntityRequestDTO entityRequestDTO=entityReqDto;
		entityService.save(entityRequestDTO);
		entityRequestDTO.setExternalEntityAlias(new HashSet<>());
		assertThrows(ValidationException.class,() -> {
			entityService.save(entityRequestDTO);
		});
	}
	
	@Test
	void checkAliasIsUnique() {
		ExternalEntityRequestDTO entityRequestDTO=entityReqDto;
		entityService.save(entityRequestDTO);
		assertThrows(ValidationException.class,() -> {
			entityService.save(entityRequestDTO);
		});
	}
	
	@Test
	void checkCodeIsUnique() {
		ExternalEntityRequestDTO entityRequestDTO=entityReqDto;
		entityService.save(entityRequestDTO);
		entityRequestDTO.setName("Paras Group");
		entityRequestDTO.setExternalEntityAlias(new HashSet<>());
		assertThrows(ValidationException.class,() -> {
			entityService.save(entityRequestDTO);
		});
	}
	
	@Test 
	void checkCustomerAndVendorIsRequired() {
		ExternalEntityRequestDTO entityRequestDTO=entityReqDto;
		entityRequestDTO.setCustomerFlag(Boolean.FALSE);
		entityRequestDTO.setVendorFlag(Boolean.FALSE);
		assertThrows(ValidationException.class, () -> {
			entityService.save(entityRequestDTO);
		});
	}
	
	@Test
	void checkKeyPersonNameIsRequired() {
		ExternalEntityRequestDTO entityRequestDTO=entityReqDto;
		entityRequestDTO.setKeyPersonName(null);
		assertThrows(ValidationException.class,() -> {
			entityService.save(entityRequestDTO);
		});
	}
	
	@Test
	void checkCellNoIsRequired() {
		ExternalEntityRequestDTO entityRequestDTO=entityReqDto;
		entityRequestDTO.setCellNo(null);
		assertThrows(ValidationException.class,() -> {
			entityService.save(entityRequestDTO);
		});
	}
	
	@Test
	void checkLandlineNoIsRequired() {
		ExternalEntityRequestDTO entityRequestDTO=entityReqDto;
		entityRequestDTO.setLandlineNo(null);
		assertThrows(ValidationException.class,() -> {
			entityService.save(entityRequestDTO);
		});
	}
	
	@Test
	void checkCompanyTypeIsRequired() {
		ExternalEntityRequestDTO entityRequestDTO=entityReqDto;
		entityRequestDTO.setCompanyType(null);
		assertThrows(ValidationException.class,() -> {
			entityService.save(entityRequestDTO);
		});
	}
	
	@Test
	void checkTdsExemptionIsRequired() {
		ExternalEntityRequestDTO entityRequestDTO=entityReqDto;
		entityRequestDTO.setTdsExemption(null);
		assertThrows(ValidationException.class,() -> {
			entityService.save(entityRequestDTO);
		});
	}
	
	@Test
	void checkDomicileIsRequired() {
		ExternalEntityRequestDTO entityRequestDTO=entityReqDto;
		entityRequestDTO.setDomicile(null);
		assertThrows(ValidationException.class,() -> {
			entityService.save(entityRequestDTO);
		});
	}
	
	@Test
	void checkPanNoIsRequired() {
		ExternalEntityRequestDTO entityRequestDTO=entityReqDto;
		entityRequestDTO.setPanNoTaxId(null);
		assertThrows(ValidationException.class,() -> {
			entityService.save(entityRequestDTO);
		});
	}
	
	@Test 
	void checkTdsWefAndValidUptoDateIsRequired() {
		ExternalEntityRequestDTO entityRequestDTO=entityReqDto;
		entityRequestDTO.setTdsExemption(TdsExemption.TOTAL);
		entityRequestDTO.setTdsExemptionWefDate(null);
		entityRequestDTO.setTdsExemptionValidUptoDate(null);
		assertThrows(ValidationException.class, () -> {
			entityService.save(entityRequestDTO);
		});
	}
	
	@Test 
	void checkTdsWefAndValidUptoDateAndPercentageIsRequired() {
		ExternalEntityRequestDTO entityRequestDTO=entityReqDto;
		entityRequestDTO.setTdsExemption(TdsExemption.PARTIAL);
		entityRequestDTO.setTdsExemptionWefDate(LocalDate.now());
		entityRequestDTO.setTdsExemptionValidUptoDate(LocalDate.now());
		entityRequestDTO.setTdsExemptionPercentage(null);
		assertThrows(ValidationException.class, () -> {
			entityService.save(entityRequestDTO);
		});
	}
	
	@Test 
	void checkGstTypeIsRequired() {
		ExternalEntityRequestDTO entityRequestDTO=entityReqDto;
		entityRequestDTO.setDomicile(Domicile.DOMESTIC);
		entityRequestDTO.setGstType(null);
		assertThrows(ValidationException.class, () -> {
			entityService.save(entityRequestDTO);
		});
	}
	
	@Test 
	void checkGstNoIsRequired() {
		ExternalEntityRequestDTO entityRequestDTO=entityReqDto;
		entityRequestDTO.setDomicile(Domicile.DOMESTIC);
		entityRequestDTO.setGstType(EntityGstType.REGISTERED);
		entityRequestDTO.setGstNo(null);
		assertThrows(ValidationException.class, () -> {
			entityService.save(entityRequestDTO);
		});
	}

	public DeactivationDTO prepareDeactivationDTO() {
    	DeactivationDTO deactivateDto = new DeactivationDTO();
    	deactivateDto.setDeactivationWefDate(LocalDate.now());
    	deactivateDto.setDeactivationReason("I no longer need this Entity");
    	deactivateDto.setType(DEACTIVATE);
		return deactivateDto;
    }
	
	@Test
	void testDeactivate() {
		ExternalEntityRequestDTO entityRequestDTO=entityReqDto;
		ExternalEntityDTO savedEntity=entityService.save(entityRequestDTO);
		DeactivationDTO deactivateDto = prepareDeactivationDTO();
		deactivateDto.setReferenceId(savedEntity.getId());

		ExternalEntityDTO entityDTO=entityService.deactivate(deactivateDto);

		assertThat(entityDTO.getDeactivateDtls()).isNotNull();
		assertThat(entityDTO.getDeactivateDtls().getDeactivationReason()).isEqualTo(deactivateDto.getDeactivationReason());
		assertThat(entityDTO.getDeactivateDtls().getDeactivationWefDate()).isEqualTo(deactivateDto.getDeactivationWefDate());
	}

	@Test
	void testDeactivateShouldThrowExceptionWhenDeactivationDateIsPast() throws Exception {
		ExternalEntityRequestDTO entityRequestDTO=entityReqDto;
		ExternalEntityDTO savedEntity=entityService.save(entityRequestDTO);
		DeactivationDTO deactivateDto = prepareDeactivationDTO();
		deactivateDto.setReferenceId(savedEntity.getId());
		deactivateDto.setDeactivationWefDate(LocalDate.now().minus(Period.ofDays(1)));
		assertThrows(ValidationException.class, () -> {
			entityService.deactivate(deactivateDto);
		});
	}

	@Test
	void testDeactivateShouldThrowExceptionWhenReferenceIdNotFound() throws Exception {
		DeactivationDTO deactivateDto = prepareDeactivationDTO();
		deactivateDto.setReferenceId(1L);
		assertThrows(InvalidDataException.class, () -> {
			entityService.deactivate(deactivateDto);
		});
	}
	
	public ReactivationDTO prepareReactivationDTO() {
    	ReactivationDTO reactivateDto = new ReactivationDTO();
    	reactivateDto.setReactivationWefDate(LocalDate.now());
    	reactivateDto.setReactivationReason("I want to reactivate this Entity");
    	reactivateDto.setType(REACTIVATE);
		return reactivateDto;
    }
	
	@Test
	void testReactivate() {
		ExternalEntityRequestDTO entityRequestDTO=entityReqDto;
		ExternalEntityDTO savedEntity=entityService.save(entityRequestDTO);
		ReactivationDTO reactivateDto = prepareReactivationDTO();
		reactivateDto.setReferenceId(savedEntity.getId());

		ExternalEntityDTO entityDTO=entityService.reactivate(reactivateDto);
		assertThat(entityDTO.getReactivateDtls()).isNotNull();
		assertThat(entityDTO.getReactivateDtls().getReactivationReason()).isEqualTo(reactivateDto.getReactivationReason());
		assertThat(entityDTO.getReactivateDtls().getReactivationWefDate()).isEqualTo(reactivateDto.getReactivationWefDate());
	}

	@Test
	void testReactivateShouldThrowExceptionWhenReactivationDateIsPast() throws Exception {
		ExternalEntityRequestDTO entityRequestDTO=entityReqDto;
		ExternalEntityDTO savedEntity=entityService.save(entityRequestDTO);
		ReactivationDTO reactivateDto = prepareReactivationDTO();
		reactivateDto.setReferenceId(savedEntity.getId());
		reactivateDto.setReactivationWefDate(LocalDate.now().minus(Period.ofDays(1)));
		assertThrows(ValidationException.class, () -> {
			entityService.reactivate(reactivateDto);
		});
	}

	@Test
	void testReactivateShouldThrowExceptionWhenReferenceIdNotFound() throws Exception {
		ReactivationDTO reactivateDto = prepareReactivationDTO();
		reactivateDto.setReferenceId(1L);
		assertThrows(InvalidDataException.class, () -> {
			entityService.reactivate(reactivateDto);
		});
	}

}
