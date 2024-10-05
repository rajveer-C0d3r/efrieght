package com.grtship.common.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.stereotype.Component;

@Configuration("kafka-producer-configuration")
@Component
public class KafkaProducerConfig {

    @Value("${kafka.bootstrap.servers}")
    private String bootstrapServers;

    @Value("${kafka.producer.key.serializer}")
    private String keySerializer;

    @Value("${kafka.producer.value.serializer}")
    private String valueSerializer;

    @Value("${kafka.producer.lingerms}")
    private int lingerMsConfig;

    @Value("${kafka.producer.retries}")
    private int retriesConfig;

    @Value("${kafka.producer.acks}")
    private String acksConfig;

    @Value("${kafka.producer.compression_type}")
    private String compressionTypeConfig;

    @Value("${kafka.producer.request_timeout_ms}")
    private String requestTimeoutMs;

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public ProducerFactory<String, String> producerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    @Bean
    public Map<String, Object> producerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, keySerializer);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, valueSerializer);
        props.put(ProducerConfig.LINGER_MS_CONFIG, lingerMsConfig);
        props.put(ProducerConfig.RETRIES_CONFIG, retriesConfig);
        props.put(ProducerConfig.ACKS_CONFIG, acksConfig);
        props.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, compressionTypeConfig);
        props.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, requestTimeoutMs);
        return props;
    }
}
