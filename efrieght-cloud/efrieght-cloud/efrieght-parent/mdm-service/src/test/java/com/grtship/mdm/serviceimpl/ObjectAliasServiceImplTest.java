package com.grtship.mdm.serviceimpl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

import com.grtship.common.exception.InvalidDataException;
import com.grtship.common.interfaces.ValidationError;
import com.grtship.core.constant.ReferenceNameConstant;
import com.grtship.core.dto.ObjectAliasDTO;
import com.grtship.mdm.domain.ObjectAlias;
import com.grtship.mdm.mapper.ObjectAliasMapper;

@SpringBootTest(properties = "config.caching.enabled=false")
@Transactional
@EnableAutoConfiguration(exclude = { KafkaAutoConfiguration.class, KafkaMetricsAutoConfiguration.class,
		AuditAutoConfiguration.class })
@ActiveProfiles(profiles = { "test" })
public class ObjectAliasServiceImplTest {

	private static final long REFERENCE_ID = 1l;
	private static final String REFERENCE_NAME = "EXTERNAL ENTITIES";
	@Autowired
	private ObjectAliasServiceImpl aliasServiceImpl;
	@Autowired
	private ObjectAliasMapper objectAliasMapper;
	
	private ObjectAliasDTO objectAliasDTO;
	
	public static ObjectAliasDTO prepareObjectAliasDto() {
		ObjectAliasDTO objectAliasDTO = new ObjectAliasDTO();
		objectAliasDTO.setCompanyId(1L);
		objectAliasDTO.setClientId(1L);
		objectAliasDTO.setName("Test Alias Save");
		objectAliasDTO.setReferenceId(REFERENCE_ID);
		objectAliasDTO.setReferenceName(REFERENCE_NAME);
		return objectAliasDTO;
	}
	
	@BeforeEach
	void setUp() {
		objectAliasDTO=prepareObjectAliasDto();
	}
	
	@Test
	void testSave() {
		ObjectAliasDTO savedAlias = aliasServiceImpl.save(objectAliasDTO);
		assertThat(savedAlias.getId()).isNotNull();
		assertThat(savedAlias.getName()).isEqualTo(objectAliasDTO.getName());
		assertThat(savedAlias.getReferenceName()).isEqualTo(objectAliasDTO.getReferenceName());
		assertThat(savedAlias.getReferenceId()).isEqualTo(objectAliasDTO.getReferenceId());
		assertThat(savedAlias.getCompanyId()).isEqualTo(objectAliasDTO.getCompanyId());
		assertThat(savedAlias.getClientId()).isEqualTo(objectAliasDTO.getClientId());
	}
	
	@Test
	void testUpdate() {
		ObjectAliasDTO savedAlias = aliasServiceImpl.save(objectAliasDTO);
		savedAlias.setReferenceName("GROUP");
		savedAlias.setName("Test Alias Update");
		ObjectAliasDTO updatedAlias = aliasServiceImpl.save(savedAlias);
		assertThat(updatedAlias.getId()).isEqualTo(savedAlias.getId());
		assertThat(updatedAlias.getName()).isNotEqualTo(objectAliasDTO.getName());
		assertThat(updatedAlias.getReferenceName()).isNotEqualTo(objectAliasDTO.getReferenceName());
		assertThat(updatedAlias.getReferenceId()).isEqualTo(savedAlias.getReferenceId());
		assertThat(updatedAlias.getCompanyId()).isEqualTo(savedAlias.getCompanyId());
		assertThat(updatedAlias.getClientId()).isEqualTo(savedAlias.getClientId());
	}

	@Test
	void testFindAll() {
		aliasServiceImpl.save(objectAliasDTO);
		Page<ObjectAliasDTO> allAliases = aliasServiceImpl.findAll(PageRequest.of(0, 20));
		assertThat(allAliases.getNumber()).isEqualTo(0);
		assertThat(allAliases.getContent()).isNotEmpty();
		assertThat(allAliases.getContent()).allMatch(alias -> alias.getId()!=null);
		assertThat(allAliases.getContent()).allMatch(alias -> alias.getReferenceId()!=null);
		assertThat(allAliases.getContent()).allMatch(alias -> alias.getReferenceName()!=null);
		assertThat(allAliases.getContent()).allMatch(alias -> alias.getName()!=null);
	}

	@Test
	void testFindOne() {
		ObjectAliasDTO savedAlias = aliasServiceImpl.save(objectAliasDTO);
		ObjectAliasDTO getAliasById = aliasServiceImpl.findOne(savedAlias.getId()).get();
		assertThat(getAliasById.getId()).isEqualTo(savedAlias.getId());
		assertThat(getAliasById.getName()).isEqualTo(savedAlias.getName());
		assertThat(getAliasById.getReferenceName()).isEqualTo(savedAlias.getReferenceName());
		assertThat(getAliasById.getReferenceId()).isEqualTo(savedAlias.getReferenceId());
		assertThat(getAliasById.getCompanyId()).isEqualTo(savedAlias.getCompanyId());
		assertThat(getAliasById.getClientId()).isEqualTo(savedAlias.getClientId());
	}
	
	@Test
	void testFindOneForInvalidId() {
		Optional<ObjectAliasDTO> getAliasById = aliasServiceImpl.findOne(0l);
		assertFalse(getAliasById.isPresent());
	}

	@Test
	void testDelete() {
		ObjectAliasDTO savedAlias = aliasServiceImpl.save(objectAliasDTO);
		aliasServiceImpl.delete(savedAlias.getId());
		Optional<ObjectAliasDTO> getAliasById = aliasServiceImpl.findOne(0l);
		assertFalse(getAliasById.isPresent());
	}

	@Test
	void testSaveAllSetOfObjectAlias() {
		ObjectAlias entity = objectAliasMapper.toEntity(objectAliasDTO);
		Set<ObjectAlias> objectAlias = new TreeSet<>();
		objectAlias.add(entity);
		List<ObjectAlias> savedObjectAliases = aliasServiceImpl.saveAll(objectAlias);
		assertThat(savedObjectAliases).isNotEmpty();
		assertThat(savedObjectAliases).hasSize(1);
		assertThat(savedObjectAliases).allMatch(alias -> alias.getId() != null);
		assertThat(savedObjectAliases).allMatch(alias -> alias.getName() != null);
		assertThat(savedObjectAliases).allMatch(alias -> alias.getReferenceName() != null);
		assertThat(savedObjectAliases).allMatch(alias -> alias.getReferenceId() != null);
	}

	@Test
	void testGetAliasIdByReferenceIdAndReferenceName() {
		ObjectAliasDTO savedAlias = aliasServiceImpl.save(objectAliasDTO);
		Set<Long> ids = aliasServiceImpl.getAliasIdByReferenceIdAndReferenceName(savedAlias.getReferenceId(),
				savedAlias.getReferenceName());
		assertThat(ids).isNotEmpty();
		assertThat(ids).hasSizeGreaterThanOrEqualTo(1);
	}
	
	@Test
	void testGetAliasIdByReferenceIdAndReferenceNameForInvalidReferenceName() {
		ObjectAliasDTO savedAlias = aliasServiceImpl.save(objectAliasDTO);
		Set<Long> ids = aliasServiceImpl.getAliasIdByReferenceIdAndReferenceName(savedAlias.getReferenceId(),
				"ERRORERROR");
		assertThat(ids).isEmpty();
	}
	
	@Test
	void testGetAliasIdByReferenceIdAndReferenceNameForInvalidReferenceId() {
		Set<Long> ids = aliasServiceImpl.getAliasIdByReferenceIdAndReferenceName(0l,
				REFERENCE_NAME);
		assertThat(ids).isEmpty();
	}

	@Test
	void testDeleteByReferenceNameAndIdIn() {
		ObjectAliasDTO savedAlias = aliasServiceImpl.save(objectAliasDTO);
		Set<Long> ids=new TreeSet<>();
		ids.add(savedAlias.getId());
		aliasServiceImpl.deleteByReferenceNameAndIdIn(savedAlias.getReferenceName(), ids);
		Optional<ObjectAliasDTO> getAliasById = aliasServiceImpl.findOne(savedAlias.getId());
		assertFalse(getAliasById.isPresent());
	}

	@Test
	void testCheckForDuplicateAliasStringStringLongLong() {
		ObjectAliasDTO savedAlias = aliasServiceImpl.save(objectAliasDTO);
		assertThrows(InvalidDataException.class, () -> {
			aliasServiceImpl.checkForDuplicateAlias(savedAlias.getName(), REFERENCE_NAME, savedAlias.getClientId(),
					savedAlias.getCompanyId());
		});
	}

	@Test
	void testSaveAllSetOfObjectAliasDTOLongStringLongLongLong() {
		objectAliasDTO.setReferenceId(null);
		objectAliasDTO.setReferenceName(null);
		objectAliasDTO.setCompanyId(null);
		objectAliasDTO.setClientId(null);
		Set<ObjectAliasDTO> aliasDTOs = new TreeSet<>();
		aliasDTOs.add(objectAliasDTO);
		aliasServiceImpl.saveAll(aliasDTOs, REFERENCE_ID, REFERENCE_NAME, 1l, 1l, null);
		Set<Long> ids = aliasServiceImpl.getAliasIdByReferenceIdAndReferenceName(REFERENCE_ID, REFERENCE_NAME);
		assertThat(ids).isNotEmpty();
		assertThat(ids).hasSize(1);
	}

	@Test
	void testDeleteAliasOnUpdate() {
		objectAliasDTO.setReferenceName(ReferenceNameConstant.ENTITY);
		ObjectAliasDTO savedAlias = aliasServiceImpl.save(objectAliasDTO);
		objectAliasDTO.setName("Test Duplicate");
		objectAliasDTO.setReferenceName(ReferenceNameConstant.ENTITY);
		Set<ObjectAliasDTO> objectAliasDTOs = new TreeSet<>();
		objectAliasDTOs.add(objectAliasDTO);
		aliasServiceImpl.deleteAliasOnUpdate(objectAliasDTOs, ReferenceNameConstant.ENTITY, REFERENCE_ID);
		Optional<ObjectAliasDTO> getAliasById = aliasServiceImpl.findOne(savedAlias.getId());
		assertFalse(getAliasById.isPresent());
	}

	@Test
	void testCheckForDuplicateAliasStringString() {
		ObjectAliasDTO savedAlias = aliasServiceImpl.save(objectAliasDTO);
		assertThrows(InvalidDataException.class, () -> {
			aliasServiceImpl.checkForDuplicateAlias(savedAlias.getName(), REFERENCE_NAME);
		});
	}

	@Test
	void testCheckForDuplicateAliasSetOfStringStringLongLong() {
		ObjectAliasDTO savedAlias1 = aliasServiceImpl.save(objectAliasDTO);
		Set<String> aliasNames = new TreeSet<>();
		aliasNames.add(savedAlias1.getName());
		ValidationError validationError = aliasServiceImpl.checkForDuplicateAlias(aliasNames, REFERENCE_NAME,
				savedAlias1.getClientId(), savedAlias1.getCompanyId());
		assertThat(validationError).isNotNull();
	}

	@Test
	void testCheckForDuplicateAliasStringSetOfObjectAliasDTOLongLong() {
		ObjectAliasDTO savedAlias = aliasServiceImpl.save(objectAliasDTO);
		savedAlias.setId(null);
		Set<ObjectAliasDTO> objectAliasDTOs = new TreeSet<>();
		objectAliasDTOs.add(savedAlias);
		ValidationError validationError = aliasServiceImpl.checkForDuplicateAlias(REFERENCE_NAME, objectAliasDTOs,
				savedAlias.getClientId(), savedAlias.getCompanyId());
		assertThat(validationError).isNotNull();
	}

	@Test
	void testCheckForDuplicateAliasSetOfStringString() {
		ObjectAliasDTO savedAlias = aliasServiceImpl.save(objectAliasDTO);
		Set<String> aliasNames = new TreeSet<>();
		aliasNames.add(savedAlias.getName());
		ValidationError validationError = aliasServiceImpl.checkForDuplicateAlias(aliasNames, REFERENCE_NAME);
		assertThat(validationError).isNotNull();
	}

	@Test
	void testCheckForDuplicateAliasSetOfObjectAliasDTOStringLong() {
		ObjectAliasDTO savedAlias = aliasServiceImpl.save(objectAliasDTO);
		savedAlias.setId(null);
		Set<ObjectAliasDTO> aliasDTOs = new TreeSet<>();
		aliasDTOs.add(savedAlias);
		ValidationError validationError =aliasServiceImpl.checkForDuplicateAlias(aliasDTOs, REFERENCE_NAME, savedAlias.getId());
		assertThat(validationError).isNotNull();
	}
}
