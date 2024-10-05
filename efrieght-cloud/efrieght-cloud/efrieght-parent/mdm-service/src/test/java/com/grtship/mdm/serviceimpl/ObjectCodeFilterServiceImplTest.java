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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.grtship.core.dto.ObjectCodeDTO;
import com.grtship.core.filter.LongFilter;
import com.grtship.core.filter.StringFilter;
import com.grtship.mdm.criteria.ObjectCodeCriteria;

@SpringBootTest(properties = "config.caching.enabled=false")
@Transactional
@EnableAutoConfiguration(exclude = { KafkaAutoConfiguration.class, KafkaMetricsAutoConfiguration.class,
		AuditAutoConfiguration.class })
@ActiveProfiles(profiles = { "test" })
public class ObjectCodeFilterServiceImplTest {
	
	@Autowired
	private ObjectCodeServiceImpl objectCodeServiceImpl;
	@Autowired
	private ObjectCodeFilterServiceImpl codeFilterServiceImpl;
	
	private ObjectCodeDTO objectCodeDTO;
	private ObjectCodeCriteria objectCodeCriteria;
	
	@BeforeEach
	void setUp() {
		objectCodeDTO=ObjectCodeServiceImplTest.prepareObjectCodeDto();
	}

	@Test
	void testFindByCriteriaObjectCodeCriteriaId() {
		ObjectCodeDTO savedObjectCode = objectCodeServiceImpl.save(objectCodeDTO);
		LongFilter longFilter = new LongFilter();
		longFilter.setEquals(savedObjectCode.getId());
		objectCodeCriteria=new ObjectCodeCriteria();
		objectCodeCriteria.setId(longFilter);
		List<ObjectCodeDTO> objectCodeDtos = codeFilterServiceImpl.findByCriteria(objectCodeCriteria);
		assertThat(objectCodeDtos).isNotEmpty();
		assertThat(objectCodeDtos).allMatch(objectCode -> objectCode.getId().equals(longFilter.getEquals()));
		assertThat(objectCodeDtos).allMatch(objectCode -> objectCode.getObjectName() != null);
		assertThat(objectCodeDtos).allMatch(objectCode -> objectCode.getPadding() != null);
		assertThat(objectCodeDtos).allMatch(objectCode -> objectCode.getCounter() != null);
		assertThat(objectCodeDtos).allMatch(objectCode -> objectCode.getBlockSize() != null);
	}
	
	@Test
	void testFindByCriteriaObjectCodeCriteriaInvalidId() {
		LongFilter longFilter = new LongFilter();
		longFilter.setEquals(0l);
		objectCodeCriteria=new ObjectCodeCriteria();
		objectCodeCriteria.setId(longFilter);
		List<ObjectCodeDTO> objectCodeDtos = codeFilterServiceImpl.findByCriteria(objectCodeCriteria);
		assertThat(objectCodeDtos).isEmpty();
	}
	
	@Test
	void testFindByCriteriaObjectCodeCriteriaObjectName() {
		ObjectCodeDTO savedObjectCode = objectCodeServiceImpl.save(objectCodeDTO);
		StringFilter stringFilter = new StringFilter();
		stringFilter.setEquals(savedObjectCode.getObjectName());
		objectCodeCriteria = new ObjectCodeCriteria();
		objectCodeCriteria.setObjectName(stringFilter);
		List<ObjectCodeDTO> objectCodeDtos = codeFilterServiceImpl.findByCriteria(objectCodeCriteria);
		assertThat(objectCodeDtos).isNotEmpty();
		assertThat(objectCodeDtos).allMatch(objectCode -> objectCode.getId() != null);
		assertThat(objectCodeDtos).allMatch(objectCode -> objectCode.getObjectName().equals(stringFilter.getEquals()));
		assertThat(objectCodeDtos).allMatch(objectCode -> objectCode.getPadding() != null);
		assertThat(objectCodeDtos).allMatch(objectCode -> objectCode.getCounter() != null);
		assertThat(objectCodeDtos).allMatch(objectCode -> objectCode.getBlockSize() != null);
	}
	
	@Test
	void testFindByCriteriaObjectCodeCriteriaInvalidObjectName() {
		StringFilter stringFilter = new StringFilter();
		stringFilter.setEquals("ERRORERROR");
		objectCodeCriteria = new ObjectCodeCriteria();
		objectCodeCriteria.setObjectName(stringFilter);
		List<ObjectCodeDTO> objectCodeDtos = codeFilterServiceImpl.findByCriteria(objectCodeCriteria);
		assertThat(objectCodeDtos).isEmpty();
	}
	
	@Test
	void testFindByCriteriaObjectCodeCriteriaPrefix() {
		ObjectCodeDTO savedObjectCode = objectCodeServiceImpl.save(objectCodeDTO);
		StringFilter stringFilter = new StringFilter();
		stringFilter.setEquals(savedObjectCode.getPrefix());
		objectCodeCriteria = new ObjectCodeCriteria();
		objectCodeCriteria.setPrefix(stringFilter);
		List<ObjectCodeDTO> objectCodeDtos = codeFilterServiceImpl.findByCriteria(objectCodeCriteria);
		assertThat(objectCodeDtos).isNotEmpty();
		assertThat(objectCodeDtos).allMatch(objectCode -> objectCode.getId() != null);
		assertThat(objectCodeDtos).allMatch(objectCode -> objectCode.getObjectName() != null);
		assertThat(objectCodeDtos).allMatch(objectCode -> objectCode.getPrefix().equals(stringFilter.getEquals()));
		assertThat(objectCodeDtos).allMatch(objectCode -> objectCode.getCounter() != null);
		assertThat(objectCodeDtos).allMatch(objectCode -> objectCode.getBlockSize() != null);
	}
	
	@Test
	void testFindByCriteriaObjectCodeCriteriaInvalidPrefix() {
		StringFilter stringFilter = new StringFilter();
		stringFilter.setEquals("ERRORERROR");
		objectCodeCriteria = new ObjectCodeCriteria();
		objectCodeCriteria.setObjectName(stringFilter);
		List<ObjectCodeDTO> objectCodeDtos = codeFilterServiceImpl.findByCriteria(objectCodeCriteria);
		assertThat(objectCodeDtos).isEmpty();
	}
	
	@Test
	void testFindByCriteriaObjectCodeCriteriaPadding() {
		ObjectCodeDTO savedObjectCode = objectCodeServiceImpl.save(objectCodeDTO);
		LongFilter longFilter = new LongFilter();
		longFilter.setEquals(savedObjectCode.getPadding());
		objectCodeCriteria = new ObjectCodeCriteria();
		objectCodeCriteria.setPadding(longFilter);
		List<ObjectCodeDTO> objectCodeDtos = codeFilterServiceImpl.findByCriteria(objectCodeCriteria);
		assertThat(objectCodeDtos).isNotEmpty();
		assertThat(objectCodeDtos).allMatch(objectCode -> objectCode.getId() != null);
		assertThat(objectCodeDtos).allMatch(objectCode -> objectCode.getObjectName() != null);
		assertThat(objectCodeDtos).allMatch(objectCode -> objectCode.getCounter() != null);
		assertThat(objectCodeDtos).allMatch(objectCode -> objectCode.getPadding().equals(longFilter.getEquals()));
		assertThat(objectCodeDtos).allMatch(objectCode -> objectCode.getBlockSize() != null);
	}
	
	@Test
	void testFindByCriteriaObjectCodeCriteriaInvalidPadding() {
		LongFilter longFilter = new LongFilter();
		longFilter.setEquals(0l);
		objectCodeCriteria=new ObjectCodeCriteria();
		objectCodeCriteria.setPadding(longFilter);
		List<ObjectCodeDTO> objectCodeDtos = codeFilterServiceImpl.findByCriteria(objectCodeCriteria);
		assertThat(objectCodeDtos).isEmpty();
	}
	
	@Test
	void testFindByCriteriaObjectCodeCriteriaCounter() {
		ObjectCodeDTO savedObjectCode = objectCodeServiceImpl.save(objectCodeDTO);
		LongFilter longFilter = new LongFilter();
		longFilter.setEquals(savedObjectCode.getCounter());
		objectCodeCriteria = new ObjectCodeCriteria();
		objectCodeCriteria.setCounter(longFilter);
		List<ObjectCodeDTO> objectCodeDtos = codeFilterServiceImpl.findByCriteria(objectCodeCriteria);
		assertThat(objectCodeDtos).isNotEmpty();
		assertThat(objectCodeDtos).allMatch(objectCode -> objectCode.getId() != null);
		assertThat(objectCodeDtos).allMatch(objectCode -> objectCode.getObjectName() != null);
		assertThat(objectCodeDtos).allMatch(objectCode -> objectCode.getPadding()!=null);
		assertThat(objectCodeDtos).allMatch(objectCode -> objectCode.getCounter().equals(longFilter.getEquals()));
		assertThat(objectCodeDtos).allMatch(objectCode -> objectCode.getBlockSize() != null);
	}
	
	@Test
	void testFindByCriteriaObjectCodeCriteriaInvalidCounter() {
		LongFilter longFilter = new LongFilter();
		longFilter.setEquals(0l);
		objectCodeCriteria=new ObjectCodeCriteria();
		objectCodeCriteria.setCounter(longFilter);
		List<ObjectCodeDTO> objectCodeDtos = codeFilterServiceImpl.findByCriteria(objectCodeCriteria);
		assertThat(objectCodeDtos).isEmpty();
	}

	@Test
	void testFindByCriteriaObjectCodeCriteriaPageable() {
		ObjectCodeDTO savedObjectCode = objectCodeServiceImpl.save(objectCodeDTO);
		LongFilter longFilter = new LongFilter();
		longFilter.setEquals(savedObjectCode.getId());
		objectCodeCriteria = new ObjectCodeCriteria();
		objectCodeCriteria.setId(longFilter);
		Page<ObjectCodeDTO> objectCodeDtos = codeFilterServiceImpl.findByCriteria(objectCodeCriteria,
				PageRequest.of(0, 20));
		assertThat(objectCodeDtos.getNumber()).isEqualTo(0);
		assertThat(objectCodeDtos.getContent()).hasSizeGreaterThanOrEqualTo(1);
		assertThat(objectCodeDtos.getContent())
				.allMatch(objectCode -> objectCode.getId().equals(longFilter.getEquals()));
		assertThat(objectCodeDtos.getContent()).allMatch(objectCode -> objectCode.getObjectName() != null);
		assertThat(objectCodeDtos.getContent()).allMatch(objectCode -> objectCode.getPadding() != null);
		assertThat(objectCodeDtos.getContent()).allMatch(objectCode -> objectCode.getCounter() != null);
		assertThat(objectCodeDtos.getContent()).allMatch(objectCode -> objectCode.getBlockSize() != null);
	}

	@Test
	void testCountByCriteria() {
		ObjectCodeDTO savedObjectCode = objectCodeServiceImpl.save(objectCodeDTO);
		LongFilter longFilter = new LongFilter();
		longFilter.setEquals(savedObjectCode.getId());
		objectCodeCriteria = new ObjectCodeCriteria();
		objectCodeCriteria.setId(longFilter);
		long count = codeFilterServiceImpl.countByCriteria(objectCodeCriteria);
		assertThat(count).isGreaterThanOrEqualTo(1);
	}
	
	@Test
	void testCountByCriteriaForInvalidCriteria() {
		LongFilter longFilter = new LongFilter();
		longFilter.setEquals(0l);
		objectCodeCriteria = new ObjectCodeCriteria();
		objectCodeCriteria.setId(longFilter);
		long count = codeFilterServiceImpl.countByCriteria(objectCodeCriteria);
		assertThat(count).isEqualTo(0);
	}

}
