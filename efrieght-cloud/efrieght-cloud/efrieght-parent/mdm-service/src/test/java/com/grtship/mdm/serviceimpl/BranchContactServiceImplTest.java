package com.grtship.mdm.serviceimpl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

import com.grtship.core.dto.BranchContactDTO;
import com.grtship.core.dto.EntityBranchRequestDTO;
import com.grtship.mdm.domain.BranchContact;
import com.grtship.mdm.domain.EntityBranch;
import com.grtship.mdm.mapper.BranchContactMapper;
import com.grtship.mdm.mapper.EntityBranchMapper;
import com.grtship.mdm.mapper.ExternalEntityMapper;
import com.grtship.mdm.repository.BranchContactRepository;

@SpringBootTest(properties = "config.caching.enabled=false")
@Transactional
@EnableAutoConfiguration(exclude = { KafkaAutoConfiguration.class, KafkaMetricsAutoConfiguration.class,
		AuditAutoConfiguration.class })
@ActiveProfiles(profiles = { "test" })
public class BranchContactServiceImplTest {
	
	@Autowired
	private BranchContactServiceImpl branchContactServiceImpl;
	@Autowired
	private ExternalEntityMapper entityMapper;
	@Autowired
	private EntityBranchMapper branchMapper;
	@Autowired
	private EntityManager em;
	@Autowired
	private BranchContactMapper branchContactMapper;
	@Autowired
	private BranchContactRepository branchContactRepository;
	
	private BranchContactDTO branchContactDTO;
	
	
	public static BranchContactDTO prepareBranchContactDto(EntityManager em,EntityBranchMapper entityBranchMapper,
			ExternalEntityMapper entityMapper) {
		EntityBranchRequestDTO branchReqDto = EntityBranchServiceImplTest.createEntityBranchReqDto(em,entityMapper);
		EntityBranch entityBranch = entityBranchMapper.toEntity(branchReqDto);
		em.persist(entityBranch);
		em.flush();
		BranchContactDTO contactDTO=new BranchContactDTO();
		contactDTO.setName("Test Save");
		contactDTO.setEmail("jayesh.test@gmail.com");
		contactDTO.setCellNumber("8898617911");
		contactDTO.setDepartmentName("IT");
		contactDTO.setDesignationName("Senior Software Developer");
		contactDTO.setEntityBranchId(entityBranch.getId());
		return contactDTO;
	}
	
	@BeforeEach
	void setUp() {
		branchContactDTO=prepareBranchContactDto(em, branchMapper, entityMapper);
	}

	@Test
	void testSaveAllListOfBranchContact() {
		BranchContact branchContact = branchContactMapper.toEntity(branchContactDTO);
		List<BranchContact> branchContacts=new LinkedList<>();
		branchContacts.add(branchContact);
		List<BranchContact> savedContacts = branchContactServiceImpl.saveAll(branchContacts);
		assertThat(savedContacts).isNotEmpty();
		assertThat(savedContacts).hasSize(1);
		assertThat(savedContacts).allMatch(contact -> contact.getId()!=null);
		assertThat(savedContacts).allMatch(contact -> contact.getEmail()!=null);
		assertThat(savedContacts).allMatch(contact -> contact.getName()!=null);
		assertThat(savedContacts).allMatch(contact -> contact.getEntityBranch()!=null);
		assertThat(savedContacts).allMatch(contact -> contact.getDepartmentName()!=null);
		assertThat(savedContacts).allMatch(contact -> contact.getDesignationName()!=null);
		assertThat(savedContacts).allMatch(contact -> contact.getCellNumber()!=null);
	}

	@Test
	void testGetBranchContactsByBranchId() {
		BranchContact branchContact = branchContactMapper.toEntity(branchContactDTO);
		List<BranchContact> branchContacts = new LinkedList<>();
		branchContacts.add(branchContact);
		List<BranchContact> savedContacts = branchContactServiceImpl.saveAll(branchContacts);
		List<Long> branchIds = savedContacts.stream().filter(contact -> contact.getEntityBranch() != null)
				.map(contact -> contact.getEntityBranch().getId()).collect(Collectors.toList());
		List<BranchContactDTO> contactsByBranchId = branchContactServiceImpl.getBranchContactsByBranchId(branchIds);
		assertThat(contactsByBranchId).isNotEmpty();
		assertThat(contactsByBranchId).allMatch(contact -> contact.getId() != null);
		assertThat(contactsByBranchId).allMatch(contact -> contact.getEmail() != null);
		assertThat(contactsByBranchId).allMatch(contact -> contact.getName() != null);
		assertThat(contactsByBranchId).allMatch(contact -> contact.getEntityBranchId().equals(branchIds.get(0)));
		assertThat(contactsByBranchId).allMatch(contact -> contact.getDepartmentName() != null);
		assertThat(contactsByBranchId).allMatch(contact -> contact.getDesignationName() != null);
		assertThat(contactsByBranchId).allMatch(contact -> contact.getCellNumber() != null);
	}
	
	@Test
	void testGetBranchContactsByBranchIdForInvalidId() {
		List<Long> ids=new LinkedList<>();
		ids.add(0l);
		List<BranchContactDTO> contactsByBranchId = branchContactServiceImpl.getBranchContactsByBranchId(ids);
		assertThat(contactsByBranchId).isEmpty();
	}

	@Test
	void testSaveAllListOfBranchContactDTOLong() {
		List<BranchContactDTO> branchContacts=new LinkedList<>();
		branchContacts.add(branchContactDTO);
		branchContactServiceImpl.saveAll(branchContacts,branchContactDTO.getEntityBranchId());
		List<Long> branchIds = branchContacts.stream().map(BranchContactDTO::getEntityBranchId).collect(Collectors.toList());
		List<BranchContact> findByEntityBranch_IdIn = branchContactRepository.findByEntityBranch_IdIn(branchIds);
		assertThat(findByEntityBranch_IdIn).isNotEmpty();
	}

	@Test
	void testDeleteBranchContactsOnUpdate() {
		BranchContact branchContact1 = branchContactMapper.toEntity(branchContactDTO);
		branchContactDTO.setName("Account");
		branchContactDTO.setEmail("test.account@localhost.com");
		branchContactDTO.setCellNumber("8898617911");
		BranchContact branchContact2 = branchContactMapper.toEntity(branchContactDTO);
		List<BranchContact> branchContacts = new LinkedList<>();
		branchContacts.add(branchContact1);
		branchContacts.add(branchContact2);
		List<BranchContact> savedContacts = branchContactServiceImpl.saveAll(branchContacts);
		List<BranchContact> contacts=new LinkedList<>();
		contacts.add(savedContacts.get(0));
		List<BranchContactDTO> branchContactDTOs = branchContactMapper.toDto(contacts);
		branchContactServiceImpl.deleteBranchContactsOnUpdate(branchContactDTOs, branchContactDTO.getEntityBranchId());
		Optional<BranchContact> findById = branchContactRepository.findById(savedContacts.get(1).getId());
		assertFalse(findById.isPresent());
	}

}
