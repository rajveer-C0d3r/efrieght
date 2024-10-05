package com.grtship.mdm.serviceimpl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;

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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.grtship.core.dto.ObjectCodeDTO;

@SpringBootTest(properties = "config.caching.enabled=false")
@Transactional
@EnableAutoConfiguration(exclude = { KafkaAutoConfiguration.class, KafkaMetricsAutoConfiguration.class,
		AuditAutoConfiguration.class })
@ActiveProfiles(profiles = { "test" })
public class ObjectCodeServiceImplTest {
	
	@Autowired
	private ObjectCodeServiceImpl objectCodeServiceImpl;
	
	private ObjectCodeDTO objectCodeDTO;
	
	public static ObjectCodeDTO prepareObjectCodeDto() {
		ObjectCodeDTO objectCodeDTO=new ObjectCodeDTO();
		objectCodeDTO.setObjectName("EXTERNAL ENTITIES");
		objectCodeDTO.setPrefix("E");
		objectCodeDTO.setCounter(1500l);
		objectCodeDTO.setBlockSize(1);
		objectCodeDTO.setParentCode("ENT0");
		return objectCodeDTO;
	}
	
	@BeforeEach
	void setUp() {
		objectCodeDTO=prepareObjectCodeDto();
	}

	@Test
	void testSave() {
		ObjectCodeDTO savedObjectCode = objectCodeServiceImpl.save(objectCodeDTO);
		assertThat(savedObjectCode.getId()).isNotNull();
		assertThat(savedObjectCode.getObjectName()).isEqualTo(objectCodeDTO.getObjectName());
		assertThat(savedObjectCode.getCounter()).isEqualTo(objectCodeDTO.getCounter());
		assertThat(savedObjectCode.getParentCode()).isEqualTo(objectCodeDTO.getParentCode());
		assertThat(savedObjectCode.getPadding()).isEqualTo(objectCodeDTO.getPadding());
		assertThat(savedObjectCode.getBlockSize()).isEqualTo(objectCodeDTO.getBlockSize());
		assertThat(savedObjectCode.getPrefix()).isEqualTo(objectCodeDTO.getPrefix());
	}

	@Test
	void testFindAll() {
		objectCodeServiceImpl.save(objectCodeDTO);
		Page<ObjectCodeDTO> objectCodeDtos = objectCodeServiceImpl.findAll(PageRequest.of(0, 20));
		assertThat(objectCodeDtos.getNumber()).isEqualTo(0);
		assertThat(objectCodeDtos.getContent()).hasSizeGreaterThanOrEqualTo(1);
		assertThat(objectCodeDtos.getContent()).allMatch(objectCode -> objectCode.getId() != null);
		assertThat(objectCodeDtos.getContent()).allMatch(objectCode -> objectCode.getObjectName() != null);
		assertThat(objectCodeDtos.getContent()).allMatch(objectCode -> objectCode.getPadding() != null);
		assertThat(objectCodeDtos.getContent()).allMatch(objectCode -> objectCode.getBlockSize() != null);
		assertThat(objectCodeDtos.getContent()).allMatch(objectCode -> objectCode.getCounter() != null);
	}

	@Test
	void testFindOne() {
		ObjectCodeDTO savedObjectCode = objectCodeServiceImpl.save(objectCodeDTO);
		ObjectCodeDTO getObjectCodeById = objectCodeServiceImpl.findOne(savedObjectCode.getId()).get();
		assertThat(getObjectCodeById.getId()).isEqualTo(savedObjectCode.getId());
		assertThat(savedObjectCode.getObjectName()).isEqualTo(getObjectCodeById.getObjectName());
		assertThat(savedObjectCode.getCounter()).isEqualTo(getObjectCodeById.getCounter());
		assertThat(savedObjectCode.getParentCode()).isEqualTo(getObjectCodeById.getParentCode());
		assertThat(savedObjectCode.getPadding()).isEqualTo(getObjectCodeById.getPadding());
		assertThat(savedObjectCode.getBlockSize()).isEqualTo(getObjectCodeById.getBlockSize());
		assertThat(savedObjectCode.getPrefix()).isEqualTo(getObjectCodeById.getPrefix());
	}
	
	@Test
	void testFindOneForInvalidId() {
		Optional<ObjectCodeDTO> getObjectCodeById = objectCodeServiceImpl.findOne(0l);
		assertFalse(getObjectCodeById.isPresent());
	}

	@Test
	void testDelete() {
		ObjectCodeDTO savedObjectCode = objectCodeServiceImpl.save(objectCodeDTO);
		objectCodeServiceImpl.delete(savedObjectCode.getId());
		Optional<ObjectCodeDTO> getObjectCodeById = objectCodeServiceImpl.findOne(savedObjectCode.getId());
		assertFalse(getObjectCodeById.isPresent());
	}

	@Test
	void testFindByObjectNameAndParentCode() {
		ObjectCodeDTO savedObjectCode = objectCodeServiceImpl.save(objectCodeDTO);
		ObjectCodeDTO objectCode = objectCodeServiceImpl.findByObjectNameAndParentCode(savedObjectCode.getObjectName(),
				savedObjectCode.getParentCode());
		assertThat(objectCode.getId()).isEqualTo(savedObjectCode.getId());
		assertThat(savedObjectCode.getObjectName()).isEqualTo(objectCode.getObjectName());
		assertThat(savedObjectCode.getCounter()).isEqualTo(objectCode.getCounter());
		assertThat(savedObjectCode.getParentCode()).isEqualTo(objectCode.getParentCode());
		assertThat(savedObjectCode.getPadding()).isEqualTo(objectCode.getPadding());
		assertThat(savedObjectCode.getBlockSize()).isEqualTo(objectCode.getBlockSize());
		assertThat(savedObjectCode.getPrefix()).isEqualTo(objectCode.getPrefix());
	}
	
	@Test
	void testFindByObjectNameAndParentCodeForInvalidName() {
		ObjectCodeDTO savedObjectCode = objectCodeServiceImpl.save(objectCodeDTO);
		ObjectCodeDTO objectCode = objectCodeServiceImpl.findByObjectNameAndParentCode("ERRORERROR",
				savedObjectCode.getParentCode());
		assertThat(objectCode).isNull();
	}
	
	@Test
	void testFindByObjectNameAndParentCodeForInvalidParentCode() {
		ObjectCodeDTO savedObjectCode = objectCodeServiceImpl.save(objectCodeDTO);
		ObjectCodeDTO objectCode = objectCodeServiceImpl.findByObjectNameAndParentCode(savedObjectCode.getObjectName(),
				"ERROR");
		assertThat(objectCode).isNull();
	}

	@Test
	void testFindByObjectName() {
		ObjectCodeDTO savedObjectCode = objectCodeServiceImpl.save(objectCodeDTO);
		List<ObjectCodeDTO> objectCodeDtos = objectCodeServiceImpl.findByObjectName(savedObjectCode.getObjectName());
		assertThat(objectCodeDtos).hasSizeGreaterThanOrEqualTo(1);
		assertThat(objectCodeDtos).allMatch(objectCode -> objectCode.getId() != null);
		assertThat(objectCodeDtos).allMatch(objectCode -> objectCode.getObjectName() != null);
		assertThat(objectCodeDtos).allMatch(objectCode -> objectCode.getPadding() != null);
		assertThat(objectCodeDtos).allMatch(objectCode -> objectCode.getBlockSize() != null);
		assertThat(objectCodeDtos).allMatch(objectCode -> objectCode.getCounter() != null);
	}
	
	@Test
	void testFindByObjectNameForInvalidName() {
		List<ObjectCodeDTO> objectCodeDtos = objectCodeServiceImpl.findByObjectName("ERRORERROR");
		assertThat(objectCodeDtos).isEmpty();
	}

	@Test
	void testGenerateCode() {
		ObjectCodeDTO savedObjectCode = objectCodeServiceImpl.save(objectCodeDTO);
		String code = objectCodeServiceImpl.generateCode(savedObjectCode.getObjectName(),
				savedObjectCode.getParentCode());
		assertThat(code).isNotNull();
	}

}
