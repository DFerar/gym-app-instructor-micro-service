package com.gym.gymmicroservice.properties;

import lombok.Data;
import lombok.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@Value
@ConfigurationProperties(prefix = "token")
public class JwtProperties {
    String key;
    Long expiration;
}
