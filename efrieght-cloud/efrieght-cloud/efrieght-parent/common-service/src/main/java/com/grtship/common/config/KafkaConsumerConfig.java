package com.grtship.common.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.stereotype.Component;

@EnableKafka
@Configuration("kafka-consumer-configuration")
@Component
public class KafkaConsumerConfig {

	@Value("${kafka.bootstrap.servers}")
	private String bootstrapServers;

	@Value("${kafka.consumer.group_id}")
	private String groupId;

	@Value("${kafka.consumer.offset.reset_config}")
	private String offsetResetConfig;

	@Value("${kafka.consumer.enable.auto_commit}")
	private boolean enableAutoCommit;

	@Value("${kafka.consumer.key.deserializer}")
	private String keyDeserializer;

	@Value("${kafka.consumer.value.deserializer}")
	private String valueDeserializer;

	@Value("${kafka.consumer.auto.commit.interval_ms}")
	private String autoCommitIntervalMsConfig;

	@Value("${kafka.consumers.concurrency.default:3}")
	private int concurrency;

	@Value("${kafka.batch.fetch.min.bytes: 10000}")
	private Integer minFetchBytes;

	@Value("${kafka.batch.fetch.max.wait.ms: 25000}")
	private Integer maxFetchWaitMs;

	@Bean
	public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
		ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
		factory.setConsumerFactory(consumerFactory(enableAutoCommit));
		factory.setConcurrency(concurrency);
		factory.getContainerProperties().setPollTimeout(3000);
		return factory;
	}

	@Bean
	public ConsumerFactory<String, String> consumerFactory(boolean isEnableAutoCommit) {
		Map<String, Object> consumerConfigMap = commonConsumerConfig();
		consumerConfigMap.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, isEnableAutoCommit);
		return new DefaultKafkaConsumerFactory<>(consumerConfigMap,
				new org.apache.kafka.common.serialization.StringDeserializer(),
				new org.apache.kafka.common.serialization.StringDeserializer());
	}

	private Map<String, Object> commonConsumerConfig() {
		Map<String, Object> props = new HashMap<>();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, keyDeserializer);
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, valueDeserializer);
		props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, offsetResetConfig);
		props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, autoCommitIntervalMsConfig);
		return props;
	}

	@Bean
	public ConcurrentKafkaListenerContainerFactory<String, String> kafkaManualAckListenerContainerFactory() {
		ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
		factory.setConsumerFactory(consumerFactory(false));
		factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
		factory.setConcurrency(concurrency);
		factory.getContainerProperties().setPollTimeout(3000);
		return factory;
	}

}