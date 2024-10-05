package com.grtship.mdm.serviceimpl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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

import com.grtship.core.dto.EntityBranchTaxDTO;
import com.grtship.mdm.domain.EntityBranch;
import com.grtship.mdm.domain.EntityBranchTax;
import com.grtship.mdm.mapper.EntityBranchMapper;
import com.grtship.mdm.mapper.EntityBranchTaxMapper;
import com.grtship.mdm.mapper.ExternalEntityMapper;
import com.grtship.mdm.repository.EntityBranchTaxRepository;

@SpringBootTest(properties = "config.caching.enabled=false")
@Transactional
@EnableAutoConfiguration(exclude = { KafkaAutoConfiguration.class, KafkaMetricsAutoConfiguration.class,
		AuditAutoConfiguration.class })
@ActiveProfiles(profiles = { "test" })
public class EntityBranchTaxServiceImplTest {
	
	@Autowired
	private EntityBranchTaxServiceImpl branchTaxServiceImpl;
	@Autowired
	private ExternalEntityMapper entityMapper;
	@Autowired
	private EntityManager em;
	@Autowired
	private EntityBranchMapper entityBranchMapper;
	@Autowired
	private EntityBranchTaxRepository branchTaxRepository;
	@Autowired
	private EntityBranchTaxMapper branchTaxMapper;
	
	private EntityBranchTaxDTO branchTaxDTO;
	
	public static EntityBranchTaxDTO prepareBranchTaxDto(EntityManager em, ExternalEntityMapper eem,
			EntityBranchMapper entityBranchMapper) {
		EntityBranch entityBranch = entityBranchMapper
				.toEntity(EntityBranchServiceImplTest.createEntityBranchReqDto(em, eem));
        em.persist(entityBranch);
        em.flush();
		EntityBranchTaxDTO branchTaxDTO = new EntityBranchTaxDTO();
		branchTaxDTO.setGstNo("GVBJ89465161KM");
		branchTaxDTO.setClientId(1L);
		branchTaxDTO.setCompanyId(1L);
		branchTaxDTO.setEntityBranchId(entityBranch.getId());
		branchTaxDTO.setDescription("First Gst No");
		return branchTaxDTO;
	}

	@BeforeEach
	void setUp() {
		branchTaxDTO = prepareBranchTaxDto(em, entityMapper, entityBranchMapper);
	}

	@Test
	void testSave() {
		EntityBranchTaxDTO savedBranchTax = branchTaxServiceImpl.save(branchTaxDTO);
		assertThat(savedBranchTax.getId()).isNotNull();
		assertThat(savedBranchTax.getGstNo()).isEqualTo(branchTaxDTO.getGstNo());
		assertThat(savedBranchTax.getDescription()).isEqualTo(branchTaxDTO.getDescription());
		assertThat(savedBranchTax.getClientId()).isEqualTo(branchTaxDTO.getClientId());
		assertThat(savedBranchTax.getCompanyId()).isEqualTo(branchTaxDTO.getCompanyId());
		assertThat(savedBranchTax.getEntityBranchId()).isEqualTo(branchTaxDTO.getEntityBranchId());
	}
	
	@Test
	void testUpdate() {
		EntityBranchTaxDTO savedBranchTax = branchTaxServiceImpl.save(branchTaxDTO);
		savedBranchTax.setGstNo("RFCTV8818151MK");
		savedBranchTax.setDescription("Test Tax Update");
		EntityBranchTaxDTO updatedBranchTax = branchTaxServiceImpl.save(savedBranchTax);
		assertThat(updatedBranchTax.getId()).isEqualTo(savedBranchTax.getId());
		assertThat(updatedBranchTax.getGstNo()).isNotEqualTo(branchTaxDTO.getGstNo());
		assertThat(updatedBranchTax.getDescription()).isNotEqualTo(branchTaxDTO.getDescription());
		assertThat(updatedBranchTax.getClientId()).isEqualTo(savedBranchTax.getClientId());
		assertThat(updatedBranchTax.getCompanyId()).isEqualTo(savedBranchTax.getCompanyId());
		assertThat(updatedBranchTax.getEntityBranchId()).isEqualTo(savedBranchTax.getEntityBranchId());
	}

	@Test
	void testFindAll() {
		branchTaxServiceImpl.save(branchTaxDTO);
		List<EntityBranchTaxDTO> findAll = branchTaxServiceImpl.findAll();
		assertThat(findAll).isNotEmpty();
		assertThat(findAll).allMatch(branchTax -> branchTax.getId()!=null);
		assertThat(findAll).allMatch(branchTax -> branchTax.getGstNo()!=null);
		assertThat(findAll).allMatch(branchTax -> branchTax.getEntityBranchId()!=null);
	}

	@Test
	void testFindOne() {
		EntityBranchTaxDTO savedBranchTax = branchTaxServiceImpl.save(branchTaxDTO);
		EntityBranchTaxDTO findOneById = branchTaxServiceImpl.findOne(savedBranchTax.getId()).get();
		assertThat(savedBranchTax.getId()).isEqualTo(findOneById.getId());
		assertThat(savedBranchTax.getGstNo()).isEqualTo(findOneById.getGstNo());
		assertThat(savedBranchTax.getDescription()).isEqualTo(findOneById.getDescription());
		assertThat(savedBranchTax.getClientId()).isEqualTo(findOneById.getClientId());
		assertThat(savedBranchTax.getCompanyId()).isEqualTo(findOneById.getCompanyId());
		assertThat(savedBranchTax.getEntityBranchId()).isEqualTo(findOneById.getEntityBranchId());
	}
	
	@Test
	void testFindOneForInvalidId() {
		Optional<EntityBranchTaxDTO> findOneById = branchTaxServiceImpl.findOne(0l);
		assertFalse(findOneById.isPresent());
	}

	@Test
	void testDelete() {
		EntityBranchTaxDTO savedBranchTax = branchTaxServiceImpl.save(branchTaxDTO);
		branchTaxServiceImpl.delete(savedBranchTax.getId());
		Optional<EntityBranchTaxDTO> findOneById = branchTaxServiceImpl.findOne(savedBranchTax.getId());
		assertFalse(findOneById.isPresent());
	}

	@Test
	void testSaveAllListOfEntityBranchTax() {
		EntityBranchTax branchTax = branchTaxMapper.toEntity(branchTaxDTO);
		List<EntityBranchTax> branchTaxs = new ArrayList<>();
		branchTaxs.add(branchTax);
		List<EntityBranchTax> savedBranchTaxs = branchTaxServiceImpl.saveAll(branchTaxs);
		assertThat(savedBranchTaxs).isNotEmpty();
		assertThat(savedBranchTaxs).hasSize(1);
		assertThat(savedBranchTaxs).allMatch(tax -> tax.getId() != null);
		assertThat(savedBranchTaxs).allMatch(tax -> tax.getGstNo() != null);
		assertThat(savedBranchTaxs).allMatch(tax -> tax.getEntityBranch().getId() != null);
	}

	@Test
	void testGetBranchTaxDetailsByBranchId() {
		EntityBranchTaxDTO savedBranchTax = branchTaxServiceImpl.save(branchTaxDTO);
		branchTaxServiceImpl.getBranchTaxDetailsIdsByBranchId(savedBranchTax.getId());
	}

	@Test
	void testGetBranchTaxDetailsIdsByBranchId() {
		EntityBranchTaxDTO savedBranchTax = branchTaxServiceImpl.save(branchTaxDTO);
		Set<Long> branchIds = branchTaxServiceImpl.getBranchTaxDetailsIdsByBranchId(savedBranchTax.getEntityBranchId());
		assertThat(branchIds).isNotEmpty();
		assertThat(branchIds).hasSize(1);
		assertTrue(branchIds.contains(savedBranchTax.getId()));
	}
	
	@Test
	void testGetBranchTaxDetailsIdsByBranchIdForInvalidId() {
		Set<Long> branchIds = branchTaxServiceImpl.getBranchTaxDetailsIdsByBranchId(0l);
		assertThat(branchIds).isEmpty();
	}

	@Test
	void testDeleteByIdIn() {
		EntityBranchTaxDTO savedBranchTax = branchTaxServiceImpl.save(branchTaxDTO);
		Set<Long> ids=new TreeSet<>();
		ids.add(savedBranchTax.getId());
		branchTaxServiceImpl.deleteByIdIn(ids);
		Optional<EntityBranchTaxDTO> findOne = branchTaxServiceImpl.findOne(savedBranchTax.getId());
		assertFalse(findOne.isPresent());
	}

	@Test
	void testSaveAllListOfEntityBranchTaxDTOLong() {
		Long branchId = branchTaxDTO.getEntityBranchId();
		EntityBranchTaxDTO entityBranchTaxDTO = branchTaxDTO;
		entityBranchTaxDTO.setEntityBranchId(null);
		List<EntityBranchTaxDTO> branchTaxDTOs = new ArrayList<>();
		branchTaxDTOs.add(entityBranchTaxDTO);
		branchTaxServiceImpl.saveAll(branchTaxDTOs, branchId);
		List<Long> branchIds=new ArrayList<Long>();
		branchIds.add(branchId);
		List<EntityBranchTax> list = branchTaxRepository.findByEntityBranch_IdIn(branchIds);
		assertThat(list).isNotEmpty();
		assertThat(list).hasSize(1);
		assertThat(list).allMatch(branchTax -> branchTax.getId() != null);
		assertThat(list).allMatch(branchTax -> branchTax.getGstNo() != null);
		assertThat(list).allMatch(branchTax -> branchTax.getEntityBranch().getId().equals(branchId));
	}

	@Test
	void testDeleteTaxDetailsOnUpdate() {
		Long branchId = branchTaxDTO.getEntityBranchId();
		EntityBranchTaxDTO entityBranchTaxDTO = branchTaxDTO;
		entityBranchTaxDTO.setEntityBranchId(null);
		List<EntityBranchTaxDTO> branchTaxDTOs = new ArrayList<EntityBranchTaxDTO>();
		branchTaxDTOs.add(entityBranchTaxDTO);
		branchTaxServiceImpl.saveAll(branchTaxDTOs, branchId);
		branchTaxDTO.setGstNo("TFVY84168156MK");
		List<EntityBranchTaxDTO> entityBranchTaxDTOs = new ArrayList<>();
		entityBranchTaxDTOs.add(branchTaxDTO);
		branchTaxServiceImpl.deleteTaxDetailsOnUpdate(entityBranchTaxDTOs, branchId);
		List<Long> branchIds=new ArrayList<Long>();
		branchIds.add(branchId);
		List<EntityBranchTax> findByEntityBranch_IdIn = branchTaxRepository.findByEntityBranch_IdIn(branchIds);
		assertThat(findByEntityBranch_IdIn).isEmpty();
	}

}
