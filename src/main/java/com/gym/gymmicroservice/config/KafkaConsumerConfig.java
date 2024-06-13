package com.gym.gymmicroservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gym.gymmicroservice.dto.request.AcceptWorkloadRequestDto;
import com.gym.gymmicroservice.properties.KafkaProperties;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(KafkaProperties.class)
@EnableKafka
public class KafkaConsumerConfig {
    private final KafkaProperties kafkaProperties;
    private static final String GROUP_ID = "gym-group";
    private static final String TOPIC = "gym-topic";

    @Bean
    public Map<String, Object> consumerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID);
        props.remove(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG);
        props.remove(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG);
        return props;
    }

    @Bean
    public ConsumerFactory<String, Map<Object, Object>> consumerFactory() {
        var keyDeserializer = new StringDeserializer();
        var valueDeserializer = new JsonDeserializer<Map<Object, Object>>(Map.class, new ObjectMapper());
        var errorHandlingDeserializer = new ErrorHandlingDeserializer<Map<Object, Object>>(valueDeserializer);
        valueDeserializer.ignoreTypeHeaders();
        return new DefaultKafkaConsumerFactory<>(consumerConfigs(), keyDeserializer, errorHandlingDeserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Map<Object, Object>> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Map<Object, Object>> factory =
            new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }
}
