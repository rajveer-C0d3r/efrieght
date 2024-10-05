/**
 * 
 */
package com.grtship.common.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

/**
 * @author Ajay
 *
 */
@Configuration
public class KafkaTopicConfig {
	@Bean
	  public NewTopic auditTopic() {
	    return TopicBuilder.name("audit").build();
	  }

	  @Bean
	  public NewTopic systemAudit() {
	    return TopicBuilder.name("system-audit").build();
	  }
	  
	  @Bean
	  public NewTopic test() {
	    return TopicBuilder.name("test").build();
	  }
	  
	  @Bean
	  public NewTopic csaUser() {
	    return TopicBuilder.name("csa-user").build();
	  }
	  
	  @Bean
	  public NewTopic gsaUser() {
	    return TopicBuilder.name("gsa-user").build();
	  }
	  
	  @Bean
	  public NewTopic userLog() {
	    return TopicBuilder.name("user-log").build();
	  }
}
