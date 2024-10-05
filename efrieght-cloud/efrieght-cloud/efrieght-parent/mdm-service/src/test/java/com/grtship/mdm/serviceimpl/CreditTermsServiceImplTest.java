package com.grtship.mdm.serviceimpl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.audit.AuditAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.metrics.KafkaMetricsAutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.grtship.core.dto.CreditTermsDTO;
import com.grtship.core.enumeration.DomainStatus;
import com.grtship.core.enumeration.EntityType;
import com.grtship.mdm.domain.CreditTerms;
import com.grtship.mdm.mapper.CreditTermsMapper;
import com.grtship.mdm.repository.CreditTermsRepository;

@SpringBootTest(properties = "config.caching.enabled=false")
@Transactional
@EnableAutoConfiguration(exclude = { KafkaAutoConfiguration.class, KafkaMetricsAutoConfiguration.class,
		AuditAutoConfiguration.class })
@ActiveProfiles(profiles = { "test" })
public class CreditTermsServiceImplTest {
	
	private static final String ENTITY = "ENTITY";

	@Autowired
	private CreditTermsServiceImpl creditTermsServiceImpl;
	
	@Autowired
	private CreditTermsMapper creditTermsMapper;
	
	@Autowired
	private CreditTermsRepository creditTermsRepository;
	
	private CreditTermsDTO creditTermsDTO;

	public static CreditTermsDTO prepareCreditTermsDto() {
		CreditTermsDTO creditTermsDTO=new CreditTermsDTO();
		creditTermsDTO.setCreditAmount(4561.5);
		creditTermsDTO.setEntityType(EntityType.CUSTOMER);
		creditTermsDTO.setReferenceId(1L);
		creditTermsDTO.setStatus(DomainStatus.PENDING);
		creditTermsDTO.setCreditDays(784);
		creditTermsDTO.setReferenceName(ENTITY);
		return creditTermsDTO;
	}
	
	@BeforeEach
	void setUp() {
		creditTermsDTO=prepareCreditTermsDto();
	}

	@Test
	void testSave() {
		CreditTermsDTO savedCreditTerms = creditTermsServiceImpl.save(creditTermsDTO);
		assertThat(savedCreditTerms.getId()).isNotNull();
		assertThat(savedCreditTerms.getCreditAmount()).isEqualTo(creditTermsDTO.getCreditAmount());
		assertThat(savedCreditTerms.getEntityType()).isEqualTo(creditTermsDTO.getEntityType());
		assertThat(savedCreditTerms.getReferenceId()).isEqualTo(creditTermsDTO.getReferenceId());
		assertThat(savedCreditTerms.getCreditDays()).isEqualTo(creditTermsDTO.getCreditDays());
		assertThat(savedCreditTerms.getReferenceName()).isEqualTo(creditTermsDTO.getReferenceName());
	}

	@Test
	void testFindAll() {
		creditTermsServiceImpl.save(creditTermsDTO);
		Page<CreditTermsDTO> findAll = creditTermsServiceImpl.findAll(PageRequest.of(0, 20));
		assertThat(findAll.getNumber()).isEqualTo(0);
		assertThat(findAll.getContent()).isNotEmpty();
		assertThat(findAll.getContent()).hasSizeLessThanOrEqualTo(20);
		assertThat(findAll.getContent()).allMatch(creditTerm -> creditTerm.getId() != null);
		assertThat(findAll.getContent()).allMatch(creditTerm -> creditTerm.getReferenceId() != null);
		assertThat(findAll.getContent()).allMatch(creditTerm -> creditTerm.getReferenceName() != null);
		assertThat(findAll.getContent()).allMatch(creditTerm -> creditTerm.getCreditDays() != null);
		assertThat(findAll.getContent()).allMatch(creditTerm -> creditTerm.getCreditAmount() != null);
	}

	@Test
	void testFindOne() {
		CreditTermsDTO savedCreditTerms = creditTermsServiceImpl.save(creditTermsDTO);
		CreditTermsDTO getCreditById = creditTermsServiceImpl.findOne(savedCreditTerms.getId()).get();
		assertThat(savedCreditTerms.getId()).isEqualTo(getCreditById.getId());
		assertThat(savedCreditTerms.getCreditAmount()).isEqualTo(getCreditById.getCreditAmount());
		assertThat(savedCreditTerms.getEntityType()).isEqualTo(getCreditById.getEntityType());
		assertThat(savedCreditTerms.getReferenceId()).isEqualTo(getCreditById.getReferenceId());
		assertThat(savedCreditTerms.getCreditDays()).isEqualTo(getCreditById.getCreditDays());
		assertThat(savedCreditTerms.getReferenceName()).isEqualTo(getCreditById.getReferenceName());
	}
	
	@Test
	void testFindOneForInvalidId() {
		Optional<CreditTermsDTO> getCreditById = creditTermsServiceImpl.findOne(0L);
		assertFalse(getCreditById.isPresent());
	}

	@Test
	void testDelete() {
		CreditTermsDTO savedCreditTerms = creditTermsServiceImpl.save(creditTermsDTO);
		creditTermsServiceImpl.delete(savedCreditTerms.getId());
		Optional<CreditTermsDTO> getCreditById = creditTermsServiceImpl.findOne(savedCreditTerms.getId());
		assertFalse(getCreditById.isPresent());
	}

	@Test
	void testSaveAllListOfCreditTerms() {
		CreditTerms creditTerms = creditTermsMapper.toEntity(creditTermsDTO);
		List<CreditTerms> terms = new ArrayList<>();
		terms.add(creditTerms);
		List<CreditTerms> savedCreditTerms = creditTermsServiceImpl.saveAll(terms);
		assertThat(savedCreditTerms).isNotEmpty();
		assertThat(savedCreditTerms).hasSize(terms.size());
		assertThat(savedCreditTerms.get(0).getId()).isNotNull();
		assertThat(savedCreditTerms.get(0).getCreditAmount()).isEqualTo(creditTerms.getCreditAmount());
		assertThat(savedCreditTerms.get(0).getCreditDays()).isEqualTo(creditTerms.getCreditDays());
		assertThat(savedCreditTerms.get(0).getReferenceId()).isEqualTo(creditTerms.getReferenceId());
		assertThat(savedCreditTerms.get(0).getReferenceName()).isEqualTo(creditTerms.getReferenceName());
	}

	@Test
	void testGetCreditTermsByReferenceIdsAndReferenceName() {
		CreditTermsDTO savedCreditTerms = creditTermsServiceImpl.save(creditTermsDTO);
		List<Long> referenceIds=new ArrayList<>();
		referenceIds.add(savedCreditTerms.getReferenceId());
		Set<CreditTermsDTO> creditTerms = creditTermsServiceImpl.getCreditTermsByReferenceIdsAndReferenceName(referenceIds, savedCreditTerms.getReferenceName());
	    assertThat(creditTerms).isNotEmpty();
	    assertThat(creditTerms).hasSizeGreaterThanOrEqualTo(1);
	    assertThat(creditTerms).allMatch(creditTerm -> creditTerm.getId()!=null);
	    assertThat(creditTerms).allMatch(creditTerm -> referenceIds.contains(creditTerm.getReferenceId()));
	    assertThat(creditTerms).allMatch(creditTerm -> creditTerm.getReferenceName().equals(savedCreditTerms.getReferenceName()));
	    assertThat(creditTerms).allMatch(creditTerm -> creditTerm.getCreditDays()!=null);
	    assertThat(creditTerms).allMatch(creditTerm -> creditTerm.getCreditAmount()!=null);
	}
	
	@Test
	void testGetCreditTermsByReferenceIdsAndReferenceNameForInvalidIds() {
		List<Long> referenceIds=new ArrayList<>();
		referenceIds.add(0l);
		Set<CreditTermsDTO> creditTerms = creditTermsServiceImpl.getCreditTermsByReferenceIdsAndReferenceName(referenceIds,"ENTITY");
	    assertThat(creditTerms).isEmpty();
	}
	
	@Test
	void testGetCreditTermsByReferenceIdsAndReferenceNameForInvalidName() {
		List<Long> referenceIds=new ArrayList<>();
		referenceIds.add(1l);
		Set<CreditTermsDTO> creditTerms = creditTermsServiceImpl.getCreditTermsByReferenceIdsAndReferenceName(referenceIds,"ERRORERROR");
	    assertThat(creditTerms).isEmpty();
	}
	
	@Test
	void testGetCreditTermsByReferenceIdsAndReferenceNameForEmptyListOfIds() {
		List<Long> referenceIds=new ArrayList<Long>();
		Set<CreditTermsDTO> creditTerms = creditTermsServiceImpl.getCreditTermsByReferenceIdsAndReferenceName(referenceIds,"ERRORERROR");
	    assertThat(creditTerms).isEmpty();
	}

	@Test
	void testGetCreditTermsIdByReferenceIdAndReferenceName() {
		CreditTermsDTO savedCreditTerms = creditTermsServiceImpl.save(creditTermsDTO);
		Set<Long> ids = creditTermsServiceImpl.getCreditTermsIdByReferenceIdAndReferenceName(savedCreditTerms.getReferenceId(), savedCreditTerms.getReferenceName());
	    assertThat(ids).isNotEmpty();
	    assertThat(ids).hasSizeGreaterThanOrEqualTo(1);
	}
	
	@Test
	void testGetCreditTermsIdByReferenceIdAndReferenceNameForInvalidId() {
		Set<Long> ids = creditTermsServiceImpl.getCreditTermsIdByReferenceIdAndReferenceName(0l, ENTITY);
	    assertThat(ids).isEmpty();
	}
	
	@Test
	void testGetCreditTermsIdByReferenceIdAndReferenceNameForInvalidRefernceName() {
		CreditTermsDTO savedCreditTerms = creditTermsServiceImpl.save(creditTermsDTO);
		Set<Long> ids = creditTermsServiceImpl.getCreditTermsIdByReferenceIdAndReferenceName(savedCreditTerms.getReferenceId(), "ERRORERROR");
	    assertThat(ids).isEmpty();
	}

	@Test
	void testDeleteByReferenceNameAndIdIn() {
		CreditTermsDTO savedCreditTerms = creditTermsServiceImpl.save(creditTermsDTO);
		Set<Long> ids = new TreeSet<>();
		ids.add(savedCreditTerms.getId());
		creditTermsServiceImpl.deleteByReferenceNameAndIdIn(ENTITY, ids);
		List<Long> id=new ArrayList<>();
		id.add(savedCreditTerms.getId());
		List<CreditTerms> creditTerms = creditTermsRepository.findAllById(id);
		assertThat(creditTerms).isEmpty();
	}

	@Test
	void testSaveAllListOfCreditTermsDTOStringLong() {
		creditTermsDTO.setReferenceId(null);
		creditTermsDTO.setReferenceName(null);
		List<CreditTermsDTO> creditTermsDTOs = new ArrayList<>();
		creditTermsDTOs.add(creditTermsDTO);
		Long referenceId = 1l;
		List<Long> referenceIdsList = new ArrayList<>();
		referenceIdsList.add(referenceId);
		creditTermsServiceImpl.saveAll(creditTermsDTOs, ENTITY, referenceId);
		Set<CreditTerms> creditTerms = creditTermsRepository.findByReferenceNameAndReferenceIdIn(ENTITY,
				referenceIdsList);
		assertThat(creditTerms).isNotEmpty();
		assertThat(creditTerms).allMatch(creditTerm -> creditTerm.getId() != null);
		assertThat(creditTerms).allMatch(creditTerm -> creditTerm.getCreditDays() != null);
		assertThat(creditTerms).allMatch(creditTerm -> creditTerm.getCreditAmount() != null);
		assertThat(creditTerms).allMatch(creditTerm -> creditTerm.getReferenceId().equals(referenceId));
		assertThat(creditTerms).allMatch(creditTerm -> creditTerm.getReferenceName().equals(ENTITY));
	}

	@Test
	void testDeleteCreditTermOnUpdate() {
		CreditTermsDTO savedCreditTerms = creditTermsServiceImpl.save(creditTermsDTO);
		List<CreditTermsDTO> creditTermsDTOs = new ArrayList<>();
		creditTermsDTOs.add(creditTermsDTO);
		creditTermsServiceImpl.deleteCreditTermOnUpdate(creditTermsDTOs, ENTITY,
				savedCreditTerms.getReferenceId());
		Optional<CreditTermsDTO> getCreditById = creditTermsServiceImpl.findOne(savedCreditTerms.getId());
		assertFalse(getCreditById.isPresent());
	}

}
