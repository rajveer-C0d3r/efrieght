package com.grtship.mdm.serviceimpl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;

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

import com.grtship.core.dto.EquipmentDTO;
import com.grtship.core.enumeration.EquipmentType;

@SpringBootTest(properties = "config.caching.enabled=false")
@Transactional
@EnableAutoConfiguration(exclude = { KafkaAutoConfiguration.class, KafkaMetricsAutoConfiguration.class,
		AuditAutoConfiguration.class })
@ActiveProfiles(profiles = { "test" })
public class EquipmentServiceImplTest {
	
	@Autowired
	private EquipmentServiceImpl equipmentServiceImpl;
	
	private EquipmentDTO equipmentDTO;
	
	
	public static EquipmentDTO prepareEquipmentDto() {
		EquipmentDTO equipmentDTO=new EquipmentDTO();
		equipmentDTO.setCode("MKL789");
		equipmentDTO.setName("TEST EQUIPMENT SAVE");
		equipmentDTO.setType(EquipmentType.FLEXI_TANK);
		return equipmentDTO;
	}
	
	@BeforeEach
	void setUp() {
		equipmentDTO=prepareEquipmentDto();
	}

	@Test
	void testSave() {
		EquipmentDTO savedEquipment = equipmentServiceImpl.save(equipmentDTO);
		assertThat(savedEquipment.getId()).isNotNull();
		assertThat(savedEquipment.getCode()).isEqualTo(equipmentDTO.getCode());
		assertThat(savedEquipment.getName()).isEqualTo(equipmentDTO.getName());
		assertThat(savedEquipment.getType()).isEqualTo(equipmentDTO.getType());
	}
	
	@Test
	void testUpdate() {
		EquipmentDTO savedEquipment = equipmentServiceImpl.save(equipmentDTO);
		savedEquipment.setCode("MKL");
		savedEquipment.setName("TEST EQUIPMENT UPDATE");
		EquipmentDTO updatedEquipment = equipmentServiceImpl.save(savedEquipment);
		assertThat(updatedEquipment.getId()).isEqualTo(savedEquipment.getId());
		assertThat(updatedEquipment.getCode()).isNotEqualTo(equipmentDTO.getCode());
		assertThat(updatedEquipment.getName()).isNotEqualTo(equipmentDTO.getName());
		assertThat(updatedEquipment.getType()).isEqualTo(savedEquipment.getType());
	}

	@Test
	void testFindAll() {
		equipmentServiceImpl.save(equipmentDTO);
		Page<EquipmentDTO> findAll = equipmentServiceImpl.findAll(PageRequest.of(0, 20));
		assertThat(findAll).isNotEmpty();
		assertThat(findAll.getContent()).hasSizeLessThanOrEqualTo(20);
		assertThat(findAll.getContent()).allMatch(equipment -> equipment.getId() != null);
		assertThat(findAll.getContent()).allMatch(equipment -> equipment.getCode() != null);
		assertThat(findAll.getContent()).allMatch(equipment -> equipment.getName() != null);
		assertThat(findAll.getContent()).allMatch(equipment -> equipment.getType() != null);
	}

	@Test
	void testFindOne() {
		EquipmentDTO savedEquipment = equipmentServiceImpl.save(equipmentDTO);
		EquipmentDTO getEquipmentById = equipmentServiceImpl.findOne(savedEquipment.getId()).get();
		assertThat(savedEquipment.getId()).isEqualTo(getEquipmentById.getId());
		assertThat(savedEquipment.getCode()).isEqualTo(getEquipmentById.getCode());
		assertThat(savedEquipment.getName()).isEqualTo(getEquipmentById.getName());
		assertThat(savedEquipment.getType()).isEqualTo(getEquipmentById.getType());
	}
	
	@Test
	void testFindOneForInvalidId() {
		Optional<EquipmentDTO> getEquipmentById = equipmentServiceImpl.findOne(0l);
		assertFalse(getEquipmentById.isPresent());
	}

	@Test
	void testDelete() {
		EquipmentDTO savedEquipment = equipmentServiceImpl.save(equipmentDTO);
		equipmentServiceImpl.delete(savedEquipment.getId());
		Optional<EquipmentDTO> getEquipmentById = equipmentServiceImpl.findOne(0l);
		assertFalse(getEquipmentById.isPresent());
	}

}
