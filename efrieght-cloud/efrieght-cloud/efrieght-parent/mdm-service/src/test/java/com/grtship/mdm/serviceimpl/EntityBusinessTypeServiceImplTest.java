package com.grtship.mdm.serviceimpl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

import com.grtship.core.dto.ExternalEntityDTO;
import com.grtship.core.dto.ExternalEntityRequestDTO;
import com.grtship.core.enumeration.EntityType;
import com.grtship.mdm.domain.ExternalEntity;
import com.grtship.mdm.mapper.ExternalEntityMapper;

@SpringBootTest(properties = "config.caching.enabled=false")
@Transactional
@EnableAutoConfiguration(exclude = { KafkaAutoConfiguration.class, KafkaMetricsAutoConfiguration.class,
		AuditAutoConfiguration.class })
@ActiveProfiles(profiles = { "test" })
public class EntityBusinessTypeServiceImplTest {
	
	@Autowired
	private EntityBusinessTypeServiceImpl businessTypeServiceImpl;
	@Autowired ExternalEntityServiceImpl entityServiceImpl;
	@Autowired EntityManager em;
	@Autowired private ExternalEntityMapper entityMapper;
	private ExternalEntityRequestDTO externalEntityDTO;
	
	@BeforeEach
	void setUp() {
        externalEntityDTO=ExternalEntityServiceImplTest.createEntityRequestDto(em);		
	}

	@Test
	void testGetEntityBusinessTypesByEntityId() {
		ExternalEntityDTO savedEntity = entityServiceImpl.save(externalEntityDTO);
		Set<String> businessTypesByEntityId = businessTypeServiceImpl
				.getEntityBusinessTypesByEntityId(savedEntity.getId());
		assertThat(businessTypesByEntityId).isNotEmpty();
		assertThat(businessTypesByEntityId).hasSize(externalEntityDTO.getEntityDetails().size());
		assertTrue(businessTypesByEntityId.equals(externalEntityDTO.getEntityDetails()));
	}
	
	@Test
	void testGetEntityBusinessTypesByEntityIdForInvalidId() {
		Set<String> businessTypesByEntityId = businessTypeServiceImpl.getEntityBusinessTypesByEntityId(0l);
		assertThat(businessTypesByEntityId).isEmpty();
	}

	@Test
	void testSaveAll() {
		ExternalEntityDTO savedEntity = entityServiceImpl.save(externalEntityDTO);
		ExternalEntity entity = entityMapper.toEntity(savedEntity);
		businessTypeServiceImpl.saveAll(externalEntityDTO.getEntityDetails(),entity);
		Set<String> businessTypesByEntityId = businessTypeServiceImpl
				.getEntityBusinessTypesByEntityId(savedEntity.getId());
		assertThat(businessTypesByEntityId).isNotEmpty();
		assertThat(businessTypesByEntityId).hasSize(externalEntityDTO.getEntityDetails().size());
		assertTrue(businessTypesByEntityId.equals(externalEntityDTO.getEntityDetails()));
	}

	@Test
	void testDeleteEntityBusinessTypeOnUpdate() {
		ExternalEntityDTO savedEntity = entityServiceImpl.save(externalEntityDTO);
		ExternalEntity entity = entityMapper.toEntity(savedEntity);
		businessTypeServiceImpl.saveAll(externalEntityDTO.getEntityDetails(), entity);
		Set<String> entityDetails = new TreeSet<>();
		entityDetails.add(EntityType.EXPORTER.name());
		businessTypeServiceImpl.deleteEntityBusinessTypeOnUpdate(entityDetails, savedEntity.getId());
		Set<String> businessTypesByEntityId = businessTypeServiceImpl
				.getEntityBusinessTypesByEntityId(savedEntity.getId());
		assertThat(businessTypesByEntityId).isNotEmpty();
		assertThat(businessTypesByEntityId).hasSize(entityDetails.size());
		assertTrue(businessTypesByEntityId.equals(entityDetails));
	}

}
