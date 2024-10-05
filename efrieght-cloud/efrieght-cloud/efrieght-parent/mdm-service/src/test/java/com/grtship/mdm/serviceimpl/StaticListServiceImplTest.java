package com.grtship.mdm.serviceimpl;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.audit.AuditAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.metrics.KafkaMetricsAutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.grtship.core.dto.KeyLabelBeanDTO;
import com.grtship.core.enumeration.DomainStatus;

@SpringBootTest(properties = "config.caching.enabled=false")
@Transactional
@EnableAutoConfiguration(exclude = { KafkaAutoConfiguration.class, KafkaMetricsAutoConfiguration.class,
		AuditAutoConfiguration.class })
@ActiveProfiles(profiles = { "test" })
public class StaticListServiceImplTest {

	@Autowired
	private  StaticListServiceImpl staticListServiceImpl;
	
	@Test
	void testGetList() {
		List<KeyLabelBeanDTO> staticList = staticListServiceImpl.getList(DomainStatus.class);
		assertThat(staticList).isNotEmpty();
		assertThat(staticList).hasSizeGreaterThanOrEqualTo(1);
		assertThat(staticList).allMatch(keyLabel -> keyLabel.getKey()!=null);
		assertThat(staticList).allMatch(keyLabel -> keyLabel.getLabel()!=null);
	}
	
}
