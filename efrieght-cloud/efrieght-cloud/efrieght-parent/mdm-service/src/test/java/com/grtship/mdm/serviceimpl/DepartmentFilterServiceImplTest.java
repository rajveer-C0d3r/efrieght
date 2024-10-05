package com.grtship.mdm.serviceimpl;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.audit.AuditAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.metrics.KafkaMetricsAutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.grtship.core.dto.DepartmentDTO;
import com.grtship.core.filter.StringFilter;
import com.grtship.mdm.criteria.DepartmentCriteria;
import com.grtship.mdm.domain.Department;
import com.grtship.mdm.service.DepartmentService;

@SpringBootTest(properties = "config.caching.enabled=false")
@Transactional
@EnableAutoConfiguration(exclude = { KafkaAutoConfiguration.class, KafkaMetricsAutoConfiguration.class,
		AuditAutoConfiguration.class })
@ActiveProfiles(profiles = { "test" })
public class DepartmentFilterServiceImplTest {
	
	@Autowired 
	private DepartmentService departmentService;

	@Autowired
	private DepartmentFilterServiceImpl departmentFilterServiceImpl;
	
	private DepartmentDTO departmentDto;
	private DepartmentCriteria departmentCriteria;
	
	@Mock 
	Pageable pageable;
	
	@BeforeEach
	void setUp() {
		departmentDto=DepartmentServiceImplTest.prepareDepartmentDto();
	}

	@Test
	void testFindByCriteriaDepartmentCriteriaCode() {
		DepartmentDTO savedDepartment = departmentService.save(departmentDto);
		StringFilter stringFilter=new StringFilter();
		stringFilter.setEquals(savedDepartment.getCode());
		departmentCriteria=new DepartmentCriteria();
		departmentCriteria.setCode(stringFilter);
		List<Department> findByCriteria = departmentFilterServiceImpl.findByCriteria(departmentCriteria);
		assertThat(findByCriteria).isNotEmpty();
		assertThat(findByCriteria).allMatch(department -> department.getId()!=null);
		assertThat(findByCriteria).allMatch(department -> department.getCode().equals(stringFilter.getEquals()));
		assertThat(findByCriteria).allMatch(department -> department.getName()!=null);
		assertThat(findByCriteria).allMatch(department -> department.getType()!=null);
	}
	
	@Test
	void testFindByCriteriaDepartmentCriteriaCodeForInvalidCode() {
		StringFilter stringFilter=new StringFilter();
		stringFilter.setEquals("ERRORERROR");
		departmentCriteria=new DepartmentCriteria();
		departmentCriteria.setCode(stringFilter);
		List<Department> findByCriteria = departmentFilterServiceImpl.findByCriteria(departmentCriteria);
		assertThat(findByCriteria).isEmpty();
	}
	
	@Test
	void testFindByCriteriaDepartmentCriteriaName() {
		DepartmentDTO savedDepartment = departmentService.save(departmentDto);
		StringFilter stringFilter=new StringFilter();
		stringFilter.setEquals(savedDepartment.getName());
		departmentCriteria=new DepartmentCriteria();
		departmentCriteria.setName(stringFilter);
		List<Department> findByCriteria = departmentFilterServiceImpl.findByCriteria(departmentCriteria);
		assertThat(findByCriteria).isNotEmpty();
		assertThat(findByCriteria).allMatch(department -> department.getId()!=null);
		assertThat(findByCriteria).allMatch(department -> department.getCode()!=null);
		assertThat(findByCriteria).allMatch(department -> department.getName().equals(stringFilter.getEquals()));
		assertThat(findByCriteria).allMatch(department -> department.getType()!=null);
	}
	
	@Test
	void testFindByCriteriaDepartmentCriteriaCodeForInvalidName() {
		StringFilter stringFilter=new StringFilter();
		stringFilter.setEquals("ERRORERROR");
		departmentCriteria=new DepartmentCriteria();
		departmentCriteria.setName(stringFilter);
		List<Department> findByCriteria = departmentFilterServiceImpl.findByCriteria(departmentCriteria);
		assertThat(findByCriteria).isEmpty();
	}
	
	@Test
	void testFindByCriteriaDepartmentCriteriaType() {
		DepartmentDTO savedDepartment = departmentService.save(departmentDto);
		departmentCriteria = new DepartmentCriteria();
		departmentCriteria.setType(savedDepartment.getType().name());
		List<Department> findByCriteria = departmentFilterServiceImpl.findByCriteria(departmentCriteria);
		assertThat(findByCriteria).isNotEmpty();
		assertThat(findByCriteria).allMatch(department -> department.getId() != null);
		assertThat(findByCriteria).allMatch(department -> department.getCode() != null);
		assertThat(findByCriteria).allMatch(department -> department.getName() != null);
		assertThat(findByCriteria)
				.allMatch(department -> department.getType().name().equals(departmentCriteria.getType()));
	}
	
	@Test
	void testFindByCriteriaDepartmentCriteriaIds() {
		DepartmentDTO savedDepartment = departmentService.save(departmentDto);
		departmentCriteria = new DepartmentCriteria();
		List<Long> departmentIds=new ArrayList<Long>();
		departmentIds.add(savedDepartment.getId());
		departmentCriteria.setIds(departmentIds);
		List<Department> findByCriteria = departmentFilterServiceImpl.findByCriteria(departmentCriteria);
		assertThat(findByCriteria).isNotEmpty();
		assertThat(findByCriteria).allMatch(department -> department.getId().equals(departmentCriteria.getIds().get(0)));
		assertThat(findByCriteria).allMatch(department -> department.getCode() != null);
		assertThat(findByCriteria).allMatch(department -> department.getName() != null);
		assertThat(findByCriteria).allMatch(department -> department.getType() != null);
	}
	
	@Test
	void testFindByCriteriaDepartmentCriteriaIdsForInvalidIds() {
		List<Long> ids=new ArrayList<Long>();
		ids.add(0l);
		departmentCriteria = new DepartmentCriteria();
		departmentCriteria.setIds(ids);
		List<Department> findByCriteria = departmentFilterServiceImpl.findByCriteria(departmentCriteria);
		assertThat(findByCriteria).isEmpty();
	}

	@Test
	void testFindByCriteriaDepartmentCriteriaPageable() {
		DepartmentDTO savedDepartment = departmentService.save(departmentDto);
		StringFilter stringFilter = new StringFilter();
		stringFilter.setEquals(savedDepartment.getName());
		departmentCriteria = new DepartmentCriteria();
		departmentCriteria.setName(stringFilter);
		Page<DepartmentDTO> findByCriteria = departmentFilterServiceImpl.findByCriteria(departmentCriteria,
				PageRequest.of(0, 20));
		assertThat(findByCriteria.getNumber()).isEqualTo(0);
		assertThat(findByCriteria.getContent()).hasSizeLessThanOrEqualTo(20);
		assertThat(findByCriteria.getContent()).allMatch(department -> department.getId() != null);
		assertThat(findByCriteria.getContent()).allMatch(department -> department.getCode() != null);
		assertThat(findByCriteria.getContent())
				.allMatch(department -> department.getName().equals(stringFilter.getEquals()));
		assertThat(findByCriteria.getContent()).allMatch(department -> department.getType() != null);
	}

	@Test
	void testCountByCriteria() {
		DepartmentDTO savedDepartment = departmentService.save(departmentDto);
		StringFilter stringFilter = new StringFilter();
		stringFilter.setEquals(savedDepartment.getName());
		departmentCriteria = new DepartmentCriteria();
		departmentCriteria.setName(stringFilter);
		long count = departmentFilterServiceImpl.countByCriteria(departmentCriteria);
		assertThat(count).isGreaterThanOrEqualTo(1);
	}
	
	@Test
	void testCountByCriteriaForInvalidCriteria() {
		StringFilter stringFilter = new StringFilter();
		stringFilter.setEquals("ERRORERROR");
		departmentCriteria = new DepartmentCriteria();
		departmentCriteria.setName(stringFilter);
		long count = departmentFilterServiceImpl.countByCriteria(departmentCriteria);
		assertThat(count).isEqualTo(0);
	}

}
