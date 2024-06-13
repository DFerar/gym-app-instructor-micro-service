package com.gym.gymmicroservice.properties;

import lombok.Data;
import lombok.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@Value
@ConfigurationProperties(prefix = "spring.kafka")
public class KafkaProperties {
    String bootstrapServers;
    String groupId;
    String defaultTopic;
}
