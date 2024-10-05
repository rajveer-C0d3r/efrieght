package com.grtship.mdm.serviceimpl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
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

import com.grtship.common.exception.ValidationException;
import com.grtship.core.dto.DesignationDTO;

@SpringBootTest(properties = "config.caching.enabled=false")
@Transactional
@EnableAutoConfiguration(exclude = { KafkaAutoConfiguration.class, KafkaMetricsAutoConfiguration.class,
		AuditAutoConfiguration.class })
@ActiveProfiles(profiles = { "test" })
public class DesignationServiceImplTest {

	
	@Autowired
	private DesignationServiceImpl designationServiceImpl;
	
	private DesignationDTO designationDTO;
	
	public static DesignationDTO  prepareDesignationDto() {
		DesignationDTO designationDTO=new DesignationDTO();
		designationDTO.setCode("ACC");
		designationDTO.setName("Account Manager");
		designationDTO.setClientId(1L);
		designationDTO.setCompanyId(1L);
		return designationDTO;
	}
	
	@BeforeEach
	void setUp() {
		designationDTO=prepareDesignationDto();
	}

	@Test
	void testSave() {
		DesignationDTO savedDesignation = designationServiceImpl.save(designationDTO);
		assertThat(savedDesignation.getId()).isNotNull();
		assertThat(savedDesignation.getCode()).isEqualTo(designationDTO.getCode());
		assertThat(savedDesignation.getName()).isEqualTo(designationDTO.getName());
	}
	
	@Test
	void checkIfCodeIsUnique() {
		designationServiceImpl.save(designationDTO);
		assertThrows(ValidationException.class,() -> {
			designationServiceImpl.save(designationDTO);
		});
	}
	
	@Test
	void checkIfNameIsUnique() {
		designationServiceImpl.save(designationDTO);
		designationDTO.setCode("YBU");
		assertThrows(ValidationException.class,() -> {
			designationServiceImpl.save(designationDTO);
		});
	}
	
	@Test
	void testUpdate() {
		DesignationDTO savedDesignation = designationServiceImpl.save(designationDTO);
		savedDesignation.setName("Account Helper");
		DesignationDTO updatedDesignation = designationServiceImpl.save(savedDesignation);
		assertThat(savedDesignation.getId()).isEqualTo(updatedDesignation.getId());
		assertThat(savedDesignation.getCode()).isEqualTo(updatedDesignation.getCode());
		assertThat(designationDTO.getName()).isNotEqualTo(updatedDesignation.getName());
	}


	@Test
	void testFindOne() {
		DesignationDTO savedDesignation = designationServiceImpl.save(designationDTO);
		Optional<DesignationDTO> findOne = designationServiceImpl.findOne(savedDesignation.getId());
		assertTrue(findOne.isPresent());
		assertThat(savedDesignation.getId()).isEqualTo(findOne.get().getId());
		assertThat(savedDesignation.getCode()).isEqualTo(findOne.get().getCode());
		assertThat(savedDesignation.getName()).isEqualTo(findOne.get().getName());
	}
	
	@Test
	void testFindOneForInvalidId() {
		Optional<DesignationDTO> findOne = designationServiceImpl.findOne(0l);
		assertFalse(findOne.isPresent());
	}

	@Test
	void testDelete() {
		DesignationDTO savedDesignation = designationServiceImpl.save(designationDTO);
		designationServiceImpl.delete(savedDesignation.getId());
		Optional<DesignationDTO> findOne = designationServiceImpl.findOne(0l);
		assertFalse(findOne.isPresent());
	}

	@Test
	void testGetByIds() {
		DesignationDTO savedDesignation = designationServiceImpl.save(designationDTO);
//		List<Long> ids = List.of(savedDesignation.getId());
		List<Long> ids = new ArrayList<>();
		ids.add(savedDesignation.getId());
		List<DesignationDTO> designationDtos = designationServiceImpl.getByIds(ids);
		assertThat(designationDtos).isNotEmpty();
		assertThat(designationDtos).hasSize(1);
		assertThat(designationDtos).allMatch(designation -> designation.getId() != null);
		assertThat(designationDtos).allMatch(designation -> designation.getCode() != null);
		assertThat(designationDtos).allMatch(designation -> designation.getName() != null);
	}
	
	@Test
	void testGetByIdsForInvalidIds() {
		List<Long> ids = new ArrayList<>();
		ids.add(0l);
		List<DesignationDTO> designationDtos = designationServiceImpl.getByIds(ids);
		assertThat(designationDtos).isEmpty();
	}
}
