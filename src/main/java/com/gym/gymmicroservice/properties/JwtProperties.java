package com.gym.gymmicroservice.properties;

import lombok.Data;
import lombok.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@Value
@ConfigurationProperties(prefix = "token")
public class JwtProperties { //TODO: remove jwt security properties
    String key;
    Long expiration;
}
