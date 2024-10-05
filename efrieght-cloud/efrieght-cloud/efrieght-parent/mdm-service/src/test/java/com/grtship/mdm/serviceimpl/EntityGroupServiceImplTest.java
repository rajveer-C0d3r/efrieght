package com.grtship.mdm.serviceimpl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

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

import com.grtship.core.dto.EntityGroupCriteriaDTO;
import com.grtship.core.dto.EntityGroupDTO;

@SpringBootTest(properties = "config.caching.enabled=false")
@Transactional
@EnableAutoConfiguration(exclude = { KafkaAutoConfiguration.class, KafkaMetricsAutoConfiguration.class,
		AuditAutoConfiguration.class })
@ActiveProfiles(profiles = { "test" })
public class EntityGroupServiceImplTest {
	
	
	@Autowired
	private EntityGroupServiceImpl entityGroupServiceImpl;

	private EntityGroupDTO entityGroupDTO;
	private EntityGroupCriteriaDTO entityGroupCriteriaDTO;
	
	public static EntityGroupDTO prepareEntityGroupDto() {
		EntityGroupDTO entityGroupDTO = new EntityGroupDTO();
		entityGroupDTO.setCode("VMG");
		entityGroupDTO.setName("Vimal Group");
		return entityGroupDTO;
	}
	
	@BeforeEach
	void setUp() {
		entityGroupDTO=prepareEntityGroupDto();
	}

	@Test
	void testSave() {
		EntityGroupDTO savedEntityGroup = entityGroupServiceImpl.save(entityGroupDTO);
		assertThat(savedEntityGroup.getId()).isNotNull();
		assertThat(savedEntityGroup.getCode()).isEqualTo(entityGroupDTO.getCode());
		assertThat(savedEntityGroup.getName()).isEqualTo(entityGroupDTO.getName());
	}
	
	@Test
	void testSaveIfCodeIsNull() {
		entityGroupDTO.setCode(null);
		EntityGroupDTO savedEntityGroup = entityGroupServiceImpl.save(entityGroupDTO);
		assertThat(savedEntityGroup.getId()).isNotNull();
		assertThat(savedEntityGroup.getCode()).isNotNull();
		assertThat(savedEntityGroup.getName()).isEqualTo(entityGroupDTO.getName());
	}
	
	@Test
	void testSaveIfGroupWithSameNameAlreadyPresent() {
		EntityGroupDTO savedEntityGroup = entityGroupServiceImpl.save(entityGroupDTO);
		entityGroupDTO.setCode("TESTSAVE");
		EntityGroupDTO savedGroup = entityGroupServiceImpl.save(entityGroupDTO);
		assertThat(savedGroup.getId()).isEqualTo(savedEntityGroup.getId());
		assertThat(savedEntityGroup.getCode()).isEqualTo(savedGroup.getCode());
		assertThat(savedEntityGroup.getName()).isEqualTo(savedGroup.getName());
	}

	@Test
	void testFindAllByCriteriaCode() {
		EntityGroupDTO savedEntityGroup = entityGroupServiceImpl.save(entityGroupDTO);
		entityGroupCriteriaDTO = new EntityGroupCriteriaDTO(null, savedEntityGroup.getCode(), null);
		List<EntityGroupDTO> findAll = entityGroupServiceImpl.findAll(entityGroupCriteriaDTO);
		assertThat(findAll).isNotEmpty();
		assertThat(findAll).allMatch(entityGroup -> entityGroup.getId() != null);
		assertThat(findAll).allMatch(entityGroup -> entityGroup.getCode().contains(entityGroupCriteriaDTO.getCode()));
		assertThat(findAll).allMatch(entityGroup -> entityGroup.getName() != null);
	}
	
	@Test
	void testFindAllByCriteriaCodeForInvalidCode() {
		entityGroupCriteriaDTO = new EntityGroupCriteriaDTO(null, "ERRORERROR", null);
		List<EntityGroupDTO> findAll = entityGroupServiceImpl.findAll(entityGroupCriteriaDTO);
		assertThat(findAll).isEmpty();
	}
	
	@Test
	void testFindAllByCriteriaName() {
		EntityGroupDTO savedEntityGroup = entityGroupServiceImpl.save(entityGroupDTO);
		entityGroupCriteriaDTO = new EntityGroupCriteriaDTO(null, null,savedEntityGroup.getName());
		List<EntityGroupDTO> findAll = entityGroupServiceImpl.findAll(entityGroupCriteriaDTO);
		assertThat(findAll).isNotEmpty();
		assertThat(findAll).allMatch(entityGroup -> entityGroup.getId() != null);
		assertThat(findAll).allMatch(entityGroup -> entityGroup.getCode()!=null);
		assertThat(findAll).allMatch(entityGroup -> entityGroup.getName() != null);
	}
	
	@Test
	void testFindAllByCriteriaNameForInvalidName() {
		entityGroupCriteriaDTO = new EntityGroupCriteriaDTO(null, null, "ERRORERROR");
		List<EntityGroupDTO> findAll = entityGroupServiceImpl.findAll(entityGroupCriteriaDTO);
		assertThat(findAll).isEmpty();
	}

	@Test
	void testFindOne() {
		EntityGroupDTO savedEntityGroup = entityGroupServiceImpl.save(entityGroupDTO);
		Optional<EntityGroupDTO> findOne = entityGroupServiceImpl.findOne(savedEntityGroup.getId());
		assertTrue(findOne.isPresent());
		assertThat(savedEntityGroup.getId()).isEqualTo(findOne.get().getId());
		assertThat(savedEntityGroup.getCode()).isEqualTo(findOne.get().getCode());
		assertThat(savedEntityGroup.getName()).isEqualTo(findOne.get().getName());
	}
	
	@Test
	void testFindOneForInvalidId() {
		Optional<EntityGroupDTO> findOne = entityGroupServiceImpl.findOne(0l);
		assertFalse(findOne.isPresent());
	}

	@Test
	void testDelete() {
		EntityGroupDTO savedEntityGroup = entityGroupServiceImpl.save(entityGroupDTO);
		entityGroupServiceImpl.delete(savedEntityGroup.getId());
		Optional<EntityGroupDTO> findOne = entityGroupServiceImpl.findOne(savedEntityGroup.getId());
		assertFalse(findOne.isPresent());
	}

}
