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

import com.grtship.core.dto.DesignationDTO;
import com.grtship.core.filter.LongFilter;
import com.grtship.core.filter.StringFilter;
import com.grtship.mdm.criteria.DesignationCriteria;
import com.grtship.mdm.domain.Designation;

@SpringBootTest(properties = "config.caching.enabled=false")
@Transactional
@EnableAutoConfiguration(exclude = { KafkaAutoConfiguration.class, KafkaMetricsAutoConfiguration.class,
		AuditAutoConfiguration.class })
@ActiveProfiles(profiles = { "test" })
public class DesignationFilterServiceImplTest {
	
	@Autowired
	private DesignationServiceImpl designationServiceImpl;
	@Autowired
	private DesignationFilterServiceImpl designationFilterServiceImpl;
	private DesignationDTO designationDTO;
	private DesignationCriteria designationCriteria;
	
	@BeforeEach
	void setUp() {
		designationDTO=DesignationServiceImplTest.prepareDesignationDto();
	}
	
	@Test
	void testFindByCriteriaDesignationCriteriaId() {
		DesignationDTO savedDesignation = designationServiceImpl.save(designationDTO);
		LongFilter longFilter = new LongFilter();
		longFilter.setEquals(savedDesignation.getId());
		designationCriteria = new DesignationCriteria();
		designationCriteria.setId(longFilter);
		List<Designation> findAll = designationFilterServiceImpl.findByCriteria(designationCriteria);
		assertThat(findAll).isNotEmpty();
		assertThat(findAll).allMatch(designation -> designation.getId().equals(longFilter.getEquals()));
		assertThat(findAll).allMatch(designation -> designation.getCode() != null);
		assertThat(findAll).allMatch(designation -> designation.getName() != null);
	}
	
	@Test
	void testFindByCriteriaDesignationCriteriaCodeForInvalidId() {
		LongFilter longFilter = new LongFilter();
		longFilter.setEquals(0l);
		designationCriteria = new DesignationCriteria();
		designationCriteria.setId(longFilter);
		List<Designation> findAll = designationFilterServiceImpl.findByCriteria(designationCriteria);
		assertThat(findAll).isEmpty();
	}

	@Test
	void testFindByCriteriaDesignationCriteriaCode() {
		DesignationDTO savedDesignation = designationServiceImpl.save(designationDTO);
		StringFilter stringFilter = new StringFilter();
		stringFilter.setEquals(savedDesignation.getCode());
		designationCriteria = new DesignationCriteria();
		designationCriteria.setCode(stringFilter);
		List<Designation> findAll = designationFilterServiceImpl.findByCriteria(designationCriteria);
		assertThat(findAll).isNotEmpty();
		assertThat(findAll).allMatch(designation -> designation.getId() != null);
		assertThat(findAll).allMatch(designation -> designation.getCode().equals(stringFilter.getEquals()));
		assertThat(findAll).allMatch(designation -> designation.getName() != null);
	}
	
	@Test
	void testFindByCriteriaDesignationCriteriaCodeForInvalidCode() {
		StringFilter stringFilter = new StringFilter();
		stringFilter.setEquals("ERRORERROR");
		designationCriteria = new DesignationCriteria();
		designationCriteria.setCode(stringFilter);
		List<Designation> findAll = designationFilterServiceImpl.findByCriteria(designationCriteria);
		assertThat(findAll).isEmpty();
	}
	
	@Test
	void testFindByCriteriaDesignationCriteriaName() {
		DesignationDTO savedDesignation = designationServiceImpl.save(designationDTO);
		StringFilter stringFilter = new StringFilter();
		stringFilter.setEquals(savedDesignation.getName());
		designationCriteria = new DesignationCriteria();
		designationCriteria.setName(stringFilter);
		List<Designation> findAll = designationFilterServiceImpl.findByCriteria(designationCriteria);
		assertThat(findAll).isNotEmpty();
		assertThat(findAll).allMatch(designation -> designation.getId() != null);
		assertThat(findAll).allMatch(designation -> designation.getCode()!=null);
		assertThat(findAll).allMatch(designation -> designation.getName().equals(stringFilter.getEquals()));
	}
	
	@Test
	void testFindByCriteriaDesignationCriteriaCodeForInvalidName() {
		StringFilter stringFilter = new StringFilter();
		stringFilter.setEquals("ERRORERROR");
		designationCriteria = new DesignationCriteria();
		designationCriteria.setName(stringFilter);
		List<Designation> findAll = designationFilterServiceImpl.findByCriteria(designationCriteria);
		assertThat(findAll).isEmpty();
	}

	@Test
	void testFindByCriteriaDesignationCriteriaPageable() {
		DesignationDTO savedDesignation = designationServiceImpl.save(designationDTO);
		StringFilter stringFilter = new StringFilter();
		stringFilter.setEquals(savedDesignation.getCode());
		designationCriteria = new DesignationCriteria();
		designationCriteria.setCode(stringFilter);
		Page<DesignationDTO> findAll = designationFilterServiceImpl.findByCriteria(designationCriteria,
				PageRequest.of(0, 20));
		assertThat(findAll.getNumber()).isEqualTo(0);
		assertThat(findAll.getContent()).allMatch(designation -> designation.getId() != null);
		assertThat(findAll.getContent())
				.allMatch(designation -> designation.getCode().equals(stringFilter.getEquals()));
		assertThat(findAll.getContent()).allMatch(designation -> designation.getName() != null);
	}

	@Test
	void testCountByCriteria() {
		DesignationDTO savedDesignation = designationServiceImpl.save(designationDTO);
		StringFilter stringFilter = new StringFilter();
		stringFilter.setEquals(savedDesignation.getCode());
		designationCriteria = new DesignationCriteria();
		designationCriteria.setCode(stringFilter);
		long count = designationFilterServiceImpl.countByCriteria(designationCriteria);
		assertThat(count).isGreaterThanOrEqualTo(1);
	}
	
	@Test
	void testCountByCriteriaForInvalidCriteria() {
		StringFilter stringFilter = new StringFilter();
		stringFilter.setEquals("ERRORERROR");
		designationCriteria = new DesignationCriteria();
		designationCriteria.setCode(stringFilter);
		long count = designationFilterServiceImpl.countByCriteria(designationCriteria);
		assertThat(count).isEqualTo(0);
	}

}
