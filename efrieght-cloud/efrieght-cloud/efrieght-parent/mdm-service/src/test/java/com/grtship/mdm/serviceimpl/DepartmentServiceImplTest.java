package com.grtship.mdm.serviceimpl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.validation.ConstraintViolationException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.audit.AuditAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.metrics.KafkaMetricsAutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.grtship.common.exception.ValidationException;
import com.grtship.core.dto.DepartmentDTO;
import com.grtship.core.enumeration.DepartmentType;
import com.grtship.mdm.criteria.DepartmentCriteria;
import com.grtship.mdm.service.DepartmentService;

@SpringBootTest(properties = "config.caching.enabled=false")
@Transactional
@EnableAutoConfiguration(exclude = { KafkaAutoConfiguration.class, KafkaMetricsAutoConfiguration.class,
		AuditAutoConfiguration.class })
@ActiveProfiles(profiles = { "test" })
public class DepartmentServiceImplTest {

	@Autowired 
	private DepartmentService departmentService;
	
	private DepartmentDTO departmentDto;
	private DepartmentCriteria departmentCriteria;
	
	@Mock 
	Pageable pageable;
	
	public static DepartmentDTO prepareDepartmentDto() {
		DepartmentDTO departmentDto=new DepartmentDTO();
		departmentDto.setCode("SD");
		departmentDto.setName("Software Developer");
		departmentDto.setType(DepartmentType.IT);
		departmentDto.setClientId(1L);
		departmentDto.setCompanyId(1L);
		return departmentDto;
	}

	@BeforeEach
	public void setUp() {
		departmentDto=prepareDepartmentDto();
	}
	
	@Test
	void testSave() {
		DepartmentDTO savedDepartment = departmentService.save(departmentDto);
		assertThat(savedDepartment.getId()).isNotNull();
		assertThat(savedDepartment.getName()).isNotBlank();
		assertThat(savedDepartment.getCode()).isNotBlank();
		assertThat(savedDepartment.getType()).isNotNull();
		assertThat(savedDepartment.getName()).isEqualTo(departmentDto.getName());
		assertThat(savedDepartment.getType()).isEqualTo(departmentDto.getType());
		assertThat(savedDepartment.getCode()).isEqualTo(departmentDto.getCode());
	}
	
	@Test
	void testSaveWithoutCode() {
		departmentDto.setCode(null);
		DepartmentDTO savedDepartment = departmentService.save(departmentDto);
		assertThat(savedDepartment.getId()).isNotNull();
		assertThat(savedDepartment.getName()).isNotBlank();
		assertThat(savedDepartment.getCode()).isNotBlank();
		assertThat(savedDepartment.getType()).isNotNull();
		assertThat(savedDepartment.getName()).isEqualTo(departmentDto.getName());
		assertThat(savedDepartment.getType()).isEqualTo(departmentDto.getType());
	}
	
	@Test
	public void checkNameIsRequired() {
		departmentDto.setName(null);
		assertThrows(ConstraintViolationException.class, () -> {
			departmentService.save(departmentDto);
		});
	}
	
	@Test
	public void checkTypeIsRequired() {
		departmentDto.setType(null);
		assertThrows(ConstraintViolationException.class, () -> {
			departmentService.save(departmentDto);
		});
	}
	
	@Test
	public void checkNameIsUnique() {
		departmentService.save(departmentDto);
		departmentDto.setCode("SU");
		assertThrows(ValidationException.class, () -> {
			departmentService.save(departmentDto);
		});
	}
	
	@Test
	public void checkCodeIsUnique() {
		departmentService.save(departmentDto);
		assertThrows(ValidationException.class, () -> {
			departmentService.save(departmentDto);
		});
	}
	
	@Test
	public void testUpdate() {
		DepartmentDTO savedDepartment=departmentService.save(departmentDto);
        savedDepartment.setName("Account Manager");
        savedDepartment.setType(DepartmentType.ACCOUNTS);
		DepartmentDTO updatedDepartment = departmentService.save(savedDepartment);
		assertThat(updatedDepartment.getId()).isNotNull();
		assertThat(updatedDepartment.getId()).isEqualTo(savedDepartment.getId());
		assertThat(updatedDepartment.getName()).isNotBlank();
		assertThat(updatedDepartment.getCode()).isNotBlank();
		assertThat(updatedDepartment.getName()).isNotEqualTo(departmentDto.getName());
		assertThat(updatedDepartment.getCode()).isEqualTo(savedDepartment.getCode());
		assertThat(updatedDepartment.getType()).isEqualTo(DepartmentType.ACCOUNTS);
	}

	@Test
	void testFindAll() {
		departmentService.save(departmentDto);
		departmentCriteria=new DepartmentCriteria();
		List<DepartmentDTO> departments = departmentService.findAll(departmentCriteria,pageable).getContent();
		assertThat(departments).asList();
		assertThat(departments).allMatch(currency -> !currency.getCode().isEmpty());
		assertThat(departments).allMatch(currency -> !currency.getName().isEmpty());
		assertThat(departments).allMatch(currency -> currency.getType()!=null);
	}

	@Test
	void testFindOne() {
		DepartmentDTO savedDepartment=departmentService.save(departmentDto);
		System.out.println("Department Id "+savedDepartment.getId());
		DepartmentDTO getDepartment=departmentService.findOne(savedDepartment.getId()).get();
		assertThat(savedDepartment.getName()).isEqualTo(getDepartment.getName());
		assertThat(savedDepartment.getCode()).isEqualTo(getDepartment.getCode());
	}
	
	@Test
	void testFindOneForInvalidId() {
		Optional<DepartmentDTO> getDepartment=departmentService.findOne(0L);
		assertFalse(getDepartment.isPresent());
	}

	@Test
	void testDelete() {
		DepartmentDTO savedDepartment = departmentService.save(departmentDto);
		departmentService.delete(savedDepartment.getId());
		Optional<DepartmentDTO> getDepartment = departmentService.findOne(savedDepartment.getId());
		assertFalse(getDepartment.isPresent());
	}

	@Test
	void testGetDepartmentsByIds() {
		DepartmentDTO savedDepartment = departmentService.save(departmentDto);
		List<Long> ids = new ArrayList<>();
		ids.add(savedDepartment.getId());
		List<DepartmentDTO> departmentsByIds = departmentService.getDepartmentsByIds(ids);
		assertThat(departmentsByIds).isNotEmpty();
		assertThat(departmentsByIds).allMatch(department -> department.getId() != null);
		assertThat(departmentsByIds).allMatch(department -> department.getCode() != null);
		assertThat(departmentsByIds).allMatch(department -> department.getName() != null);
	}
	
	@Test
	void testGetDepartmentsByIdsForInvalidIds() {
		List<Long> ids = new ArrayList<>();
		ids.add(0l);
		List<DepartmentDTO> departmentsByIds = departmentService.getDepartmentsByIds(ids);
		assertThat(departmentsByIds).isEmpty();
	}

}
