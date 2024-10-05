package com.grtship.mdm.serviceimpl;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

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

import com.grtship.core.dto.ObjectAliasDTO;
import com.grtship.mdm.criteria.ObjectAliasCriteria;

@SpringBootTest(properties = "config.caching.enabled=false")
@Transactional
@EnableAutoConfiguration(exclude = { KafkaAutoConfiguration.class, KafkaMetricsAutoConfiguration.class,
		AuditAutoConfiguration.class })
@ActiveProfiles(profiles = { "test" })
public class ObjectAliasQueryServiceImplTest {
	
	private static final long REFERENCE_ID = 1l;
	private static final String REFERENCE_NAME = "EXTERNAL ENTITIES";
	@Autowired
	private ObjectAliasServiceImpl aliasServiceImpl;
	@Autowired
	private ObjectAliasQueryServiceImpl aliasQueryServiceImpl;
	
	private ObjectAliasDTO objectAliasDTO;
	private ObjectAliasCriteria aliasCriteria;
	
	@BeforeEach
	void setUp() {
		objectAliasDTO=ObjectAliasServiceImplTest.prepareObjectAliasDto();
	}

	@Test
	void testFindByCriteriaId() {
		ObjectAliasDTO savedAlias = aliasServiceImpl.save(objectAliasDTO);
		aliasCriteria = new ObjectAliasCriteria();
		aliasCriteria.setId(savedAlias.getId());
		List<ObjectAliasDTO> objectAliasDTOs = aliasQueryServiceImpl.findByCriteria(aliasCriteria);
		assertThat(objectAliasDTOs).isNotEmpty();
		assertThat(objectAliasDTOs).allMatch(alias -> alias.getId().equals(aliasCriteria.getId()));
		assertThat(objectAliasDTOs).allMatch(alias -> alias.getName() != null);
		assertThat(objectAliasDTOs).allMatch(alias -> alias.getReferenceId() != null);
		assertThat(objectAliasDTOs).allMatch(alias -> alias.getReferenceName() != null);
	}
	
	@Test
	void testFindByCriteriaIdForInvalidId() {
		aliasCriteria = new ObjectAliasCriteria();
		aliasCriteria.setId(0l);
		List<ObjectAliasDTO> objectAliasDTOs = aliasQueryServiceImpl.findByCriteria(aliasCriteria);
		assertThat(objectAliasDTOs).isEmpty();
	}
	
	@Test
	void testFindByCriteriaReferenceId() {
		ObjectAliasDTO savedAlias = aliasServiceImpl.save(objectAliasDTO);
		aliasCriteria = new ObjectAliasCriteria();
		aliasCriteria.setReferenceId(savedAlias.getReferenceId());
		List<ObjectAliasDTO> objectAliasDTOs = aliasQueryServiceImpl.findByCriteria(aliasCriteria);
		assertThat(objectAliasDTOs).isNotEmpty();
		assertThat(objectAliasDTOs).allMatch(alias -> alias.getId() != null);
		assertThat(objectAliasDTOs).allMatch(alias -> alias.getName() != null);
		assertThat(objectAliasDTOs).allMatch(alias -> alias.getReferenceId().equals(aliasCriteria.getReferenceId()));
		assertThat(objectAliasDTOs).allMatch(alias -> alias.getReferenceName() != null);
	}
	
	@Test
	void testFindByCriteriaIdForInvalidReferenceId() {
		aliasCriteria = new ObjectAliasCriteria();
		aliasCriteria.setReferenceId(0l);
		List<ObjectAliasDTO> objectAliasDTOs = aliasQueryServiceImpl.findByCriteria(aliasCriteria);
		assertThat(objectAliasDTOs).isEmpty();
	}
	
	@Test
	void testFindByCriteriaReferenceName() {
		ObjectAliasDTO savedAlias = aliasServiceImpl.save(objectAliasDTO);
		aliasCriteria = new ObjectAliasCriteria();
		aliasCriteria.setReferenceName(savedAlias.getReferenceName());
		List<ObjectAliasDTO> objectAliasDTOs = aliasQueryServiceImpl.findByCriteria(aliasCriteria);
		assertThat(objectAliasDTOs).isNotEmpty();
		assertThat(objectAliasDTOs).allMatch(alias -> alias.getId() != null);
		assertThat(objectAliasDTOs).allMatch(alias -> alias.getName() != null);
		assertThat(objectAliasDTOs).allMatch(alias -> alias.getReferenceId() != null);
		assertThat(objectAliasDTOs)
				.allMatch(alias -> alias.getReferenceName().equals(aliasCriteria.getReferenceName()));
	}
	
	@Test
	void testFindByCriteriaIdForInvalidReferenceName() {
		aliasCriteria = new ObjectAliasCriteria();
		aliasCriteria.setReferenceName("ERRORERROR");
		List<ObjectAliasDTO> objectAliasDTOs = aliasQueryServiceImpl.findByCriteria(aliasCriteria);
		assertThat(objectAliasDTOs).isEmpty();
	}
	
	@Test
	void testGetListOfAliasByReferanceIdAndReferenceName() {
		aliasServiceImpl.save(objectAliasDTO);
		List<ObjectAliasDTO> aliasDTOs = aliasQueryServiceImpl.getListOfAliasByReferanceIdAndReferenceName(REFERENCE_ID,
				REFERENCE_NAME);
		assertThat(aliasDTOs).isNotEmpty();
		assertThat(aliasDTOs).allMatch(alias -> alias.getId() != null);
		assertThat(aliasDTOs).allMatch(alias -> alias.getName() != null);
		assertThat(aliasDTOs).allMatch(alias -> alias.getReferenceId() != null);
		assertThat(aliasDTOs).allMatch(alias -> alias.getReferenceName() != null);
	}
	
	@Test
	void testGetListOfAliasByReferanceIdAndReferenceNameForInvalidId() {
		List<ObjectAliasDTO> aliasDTOs = aliasQueryServiceImpl.getListOfAliasByReferanceIdAndReferenceName(0l,
				REFERENCE_NAME);
		assertThat(aliasDTOs).isEmpty();
	}
	
	@Test
	void testGetListOfAliasByReferanceIdAndReferenceNameForInvalidReferenceName() {
		List<ObjectAliasDTO> aliasDTOs = aliasQueryServiceImpl.getListOfAliasByReferanceIdAndReferenceName(REFERENCE_ID,
				"ERRORERROR");
		assertThat(aliasDTOs).isEmpty();
	}

}
