package com.grtship.mdm.serviceimpl;

import static org.assertj.core.api.Assertions.assertThat;

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

import com.grtship.mdm.domain.ObjectCode;
import com.grtship.mdm.repository.ObjectCodeRepository;
import com.grtship.mdm.service.CodeGeneratorService;

@SpringBootTest(properties = "config.caching.enabled=false")
@Transactional
@EnableAutoConfiguration(exclude = { KafkaAutoConfiguration.class, KafkaMetricsAutoConfiguration.class,
		AuditAutoConfiguration.class })
@ActiveProfiles(profiles = { "test" })
public class CodeGeneratorServiceImplTest {
	
	private static final String DEPARTMENT = "Department";
	
	@Autowired
	private CodeGeneratorService codeGeneratorService;
	
	@Autowired
	private ObjectCodeRepository objectCodeRepository;
	
	private ObjectCode objectCode;
	
	public static ObjectCode prepareObjectCode() {
	    ObjectCode objectCode = new ObjectCode();
    	objectCode.setObjectName(DEPARTMENT);
    	objectCode.setPrefix("D");
    	objectCode.setPadding(2L);
    	objectCode.setCounter(50L);
    	objectCode.setBlockSize(200);
    	return objectCode;
	}
	
	@BeforeEach
    public void init() {
    	objectCode=prepareObjectCode();
    }

	@Test
	void testGenerateCode() {
		objectCodeRepository.save(objectCode);
		String departmentCode = codeGeneratorService.generateCode(DEPARTMENT, null);
//		assertThat(departmentCode).isNotNull().startsWith("D").hasSize(3);
//		String designationCode = codeGeneratorService.generateCode("Entity", "A");
//		assertThat(designationCode).isNotNull().startsWith("A");
	}
	
	@Test
    public void testGenerateCodeIfObjectNameAlreadyNotPresent() {
		String departmentCode = codeGeneratorService.generateCode(DEPARTMENT, null);
		assertThat(departmentCode).isNotNull();
	}
}
