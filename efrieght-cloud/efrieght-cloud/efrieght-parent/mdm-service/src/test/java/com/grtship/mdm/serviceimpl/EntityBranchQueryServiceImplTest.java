package com.grtship.mdm.serviceimpl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.persistence.EntityManager;

import org.apache.commons.collections4.SetUtils;
import org.apache.commons.collections4.list.TreeList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.audit.AuditAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.metrics.KafkaMetricsAutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.grtship.core.dto.AddressDTO;
import com.grtship.core.dto.BranchContactDTO;
import com.grtship.core.dto.EntityBranchDTO;
import com.grtship.core.dto.EntityBranchRequestDTO;
import com.grtship.core.dto.EntityBranchTaxDTO;
import com.grtship.core.enumeration.DomainStatus;
import com.grtship.core.enumeration.EntityLevel;
import com.grtship.mdm.criteria.EntityBranchCriteria;
import com.grtship.mdm.domain.BranchContact;
import com.grtship.mdm.domain.CreditTerms;
import com.grtship.mdm.domain.EntityBranchTax;
import com.grtship.mdm.domain.ExternalEntity;
import com.grtship.mdm.dto.EntityBranchMultiDropDownDTO;
import com.grtship.mdm.mapper.BranchContactMapper;
import com.grtship.mdm.mapper.ExternalEntityMapper;
import com.grtship.mdm.repository.BranchContactRepository;
import com.grtship.mdm.repository.CreditTermsRepository;
import com.grtship.mdm.repository.EntityBranchTaxRepository;
import com.grtship.mdm.service.EntityBranchQueryService;
import com.grtship.mdm.service.EntityBranchService;

@SpringBootTest(properties = "config.caching.enabled=false")
@Transactional
@EnableAutoConfiguration(exclude = { KafkaAutoConfiguration.class, KafkaMetricsAutoConfiguration.class,
		AuditAutoConfiguration.class })
@ActiveProfiles(profiles = { "test" })
public class EntityBranchQueryServiceImplTest {
	
	private static final String INDIA = "India";
	private static final String ENTITY_BRANCH = "Entity Branch";
	@Autowired private EntityBranchService entityBranchService;
	@Autowired private EntityManager em;
	@Autowired BranchContactRepository contactRepository;
	@Autowired BranchContactMapper contactMapper;
	@Autowired private EntityBranchQueryService branchFilterService;
	@Autowired private ExternalEntityMapper entityMapper;
	@Autowired private CreditTermsRepository creditTermsRepository;
	@Autowired private EntityBranchTaxRepository branchTaxRepository;
	@Autowired private BranchContactRepository branchContactRepository;
	@Mock Pageable pageable;
	private EntityBranchRequestDTO entityBranchReqDto;
	private EntityBranchCriteria branchCriteria;
	
	public EntityBranchRequestDTO createEntityBranchReqDto(EntityManager em) {
		Set<String> cellNumbers = new HashSet<>();
		cellNumbers.add("8898617911");
		cellNumbers.add("8898685830");

		AddressDTO address = AddressServiceImplTest.setUp(em);
		address.setCountryName(INDIA);	
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

	private List<BranchContactDTO> prepareBranchContactDetails() {
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

	private List<EntityBranchTaxDTO> prepareBranchTaxDetails() {
		List<EntityBranchTaxDTO> taxDetialsDto = new ArrayList<>();
		EntityBranchTaxDTO taxDetails = new EntityBranchTaxDTO();
		taxDetails.setGstNo("FCV49816516KM");
		taxDetails.setDescription("First GST No");
		taxDetialsDto.add(taxDetails);
		return taxDetialsDto;
	}
	
	@BeforeEach
	public void setUp() {
		entityBranchReqDto = createEntityBranchReqDto(em);
	}

	@Test
	void testFindAll() {
		EntityBranchDTO savedEntityBranch = entityBranchService.save(entityBranchReqDto);
		branchCriteria = new EntityBranchCriteria();
		branchCriteria.setName(savedEntityBranch.getName());
		Page<EntityBranchDTO> findAll = branchFilterService.findAll(branchCriteria, PageRequest.of(0, 20));
		assertThat(findAll.getSize()).isEqualTo(20);
		assertThat(findAll.getContent()).isNotEmpty();
		assertThat(findAll.getContent()).allMatch(branch -> branch.getId() != null);
		assertThat(findAll.getContent()).allMatch(branch -> branch.getName().contains(branchCriteria.getName()));
		assertThat(findAll.getContent()).allMatch(branch -> branch.getCode() != null);
		assertThat(findAll.getContent()).allMatch(branch -> branch.getExternalEntityId() != null);
	}

	@Test
	void testFindByCriteriaName() {
		EntityBranchDTO savedEntityBranch = entityBranchService.save(entityBranchReqDto);
		branchCriteria = new EntityBranchCriteria();
		branchCriteria.setName(savedEntityBranch.getName());
		List<EntityBranchDTO> branchDTOs = branchFilterService.findByCriteria(branchCriteria);
		assertThat(branchDTOs).isNotEmpty();
		assertThat(branchDTOs).allMatch(branch -> branch.getId() != null);
		assertThat(branchDTOs).allMatch(branch -> branch.getName().contains(branchCriteria.getName()));
		assertThat(branchDTOs).allMatch(branch -> branch.getCode() != null);
		assertThat(branchDTOs).allMatch(branch -> branch.getExternalEntityId() != null);
	}

	@Test
	void testFindByCriteriaNameForInvalidName() {
		branchCriteria = new EntityBranchCriteria();
		branchCriteria.setName("ERRORERRORERROR");
		List<EntityBranchDTO> branchDTOs = branchFilterService.findByCriteria(branchCriteria);
		assertThat(branchDTOs).isEmpty();
	}
	
	@Test
	void testFindByCriteriaCode() {
		EntityBranchDTO savedEntityBranch = entityBranchService.save(entityBranchReqDto);
		branchCriteria = new EntityBranchCriteria();
		branchCriteria.setCode(savedEntityBranch.getCode());
		List<EntityBranchDTO> branchDTOs = branchFilterService.findByCriteria(branchCriteria);
		assertThat(branchDTOs).allMatch(branch -> branch.getId() != null);
		assertThat(branchDTOs).allMatch(branch -> branch.getName() != null);
		assertThat(branchDTOs).allMatch(branch -> branch.getCode().contains(branchCriteria.getCode()));
		assertThat(branchDTOs).allMatch(branch -> branch.getExternalEntityId() != null);
	}

	@Test
	void testFindByCriteriaNameForInvalidCode() {
		branchCriteria = new EntityBranchCriteria();
		branchCriteria.setCode("ERRORERRORERROR");
		List<EntityBranchDTO> branchDTOs = branchFilterService.findByCriteria(branchCriteria);
		assertThat(branchDTOs).isEmpty();
	}
	
	@Test
	void testFindByCriteriaCustomerFlag() {
		EntityBranchDTO savedEntityBranch = entityBranchService.save(entityBranchReqDto);
		branchCriteria = new EntityBranchCriteria();
		branchCriteria.setCustomerFlag(savedEntityBranch.getCustomerFlag());
		List<EntityBranchDTO> branchDTOs = branchFilterService.findByCriteria(branchCriteria);
		assertThat(branchDTOs).allMatch(branch -> branch.getId() != null);
		assertThat(branchDTOs).allMatch(branch -> branch.getName() != null);
		assertThat(branchDTOs).allMatch(branch -> branch.getCode() != null);
		assertThat(branchDTOs).allMatch(branch -> branch.getExternalEntityId() != null);
		assertThat(branchDTOs).allMatch(branch -> branch.getCustomerFlag().equals(branchCriteria.getCustomerFlag()));
	}
	
	@Test
	void testFindByCriteriaVendorFlag() {
		EntityBranchDTO savedEntityBranch = entityBranchService.save(entityBranchReqDto);
		branchCriteria = new EntityBranchCriteria();
		branchCriteria.setVendorFlag(savedEntityBranch.getVendorFlag());
		List<EntityBranchDTO> branchDTOs = branchFilterService.findByCriteria(branchCriteria);
		assertThat(branchDTOs).allMatch(branch -> branch.getId() != null);
		assertThat(branchDTOs).allMatch(branch -> branch.getName() != null);
		assertThat(branchDTOs).allMatch(branch -> branch.getCode() != null);
		assertThat(branchDTOs).allMatch(branch -> branch.getExternalEntityId() != null);
		assertThat(branchDTOs).allMatch(branch -> branch.getVendorFlag().equals(branchCriteria.getVendorFlag()));
	}
	
	@Test
	void testFindByCriteriaActiveFlag() {
		EntityBranchDTO savedEntityBranch = entityBranchService.save(entityBranchReqDto);
		branchCriteria = new EntityBranchCriteria();
		branchCriteria.setActiveFlag(savedEntityBranch.getActiveFlag());
		List<EntityBranchDTO> branchDTOs = branchFilterService.findByCriteria(branchCriteria);
		assertThat(branchDTOs).allMatch(branch -> branch.getId() != null);
		assertThat(branchDTOs).allMatch(branch -> branch.getName() != null);
		assertThat(branchDTOs).allMatch(branch -> branch.getCode() != null);
		assertThat(branchDTOs).allMatch(branch -> branch.getExternalEntityId() != null);
		assertThat(branchDTOs).allMatch(branch -> branch.getActiveFlag().equals(branchCriteria.getActiveFlag()));
	}
	
	@Test
	void testFindByCriteriaEntityId() {
		EntityBranchDTO savedEntityBranch = entityBranchService.save(entityBranchReqDto);
		branchCriteria = new EntityBranchCriteria();
		branchCriteria.setEntiyId(savedEntityBranch.getExternalEntityId());
		List<EntityBranchDTO> branchDTOs = branchFilterService.findByCriteria(branchCriteria);
		assertThat(branchDTOs).allMatch(branch -> branch.getId() != null);
		assertThat(branchDTOs).allMatch(branch -> branch.getName() != null);
		assertThat(branchDTOs).allMatch(branch -> branch.getCode() != null);
		assertThat(branchDTOs).allMatch(branch -> branch.getExternalEntityId().equals(branchCriteria.getEntiyId()));
	}
	
	@Test
	void testFindByCriteriaNameForInvalidEntityId() {
		branchCriteria = new EntityBranchCriteria();
		branchCriteria.setEntiyId(0L);
		List<EntityBranchDTO> branchDTOs = branchFilterService.findByCriteria(branchCriteria);
		assertThat(branchDTOs).isEmpty();
	}
	
	@Test
	void testFindByCriteriaEntityIdList() {
		EntityBranchDTO savedEntityBranch = entityBranchService.save(entityBranchReqDto);
		branchCriteria = new EntityBranchCriteria();
		List<Long> entityIds=new TreeList<>();
		entityIds.add(savedEntityBranch.getExternalEntityId());
		branchCriteria.setEntityIds(entityIds);
		List<EntityBranchDTO> branchDTOs = branchFilterService.findByCriteria(branchCriteria);
		assertThat(branchDTOs).allMatch(branch -> branch.getId() != null);
		assertThat(branchDTOs).allMatch(branch -> branch.getName() != null);
		assertThat(branchDTOs).allMatch(branch -> branch.getCode() != null);
		assertThat(branchDTOs)
				.allMatch(branch -> branch.getExternalEntityId().equals(branchCriteria.getEntityIds().get(0)));
	}
	
	@Test
	void testFindByCriteriaNameForInvalidEntityIdList() {
		List<Long> entityIds=new LinkedList<>();
		entityIds.add(0l);
		branchCriteria = new EntityBranchCriteria();
		branchCriteria.setEntityIds(entityIds);
		List<EntityBranchDTO> branchDTOs = branchFilterService.findByCriteria(branchCriteria);
		assertThat(branchDTOs).isEmpty();
	}
	
	@Test
	void testFindByCriteriaId() {
		EntityBranchDTO savedEntityBranch = entityBranchService.save(entityBranchReqDto);
		branchCriteria = new EntityBranchCriteria();
		branchCriteria.setId(savedEntityBranch.getId());
		List<EntityBranchDTO> branchDTOs = branchFilterService.findByCriteria(branchCriteria);
		assertThat(branchDTOs).allMatch(branch -> branch.getId().equals(branchCriteria.getId()));
		assertThat(branchDTOs).allMatch(branch -> branch.getName() != null);
		assertThat(branchDTOs).allMatch(branch -> branch.getCode() != null);
		assertThat(branchDTOs).allMatch(branch -> branch.getExternalEntityId() != null);
	}
	
	@Test
	void testFindByCriteriaNameForInvalidId() {
		branchCriteria = new EntityBranchCriteria();
		branchCriteria.setId(0L);
		List<EntityBranchDTO> branchDTOs = branchFilterService.findByCriteria(branchCriteria);
		assertThat(branchDTOs).isEmpty();
	}
	
	@Test
	void testFindByCriteriaCountryName() {
		entityBranchService.save(entityBranchReqDto);
		branchCriteria = new EntityBranchCriteria();
		branchCriteria.setCountry(INDIA);
		List<EntityBranchDTO> branchDTOs = branchFilterService.findByCriteria(branchCriteria);
		assertThat(branchDTOs).allMatch(branch -> branch.getId() != null);
		assertThat(branchDTOs).allMatch(branch -> branch.getName() != null);
		assertThat(branchDTOs).allMatch(branch -> branch.getCode() != null);
		assertThat(branchDTOs).allMatch(branch -> branch.getExternalEntityId() != null);
		assertThat(branchDTOs)
				.allMatch(branch -> branch.getAddress().getCountryName().contains(branchCriteria.getCountry()));
	}
	
	@Test
	void testFindByCriteriaCountryNameForInvalidCountry() {
		branchCriteria = new EntityBranchCriteria();
		branchCriteria.setCountry("ERRORERRORERROR");
		List<EntityBranchDTO> branchDTOs = branchFilterService.findByCriteria(branchCriteria);
		assertThat(branchDTOs).isEmpty();
	}
	
	@Test
	void testFindByCriteriaStatus() {
		EntityBranchDTO savedEntityBranch = entityBranchService.save(entityBranchReqDto);
		branchCriteria = new EntityBranchCriteria();
		branchCriteria.setStatus(savedEntityBranch.getStatus().name());
		List<EntityBranchDTO> branchDTOs = branchFilterService.findByCriteria(branchCriteria);
		assertThat(branchDTOs).allMatch(branch -> branch.getId() != null);
		assertThat(branchDTOs).allMatch(branch -> branch.getName() != null);
		assertThat(branchDTOs).allMatch(branch -> branch.getCode() != null);
		assertThat(branchDTOs).allMatch(branch -> branch.getExternalEntityId() != null);
		assertThat(branchDTOs).allMatch(branch -> branch.getStatus().name().equals(branchCriteria.getName()));
	}

	@Test
	void testFindOne() {
		EntityBranchDTO savedEntityBranch = entityBranchService.save(entityBranchReqDto);
		EntityBranchDTO findBranchById = branchFilterService.findOne(savedEntityBranch.getId()).get();
		assertThat(findBranchById).isNotNull();
		assertThat(savedEntityBranch.getName()).isEqualTo(findBranchById.getName());
		assertThat(savedEntityBranch.getCode()).isEqualTo(findBranchById.getCode());
		assertThat(savedEntityBranch.getAddress().getId()).isNotNull();
		assertThat(savedEntityBranch.getExternalEntityId()).isEqualTo(findBranchById.getExternalEntityId());
		assertThat(savedEntityBranch.getId()).isNotNull();
		assertThat(savedEntityBranch.getActiveFlag()).isEqualTo(findBranchById.getActiveFlag());
		assertThat(savedEntityBranch.getStatus()).isEqualTo(findBranchById.getStatus());
		assertThat(savedEntityBranch.getVendorFlag()).isEqualTo(findBranchById.getVendorFlag());
		assertThat(savedEntityBranch.getCustomerFlag()).isEqualTo(findBranchById.getCustomerFlag());
		assertThat(savedEntityBranch.getCustomerEntityLevel()).isEqualTo(findBranchById.getCustomerEntityLevel());
		assertThat(savedEntityBranch.getVendorFlag()).isEqualTo(findBranchById.getVendorFlag());
		assertThat(findBranchById.getSez()).isFalse();
		assertThat(savedEntityBranch.getSezValidUptoDate()).isEqualTo(findBranchById.getSezValidUptoDate());
		assertThat(savedEntityBranch.getSezWEFDate()).isEqualTo(findBranchById.getSezWEFDate());
		Set<CreditTerms> savedCreditTerms = creditTermsRepository.findByReferenceNameAndReferenceIdIn(ENTITY_BRANCH,
				Arrays.asList(savedEntityBranch.getId()));
		Set<CreditTerms> findByIdCreditTerms = creditTermsRepository.findByReferenceNameAndReferenceIdIn(ENTITY_BRANCH,
				Arrays.asList(findBranchById.getId()));
		assertTrue(SetUtils.isEqualSet(savedCreditTerms, findByIdCreditTerms));
		List<Long> entityBranchIds=new LinkedList<Long>();
		entityBranchIds.add(savedEntityBranch.getId());
		List<EntityBranchTax> savedBranchTaxes = branchTaxRepository
				.findByEntityBranch_IdIn(entityBranchIds);
		List<Long> branchIds=new LinkedList<Long>();
		List<EntityBranchTax> findBranchByIdBranchTaxes = branchTaxRepository
				.findByEntityBranch_IdIn(branchIds);
		assertTrue(SetUtils.isEqualSet(savedBranchTaxes, findBranchByIdBranchTaxes));
		List<BranchContact> findBranchByIdBranchContacts = branchContactRepository
				.findByEntityBranch_IdIn(branchIds);
		List<BranchContact> savedBranchContacts = branchContactRepository
				.findByEntityBranch_IdIn(branchIds);
		assertTrue(SetUtils.isEqualSet(findBranchByIdBranchContacts, savedBranchContacts));
	}
	
	@Test
	void testFindOneForInvalidId() {
		Optional<EntityBranchDTO> findBranchById = branchFilterService.findOne(0L);
		assertFalse(findBranchById.isPresent());
	}

	@Test
	void testGetAllBranchesForMultiDD() {
		EntityBranchDTO savedEntityBranch = entityBranchService.save(entityBranchReqDto);
		branchCriteria = new EntityBranchCriteria();
		branchCriteria.setEntiyId(savedEntityBranch.getExternalEntityId());
		Page<EntityBranchMultiDropDownDTO> allBranchesForMultiDD = branchFilterService
				.getAllBranchesForMultiDD(branchCriteria, PageRequest.of(0, 20));
		assertThat(allBranchesForMultiDD.getNumber()).isEqualTo(0);
		assertThat(allBranchesForMultiDD.getContent()).hasSizeLessThanOrEqualTo(20);
		assertThat(allBranchesForMultiDD.getContent()).isNotEmpty();
		assertThat(allBranchesForMultiDD.getContent()).allMatch(branch -> branch.getId() != null);
		assertThat(allBranchesForMultiDD.getContent()).allMatch(branch -> branch.getName() != null);
		assertThat(allBranchesForMultiDD.getContent()).allMatch(branch -> branch.getCode() != null);
	}
	
	@Test
	void testGetAllBranchesForMultiDDForInvalidCriteria() {
		branchCriteria = new EntityBranchCriteria();
		branchCriteria.setEntiyId(0L);
		Page<EntityBranchMultiDropDownDTO> allBranchesForMultiDD = branchFilterService
				.getAllBranchesForMultiDD(branchCriteria, PageRequest.of(0, 20));
		assertThat(allBranchesForMultiDD.getContent()).isEmpty();
	}

	@Test
	void testGetBranchIdsByEntityIdAndActiveFlag() {
		EntityBranchDTO savedEntityBranch = entityBranchService.save(entityBranchReqDto);
		List<Long> branchIds = branchFilterService.getBranchIdsByEntityIdAndActiveFlag(
				savedEntityBranch.getExternalEntityId(), savedEntityBranch.getActiveFlag());
		assertThat(branchIds).isNotEmpty();
		assertThat(branchIds).hasSizeGreaterThanOrEqualTo(1);
	}
	
	@Test
	void testGetBranchIdsByEntityIdAndActiveFlagForInvalidId() {
		List<Long> branchIds = branchFilterService.getBranchIdsByEntityIdAndActiveFlag(0L, Boolean.TRUE);
		assertThat(branchIds).isEmpty();
	}
}
